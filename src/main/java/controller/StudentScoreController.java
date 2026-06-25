package controller;

import bean.User;
import dao.DocumentDao;
import dao.EvaluationDao;
import dao.FinalAssignmentDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学生查看最终题目、资料与成绩评语。
 */
public class StudentScoreController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();
    private EvaluationDao evaluationDao = new EvaluationDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("assignment", assignmentDao.findByStudent(user.getId()));
            request.setAttribute("documents", documentDao.findByStudent(user.getId()));
            request.setAttribute("evaluation", evaluationDao.findByStudent(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/scores.jsp").forward(request, response);
    }
}
