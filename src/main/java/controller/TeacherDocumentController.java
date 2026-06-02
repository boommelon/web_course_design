package controller;

import bean.User;
import dao.DocumentDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师-文档审核控制器
 */
public class TeacherDocumentController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("documents", documentDao.findByTeacher(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/documents.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        int id = Integer.parseInt(request.getParameter("id"));
        int score = Integer.parseInt(request.getParameter("score"));
        String feedback = request.getParameter("feedback");
        String status = request.getParameter("status");
        try {
            documentDao.updateReview(id, user.getId(), score, feedback, status);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/documents.action");
    }
}
