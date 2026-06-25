package dao;

import bean.FileItem;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherResourceDao {
    private String baseSql = "SELECT r.*, u.name AS teacher_name FROM teacher_resources r JOIN users u ON r.teacher_id=u.id ";

    public List<FileItem> findAll() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "ORDER BY r.id DESC");
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<FileItem> findByTeacher(int teacherId) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE r.teacher_id=? ORDER BY r.id DESC", teacherId);
        try {
            return toList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public FileItem findById(int id) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery(baseSql + "WHERE r.id=?", id);
        try {
            if (rs.next()) {
                return row(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void insert(FileItem item) throws SQLException {
        SQLHelper.executeUpdate("INSERT INTO teacher_resources(teacher_id,title,file_path,file_name) VALUES(?,?,?,?)",
                item.getTeacherId(), item.getTitle(), item.getFilePath(), item.getFileName());
    }

    public void delete(int id, int teacherId) throws SQLException {
        SQLHelper.executeUpdate("DELETE FROM teacher_resources WHERE id=? AND teacher_id=?", id, teacherId);
    }

    private List<FileItem> toList(ResultSet rs) throws SQLException {
        List<FileItem> list = new ArrayList<FileItem>();
        while (rs.next()) {
            list.add(row(rs));
        }
        return list;
    }

    private FileItem row(ResultSet rs) throws SQLException {
        FileItem item = new FileItem();
        item.setId(rs.getInt("id"));
        item.setTeacherId(rs.getInt("teacher_id"));
        item.setTeacherName(rs.getString("teacher_name"));
        item.setTitle(rs.getString("title"));
        item.setFilePath(rs.getString("file_path"));
        item.setFileName(rs.getString("file_name"));
        item.setCreatedAt(rs.getTimestamp("created_at"));
        return item;
    }
}
