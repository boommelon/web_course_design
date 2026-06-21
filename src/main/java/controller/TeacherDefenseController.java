package controller;

import bean.TopicSelection;
import bean.User;
import dao.DefenseScoreDao;
import dao.TopicSelectionDao;
import util.ParamUtil;

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
        Integer studentId = ParamUtil.getInt(request, "studentId");
        Integer score = ParamUtil.getInt(request, "score");
        if (studentId == null || score == null) {
            response.sendRedirect(request.getContextPath() + "/teacher/defense.action");
            return;
        }
        try {
            TopicSelection selection = selectionDao.findApprovedByStudent(studentId);
            if (selection == null || !selectionDao.isStudentOfTeacher(studentId, user.getId())) {
                response.sendRedirect(request.getContextPath() + "/teacher/defense.action");
                return;
            }
            defenseDao.save(
                    studentId,
                    selection.getTopicId(),
                    user.getId(),
                    score,
                    request.getParameter("comment")
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/defense.action");
    }
}
