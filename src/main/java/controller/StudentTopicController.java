package controller;

import bean.User;
import dao.SystemSettingDao;
import dao.TopicDao;
import dao.TopicSelectionDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 



public class StudentTopicController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            int roundNo = parseInt(settingDao.getValue("selection_round"), 1);
            boolean hasApproved = selectionDao.hasApprovedSelection(user.getId());
            boolean hasSubmittedCurrentRound = selectionDao.hasSelectionInRound(user.getId(), roundNo);

            request.setAttribute("topics", topicDao.findOpen());
            request.setAttribute("studentSelectionOpen", settingDao.isOpen("student_selection_open"));
            request.setAttribute("selectionRound", roundNo);
            request.setAttribute("hasApproved", hasApproved);
            request.setAttribute("hasSubmittedCurrentRound", hasSubmittedCurrentRound);
            request.setAttribute("canSubmitSelection",
                    settingDao.isOpen("student_selection_open") && !hasApproved && !hasSubmittedCurrentRound);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/topics.jsp").forward(request, response);
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
}
