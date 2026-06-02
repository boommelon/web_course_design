package controller;

import bean.User;
import bean.Topic;
import dao.TopicDao;
import dao.TopicSelectionDao;

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
        int id = Integer.parseInt(request.getParameter("id"));
        int topicId = Integer.parseInt(request.getParameter("topicId"));
        String action = request.getParameter("action");

        try {
            if ("approve".equals(action)) {
                Topic topic = topicDao.findById(topicId);
                if (topic != null && topic.getSelectedCount() < topic.getMaxStudents()) {
                    selectionDao.updateStatus(id, "approved");
                    topicDao.incrementSelected(topicId);
                    topicDao.closeIfFull(topicId);
                }
            } else if ("reject".equals(action)) {
                selectionDao.updateStatus(id, "rejected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/selections.action");
    }
}
