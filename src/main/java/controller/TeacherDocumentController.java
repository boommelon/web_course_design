package controller;

import bean.User;
import dao.DocumentDao;
import util.ParamUtil;

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
        Integer id = ParamUtil.getInt(request, "id");
        Integer score = ParamUtil.getInt(request, "score");
        if (id == null || score == null) {
            response.sendRedirect(request.getContextPath() + "/teacher/documents.action");
            return;
        }
        String feedback = request.getParameter("feedback");
        String status = request.getParameter("status");
        try {
            int rows = documentDao.updateReview(id, user.getId(), user.getId(), score, feedback, status);
            if (rows == 0) {
                response.sendRedirect(request.getContextPath() + "/teacher/documents.action");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/documents.action");
    }
}
