package controller;

import bean.User;
import dao.AnnouncementDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

 


public class AjaxAnnouncementController extends HttpServlet {

    private AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isAdmin(request)) {
            writeJson(response, HttpServletResponse.SC_FORBIDDEN, "{\"success\":false,\"message\":\"\\u65e0\\u6743\\u9650\"}");
            return;
        }

        try {
            Integer id = ParamUtil.getInt(request, "id");
            if (id == null) {
                writeJson(response, HttpServletResponse.SC_BAD_REQUEST, "{\"success\":false,\"message\":\"\\u53c2\\u6570\\u9519\\u8bef\"}");
                return;
            }
            announcementDao.delete(id);
            writeJson(response, HttpServletResponse.SC_OK, "{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"success\":false,\"message\":\"\\u5220\\u9664\\u5931\\u8d25\"}");
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
