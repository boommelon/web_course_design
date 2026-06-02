package controller;

import bean.User;
import dao.DefenseScoreDao;
import dao.TopicSelectionDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TeacherDefenseController extends HttpServlet {
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private DefenseScoreDao defenseDao = new DefenseScoreDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("students", selectionDao.findApprovedByTeacher(user.getId()));
            request.setAttribute("scores", defenseDao.findByTeacher(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/defense.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            defenseDao.save(
                    Integer.parseInt(request.getParameter("studentId")),
                    Integer.parseInt(request.getParameter("topicId")),
                    user.getId(),
                    Integer.parseInt(request.getParameter("score")),
                    request.getParameter("comment")
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/defense.action");
    }
}
