package controller;

import dao.SystemSettingDao;
import dao.TopicDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学生-浏览课题控制器
 * 展示所有开放状态的课题供学生选择
 */
public class StudentTopicController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("topics", topicDao.findOpen());
            request.setAttribute("studentSelectionOpen", settingDao.isOpen("student_selection_open"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/topics.jsp").forward(request, response);
    }
}
