package controller;

import bean.FileItem;
import bean.User;
import dao.TeacherResourceDao;
import util.UploadUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MultipartConfig(maxFileSize = 20 * 1024 * 1024, maxRequestSize = 25 * 1024 * 1024)
public class TeacherResourceController extends HttpServlet {
    private TeacherResourceDao resourceDao = new TeacherResourceDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            if ("delete".equals(request.getParameter("action"))) {
                resourceDao.delete(Integer.parseInt(request.getParameter("id")), user.getId());
                response.sendRedirect(request.getContextPath() + "/teacher/resources.action");
                return;
            }
            request.setAttribute("resources", resourceDao.findByTeacher(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/resources.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            UploadUtil.UploadedFile file = UploadUtil.save(getServletContext(), request.getPart("file"), "resources", user.getId());
            if (file != null) {
                FileItem item = new FileItem();
                item.setTeacherId(user.getId());
                item.setTitle(request.getParameter("title"));
                item.setFileName(file.getFileName());
                item.setFilePath(file.getFilePath());
                resourceDao.insert(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/resources.action");
    }
}
