package dao;

import bean.Topic;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

 



public class TopicDao {

     


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

     


    public List<Topic> findOpen() throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id "
                + "WHERE t.status = 'open' AND t.review_status = 'approved' "
                + "AND t.selected_count < t.max_students ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<Topic> findAvailableApproved() throws SQLException {
        String sql = "SELECT t.*, u.name AS teacher_name FROM topics t "
                + "JOIN users u ON t.teacher_id = u.id "
                + "WHERE t.status='open' AND t.review_status='approved' AND t.selected_count < t.max_students "
                + "ORDER BY t.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


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

     
    public void insert(Topic topic) throws SQLException {
        String sql = "INSERT INTO topics(title, description, teacher_id, max_students, status, review_status) VALUES(?,?,?,?,?,?)";
        SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getTeacherId(), topic.getMaxStudents(), "closed", "pending");
    }

     
    public void update(Topic topic) throws SQLException {
        String sql = "UPDATE topics SET title=?, description=?, max_students=?, status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getMaxStudents(), topic.getStatus(), topic.getId());
    }

     
    public int update(Topic topic, int teacherId) throws SQLException {
        String sql = "UPDATE topics SET title=?, description=?, max_students=?, status=? WHERE id=? AND teacher_id=?";
        return SQLHelper.executeUpdate(sql, topic.getTitle(), topic.getDescription(),
                topic.getMaxStudents(), topic.getStatus(), topic.getId(), teacherId);
    }

     
    public void updateReview(int id, String reviewStatus, String reviewComment) throws SQLException {
        String status = "approved".equals(reviewStatus) ? "open" : "closed";
        String sql = "UPDATE topics SET review_status=?, review_comment=?, status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, reviewStatus, reviewComment, status, id);
    }

     
    public int delete(int id, int teacherId) throws SQLException {
        return SQLHelper.executeUpdate("DELETE FROM topics WHERE id=? AND teacher_id=?", id, teacherId);
    }

     
    public void incrementSelected(int id) throws SQLException {
        SQLHelper.executeUpdate("UPDATE topics SET selected_count = selected_count + 1 WHERE id=?", id);
    }

     
    public void closeIfFull(int id) throws SQLException {
        SQLHelper.executeUpdate("UPDATE topics SET status='closed' WHERE id=? AND selected_count >= max_students", id);
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
        topic.setMaxStudents(rs.getInt("max_students"));
        topic.setSelectedCount(rs.getInt("selected_count"));
        topic.setStatus(rs.getString("status"));
        topic.setReviewStatus(rs.getString("review_status"));
        topic.setReviewComment(rs.getString("review_comment"));
        topic.setCreatedAt(rs.getTimestamp("created_at"));
        return topic;
    }
}
