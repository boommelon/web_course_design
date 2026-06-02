package dao;

import bean.Topic;
import dbutil.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 课题数据访问类
 * 负责topics表的增删改查操作
 */
public class TopicDao {

    /**
     * 查询某个教师发布的所有课题
     */
    public List<Topic> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id "
                + "WHERE t.teacher_id = ? ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询所有开放状态的课题（学生浏览用）
     */
    public List<Topic> findOpen() throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id "
                + "WHERE t.status = 'open' AND t.review_status = 'approved' ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询已审核且仍有名额的课题
     */
    public List<Topic> findAvailableApproved() throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id "
                + "WHERE t.review_status='approved' AND t.selected_count < t.max_students "
                + "ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询所有课题
     */
    public List<Topic> findAll() throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询所有待管理员审核的课题
     */
    public List<Topic> findPendingReview() throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id "
                + "WHERE t.review_status = 'pending' ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 根据ID查询课题
     */
    public Topic findById(int id) throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id WHERE t.id = ?";
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

    /** 新增课题 */
    public void insert(Topic topic) throws SQLException {
        String sql = "INSERT INTO topics(title, description, teacher_id, max_students, status, review_status) VALUES(?,?,?,?,?,?)";
        SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getTeacherId(), topic.getMaxStudents(), "closed", "pending");
    }

    /** 修改课题 */
    public void update(Topic topic) throws SQLException {
        String sql = "UPDATE topics SET title=?, description=?, max_students=?, status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getMaxStudents(), topic.getStatus(), topic.getId());
    }

    /** 管理员审核课题 */
    public void updateReview(int id, String reviewStatus, String reviewComment) throws SQLException {
        String status = "approved".equals(reviewStatus) ? "open" : "closed";
        String sql = "UPDATE topics SET review_status=?, review_comment=?, status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, reviewStatus, reviewComment, status, id);
    }

    /** 删除课题 */
    public void delete(int id) throws SQLException {
        SQLHelper.executeUpdate("DELETE FROM topics WHERE id=?", id);
    }

    /** 课题已选人数+1 */
    public void incrementSelected(int id) throws SQLException {
        SQLHelper.executeUpdate("UPDATE topics SET selected_count = selected_count + 1 WHERE id=?", id);
    }

    /** 如果课题已选满则自动关闭 */
    public void closeIfFull(int id) throws SQLException {
        SQLHelper.executeUpdate("UPDATE topics SET status='closed' WHERE id=? AND selected_count >= max_students", id);
    }

    /** 统计课题总数 */
    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM topics");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    // 将ResultSet转换为List
    private List<Topic> resultSetToList(ResultSet rs) throws SQLException {
        List<Topic> list = new ArrayList<Topic>();
        while (rs.next()) {
            list.add(rowToTopic(rs));
        }
        return list;
    }

    // 将一行数据转换为Topic对象
    private Topic rowToTopic(ResultSet rs) throws SQLException {
        Topic topic = new Topic();
        topic.setId(rs.getInt("id"));
        topic.setTitle(rs.getString("title"));
        topic.setDescription(rs.getString("description"));
        topic.setTeacherId(rs.getInt("teacher_id"));
        topic.setTeacherName(rs.getString("teacher_name"));
        topic.setMaxStudents(rs.getInt("max_students"));
        topic.setSelectedCount(rs.getInt("selected_count"));
        topic.setStatus(rs.getString("status"));
        topic.setReviewStatus(rs.getString("review_status"));
        topic.setReviewComment(rs.getString("review_comment"));
        topic.setCreatedAt(rs.getTimestamp("created_at"));
        return topic;
    }
}
