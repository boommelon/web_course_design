package controller;

import bean.Topic;
import dao.SystemSettingDao;
import dao.TopicDao;
import dao.TopicSelectionDao;
import dao.UserDao;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("selections", selectionDao.findAll());
            request.setAttribute("unselectedStudents", userDao.findStudentsWithoutApprovedSelection());
            request.setAttribute("availableTopics", topicDao.findAvailableApproved());
            request.setAttribute("settings", settingDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/selections.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("settings".equals(action)) {
                settingDao.update("student_selection_open", request.getParameter("student_selection_open") != null);
                settingDao.updateValue("selection_round", request.getParameter("selection_round"));
            } else if ("forceAssign".equals(action)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                int topicId = Integer.parseInt(request.getParameter("topicId"));
                int roundNo = Integer.parseInt(settingDao.getValue("selection_round"));
                Topic topic = topicDao.findById(topicId);
                if (topic != null && topic.getSelectedCount() < topic.getMaxStudents()
                        && !selectionDao.hasActiveSelection(studentId)) {
                    selectionDao.insert(studentId, topicId, "管理员最终分配", roundNo, "approved");
                    topicDao.incrementSelected(topicId);
                    topicDao.closeIfFull(topicId);
                }
            } else if ("approve".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                int topicId = Integer.parseInt(request.getParameter("topicId"));
                Topic topic = topicDao.findById(topicId);
                if (topic != null && topic.getSelectedCount() < topic.getMaxStudents()) {
                    selectionDao.updateStatus(id, "approved");
                    topicDao.incrementSelected(topicId);
                    topicDao.closeIfFull(topicId);
                }
            } else if ("reject".equals(action)) {
                selectionDao.updateStatus(Integer.parseInt(request.getParameter("id")), "rejected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/selections.action");
    }
}
