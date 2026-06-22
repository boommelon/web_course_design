package controller;

import bean.Topic;
import bean.User;
import dao.SystemSettingDao;
import dao.TopicDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师-课题管理控制器
 */
public class TeacherTopicController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/teacher/topics.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/teacher/topics.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String action = request.getParameter("opttype");

        try {
            if ("delete".equals(action)) {
                deleteTopic(request, user);
                redirectToList(request, response);
                return;
            }

            showTopicList(request, user);
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
        String action = request.getParameter("opttype");

        try {
            if ("add".equals(action)) {
                addTopic(request, user);
            }

            if ("edit".equals(action)) {
                editTopic(request, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showTopicList(HttpServletRequest request, User user) throws Exception {
        request.setAttribute("topics", topicDao.findByTeacher(user.getId()));
        request.setAttribute("topicPublishOpen", settingDao.isOpen("topic_publish_open"));
    }

    private void addTopic(HttpServletRequest request, User user) throws Exception {
        if (!settingDao.isOpen("topic_publish_open")) {
            return;
        }

        Integer maxStudents = ParamUtil.getInt(request, "maxStudents");
        if (maxStudents == null) {
            return;
        }

        Topic topic = buildTopic(request, maxStudents);
        topic.setTeacherId(user.getId());
        topicDao.insert(topic);
    }

    private void editTopic(HttpServletRequest request, User user) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        Integer maxStudents = ParamUtil.getInt(request, "maxStudents");
        if (id == null || maxStudents == null) {
            return;
        }

        Topic topic = buildTopic(request, maxStudents);
        topic.setId(id);
        topic.setStatus(request.getParameter("status"));
        topicDao.update(topic, user.getId());
    }

    private Topic buildTopic(HttpServletRequest request, Integer maxStudents) {
        Topic topic = new Topic();
        topic.setTitle(request.getParameter("title"));
        topic.setDescription(request.getParameter("description"));
        topic.setMaxStudents(maxStudents);
        return topic;
    }

    private void deleteTopic(HttpServletRequest request, User user) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id != null) {
            topicDao.delete(id, user.getId());
        }
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
