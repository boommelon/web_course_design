package controller;

import bean.User;
import dao.DocumentDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 


public class TeacherDocumentController extends HttpServlet {

    private DocumentDao documentDao = new DocumentDao();
    private static final String LIST_PAGE = "/teacher/documents.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/teacher/documents.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            showDocumentList(request, user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");

        try {
            reviewDocument(request, user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showDocumentList(HttpServletRequest request, User user) throws Exception {
        request.setAttribute("documents", documentDao.findByTeacher(user.getId()));
    }

    private void reviewDocument(HttpServletRequest request, User teacher) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        Integer score = ParamUtil.getInt(request, "score");
        if (id == null || score == null) {
            return;
        }

        String feedback = request.getParameter("feedback");
        String status = request.getParameter("status");
        documentDao.updateReview(id, teacher.getId(), teacher.getId(), score, feedback, status);
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
