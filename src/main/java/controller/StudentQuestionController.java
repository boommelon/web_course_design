package controller;

import bean.TopicSelection;
import bean.User;
import dao.QuestionDao;
import dao.TopicDao;
import dao.TopicSelectionDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StudentQuestionController extends HttpServlet {
    private QuestionDao questionDao = new QuestionDao();
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private TopicDao topicDao = new TopicDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            TopicSelection selection = selectionDao.findApprovedByStudent(user.getId());
            request.setAttribute("selection", selection);
            if (selection != null) {
                request.setAttribute("topic", topicDao.findById(selection.getTopicId()));
            }
            request.setAttribute("questions", questionDao.findByStudent(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/questions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            TopicSelection selection = selectionDao.findApprovedByStudent(user.getId());
            if (selection != null) {
                Integer teacherId = ParamUtil.getInt(request, "teacherId");
                if (teacherId == null) {
                    response.sendRedirect(request.getContextPath() + "/student/questions.action");
                    return;
                }
                questionDao.insert(user.getId(), teacherId, selection.getTopicId(), request.getParameter("question"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/student/questions.action");
    }
}
