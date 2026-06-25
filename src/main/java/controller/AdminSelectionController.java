package controller;

import dao.SystemSettingDao;
import dao.TopicDao;
import dao.TopicSelectionDao;
import dao.UserDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminSelectionController extends HttpServlet {
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private TopicDao topicDao = new TopicDao();
    private UserDao userDao = new UserDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/admin/selections.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/admin/selections.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            showSelectionPage(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("opttype");

        try {
            if ("settings".equals(action)) {
                saveSettings(request);
            }

            if ("forceAssign".equals(action)) {
                forceAssign(request);
            }

            if ("approve".equals(action)) {
                approveSelection(request);
            }

            if ("reject".equals(action)) {
                rejectSelection(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showSelectionPage(HttpServletRequest request) throws Exception {
        request.setAttribute("selections", selectionDao.findAll());
        request.setAttribute("students", userDao.findByRole("student"));
        request.setAttribute("unselectedStudents", userDao.findStudentsWithoutApprovedSelection());
        request.setAttribute("availableTopics", topicDao.findAvailableApproved());
        request.setAttribute("settings", settingDao.findAll());
    }

    private void saveSettings(HttpServletRequest request) throws Exception {
        settingDao.update("student_selection_open", request.getParameter("student_selection_open") != null);
        settingDao.updateValue("selection_round", request.getParameter("selection_round"));
    }

    private void forceAssign(HttpServletRequest request) throws Exception {
        Integer studentId = ParamUtil.getInt(request, "studentId");
        Integer topicId = ParamUtil.getInt(request, "topicId");
        if (studentId == null || topicId == null) {
            return;
        }

        int roundNo = parseInt(settingDao.getValue("selection_round"), 1);
        selectionDao.forceAssign(studentId, topicId, "管理员最终分配", roundNo);
    }

    private void approveSelection(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            return;
        }

        selectionDao.approvePendingSelection(id);
    }

    private void rejectSelection(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id != null) {
            selectionDao.updateStatus(id, "rejected");
        }
    }

    private int parseInt(String value, int def) {
        if (value == null) {
            return def;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
