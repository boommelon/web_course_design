package controller;

import bean.FileItem;
import dao.FileTemplateDao;
import util.UploadUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig(maxFileSize = 20 * 1024 * 1024, maxRequestSize = 25 * 1024 * 1024)
public class AdminTemplateController extends HttpServlet {
    private FileTemplateDao templateDao = new FileTemplateDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if ("delete".equals(request.getParameter("action"))) {
                templateDao.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/admin/templates.action");
                return;
            }
            request.setAttribute("templates", templateDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/templates.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UploadUtil.UploadedFile file = UploadUtil.save(getServletContext(), request.getPart("file"), "templates", 0);
            if (file != null) {
                FileItem item = new FileItem();
                item.setTitle(request.getParameter("title"));
                item.setFileName(file.getFileName());
                item.setFilePath(file.getFilePath());
                templateDao.insert(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/templates.action");
    }
}
