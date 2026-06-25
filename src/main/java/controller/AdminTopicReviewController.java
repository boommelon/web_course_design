package controller;

import bean.Topic;
import dao.TopicDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 


public class AdminTopicReviewController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private static final String LIST_PAGE = "/admin/topics.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/admin/topics.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            showTopicList(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            redirectToList(request, response);
            return;
        }

        String action = request.getParameter("opttype");

        try {
            if ("approve".equals(action)) {
                approveTopic(request, id);
            }

            if ("reject".equals(action)) {
                rejectTopic(request, id);
            }

            if ("edit".equals(action)) {
                editTopic(request, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showTopicList(HttpServletRequest request) throws Exception {
        request.setAttribute("topics", topicDao.findAll());
    }

    private void approveTopic(HttpServletRequest request, int id) throws Exception {
        String comment = request.getParameter("comment");
        topicDao.updateReview(id, "approved", comment);
    }

    private void rejectTopic(HttpServletRequest request, int id) throws Exception {
        String comment = request.getParameter("comment");
        topicDao.updateReview(id, "rejected", comment);
    }

    private void editTopic(HttpServletRequest request, int id) throws Exception {
        Integer maxStudents = ParamUtil.getInt(request, "maxStudents");
        if (maxStudents == null) {
            return;
        }

        Topic topic = new Topic();
        topic.setId(id);
        topic.setTitle(request.getParameter("title"));
        topic.setDescription(request.getParameter("description"));
        topic.setMaxStudents(maxStudents);
        topic.setStatus(request.getParameter("status"));
        topicDao.update(topic);
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
