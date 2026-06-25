package controller;

import bean.Announcement;
import bean.User;
import dao.AnnouncementDao;
import dao.DocumentDao;
import dao.FinalAssignmentDao;
import dao.SelectionDao;
import dao.SystemSettingDao;
import dao.TopicDao;
import dao.UserDao;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 仪表盘：按角色展示不同概览数据。
 */
public class DashboardController extends HttpServlet {

    private UserDao userDao = new UserDao();
    private TopicDao topicDao = new TopicDao();
    private SelectionDao selectionDao = new SelectionDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private DocumentDao documentDao = new DocumentDao();
    private AnnouncementDao announcementDao = new AnnouncementDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");

        try {
            List<Announcement> announcements = announcementDao.findAll();
            request.setAttribute("announcements", announcements);

            String role = user.getRole();
            if (Stage.ROLE_ADMIN.equals(role)) {
                request.setAttribute("directorCount", userDao.countByRole("director"));
                request.setAttribute("teacherCount", userDao.countByRole("teacher"));
                request.setAttribute("studentCount", userDao.countByRole("student"));
                request.setAttribute("topicCount", topicDao.count());
                request.setAttribute("assignedCount", assignmentDao.count());
            } else if (Stage.ROLE_DIRECTOR.equals(role)) {
                String college = user.getCollege();
                String major = user.getMajor();
                int round = settingDao.getInt(Stage.CURRENT_ROUND, 1);
                request.setAttribute("major", major);
                request.setAttribute("round", round);
                request.setAttribute("pendingTopicCount",
                        topicDao.countByMajorAndStatus(college, major, "pending"));
                request.setAttribute("studentCount",
                        userDao.countByRoleAndMajor("student", college, major));
                request.setAttribute("assignedCount", assignmentDao.countByMajor(college, major));
                request.setAttribute("submittedCount",
                        selectionDao.countSubmittedStudents(college, major, round));
            } else if (Stage.ROLE_TEACHER.equals(role)) {
                request.setAttribute("topicCount", topicDao.findByTeacher(user.getId()).size());
                request.setAttribute("myStudentCount", assignmentDao.findByTeacher(user.getId()).size());
                request.setAttribute("pendingDocuments", documentDao.countPendingByTeacher(user.getId()));
            } else if (Stage.ROLE_STUDENT.equals(role)) {
                request.setAttribute("myAssignment", assignmentDao.findByStudent(user.getId()));
                request.setAttribute("docCount", documentDao.findByStudent(user.getId()).size());
                request.setAttribute("announcementCount", announcements.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/dashboard.jsp").forward(request, response);
    }
}
