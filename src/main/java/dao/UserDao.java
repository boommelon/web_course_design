package dao;

import bean.User;
import util.SQLHelper;
import util.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

 



public class UserDao {

     





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

     


    public int fixDuplicatePhones() throws SQLException {
        List<User> users = findAll();
        Set<String> usedPhones = new HashSet<String>();
        int fixedCount = 0;

        for (User user : users) {
            String phone = user.getPhone();
            if (phone == null || phone.trim().length() == 0) {
                continue;
            }

            phone = phone.trim();
            if (usedPhones.contains(phone)) {
                String newPhone = buildUniquePhone(user.getId(), usedPhones);
                updatePhone(user.getId(), newPhone);
                usedPhones.add(newPhone);
                fixedCount++;
            } else {
                usedPhones.add(phone);
            }
        }

        return fixedCount;
    }

    private String buildUniquePhone(int userId, Set<String> usedPhones) {
        String phone = "199" + String.format("%08d", userId);
        int index = 1;
        while (usedPhones.contains(phone)) {
            phone = "199" + String.format("%08d", userId + index);
            index++;
        }
        return phone;
    }

    private void updatePhone(int id, String phone) throws SQLException {
        String sql = "UPDATE users SET phone=? WHERE id=?";
        SQLHelper.executeUpdate(sql, phone, id);
    }

     


    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, name, role, email, phone) VALUES(?,?,?,?,?,?)";
        String md5Pass = PasswordUtil.md5(user.getPassword());
        SQLHelper.executeUpdate(sql, user.getUsername(), md5Pass,
                user.getName(), user.getRole(), user.getEmail(), user.getPhone());
    }

     


    public void upsertImported(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password, name, role, email, phone) VALUES(?,?,?,?,?,?) "
                + "ON DUPLICATE KEY UPDATE password=VALUES(password), name=VALUES(name), "
                + "role=VALUES(role), email=VALUES(email), phone=VALUES(phone)";
        String md5Pass = PasswordUtil.md5(user.getPassword());
        SQLHelper.executeUpdate(sql, user.getUsername(), md5Pass,
                user.getName(), user.getRole(), user.getEmail(), user.getPhone());
    }

     


    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, role=?, email=?, phone=? WHERE id=?";
        SQLHelper.executeUpdate(sql, user.getName(), user.getRole(),
                user.getEmail(), user.getPhone(), user.getId());
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
