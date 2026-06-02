package dao;

import bean.FileItem;
import dbutil.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileTemplateDao {
    public List<FileItem> findAll() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT * FROM file_templates ORDER BY id DESC");
        try {
            List<FileItem> list = new ArrayList<FileItem>();
            while (rs.next()) {
                FileItem item = new FileItem();
                item.setId(rs.getInt("id"));
                item.setTitle(rs.getString("title"));
                item.setFilePath(rs.getString("file_path"));
                item.setFileName(rs.getString("file_name"));
                item.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(item);
            }
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public FileItem findById(int id) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT * FROM file_templates WHERE id=?", id);
        try {
            if (rs.next()) {
                FileItem item = new FileItem();
                item.setId(rs.getInt("id"));
                item.setTitle(rs.getString("title"));
                item.setFilePath(rs.getString("file_path"));
                item.setFileName(rs.getString("file_name"));
                item.setCreatedAt(rs.getTimestamp("created_at"));
                return item;
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void insert(FileItem item) throws SQLException {
        SQLHelper.executeUpdate("INSERT INTO file_templates(title,file_path,file_name) VALUES(?,?,?)",
                item.getTitle(), item.getFilePath(), item.getFileName());
    }

    public void delete(int id) throws SQLException {
        SQLHelper.executeUpdate("DELETE FROM file_templates WHERE id=?", id);
    }
}
