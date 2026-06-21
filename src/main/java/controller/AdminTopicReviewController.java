package controller;

import bean.Topic;
import dao.TopicDao;
import util.ParamUtil;

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
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            response.sendRedirect(request.getContextPath() + "/admin/topics.opttype");
            return;
        }
        String opttype = request.getParameter("opttype");
        String comment = request.getParameter("comment");

        try {
            if ("approve".equals(opttype)) {
                topicDao.updateReview(id, "approved", comment);
            } else if ("reject".equals(opttype)) {
                topicDao.updateReview(id, "rejected", comment);
            } else if ("edit".equals(opttype)) {
                Topic topic = new Topic();
                topic.setId(id);
                topic.setTitle(request.getParameter("title"));
                topic.setDescription(request.getParameter("description"));
                Integer maxStudents = ParamUtil.getInt(request, "maxStudents");
                if (maxStudents == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/topics.opttype");
                    return;
                }
                topic.setMaxStudents(maxStudents);
                topic.setStatus(request.getParameter("status"));
                topicDao.update(topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        response.sendRedirect(request.getContextPath() + "/admin/topics.opttype");
    }
}
