package controller;

import bean.TopicSelection;
import bean.User;
import dao.EvaluationDao;
import dao.TopicSelectionDao;
import util.ParamUtil;

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
        Integer studentId = ParamUtil.getInt(request, "studentId");
        Integer score = ParamUtil.getInt(request, "score");
        if (studentId == null || score == null) {
            response.sendRedirect(request.getContextPath() + "/teacher/evaluations.action");
            return;
        }
        try {
            TopicSelection selection = selectionDao.findApprovedByStudent(studentId);
            if (selection == null || !selectionDao.isStudentOfTeacher(studentId, user.getId())) {
                response.sendRedirect(request.getContextPath() + "/teacher/evaluations.action");
                return;
            }
            evaluationDao.save(
                    studentId,
                    selection.getTopicId(),
                    user.getId(),
                    request.getParameter("selfComment"),
                    request.getParameter("peerComment"),
                    score
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/evaluations.action");
    }
}
