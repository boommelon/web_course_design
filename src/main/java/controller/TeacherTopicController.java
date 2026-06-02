package controller;

import bean.Topic;
import bean.User;
import dao.SystemSettingDao;
import dao.TopicDao;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String action = request.getParameter("action");
        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                topicDao.delete(id);
                response.sendRedirect(request.getContextPath() + "/teacher/topics.action");
                return;
            }
            request.setAttribute("topics", topicDao.findByTeacher(user.getId()));
            request.setAttribute("topicPublishOpen", settingDao.isOpen("topic_publish_open"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/teacher/topics.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                if (!settingDao.isOpen("topic_publish_open")) {
                    response.sendRedirect(request.getContextPath() + "/teacher/topics.action");
                    return;
                }
                Topic topic = new Topic();
                topic.setTitle(request.getParameter("title"));
                topic.setDescription(request.getParameter("description"));
                topic.setTeacherId(user.getId());
                topic.setMaxStudents(Integer.parseInt(request.getParameter("maxStudents")));
                topicDao.insert(topic);
            } else if ("edit".equals(action)) {
                Topic topic = new Topic();
                topic.setId(Integer.parseInt(request.getParameter("id")));
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
        response.sendRedirect(request.getContextPath() + "/teacher/topics.action");
    }
}
