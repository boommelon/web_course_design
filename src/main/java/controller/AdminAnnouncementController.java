package controller;

import bean.Announcement;
import dao.AnnouncementDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 


public class AdminAnnouncementController extends HttpServlet {

    private AnnouncementDao announcementDao = new AnnouncementDao();
    private static final String LIST_PAGE = "/admin/announcements.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/admin/announcements.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("opttype");

        try {
            if ("delete".equals(action)) {
                deleteAnnouncement(request);
                redirectToList(request, response);
                return;
            }

            showAnnouncementList(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("opttype");

        try {
            if ("add".equals(action)) {
                addAnnouncement(request);
            }

            if ("edit".equals(action)) {
                editAnnouncement(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showAnnouncementList(HttpServletRequest request) throws Exception {
        request.setAttribute("announcements", announcementDao.findAll());
    }

    private void addAnnouncement(HttpServletRequest request) throws Exception {
        Announcement announcement = buildAnnouncement(request);
        announcementDao.insert(announcement);
    }

    private void editAnnouncement(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            return;
        }

        Announcement announcement = buildAnnouncement(request);
        announcement.setId(id);
        announcementDao.update(announcement);
    }

    private Announcement buildAnnouncement(HttpServletRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getParameter("title"));
        announcement.setContent(request.getParameter("content"));
        announcement.setIsTop(request.getParameter("isTop") != null ? 1 : 0);
        return announcement;
    }

    private void deleteAnnouncement(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id != null) {
            announcementDao.delete(id);
        }
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
