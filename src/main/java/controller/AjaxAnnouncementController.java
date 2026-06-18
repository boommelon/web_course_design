package controller;

import bean.User;
import dao.AnnouncementDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Ajax公告删除接口
 */
public class AjaxAnnouncementController extends HttpServlet {

    private AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, "{\"success\":false,\"message\":\"无权限\"}");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            announcementDao.delete(id);
            writeJson(response, HttpServletResponse.SC_OK, "{\"success\":true}");
        } catch (NumberFormatException e) {
            writeJson(response, HttpServletResponse.SC_BAD_REQUEST, "{\"success\":false,\"message\":\"参数错误\"}");
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"success\":false,\"message\":\"删除失败\"}");
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null && "admin".equals(loginUser.getRole());
    }

    private void writeJson(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }
}
