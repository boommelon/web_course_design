package controller;

import dao.SystemSettingDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员-系统流程开关控制器。
 */
public class AdminSystemSettingController extends HttpServlet {

    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("settings", settingDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            settingDao.update("topic_publish_open", request.getParameter("topic_publish_open") != null);
            settingDao.update("student_selection_open", request.getParameter("student_selection_open") != null);
            settingDao.update("document_upload_open", request.getParameter("document_upload_open") != null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/settings.action");
    }
}
