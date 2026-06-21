package util;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求参数解析工具。
 */
public class ParamUtil {

    public static Integer getInt(HttpServletRequest req, String name) {
        String value = getString(req, name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int getInt(HttpServletRequest req, String name, int def) {
        Integer value = getInt(req, name);
        return value != null ? value.intValue() : def;
    }

    public static String getString(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.length() > 0 ? value : null;
    }
}
