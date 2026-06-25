package controller;

import bean.Announcement;
import bean.User;
import dao.AnnouncementDao;
import dao.DocumentDao;
import dao.TopicDao;
import dao.TopicSelectionDao;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

 



public class DashboardController extends HttpServlet {

    private UserDao userDao = new UserDao();
    private TopicDao topicDao = new TopicDao();
    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private DocumentDao documentDao = new DocumentDao();
    private AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");

        try {
            
            List<Announcement> announcements = announcementDao.findAll();
            request.setAttribute("announcements", announcements);

            
            String role = user.getRole();
            if ("admin".equals(role)) {
                request.setAttribute("teacherCount", userDao.countByRole("teacher"));
                request.setAttribute("studentCount", userDao.countByRole("student"));
                request.setAttribute("topicCount", topicDao.count());
                request.setAttribute("selectedCount", selectionDao.countByStatus("approved"));
            } else if ("teacher".equals(role)) {
                request.setAttribute("topicCount", topicDao.findByTeacher(user.getId()).size());
                request.setAttribute("pendingSelections", selectionDao.findPendingByTeacher(user.getId()).size());
                request.setAttribute("pendingDocuments", documentDao.countPendingByTeacher(user.getId()));
            } else if ("student".equals(role)) {
                request.setAttribute("mySelection", selectionDao.findApprovedByStudent(user.getId()));
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
