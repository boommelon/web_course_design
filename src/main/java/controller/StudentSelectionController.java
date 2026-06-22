package controller;

import bean.User;
import dao.SystemSettingDao;
import dao.TopicSelectionDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学生-我的选题控制器
 * GET: 查看选题申请列表
 * POST: 提交新的选题申请
 */
public class StudentSelectionController extends HttpServlet {

    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/student/selections.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/student/selections.jsp";

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

        try {
            applyTopic(request, user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showSelectionList(HttpServletRequest request, User user) throws Exception {
        request.setAttribute("selections", selectionDao.findByStudent(user.getId()));
        request.setAttribute("hasActive", selectionDao.hasActiveSelection(user.getId()));
        request.setAttribute("studentSelectionOpen", settingDao.isOpen("student_selection_open"));
        request.setAttribute("selectionRound", settingDao.getValue("selection_round"));
    }

    private void applyTopic(HttpServletRequest request, User user) throws Exception {
        Integer topicId = ParamUtil.getInt(request, "topicId");
        if (topicId == null) {
            return;
        }

        if (!settingDao.isOpen("student_selection_open")) {
            return;
        }

        if (selectionDao.hasActiveSelection(user.getId())) {
            return;
        }

        String reason = request.getParameter("reason");
        int roundNo = parseInt(settingDao.getValue("selection_round"), 1);
        selectionDao.insert(user.getId(), topicId, reason, roundNo, "pending");
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
