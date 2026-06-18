package dao;

import bean.User;
import dbutil.SQLHelper;
import util.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问类
 * 负责users表的增删改查操作
 */
public class UserDao {

    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 明文密码（方法内部会做MD5）
     * @return 验证成功返回User对象，失败返回null
     */
    public User validate(String username, String password) throws SQLException {
        String md5Pass = PasswordUtil.md5(password);
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        ResultSet rs = SQLHelper.executeQuery(sql, username, md5Pass);
        try {
            if (rs.next()) {
                return rowToUser(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY id";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            List<User> list = new ArrayList<User>();
            while (rs.next()) {
                list.add(rowToUser(rs));
            }
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 按角色查询用户
     */
    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role=? ORDER BY id";
        ResultSet rs = SQLHelper.executeQuery(sql, role);
        try {
            List<User> list = new ArrayList<User>();
            while (rs.next()) {
                list.add(rowToUser(rs));
            }
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 查询尚未最终选题的学生
     */
    public List<User> findStudentsWithoutApprovedSelection() throws SQLException {
        String sql = "SELECT * FROM users u WHERE u.role='student' "
                + "AND NOT EXISTS (SELECT 1 FROM topic_selections ts WHERE ts.student_id=u.id AND ts.status='approved') "
                + "ORDER BY u.id";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            List<User> list = new ArrayList<User>();
            while (rs.next()) {
                list.add(rowToUser(rs));
            }
            return list;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 根据ID查询用户
     */
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        ResultSet rs = SQLHelper.executeQuery(sql, id);
        try {
            if (rs.next()) {
                return rowToUser(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";
        ResultSet rs = SQLHelper.executeQuery(sql, username);
        try {
            if (rs.next()) {
                return rowToUser(rs);
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 新增用户（密码会自动MD5加密）
     */
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, name, role, email, phone) VALUES(?,?,?,?,?,?)";
        String md5Pass = PasswordUtil.md5(user.getPassword());
        SQLHelper.executeUpdate(sql, user.getUsername(), md5Pass,
                user.getName(), user.getRole(), user.getEmail(), user.getPhone());
    }

    /**
     * 导入用户。用户名已存在时更新基础信息，密码随导入文件同步更新。
     */
    public void upsertImported(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, name, role, email, phone) VALUES(?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE password=VALUES(password), name=VALUES(name), "
                + "role=VALUES(role), email=VALUES(email), phone=VALUES(phone)";
        String md5Pass = PasswordUtil.md5(user.getPassword());
        SQLHelper.executeUpdate(sql, user.getUsername(), md5Pass,
                user.getName(), user.getRole(), user.getEmail(), user.getPhone());
    }

    /**
     * 修改用户信息（不修改密码）
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, role=?, email=?, phone=? WHERE id=?";
        SQLHelper.executeUpdate(sql, user.getName(), user.getRole(),
                user.getEmail(), user.getPhone(), user.getId());
    }

    /**
     * 用户修改个人资料（不允许修改角色）
     */
    public void updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, email=?, phone=? WHERE id=?";
        SQLHelper.executeUpdate(sql, user.getName(), user.getEmail(), user.getPhone(), user.getId());
    }

    /**
     * 修改密码
     */
    public void updatePassword(int id, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE id=?";
        SQLHelper.executeUpdate(sql, PasswordUtil.md5(newPassword), id);
    }

    /**
     * 验证指定用户的原密码
     */
    public boolean checkPassword(int id, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id=? AND password=?";
        ResultSet rs = SQLHelper.executeQuery(sql, id, PasswordUtil.md5(password));
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 删除用户
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        SQLHelper.executeUpdate(sql, id);
    }

    /**
     * 统计某个角色的用户数量
     */
    public int countByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role=?";
        ResultSet rs = SQLHelper.executeQuery(sql, role);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 将ResultSet的一行数据转换为User对象
     */
    private User rowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
