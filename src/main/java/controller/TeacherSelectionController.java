package controller;

import bean.User;
import bean.Topic;
import bean.TopicSelection;
import dao.TopicDao;
import dao.TopicSelectionDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师-选题审批控制器
 */
public class TeacherSelectionController extends HttpServlet {

    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private TopicDao topicDao = new TopicDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("selections", selectionDao.findByTeacher(user.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/selections.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/teacher/selections.opttype");
            return;
        }
        String opttype = request.getParameter("opttype");

        try {
            if ("approve".equals(opttype)) {
                TopicSelection selection = selectionDao.findById(id);
                if (selection == null) {
                    response.sendRedirect(request.getContextPath() + "/teacher/selections.opttype");
                    return;
                }
                int realTopicId = selection.getTopicId();
                Topic topic = topicDao.findById(realTopicId);
                if (topic != null && topic.getTeacherId() == user.getId()
                        && topic.getSelectedCount() < topic.getMaxStudents()) {
                    int rows = selectionDao.updateStatus(id, "approved", user.getId());
                    if (rows > 0) {
                        topicDao.incrementSelected(realTopicId);
                        topicDao.closeIfFull(realTopicId);
                    }
                }
            } else if ("reject".equals(opttype)) {
                selectionDao.updateStatus(id, "rejected", user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/selections.opttype");
    }
}
