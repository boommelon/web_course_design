package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

 



public class SQLHelper {

    
    
    
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = getConfig("GD_DB_URL",
            "jdbc:mysql://localhost:3306/graduation_design?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true");
    private static final String USERNAME = getConfig("GD_DB_USERNAME", "root");
    private static final String PASSWORD = getConfig("GD_DB_PASSWORD", "");

    
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

     


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    private static String getConfig(String key, String defaultValue) {
        String propertyValue = System.getProperty(key);
        if (propertyValue != null && propertyValue.trim().length() > 0) {
            return propertyValue.trim();
        }
        String envValue = System.getenv(key);
        if (envValue != null && envValue.trim().length() > 0) {
            return envValue.trim();
        }
        return defaultValue;
    }

     



    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            closeQuietly(ps);
            closeQuietly(conn);
            throw e;
        }
    }

     


    public static int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

     


    public static void close(ResultSet rs) {
        Statement st = null;
        Connection conn = null;
        try {
            if (rs == null) {
                return;
            }
            try {
                st = rs.getStatement();
            } catch (SQLException e) {
                
            }
            if (st != null) {
                try {
                    conn = st.getConnection();
                } catch (SQLException e) {
                    
                }
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(st);
            closeQuietly(conn);
        }
    }

    private static void closeQuietly(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            
        }
    }

    private static void closeQuietly(Statement st) {
        try {
            if (st != null) st.close();
        } catch (SQLException e) {
            
        }
    }

    private static void closeQuietly(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            
        }
    }
}
