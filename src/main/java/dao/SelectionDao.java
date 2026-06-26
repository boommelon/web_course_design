package dao;

import bean.SelectionApplication;
import bean.SelectionChoice;
import util.SQLHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 选题志愿数据访问。
 *
 * 业务规则（后端强校验）：
 *  - 每个学生每轮一个申请批次（DB UNIQUE(student_id, round) 兜底）。
 *  - 每轮至少 1 个、最多 3 个志愿，志愿之间不重复。
 *  - 一个题目同一轮被填为志愿的学生数最多 3（意向人数限制）。
 *
 * 提交志愿是多条 SQL 的事务，只取一次 Connection，统一 commit/rollback/close。
 */
public class SelectionDao {

    /** 提交结果：成功或携带失败原因。 */
    public static class SubmitResult {
        public final boolean success;
        public final String message;
        private SubmitResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public static SubmitResult ok() { return new SubmitResult(true, "提交成功"); }
        public static SubmitResult fail(String msg) { return new SubmitResult(false, msg); }
    }

    /** 一个题目在某轮的意向人数上限。 */
    private static final int TOPIC_INTENT_LIMIT = 3;
    private static final int MAX_CHOICES = 3;

    /**
     * 提交本轮三志愿。topicIds 顺序即志愿顺序（rank 1/2/3）。
     * 整个过程在单事务内完成。
     */
    public SubmitResult submitChoices(int studentId, List<Integer> topicIds, int round) throws SQLException {
        // 保序收集并显式拒绝重复志愿，避免把重复项静默去掉后仍提交成功。
        Set<Integer> ordered = new LinkedHashSet<Integer>();
        if (topicIds != null) {
            for (Integer tid : topicIds) {
                if (tid != null && tid.intValue() > 0) {
                    if (!ordered.add(tid)) {
                        return SubmitResult.fail("志愿题目不能重复");
                    }
                }
            }
        }
        if (ordered.isEmpty()) {
            return SubmitResult.fail("请至少选择 1 个志愿题目");
        }
        if (ordered.size() > MAX_CHOICES) {
            return SubmitResult.fail("最多只能填报 " + MAX_CHOICES + " 个志愿");
        }

        Connection conn = null;
        try {
            conn = SQLHelper.getConnection();
            conn.setAutoCommit(false);

            // 1. 该学生本轮是否已提交（覆盖式：删除旧批次后重提，允许截止前修改）
            Integer existingAppId = findApplicationId(conn, studentId, round);
            if (existingAppId != null) {
                deleteChoicesByApplication(conn, existingAppId);
                deleteApplication(conn, existingAppId);
            }

            // 2. 已经拿到最终分配的学生不能再提交志愿
            if (isStudentFinalAssigned(conn, studentId)) {
                conn.rollback();
                return SubmitResult.fail("你已被最终分配题目，无法再次选题");
            }

            // 3. 校验每个志愿题目：必须 approved、未被最终分配、且本轮意向人数未超限
            for (Integer tid : ordered) {
                if (!isTopicSelectable(conn, tid.intValue())) {
                    conn.rollback();
                    return SubmitResult.fail("题目 #" + tid + " 不可选（未通过审核或已被分配）");
                }
                int intent = countTopicIntent(conn, tid.intValue(), round);
                if (intent >= TOPIC_INTENT_LIMIT) {
                    conn.rollback();
                    return SubmitResult.fail("题目 #" + tid + " 本轮志愿人数已满（上限 " + TOPIC_INTENT_LIMIT + " 人）");
                }
            }

            // 4. 写入申请批次 + 志愿明细
            int appId = insertApplication(conn, studentId, round);
            int rank = 1;
            for (Integer tid : ordered) {
                insertChoice(conn, appId, studentId, tid.intValue(), rank, round);
                rank++;
            }

            conn.commit();
            return SubmitResult.ok();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    // ---------------- 查询（普通只读走 SQLHelper） ----------------

    /** 学生本轮的申请批次（含志愿明细），无则返回 null。 */
    public SelectionApplication findApplicationWithChoices(int studentId, int round) throws SQLException {
        String appSql = "SELECT * FROM selection_applications WHERE student_id=? AND round=?";
        ResultSet rs = SQLHelper.executeQuery(appSql, studentId, round);
        SelectionApplication app = null;
        try {
            if (rs.next()) {
                app = rowToApplication(rs);
            }
        } finally {
            SQLHelper.close(rs);
        }
        if (app == null) {
            return null;
        }
        app.setChoices(findChoicesByApplication(app.getId()));
        return app;
    }

    public List<SelectionChoice> findChoicesByApplication(int applicationId) throws SQLException {
        String sql = choiceBaseSelect() + "WHERE c.application_id=? ORDER BY c.choice_rank";
        ResultSet rs = SQLHelper.executeQuery(sql, applicationId);
        try {
            return resultSetToChoiceList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 专业负责人某轮确认页：本专业本轮全部志愿，带学生信息、题目教师、
     * 以及该学生/该题目是否已被最终分配的标记。按题目聚合排序。
     */
    public List<SelectionChoice> findChoicesForConfirm(String college, String major, int round) throws SQLException {
        String sql = choiceBaseSelect()
                + "WHERE t.college=? AND t.major=? AND c.round=? "
                + "ORDER BY c.topic_id, c.choice_rank, c.id";
        ResultSet rs = SQLHelper.executeQuery(sql, college, major, round);
        try {
            return resultSetToChoiceList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public int countSubmittedStudents(String college, String major, int round) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT a.student_id) FROM selection_applications a "
                + "JOIN users u ON a.student_id=u.id "
                + "WHERE u.college=? AND u.major=? AND a.round=?";
        ResultSet rs = SQLHelper.executeQuery(sql, college, major, round);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    // ---------------- 事务内私有辅助 ----------------

    private Integer findApplicationId(Connection conn, int studentId, int round) throws SQLException {
        String sql = "SELECT id FROM selection_applications WHERE student_id=? AND round=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, studentId);
            ps.setInt(2, round);
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

    private boolean isStudentFinalAssigned(Connection conn, int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM final_assignments WHERE student_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, studentId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private boolean isTopicSelectable(Connection conn, int topicId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topics t WHERE t.id=? AND t.status='approved' "
                + "AND NOT EXISTS (SELECT 1 FROM final_assignments fa WHERE fa.topic_id=t.id)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, topicId);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private int countTopicIntent(Connection conn, int topicId, int round) throws SQLException {
        String sql = "SELECT COUNT(*) FROM selection_choices WHERE topic_id=? AND round=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = null;
        try {
            ps.setInt(1, topicId);
            ps.setInt(2, round);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    private int insertApplication(Connection conn, int studentId, int round) throws SQLException {
        String sql = "INSERT INTO selection_applications(student_id, round, status) VALUES(?,?, 'submitted')";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ResultSet keys = null;
        try {
            ps.setInt(1, studentId);
            ps.setInt(2, round);
            ps.executeUpdate();
            keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } finally {
            if (keys != null) keys.close();
            ps.close();
        }
    }

    private void insertChoice(Connection conn, int appId, int studentId, int topicId, int rank, int round)
            throws SQLException {
        String sql = "INSERT INTO selection_choices(application_id, student_id, topic_id, choice_rank, round, status) "
                + "VALUES(?,?,?,?,?, 'pending')";
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.setInt(1, appId);
            ps.setInt(2, studentId);
            ps.setInt(3, topicId);
            ps.setInt(4, rank);
            ps.setInt(5, round);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void deleteChoicesByApplication(Connection conn, int appId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM selection_choices WHERE application_id=?");
        try {
            ps.setInt(1, appId);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    private void deleteApplication(Connection conn, int appId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM selection_applications WHERE id=?");
        try {
            ps.setInt(1, appId);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }

    // ---------------- 行映射 ----------------

    private String choiceBaseSelect() {
        return "SELECT c.*, u.name AS student_name, u.student_no, u.class_name, "
             + "t.title AS topic_title, t.teacher_id, te.name AS teacher_name, "
             + "(SELECT COUNT(*) FROM final_assignments fa WHERE fa.student_id=c.student_id) AS student_assigned, "
             + "(SELECT COUNT(*) FROM final_assignments fb WHERE fb.topic_id=c.topic_id) AS topic_assigned "
             + "FROM selection_choices c "
             + "JOIN users u ON c.student_id=u.id "
             + "JOIN topics t ON c.topic_id=t.id "
             + "JOIN users te ON t.teacher_id=te.id ";
    }

    private List<SelectionChoice> resultSetToChoiceList(ResultSet rs) throws SQLException {
        List<SelectionChoice> list = new ArrayList<SelectionChoice>();
        while (rs.next()) {
            list.add(rowToChoice(rs));
        }
        return list;
    }

    private SelectionChoice rowToChoice(ResultSet rs) throws SQLException {
        SelectionChoice c = new SelectionChoice();
        c.setId(rs.getInt("id"));
        c.setApplicationId(rs.getInt("application_id"));
        c.setStudentId(rs.getInt("student_id"));
        c.setTopicId(rs.getInt("topic_id"));
        c.setChoiceRank(rs.getInt("choice_rank"));
        c.setRound(rs.getInt("round"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setStudentName(rs.getString("student_name"));
        c.setStudentNo(rs.getString("student_no"));
        c.setClassName(rs.getString("class_name"));
        c.setTopicTitle(rs.getString("topic_title"));
        c.setTeacherId(rs.getInt("teacher_id"));
        c.setTeacherName(rs.getString("teacher_name"));
        c.setStudentAssigned(rs.getInt("student_assigned") > 0);
        c.setTopicAssigned(rs.getInt("topic_assigned") > 0);
        return c;
    }

    private SelectionApplication rowToApplication(ResultSet rs) throws SQLException {
        SelectionApplication app = new SelectionApplication();
        app.setId(rs.getInt("id"));
        app.setStudentId(rs.getInt("student_id"));
        app.setRound(rs.getInt("round"));
        app.setStatus(rs.getString("status"));
        app.setSubmitTime(rs.getTimestamp("submit_time"));
        return app;
    }
}
