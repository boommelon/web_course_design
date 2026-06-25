package controller;

import bean.FinalAssignment;
import bean.User;
import dao.FinalAssignmentDao;
import dao.QuestionDao;
import dao.TopicDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StudentQuestionController extends HttpServlet {
    private QuestionDao questionDao = new QuestionDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private TopicDao topicDao = new TopicDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            FinalAssignment assignment = assignmentDao.findByStudent(user.getId());
            request.setAttribute("assignment", assignment);
            if (assignment != null) {
                request.setAttribute("topic", topicDao.findById(assignment.getTopicId()));
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
            FinalAssignment assignment = assignmentDao.findByStudent(user.getId());
            if (assignment != null) {
                Integer teacherId = ParamUtil.getInt(request, "teacherId");
                if (teacherId == null) {
                    response.sendRedirect(request.getContextPath() + "/student/questions.action");
                    return;
                }
                questionDao.insert(user.getId(), teacherId, assignment.getTopicId(), request.getParameter("question"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/student/questions.action");
    }
}
