package dao;

import bean.User;
import util.SQLHelper;
import util.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问。普通增删改查统一走 SQLHelper 封装方法。
 */
public class UserDao {

    public User validate(String username, String password) throws SQLException {
        String md5Pass = PasswordUtil.md5(password);
        String sql = "SELECT * FROM users WHERE username=? AND password=? AND status=1";
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

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY id";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role=? ORDER BY id";
        ResultSet rs = SQLHelper.executeQuery(sql, role);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 按角色 + 学院 + 专业查询（专业负责人查看本专业教师/学生用）。
     */
    public List<User> findByRoleAndMajor(String role, String college, String major) throws SQLException {
        String sql = "SELECT * FROM users WHERE role=? AND college=? AND major=? ORDER BY id";
        ResultSet rs = SQLHelper.executeQuery(sql, role, college, major);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

    /**
     * 本专业内尚未拿到最终分配的学生（用于第二轮 / 强制分配 / 统计）。
     */
    public List<User> findUnassignedStudents(String college, String major) throws SQLException {
        String sql = "SELECT * FROM users u WHERE u.role='student' AND u.college=? AND u.major=? "
                + "AND NOT EXISTS (SELECT 1 FROM final_assignments fa WHERE fa.student_id=u.id) "
                + "ORDER BY u.id";
        ResultSet rs = SQLHelper.executeQuery(sql, college, major);
        try {
            return resultSetToList(rs);
        } finally {
            SQLHelper.close(rs);
        }
    }

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

    public boolean phoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE phone=?";
        ResultSet rs = SQLHelper.executeQuery(sql, phone);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public boolean phoneExistsForOtherUser(String phone, int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE phone=? AND id<>?";
        ResultSet rs = SQLHelper.executeQuery(sql, phone, userId);
        try {
            rs.next();
            return rs.getInt(1) > 0;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, name, role, college, major, student_no, class_name, email, phone, status) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        String md5Pass = PasswordUtil.md5(user.getPassword());
        SQLHelper.executeUpdate(sql, user.getUsername(), md5Pass, user.getName(), user.getRole(),
                user.getCollege(), user.getMajor(), user.getStudentNo(), user.getClassName(),
                user.getEmail(), user.getPhone(), user.getStatus());
    }

    public void upsertImported(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, name, role, college, major, student_no, class_name, email, phone, status) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE password=VALUES(password), name=VALUES(name), role=VALUES(role), "
                + "college=VALUES(college), major=VALUES(major), student_no=VALUES(student_no), "
                + "class_name=VALUES(class_name), email=VALUES(email), phone=VALUES(phone)";
        String md5Pass = PasswordUtil.md5(user.getPassword());
        SQLHelper.executeUpdate(sql, user.getUsername(), md5Pass, user.getName(), user.getRole(),
                user.getCollege(), user.getMajor(), user.getStudentNo(), user.getClassName(),
                user.getEmail(), user.getPhone(), user.getStatus());
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, role=?, college=?, major=?, student_no=?, class_name=?, email=?, phone=?, status=? WHERE id=?";
        SQLHelper.executeUpdate(sql, user.getName(), user.getRole(), user.getCollege(), user.getMajor(),
                user.getStudentNo(), user.getClassName(), user.getEmail(), user.getPhone(),
                user.getStatus(), user.getId());
    }

    public void updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, email=?, phone=? WHERE id=?";
        SQLHelper.executeUpdate(sql, user.getName(), user.getEmail(), user.getPhone(), user.getId());
    }

    public void updatePassword(int id, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE id=?";
        SQLHelper.executeUpdate(sql, PasswordUtil.md5(newPassword), id);
    }

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

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        SQLHelper.executeUpdate(sql, id);
    }

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

    public int countByRoleAndMajor(String role, String college, String major) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role=? AND college=? AND major=?";
        ResultSet rs = SQLHelper.executeQuery(sql, role, college, major);
        try {
            rs.next();
            return rs.getInt(1);
        } finally {
            SQLHelper.close(rs);
        }
    }

    private List<User> resultSetToList(ResultSet rs) throws SQLException {
        List<User> list = new ArrayList<User>();
        while (rs.next()) {
            list.add(rowToUser(rs));
        }
        return list;
    }

    private User rowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("name"));
        user.setRole(rs.getString("role"));
        user.setCollege(rs.getString("college"));
        user.setMajor(rs.getString("major"));
        user.setStudentNo(rs.getString("student_no"));
        user.setClassName(rs.getString("class_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setStatus(rs.getInt("status"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
