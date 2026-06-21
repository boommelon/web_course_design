package controller;

import bean.FileItem;
import dao.FileTemplateDao;
import dao.TeacherResourceDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class ResourceDownloadController extends HttpServlet {
    private FileTemplateDao templateDao = new FileTemplateDao();
    private TeacherResourceDao resourceDao = new TeacherResourceDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String type = request.getParameter("type");
            Integer id = ParamUtil.getInt(request, "id");
            if (id == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            FileItem item = "template".equals(type) ? templateDao.findById(id) : resourceDao.findById(id);
            if (item == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            File file = new File(item.getFilePath());
            if (!file.exists() || !file.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String encodedName = URLEncoder.encode(item.getFileName(), "UTF-8").replace("+", "%20");
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
