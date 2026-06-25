package dao;

import bean.FinalAssignment;
import util.SQLHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 最终分配数据访问。
 *
 * "一人一题、一题一人" 由 final_assignments 的 UNIQUE(student_id) 与
 * UNIQUE(topic_id) 兜底；本类在事务内先做业务校验，唯一约束作为最后防线。
 *
 * 确认 / 强制分配均为多条 SQL 的事务，只取一次 Connection。
 */
public class FinalAssignmentDao {

    public static class AssignResult {
        public final boolean success;
        public final String message;
        private AssignResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public static AssignResult ok() { return new AssignResult(true, "分配成功"); }
        public static AssignResult fail(String msg) { return new AssignResult(false, msg); }
    }

    /**
     * 专业负责人确认：把某学生分配给某题目。
     * source 取 round1 / round2；choiceRank 为该题在学生志愿中的顺序（可空）。
     */
    public AssignResult confirm(int studentId, int topicId, String source, Integer choiceRank,
                                int directorId, String comment, String college, String major) throws SQLException {
        return doAssign(studentId, topicId, source, choiceRank, directorId, comment, college, major);
    }

    /**
     * 强制分配：把剩余学生指定给剩余题目，source=manual。
     */
    public AssignResult manualAssign(int studentId, int topicId, int directorId,
                                     String comment, String college, String major) throws SQLException {
        return doAssign(studentId, topicId, "manual", null, directorId, comment, college, major);
    }

