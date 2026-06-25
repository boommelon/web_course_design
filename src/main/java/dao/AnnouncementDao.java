package dao;

import bean.Announcement;
import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

 



public class AnnouncementDao {

     


    public List<Announcement> findAll() throws SQLException {
        String sql = "SELECT * FROM announcements ORDER BY is_top DESC, id DESC";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            List<Announcement> list = new ArrayList<Announcement>();
            while (rs.next()) {
                list.add(rowToAnnouncement(rs));
            }
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

     
    public Announcement findById(int id) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT * FROM announcements WHERE id=?", id);
        try {
            if (rs.next()) return rowToAnnouncement(rs);
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

     
    public void insert(Announcement a) throws SQLException {
        String sql = "INSERT INTO announcements(title, content, is_top) VALUES(?,?,?)";
        SQLHelper.executeUpdate(sql, a.getTitle(), a.getContent(), a.getIsTop());
    }

     
    public void update(Announcement a) throws SQLException {
        String sql = "UPDATE announcements SET title=?, content=?, is_top=? WHERE id=?";
        SQLHelper.executeUpdate(sql, a.getTitle(), a.getContent(), a.getIsTop(), a.getId());
    }

     
    public void delete(int id) throws SQLException {
        SQLHelper.executeUpdate("DELETE FROM announcements WHERE id=?", id);
    }

     
    public int count() throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT COUNT(*) FROM announcements");
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private Announcement rowToAnnouncement(ResultSet rs) throws SQLException {
        Announcement a = new Announcement();
        a.setId(rs.getInt("id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setIsTop(rs.getInt("is_top"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
