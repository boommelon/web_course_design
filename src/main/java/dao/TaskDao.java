package dao;

import bean.Task;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private String baseSql = "SELECT ta.*, u.name AS teacher_name FROM tasks ta JOIN users u ON ta.teacher_id=u.id ";

    public List<Task> findAll() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "ORDER BY ta.id DESC");
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<Task> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE ta.teacher_id=? ORDER BY ta.id DESC", teacherId);
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void insert(Task task) throws SQLException {
        String sql = "INSERT INTO tasks(teacher_id,title,content,deadline) VALUES(?,?,?,?)";
        SQLHelper.executeUpdate(sql, task.getTeacherId(), task.getTitle(), task.getContent(), task.getDeadline());
    }

    public void delete(int id, int teacherId) throws SQLException {
        SQLHelper.executeUpdate("DELETE FROM tasks WHERE id=? AND teacher_id=?", id, teacherId);
    }

    private List<Task> toList(ResultSet rs) throws SQLException {
        List<Task> list = new ArrayList<Task>();
        while (rs.next()) {
            Task t = new Task();
            t.setId(rs.getInt("id"));
            t.setTeacherId(rs.getInt("teacher_id"));
            t.setTeacherName(rs.getString("teacher_name"));
            t.setTitle(rs.getString("title"));
            t.setContent(rs.getString("content"));
            t.setDeadline(rs.getDate("deadline"));
            t.setCreatedAt(rs.getTimestamp("created_at"));
            list.add(t);
        }
        return list;
    }
}
