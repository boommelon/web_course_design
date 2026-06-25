package dao;

import bean.Question;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    private String baseSql = "SELECT q.*, s.name AS student_name, te.name AS teacher_name, t.title AS topic_title "
            + "FROM questions q JOIN users s ON q.student_id=s.id "
            + "JOIN users te ON q.teacher_id=te.id LEFT JOIN topics t ON q.topic_id=t.id ";

    public List<Question> findByStudent(int studentId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE q.student_id=? ORDER BY q.id DESC", studentId);
        try { return toList(rs); } finally { SQLHelper.close(rs); }
    }

    public List<Question> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE q.teacher_id=? ORDER BY q.status, q.id DESC", teacherId);
        try { return toList(rs); } finally { SQLHelper.close(rs); }
    }

    public void insert(int studentId, int teacherId, int topicId, String question) throws SQLException {
        SQLHelper.executeUpdate("INSERT INTO questions(student_id,teacher_id,topic_id,question) VALUES(?,?,?,?)",
                studentId, teacherId, topicId, question);
    }

    public void answer(int id, int teacherId, String answer) throws SQLException {
        SQLHelper.executeUpdate("UPDATE questions SET answer=?, status='answered', answered_at=NOW() WHERE id=? AND teacher_id=?",
                answer, id, teacherId);
    }

    private List<Question> toList(ResultSet rs) throws SQLException {
        List<Question> list = new ArrayList<Question>();
        while (rs.next()) {
            Question q = new Question();
            q.setId(rs.getInt("id"));
            q.setStudentId(rs.getInt("student_id"));
            q.setTeacherId(rs.getInt("teacher_id"));
            q.setTopicId(rs.getInt("topic_id"));
            q.setStudentName(rs.getString("student_name"));
            q.setTeacherName(rs.getString("teacher_name"));
            q.setTopicTitle(rs.getString("topic_title"));
            q.setQuestion(rs.getString("question"));
            q.setAnswer(rs.getString("answer"));
            q.setStatus(rs.getString("status"));
            q.setCreatedAt(rs.getTimestamp("created_at"));
            q.setAnsweredAt(rs.getTimestamp("answered_at"));
            list.add(q);
        }
        return list;
    }
}
