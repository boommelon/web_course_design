package controller;

import dao.SystemSettingDao;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员控制系统各阶段开关。管理员只负责开关阶段，不直接审题/确认。
 */
public class AdminSystemSettingController extends HttpServlet {

    private SystemSettingDao settingDao = new SystemSettingDao();

    private static final String[] BOOLEAN_KEYS = {
            Stage.TOPIC_SUBMIT_OPEN, Stage.TOPIC_REVIEW_OPEN, Stage.SELECTION_OPEN,
            Stage.CONFIRM_OPEN, Stage.MANUAL_ASSIGN_OPEN, Stage.DOCUMENT_UPLOAD_OPEN,
            Stage.GRADE_OPEN, Stage.PROJECT_CLOSED
    };

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
            for (String key : BOOLEAN_KEYS) {
                settingDao.update(key, request.getParameter(key) != null);
            }
            String round = request.getParameter(Stage.CURRENT_ROUND);
            if ("1".equals(round) || "2".equals(round)) {
                settingDao.updateValue(Stage.CURRENT_ROUND, round);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/admin/settings.action");
    }
}
