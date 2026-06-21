package controller;

import bean.Task;
import bean.User;
import dao.TaskDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

public class TeacherTaskController extends HttpServlet {
    private TaskDao taskDao = new TaskDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            if ("delete".equals(request.getParameter("opttype"))) {
                Integer id = ParamUtil.getInt(request, "id");
                if (id != null) {
                    taskDao.delete(id, user.getId());
                }
                response.sendRedirect(request.getContextPath() + "/teacher/tasks.action");
                return;
            }
            request.setAttribute("tasks", taskDao.findByTeacher(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/tasks.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            Task task = new Task();
            task.setTeacherId(user.getId());
            task.setTitle(request.getParameter("title"));
            task.setContent(request.getParameter("content"));
            String deadline = request.getParameter("deadline");
            if (deadline != null && deadline.length() > 0) {
                task.setDeadline(Date.valueOf(deadline));
            }
            taskDao.insert(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/tasks.action");
    }
}
