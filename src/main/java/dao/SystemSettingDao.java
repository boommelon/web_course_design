package dao;

import util.SQLHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

 


public class SystemSettingDao {

    public Map<String, String> findAll() throws SQLException {
        String sql = "SELECT setting_key, setting_value FROM system_settings ORDER BY setting_key";
        ResultSet rs = SQLHelper.executeQuery(sql);
        try {
            Map<String, String> settings = new LinkedHashMap<String, String>();
            while (rs.next()) {
                settings.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
            return settings;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public boolean isOpen(String key) throws SQLException {
        return "true".equals(getValue(key));
    }

    /** 读取整数型设置（如 current_round），解析失败返回默认值。 */
    public int getInt(String key, int def) throws SQLException {
        String value = getValue(key);
        if (value == null) {
            return def;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public String getValue(String key) throws SQLException {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key=?";
        ResultSet rs = SQLHelper.executeQuery(sql, key);
        try {
            if (rs.next()) {
                return rs.getString("setting_value");
            }
            return null;
        } finally {
            SQLHelper.close(rs);
        }
    }

    public void update(String key, boolean open) throws SQLException {
        String sql = "UPDATE system_settings SET setting_value=? WHERE setting_key=?";
        SQLHelper.executeUpdate(sql, open ? "true" : "false", key);
    }

    public void updateValue(String key, String value) throws SQLException {
        String sql = "UPDATE system_settings SET setting_value=? WHERE setting_key=?";
        SQLHelper.executeUpdate(sql, value, key);
    }
}