    private AssignResult doAssign(int studentId, int topicId, String source, Integer choiceRank,
                                  int directorId, String comment, String college, String major) throws SQLException {
        Connection conn = null;
        try {
            conn = SQLHelper.getConnection();
            conn.setAutoCommit(false);

            // 1. 学生属于本专业
            if (!belongsToMajor(conn, "users", studentId, college, major, "student")) {
                conn.rollback();
                return AssignResult.fail("该学生不属于你负责的专业");
            }
            // 2. 题目属于本专业且已审核通过
            if (!topicApprovedInMajor(conn, topicId, college, major)) {
                conn.rollback();
                return AssignResult.fail("该题目不属于你负责的专业或未通过审核");
            }
            // 3. 学生未被分配
            if (existsByColumn(conn, "student_id", studentId)) {
                conn.rollback();
                return AssignResult.fail("该学生已被分配题目");
            }
            // 4. 题目未被分配
            if (existsByColumn(conn, "topic_id", topicId)) {
                conn.rollback();
                return AssignResult.fail("该题目已被分配给其他学生");
            }

            // 5. 写入最终分配（唯一约束兜底并发冲突）
            insertAssignment(conn, studentId, topicId, source, choiceRank, directorId, comment);

            // 6. 题目状态置 assigned
            updateTopicStatus(conn, topicId, "assigned");

            // 7. 同步志愿明细：该学生该题标 selected，其余志愿标 not_selected
            markChoicesAfterAssign(conn, studentId, topicId);

            conn.commit();
            return AssignResult.ok();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            // 唯一约束冲突（并发）转友好提示
            if (isDuplicateKey(e)) {
                return AssignResult.fail("分配冲突：该学生或题目刚刚已被分配");
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 撤销分配（确认阶段纠错用）：删除分配记录并把题目状态回退为 approved。
     */
    public boolean revoke(int studentId, String college, String major) throws SQLException {
        Connection conn = null;
        try {
            conn = SQLHelper.getConnection();
            conn.setAutoCommit(false);

            Integer topicId = findAssignedTopic(conn, studentId, college, major);
            if (topicId == null) {
                conn.rollback();
                return false;
            }
            PreparedStatement del = conn.prepareStatement("DELETE FROM final_assignments WHERE student_id=?");
            try {
                del.setInt(1, studentId);
                del.executeUpdate();
            } finally {
                del.close();
            }
            updateTopicStatus(conn, topicId.intValue(), "approved");
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // ---------------- 查询（只读走 SQLHelper） ----------------

    private static final String BASE_SELECT =
            "SELECT fa.*, su.name AS student_name, su.student_no, su.class_name, su.major, "
          + "t.title AS topic_title, t.teacher_id, te.name AS teacher_name "
          + "FROM final_assignments fa "
          + "JOIN users su ON fa.student_id=su.id "
          + "JOIN topics t ON fa.topic_id=t.id "
          + "JOIN users te ON t.teacher_id=te.id ";

    public FinalAssignment findByStudent(int studentId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(BASE_SELECT + "WHERE fa.student_id=?", studentId);
        try {
            if (rs.next()) {
                return rowToAssignment(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<FinalAssignment> findByMajor(String college, String major) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                BASE_SELECT + "WHERE su.college=? AND su.major=? ORDER BY fa.id DESC", college, major);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<FinalAssignment> findAll() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(BASE_SELECT + "ORDER BY fa.id DESC");
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /** 教师查看分配到自己题目下的学生。 */
    public List<FinalAssignment> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(BASE_SELECT + "WHERE t.teacher_id=? ORDER BY fa.id DESC", teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public boolean isStudentAssigned(int studentId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM final_assignments WHERE student_id=?", studentId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public int countByMajor(String college, String major) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT COUNT(*) FROM final_assignments fa JOIN users u ON fa.student_id=u.id "
                        + "WHERE u.college=? AND u.major=?", college, major);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM final_assignments");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    // ---------------- 事务内私有辅助 ----------------

    private boolean belongsToMajor(Connection conn, String table, int userId, String college, String major, String role)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id=? AND college=? AND major=? AND role=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, userId);
            ps.setString(2, college);
            ps.setString(3, major);
            ps.setString(4, role);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private boolean topicApprovedInMajor(Connection conn, int topicId, String college, String major)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM topics WHERE id=? AND college=? AND major=? AND status IN ('approved','assigned')";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, topicId);
            ps.setString(2, college);
            ps.setString(3, major);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private boolean existsByColumn(Connection conn, String column, int value) throws SQLException {
        String sql = "SELECT COUNT(*) FROM final_assignments WHERE " + column + "=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, value);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private void insertAssignment(Connection conn, int studentId, int topicId, String source,
                                  Integer choiceRank, int directorId, String comment) throws SQLException {
        String sql = "INSERT INTO final_assignments(student_id, topic_id, source, choice_rank, confirmed_by, confirm_comment) "
                + "VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, studentId);
            ps.setInt(2, topicId);
            ps.setString(3, source);
            if (choiceRank == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, choiceRank.intValue());
            }
            ps.setInt(5, directorId);
            ps.setString(6, comment);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void updateTopicStatus(Connection conn, int topicId, String status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE topics SET status=? WHERE id=?");
        try {
            ps.setString(1, status);
            ps.setInt(2, topicId);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void markChoicesAfterAssign(Connection conn, int studentId, int topicId) throws SQLException {
        // 该学生命中该题目的志愿标 selected
        PreparedStatement sel = conn.prepareStatement(
                "UPDATE selection_choices SET status='selected' WHERE student_id=? AND topic_id=?");
        try {
            sel.setInt(1, studentId);
            sel.setInt(2, topicId);
            sel.executeUpdate();
        } finally {
            sel.close();
        }
        // 该学生其余志愿标 not_selected
        PreparedStatement other = conn.prepareStatement(
                "UPDATE selection_choices SET status='not_selected' WHERE student_id=? AND topic_id<>? AND status='pending'");
        try {
            other.setInt(1, studentId);
            other.setInt(2, topicId);
            other.executeUpdate();
        } finally {
            other.close();
        }
        // 其他学生把该题目填为志愿的，标 not_selected（题目已被占）
        PreparedStatement losers = conn.prepareStatement(
                "UPDATE selection_choices SET status='not_selected' WHERE topic_id=? AND student_id<>? AND status='pending'");
        try {
            losers.setInt(1, topicId);
            losers.setInt(2, studentId);
            losers.executeUpdate();
        } finally {
            losers.close();
        }
    }

    private Integer findAssignedTopic(Connection conn, int studentId, String college, String major)
            throws SQLException {
        String sql = "SELECT fa.topic_id FROM final_assignments fa JOIN users u ON fa.student_id=u.id "
                + "WHERE fa.student_id=? AND u.college=? AND u.major=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, studentId);
            ps.setString(2, college);
            ps.setString(3, major);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private boolean isDuplicateKey(SQLException e) {
        return e.getErrorCode() == 1062
                || (e.getSQLState() != null && e.getSQLState().startsWith("23"));
    }

    private List<FinalAssignment> resultSetToList(ResultSet rs) throws SQLException {
        List<FinalAssignment> list = new ArrayList<FinalAssignment>();
        while (rs.next()) {
            list.add(rowToAssignment(rs));
        }
        return list;
    }

    private FinalAssignment rowToAssignment(ResultSet rs) throws SQLException {
        FinalAssignment fa = new FinalAssignment();
        fa.setId(rs.getInt("id"));
        fa.setStudentId(rs.getInt("student_id"));
        fa.setTopicId(rs.getInt("topic_id"));
        fa.setSource(rs.getString("source"));
        int rank = rs.getInt("choice_rank");
        fa.setChoiceRank(rs.wasNull() ? null : rank);
        fa.setConfirmedBy(rs.getInt("confirmed_by"));
        fa.setConfirmTime(rs.getTimestamp("confirm_time"));
        fa.setConfirmComment(rs.getString("confirm_comment"));
        fa.setStudentName(rs.getString("student_name"));
        fa.setStudentNo(rs.getString("student_no"));
        fa.setClassName(rs.getString("class_name"));
        fa.setMajor(rs.getString("major"));
        fa.setTopicTitle(rs.getString("topic_title"));
        fa.setTeacherId(rs.getInt("teacher_id"));
        fa.setTeacherName(rs.getString("teacher_name"));
        return fa;
    }
}
