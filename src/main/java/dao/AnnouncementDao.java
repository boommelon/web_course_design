package dao;

import bean.Announcement;
import dbutil.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 公告数据访问类
 * 负责announcements表的操作
 */
public class AnnouncementDao {

    /**
     * 查询所有公告（置顶的排前面）
     */
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

    /** 根据ID查询 */
    public Announcement findById(int id) throws SQLException {
        ResultSet rs = SQLHelper.executeQuery("SELECT * FROM announcements WHERE id=?", id);
        try {
            if (rs.next()) return rowToAnnouncement(rs);
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /** 新增公告 */
    public void insert(Announcement a) throws SQLException {
        String sql = "INSERT INTO announcements(title, content, is_top) VALUES(?,?,?)";
        SQLHelper.executeUpdate(sql, a.getTitle(), a.getContent(), a.getIsTop());
    }

    /** 修改公告 */
    public void update(Announcement a) throws SQLException {
        String sql = "UPDATE announcements SET title=?, content=?, is_top=? WHERE id=?";
        SQLHelper.executeUpdate(sql, a.getTitle(), a.getContent(), a.getIsTop(), a.getId());
    }

    /** 删除公告 */
    public void delete(int id) throws SQLException {
        SQLHelper.executeUpdate("DELETE FROM announcements WHERE id=?", id);
    }

    /** 统计公告数量 */
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