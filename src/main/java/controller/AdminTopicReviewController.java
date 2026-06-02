package controller;

import bean.Topic;
import dao.TopicDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员-课题审核控制器。
 */
public class AdminTopicReviewController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("topics", topicDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/topics.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String action = request.getParameter("action");
        String comment = request.getParameter("comment");

        try {
            if ("approve".equals(action)) {
                topicDao.updateReview(id, "approved", comment);
            } else if ("reject".equals(action)) {
                topicDao.updateReview(id, "rejected", comment);
            } else if ("edit".equals(action)) {
                Topic topic = new Topic();
                topic.setId(id);
                topic.setTitle(request.getParameter("title"));
                topic.setDescription(request.getParameter("description"));
                topic.setMaxStudents(Integer.parseInt(request.getParameter("maxStudents")));
                topic.setStatus(request.getParameter("status"));
                topicDao.update(topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        response.sendRedirect(request.getContextPath() + "/admin/topics.action");
    }
}
