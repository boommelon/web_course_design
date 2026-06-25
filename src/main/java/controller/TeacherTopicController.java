package controller;

import bean.Topic;
import bean.User;
import dao.SystemSettingDao;
import dao.TopicDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 教师出题。新题目默认 pending；被退回(rejected)可改后重提；
 * 审核通过(approved)后不能再修改(由 TopicDao.updateByTeacher 的状态条件保证)。
 * 题目所属专业沿用教师本人的 college/major。
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
            request.setAttribute("topics", topicDao.findByTeacher(user.getId()));
            request.setAttribute("topicSubmitOpen", settingDao.isOpen(Stage.TOPIC_SUBMIT_OPEN));
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
            } else if ("edit".equals(action)) {
                editTopic(request, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        redirectToList(request, response);
    }

    private void addTopic(HttpServletRequest request, User user) throws Exception {
        if (!settingDao.isOpen(Stage.TOPIC_SUBMIT_OPEN)) {
            return;
        }
        String title = trim(request.getParameter("title"));
        if (title.isEmpty()) {
            return;
        }
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDescription(request.getParameter("description"));
        topic.setTeacherId(user.getId());
        topic.setCollege(user.getCollege());
        topic.setMajor(user.getMajor());
        topicDao.insert(topic);
    }

    private void editTopic(HttpServletRequest request, User user) throws Exception {
        if (!settingDao.isOpen(Stage.TOPIC_SUBMIT_OPEN)) {
            return;
        }
        Integer id = ParamUtil.getInt(request, "id");
        String title = trim(request.getParameter("title"));
        if (id == null || title.isEmpty()) {
            return;
        }
        Topic topic = new Topic();
        topic.setId(id);
        topic.setTitle(title);
        topic.setDescription(request.getParameter("description"));
        // updateByTeacher 仅在 pending/rejected/draft 时生效，approved 后锁定
        topicDao.updateByTeacher(topic, user.getId());
    }

    private void deleteTopic(HttpServletRequest request, User user) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id != null) {
            topicDao.deleteByTeacher(id, user.getId());
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
