package controller;

import bean.Topic;
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
        String opttype = request.getParameter("opttype");
        try {
            if ("settings".equals(opttype)) {
                settingDao.update("student_selection_open", request.getParameter("student_selection_open") != null);
                settingDao.updateValue("selection_round", request.getParameter("selection_round"));
            } else if ("forceAssign".equals(opttype)) {
                Integer studentId = ParamUtil.getInt(request, "studentId");
                Integer topicId = ParamUtil.getInt(request, "topicId");
                if (studentId == null || topicId == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/selections.opttype");
                    return;
                }
                int roundNo = parseInt(settingDao.getValue("selection_round"), 1);
                Topic topic = topicDao.findById(topicId);
                if (topic != null && topic.getSelectedCount() < topic.getMaxStudents()
                        && !selectionDao.hasActiveSelection(studentId)) {
                    selectionDao.insert(studentId, topicId, "管理员最终分配", roundNo, "approved");
                    topicDao.incrementSelected(topicId);
                    topicDao.closeIfFull(topicId);
                }
            } else if ("approve".equals(opttype)) {
                Integer id = ParamUtil.getInt(request, "id");
                Integer topicId = ParamUtil.getInt(request, "topicId");
                if (id == null || topicId == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/selections.opttype");
                    return;
                }
                Topic topic = topicDao.findById(topicId);
                if (topic != null && topic.getSelectedCount() < topic.getMaxStudents()) {
                    selectionDao.updateStatus(id, "approved");
                    topicDao.incrementSelected(topicId);
                    topicDao.closeIfFull(topicId);
                }
            } else if ("reject".equals(opttype)) {
                Integer id = ParamUtil.getInt(request, "id");
                if (id == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/selections.opttype");
                    return;
                }
                selectionDao.updateStatus(id, "rejected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/selections.opttype");
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
