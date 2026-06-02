package controller;

import bean.User;
import dao.EvaluationDao;
import dao.TopicSelectionDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TeacherEvaluationController extends HttpServlet {
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private EvaluationDao evaluationDao = new EvaluationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("students", selectionDao.findApprovedByTeacher(user.getId()));
            request.setAttribute("evaluations", evaluationDao.findByTeacher(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/evaluations.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            evaluationDao.save(
                    Integer.parseInt(request.getParameter("studentId")),
                    Integer.parseInt(request.getParameter("topicId")),
                    user.getId(),
                    request.getParameter("selfComment"),
                    request.getParameter("peerComment"),
                    Integer.parseInt(request.getParameter("score"))
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/evaluations.action");
    }
}
