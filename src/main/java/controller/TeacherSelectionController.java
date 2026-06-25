package controller;

import bean.User;
import dao.TopicSelectionDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 


public class TeacherSelectionController extends HttpServlet {

    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private static final String LIST_PAGE = "/teacher/selections.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/teacher/selections.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            showSelectionList(request, user);
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
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            redirectToList(request, response);
            return;
        }

        String action = request.getParameter("opttype");

        try {
            if ("approve".equals(action)) {
                approveSelection(id, user);
            }

            if ("reject".equals(action)) {
                rejectSelection(id, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showSelectionList(HttpServletRequest request, User user) throws Exception {
        request.setAttribute("selections", selectionDao.findByTeacher(user.getId()));
    }

    private void approveSelection(int selectionId, User teacher) throws Exception {
        selectionDao.approvePendingSelection(selectionId, teacher.getId());
    }

    private void rejectSelection(int selectionId, User teacher) throws Exception {
        selectionDao.updateStatus(selectionId, "rejected", teacher.getId());
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
