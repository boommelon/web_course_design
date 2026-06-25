package dao;

import bean.DefenseScore;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefenseScoreDao {
    private String baseSql = "SELECT ds.*, s.name AS student_name, t.title AS topic_title, te.name AS teacher_name "
            + "FROM defense_scores ds JOIN users s ON ds.student_id=s.id "
            + "JOIN topics t ON ds.topic_id=t.id JOIN users te ON ds.teacher_id=te.id ";

    public List<DefenseScore> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE ds.teacher_id=? ORDER BY ds.id DESC", teacherId);
        try { return toList(rs); } finally { SQLHelper.close(rs); }
    }

    public DefenseScore findByStudent(int studentId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE ds.student_id=?", studentId);
        try {
            if (rs.next()) return row(rs);
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void save(int studentId, int topicId, int teacherId, int score, String comment) throws SQLException {
        String sql = "INSERT INTO defense_scores(student_id,topic_id,teacher_id,score,comment) VALUES(?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE teacher_id=VALUES(teacher_id), topic_id=VALUES(topic_id), "
                + "score=VALUES(score), comment=VALUES(comment), created_at=NOW()";
        SQLHelper.executeUpdate(sql, studentId, topicId, teacherId, score, comment);
    }

    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM defense_scores");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<DefenseScore> toList(ResultSet rs) throws SQLException {
        List<DefenseScore> list = new ArrayList<DefenseScore>();
        while (rs.next()) list.add(row(rs));
        return list;
    }

    private DefenseScore row(ResultSet rs) throws SQLException {
        DefenseScore d = new DefenseScore();
        d.setId(rs.getInt("id"));
        d.setStudentId(rs.getInt("student_id"));
        d.setTopicId(rs.getInt("topic_id"));
        d.setTeacherId(rs.getInt("teacher_id"));
        d.setStudentName(rs.getString("student_name"));
        d.setTopicTitle(rs.getString("topic_title"));
        d.setTeacherName(rs.getString("teacher_name"));
        d.setScore(rs.getInt("score"));
        d.setComment(rs.getString("comment"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
