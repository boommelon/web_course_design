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
import java.util.ArrayList;
import java.util.List;

 




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
        int roundNo = parseInt(settingDao.getValue("selection_round"), 1);
        request.setAttribute("selections", selectionDao.findByStudent(user.getId()));
        request.setAttribute("hasApproved", selectionDao.hasApprovedSelection(user.getId()));
        request.setAttribute("hasSubmittedCurrentRound", selectionDao.hasSelectionInRound(user.getId(), roundNo));
        request.setAttribute("studentSelectionOpen", settingDao.isOpen("student_selection_open"));
        request.setAttribute("selectionRound", roundNo);
    }

    private void applyTopic(HttpServletRequest request, User user) throws Exception {
        if (!settingDao.isOpen("student_selection_open")) {
            return;
        }

        int roundNo = parseInt(settingDao.getValue("selection_round"), 1);
        if (selectionDao.hasApprovedSelection(user.getId())
                || selectionDao.hasSelectionInRound(user.getId(), roundNo)) {
            return;
        }

        List<Integer> topicIds = getTopicIds(request);
        if (topicIds.isEmpty()) {
            return;
        }

        String reason = request.getParameter("reason");
        selectionDao.confirmRoundSelections(user.getId(), topicIds, reason, roundNo);
    }

    private List<Integer> getTopicIds(HttpServletRequest request) {
        List<Integer> topicIds = new ArrayList<Integer>();
        String[] values = request.getParameterValues("topicIds");
        if (values != null) {
            for (String value : values) {
                addTopicId(topicIds, value);
            }
        }

        if (topicIds.isEmpty()) {
            Integer topicId = ParamUtil.getInt(request, "topicId");
            if (topicId != null) {
                topicIds.add(topicId);
            }
        }

        return topicIds;
    }

    private void addTopicId(List<Integer> topicIds, String value) {
        if (value == null) {
            return;
        }
        try {
            int topicId = Integer.parseInt(value.trim());
            if (topicId > 0) {
                topicIds.add(topicId);
            }
        } catch (NumberFormatException e) {
            
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
