package controller;

import dao.FileTemplateDao;
import dao.TaskDao;
import dao.TeacherResourceDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StudentResourceController extends HttpServlet {
    private TaskDao taskDao = new TaskDao();
    private TeacherResourceDao resourceDao = new TeacherResourceDao();
    private FileTemplateDao templateDao = new FileTemplateDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("tasks", taskDao.findAll());
            request.setAttribute("resources", resourceDao.findAll());
            request.setAttribute("templates", templateDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/resources.jsp").forward(request, response);
    }
}
