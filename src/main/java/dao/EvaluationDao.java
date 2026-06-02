package dao;

import bean.Evaluation;
import dbutil.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDao {
    private String baseSql = "SELECT e.*, s.name AS student_name, t.title AS topic_title, te.name AS teacher_name "
            + "FROM evaluations e JOIN users s ON e.student_id=s.id "
            + "JOIN topics t ON e.topic_id=t.id JOIN users te ON e.teacher_id=te.id ";

    public List<Evaluation> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE e.teacher_id=? ORDER BY e.id DESC", teacherId);
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public Evaluation findByStudent(int studentId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE e.student_id=?", studentId);
        try {
            if (rs.next()) {
                return row(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void save(int studentId, int topicId, int teacherId, String selfComment, String peerComment, int score)
            throws SQLException {
        String sql = "INSERT INTO evaluations(student_id,topic_id,teacher_id,self_comment,peer_comment,score) "
                + "VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE topic_id=VALUES(topic_id), "
                + "teacher_id=VALUES(teacher_id), self_comment=VALUES(self_comment), "
                + "peer_comment=VALUES(peer_comment), score=VALUES(score), created_at=NOW()";
        SQLHelper.executeUpdate(sql, studentId, topicId, teacherId, selfComment, peerComment, score);
    }

    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM evaluations");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<Evaluation> toList(ResultSet rs) throws SQLException {
        List<Evaluation> list = new ArrayList<Evaluation>();
        while (rs.next()) {
            list.add(row(rs));
        }
        return list;
    }

    private Evaluation row(ResultSet rs) throws SQLException {
        Evaluation e = new Evaluation();
        e.setId(rs.getInt("id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setTopicId(rs.getInt("topic_id"));
        e.setTeacherId(rs.getInt("teacher_id"));
        e.setSelfComment(rs.getString("self_comment"));
        e.setPeerComment(rs.getString("peer_comment"));
        int score = rs.getInt("score");
        if (!rs.wasNull()) {
            e.setScore(score);
        }
        e.setCreatedAt(rs.getTimestamp("created_at"));
        e.setStudentName(rs.getString("student_name"));
        e.setTopicTitle(rs.getString("topic_title"));
        e.setTeacherName(rs.getString("teacher_name"));
        return e;
    }
}
