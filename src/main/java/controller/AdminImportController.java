package controller;

import bean.User;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 8 * 1024 * 1024)
public class AdminImportController extends HttpServlet {
    private UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/import.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String> errors = new ArrayList<String>();
        int successCount = 0;

        try {
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                errors.add("请选择CSV文件。");
            } else {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8));
                String line;
                int lineNo = 0;
                while ((line = reader.readLine()) != null) {
                    lineNo++;
                    line = trimBom(line).trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (lineNo == 1 && line.toLowerCase().contains("username")) {
                        continue;
                    }
                    List<String> fields = parseCsvLine(line);
                    if (fields.size() < 4) {
                        errors.add("第" + lineNo + "行列数不足，至少需要 username,password,name,role。");
                        continue;
                    }

                    String username = fields.get(0).trim();
                    String password = fields.get(1).trim();
                    String name = fields.get(2).trim();
                    String role = normalizeRole(fields.get(3).trim());
                    String email = fields.size() > 4 ? fields.get(4).trim() : "";
                    String phone = fields.size() > 5 ? fields.get(5).trim() : "";

                    if (username.length() == 0 || name.length() == 0 || role == null) {
                        errors.add("第" + lineNo + "行用户名、姓名或角色不合法。");
                        continue;
                    }
                    if (password.length() == 0) {
                        password = "123456";
                    }

                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setName(name);
                    user.setRole(role);
                    user.setEmail(email);
                    user.setPhone(phone);
                    userDao.upsertImported(user);
                    successCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.setAttribute("successCount", successCount);
        request.setAttribute("errors", errors);
        request.getRequestDispatcher("/WEB-INF/jsp/admin/import.jsp").forward(request, response);
    }

    private String normalizeRole(String role) {
        if ("student".equalsIgnoreCase(role) || "学生".equals(role)) {
            return "student";
        }
        if ("teacher".equalsIgnoreCase(role) || "教师".equals(role) || "老师".equals(role)) {
            return "teacher";
        }
        if ("admin".equalsIgnoreCase(role) || "管理员".equals(role) || "教务员".equals(role) || "系主任".equals(role)) {
            return "admin";
        }
        return null;
    }

    private String trimBom(String line) {
        if (line != null && line.length() > 0 && line.charAt(0) == '\uFEFF') {
            return line.substring(1);
        }
        return line;
    }

    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
