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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String opttype = request.getParameter("opttype");
        try {
            if ("delete".equals(opttype)) {
                Integer id = ParamUtil.getInt(request, "id");
                if (id != null) {
                    int rows = topicDao.delete(id, user.getId());
                    if (rows == 0) {
                        response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
                        return;
                    }
                }
                response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
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
        String opttype = request.getParameter("opttype");
        try {
            if ("add".equals(opttype)) {
                if (!settingDao.isOpen("topic_publish_open")) {
                    response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
                    return;
                }
                Integer maxStudents = ParamUtil.getInt(request, "maxStudents");
                if (maxStudents == null) {
                    response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
                    return;
                }
                Topic topic = new Topic();
                topic.setTitle(request.getParameter("title"));
                topic.setDescription(request.getParameter("description"));
                topic.setTeacherId(user.getId());
                topic.setMaxStudents(maxStudents);
                topicDao.insert(topic);
            } else if ("edit".equals(opttype)) {
                Integer id = ParamUtil.getInt(request, "id");
                Integer maxStudents = ParamUtil.getInt(request, "maxStudents");
                if (id == null || maxStudents == null) {
                    response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
                    return;
                }
                Topic topic = new Topic();
                topic.setId(id);
                topic.setTitle(request.getParameter("title"));
                topic.setDescription(request.getParameter("description"));
                topic.setMaxStudents(maxStudents);
                topic.setStatus(request.getParameter("status"));
                int rows = topicDao.update(topic, user.getId());
                if (rows == 0) {
                    response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/teacher/topics.opttype");
    }
}
