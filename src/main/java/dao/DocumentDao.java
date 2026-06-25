package dao;

import bean.Document;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

 



public class DocumentDao {

    
    private String baseSql = "SELECT d.*, t.teacher_id, u1.name AS student_name, t.title AS topic_title, "
            + "u2.name AS reviewer_name FROM documents d "
            + "JOIN users u1 ON d.student_id = u1.id "
            + "JOIN topics t ON d.topic_id = t.id "
            + "LEFT JOIN users u2 ON d.reviewer_id = u2.id ";

     


    public List<Document> findByStudent(int studentId) throws SQLException {
        String sql = baseSql + "WHERE d.student_id = ? ORDER BY d.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, studentId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<Document> findByTeacher(int teacherId) throws SQLException {
        String sql = baseSql + "WHERE t.teacher_id = ? ORDER BY d.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public List<Document> findAll() throws SQLException {
        String sql = baseSql + "ORDER BY d.id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public Document findById(int id) throws SQLException {
        String sql = baseSql + "WHERE d.id = ?";
        ResultSet rs = SQLHelper.executeQuery(sql, id);
        try {
            if (rs.next()) {
                return rowToDocument(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

     


    public void insert(Document doc) throws SQLException {
        String sql = "INSERT INTO documents(student_id, topic_id, type, file_path, file_name, content) VALUES(?,?,?,?,?,?)";
        SQLHelper.executeUpdate(sql, doc.getStudentId(), doc.getTopicId(),
                doc.getType(), doc.getFilePath(), doc.getFileName(), doc.getContent());
    }

     


    public int updateReview(int id, int reviewerId, int teacherId, int score, String feedback, String status) throws SQLException {
        String sql = "UPDATE documents d JOIN topics t ON d.topic_id=t.id "
                + "SET d.reviewer_id=?, d.score=?, d.feedback=?, d.status=? "
                + "WHERE d.id=? AND t.teacher_id=?";
        return SQLHelper.executeUpdate(sql, reviewerId, score, feedback, status, id, teacherId);
    }

     


    public int countPendingByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM documents d JOIN topics t ON d.topic_id = t.id "
                + "WHERE t.teacher_id = ? AND d.status = 'submitted'";
        ResultSet rs = SQLHelper.executeQuery(sql, teacherId);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public int countByStatus(String status) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM documents WHERE status=?", status);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM documents");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<Document> resultSetToList(ResultSet rs) throws SQLException {
        List<Document> list = new ArrayList<Document>();
        while (rs.next()) {
            list.add(rowToDocument(rs));
        }
        return list;
    }

    private Document rowToDocument(ResultSet rs) throws SQLException {
        Document doc = new Document();
        doc.setId(rs.getInt("id"));
        doc.setStudentId(rs.getInt("student_id"));
        doc.setTopicId(rs.getInt("topic_id"));
        doc.setTeacherId(rs.getInt("teacher_id"));
        doc.setReviewerId(rs.getInt("reviewer_id"));
        doc.setType(rs.getString("type"));
        doc.setFilePath(rs.getString("file_path"));
        doc.setFileName(rs.getString("file_name"));
        doc.setContent(rs.getString("content"));
        
        int score = rs.getInt("score");
        if (!rs.wasNull()) {
            doc.setScore(score);
        }
        doc.setFeedback(rs.getString("feedback"));
        doc.setStatus(rs.getString("status"));
        doc.setCreatedAt(rs.getTimestamp("created_at"));
        doc.setStudentName(rs.getString("student_name"));
        doc.setTopicTitle(rs.getString("topic_title"));
        doc.setReviewerName(rs.getString("reviewer_name"));
        return doc;
    }
}
