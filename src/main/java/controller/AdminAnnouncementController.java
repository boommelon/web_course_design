package controller;

import bean.Announcement;
import dao.AnnouncementDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员-公告管理控制器
 */
public class AdminAnnouncementController extends HttpServlet {

    private AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                announcementDao.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/announcements.action");
                return;
            }
            request.setAttribute("announcements", announcementDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/announcements.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) {
                Announcement a = new Announcement();
                a.setTitle(request.getParameter("title"));
                a.setContent(request.getParameter("content"));
                a.setIsTop(request.getParameter("isTop") != null ? 1 : 0);
                announcementDao.insert(a);
            } else if ("edit".equals(action)) {
                Announcement a = new Announcement();
                a.setId(Integer.parseInt(request.getParameter("id")));
                a.setTitle(request.getParameter("title"));
                a.setContent(request.getParameter("content"));
                a.setIsTop(request.getParameter("isTop") != null ? 1 : 0);
                announcementDao.update(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/announcements.action");
    }
}
