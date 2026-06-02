package controller;

import bean.Document;
import bean.User;
import dao.DocumentDao;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 文档附件下载控制器。
 */
public class DocumentDownloadController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User user = (User) request.getSession().getAttribute("loginUser");

        try {
            Document doc = documentDao.findById(id);
            if (doc == null || doc.getFilePath() == null || doc.getFilePath().length() == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if ("student".equals(user.getRole()) && doc.getStudentId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if ("teacher".equals(user.getRole()) && doc.getTeacherId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            File file = new File(doc.getFilePath());
            if (!file.exists() || !file.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String fileName = doc.getFileName() != null ? doc.getFileName() : file.getName();
            String encodedName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedName);
            response.setContentLengthLong(file.length());

            FileInputStream in = new FileInputStream(file);
            ServletOutputStream out = response.getOutputStream();
            try {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
