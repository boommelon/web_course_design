package dao;

import bean.Topic;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目数据访问（一题一人模型）。
 * status: draft / pending / approved / rejected / assigned。
 */
public class TopicDao {

    private static final String BASE_SELECT =
            "SELECT t.*, u.name AS teacher_name, r.name AS reviewer_name "
          + "FROM topics t "
          + "JOIN users u ON t.teacher_id = u.id "
          + "LEFT JOIN users r ON t.reviewer_id = r.id ";

    public List<Topic> findByTeacher(int teacherId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.teacher_id = ? ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<Topic> findAll() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 本专业题目（专业负责人审题、查看用）。
     */
    public List<Topic> findByMajor(String college, String major) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.college=? AND t.major=? ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, college, major);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 本专业指定状态题目。
     */
    public List<Topic> findByMajorAndStatus(String college, String major, String status) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.college=? AND t.major=? AND t.status=? ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, college, major, status);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 学生本专业可选题目：审核通过(approved) 且 尚未被任何学生最终分配。
     */
    public List<Topic> findSelectableByMajor(String college, String major) throws SQLException {
        String sql = BASE_SELECT
                + "WHERE t.college=? AND t.major=? AND t.status='approved' "
                + "AND NOT EXISTS (SELECT 1 FROM final_assignments fa WHERE fa.topic_id=t.id) "
                + "ORDER BY t.id";
        ResultSet rs = SQLHelper.executeQuery(sql, college, major);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public Topic findById(int id) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.id = ?";
        ResultSet rs = SQLHelper.executeQuery(sql, id);
        try {
            if (rs.next()) {
                return rowToTopic(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 教师出题：题目所属专业沿用教师本人的 college/major，初始 pending。
     */
    public void insert(Topic topic) throws SQLException {
        String sql = "INSERT INTO topics(title, description, teacher_id, college, major, status) VALUES(?,?,?,?,?, 'pending')";
        SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getTeacherId(), topic.getCollege(), topic.getMajor());
    }

    /**
     * 教师修改题目：仅限本人且题目处于 pending/rejected/draft（approved 后锁定）。
     * 返回受影响行数，0 表示无权或已锁定。
     */
    public int updateByTeacher(Topic topic, int teacherId) throws SQLException {
        String sql = "UPDATE topics SET title=?, description=?, status='pending' "
                + "WHERE id=? AND teacher_id=? AND status IN ('pending','rejected','draft')";
        return SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getId(), teacherId);
    }

    /**
     * 教师删除题目：仅限本人且未审核通过、未被分配。
     */
    public int deleteByTeacher(int id, int teacherId) throws SQLException {
        String sql = "DELETE FROM topics WHERE id=? AND teacher_id=? AND status IN ('pending','rejected','draft')";
        return SQLHelper.executeUpdate(sql, id, teacherId);
    }

    /**
     * 专业负责人审题：通过 approved / 退回 rejected。
     * 只允许审核 pending 题目；已通过、已退回、已分配的题目不能重复审核。
     * 限定本专业（college+major），防止越权审别专业题目。返回受影响行数。
     */
    public int review(int id, String status, String comment, int reviewerId, String college, String major)
            throws SQLException {
        String sql = "UPDATE topics SET status=?, review_comment=?, reviewer_id=?, review_time=NOW() "
                + "WHERE id=? AND college=? AND major=? AND status='pending'";
        return SQLHelper.executeUpdate(sql, status, comment, reviewerId, id, college, major);
    }

    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM topics");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public int countByMajorAndStatus(String college, String major, String status) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(
                "SELECT COUNT(*) FROM topics WHERE college=? AND major=? AND status=?", college, major, status);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<Topic> resultSetToList(ResultSet rs) throws SQLException {
        List<Topic> list = new ArrayList<Topic>();
        while (rs.next()) {
            list.add(rowToTopic(rs));
        }
        return list;
    }

    private Topic rowToTopic(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getInt("id"));
        topic.setTitle(rs.getString("title"));
        topic.setDescription(rs.getString("description"));
        topic.setTeacherId(rs.getInt("teacher_id"));
        topic.setTeacherName(rs.getString("teacher_name"));
        topic.setCollege(rs.getString("college"));
        topic.setMajor(rs.getString("major"));
        topic.setStatus(rs.getString("status"));
        topic.setReviewComment(rs.getString("review_comment"));
        int reviewerId = rs.getInt("reviewer_id");
        topic.setReviewerId(rs.wasNull() ? null : reviewerId);
        topic.setReviewerName(rs.getString("reviewer_name"));
        topic.setReviewTime(rs.getTimestamp("review_time"));
        topic.setCreatedAt(rs.getTimestamp("created_at"));
        return topic;
    }
}
