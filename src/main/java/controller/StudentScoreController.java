package controller;

import bean.User;
import dao.DefenseScoreDao;
import dao.DocumentDao;
import dao.EvaluationDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学生-我的成绩控制器
 * 展示文档审核结果（评分和反馈）
 */
public class StudentScoreController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();
    private DefenseScoreDao defenseDao = new DefenseScoreDao();
    private EvaluationDao evaluationDao = new EvaluationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("documents", documentDao.findByStudent(user.getId()));
            request.setAttribute("defenseScore", defenseDao.findByStudent(user.getId()));
            request.setAttribute("evaluation", evaluationDao.findByStudent(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/scores.jsp").forward(request, response);
    }
}
