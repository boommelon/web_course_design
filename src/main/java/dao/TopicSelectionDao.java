package dao;

import bean.TopicSelection;
import dbutil.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 选题申请数据访问类
 * 负责topic_selections表的操作
 */
public class TopicSelectionDao {

    // 基础查询SQL（关联了学生姓名和课题名称）
    private String baseSql = "SELECT ts.*, u.name AS student_name, t.title AS topic_title "
            + "FROM topic_selections ts "
            + "JOIN users u ON ts.student_id = u.id "
            + "JOIN topics t ON ts.topic_id = t.id ";

    /**
     * 查询某个学生的所有选题申请
     */
    public List<TopicSelection> findByStudent(int studentId) throws SQLException {
        String sql = baseSql + "WHERE ts.student_id = ? ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询某个教师名下所有选题申请
     */
    public List<TopicSelection> findByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询所有选题申请
     */
    public List<TopicSelection> findAll() throws SQLException {
        String sql = baseSql + "ORDER BY ts.round_no DESC, ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询某个教师名下待审批的申请
     */
    public List<TopicSelection> findPendingByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? AND ts.status = 'pending' ORDER BY ts.id";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询某个教师名下已通过的学生课题对应关系
     */
    public List<TopicSelection> findApprovedByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? AND ts.status = 'approved' ORDER BY ts.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 新增选题申请
     */
    public void insert(int studentId, int topicId, String reason) throws SQLException {
        insert(studentId, topicId, reason, 1, "pending");
    }

    /**
     * 新增指定轮次和状态的选题记录
     */
    public void insert(int studentId, int topicId, String reason, int roundNo, String status) throws SQLException {
        String sql = "INSERT INTO topic_selections(student_id, topic_id, reason, round_no, status) VALUES(?,?,?,?,?)";
        SQLHelper.executeUpdate(sql, studentId, topicId, reason, roundNo, status);
    }

    /**
     * 更新选题申请状态（approved/rejected）
     */
    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE topic_selections SET status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, status, id);
    }

    /**
     * 教师更新自己课题下的选题申请状态（approved/rejected）
     */
    public int updateStatus(int id, String status, int teacherId) throws SQLException {
        String sql = "UPDATE topic_selections ts JOIN topics t ON ts.topic_id=t.id "
                + "SET ts.status=? WHERE ts.id=? AND t.teacher_id=?";
        return SQLHelper.executeUpdate(sql, status, id, teacherId);
    }

    /**
     * 根据ID查询选题申请
     */
    public TopicSelection findById(int id) throws SQLException {
        String sql = baseSql + "WHERE ts.id = ?";
        ResultSet rs = SQLHelper.executeQuery(sql, id);
        try {
            if (rs.next()) {
                return rowToSelection(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 判断学生是否为指定教师指导
     */
    public boolean isStudentOfTeacher(int studentId, int teacherId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections ts JOIN topics t ON ts.topic_id=t.id "
                + "WHERE ts.student_id=? AND t.teacher_id=? AND ts.status='approved'";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId, teacherId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 判断学生是否已有有效申请（pending或approved状态）
     */
    public boolean hasActiveSelection(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM topic_selections WHERE student_id=? AND status IN ('pending','approved')";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询学生已通过的选题
     */
    public TopicSelection findApprovedByStudent(int studentId) throws SQLException {
        String sql = baseSql + "WHERE ts.student_id = ? AND ts.status = 'approved'";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            if (rs.next()) {
                return rowToSelection(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 统计某状态的申请数量
     */
    public int countByStatus(String status) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM topic_selections WHERE status=?", status);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<TopicSelection> resultSetToList(ResultSet rs) throws SQLException {
        List<TopicSelection> list = new ArrayList<TopicSelection>();
        while (rs.next()) {
            list.add(rowToSelection(rs));
        }
        return list;
    }

    private TopicSelection rowToSelection(ResultSet rs) throws SQLException {
        TopicSelection sel = new TopicSelection();
        sel.setId(rs.getInt("id"));
        sel.setStudentId(rs.getInt("student_id"));
        sel.setTopicId(rs.getInt("topic_id"));
        sel.setReason(rs.getString("reason"));
        sel.setRoundNo(rs.getInt("round_no"));
        sel.setStatus(rs.getString("status"));
        sel.setCreatedAt(rs.getTimestamp("created_at"));
        sel.setStudentName(rs.getString("student_name"));
        sel.setTopicTitle(rs.getString("topic_title"));
        return sel;
    }
}
