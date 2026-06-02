package controller;

import bean.User;
import dao.SystemSettingDao;
import dao.TopicSelectionDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 学生-我的选题控制器
 * GET: 查看选题申请列表
 * POST: 提交新的选题申请
 */
public class StudentSelectionController extends HttpServlet {

    private TopicSelectionDao selectionDao = new TopicSelectionDao();
    private SystemSettingDao settingDao = new SystemSettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("selections", selectionDao.findByStudent(user.getId()));
            request.setAttribute("hasActive", selectionDao.hasActiveSelection(user.getId()));
            request.setAttribute("studentSelectionOpen", settingDao.isOpen("student_selection_open"));
            request.setAttribute("selectionRound", settingDao.getValue("selection_round"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/student/selections.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        int topicId = Integer.parseInt(request.getParameter("topicId"));
        String reason = request.getParameter("reason");
        try {
            if (!settingDao.isOpen("student_selection_open")) {
                response.sendRedirect(request.getContextPath() + "/student/selections.action");
                return;
            }
            // 检查是否已有有效申请
            if (!selectionDao.hasActiveSelection(user.getId())) {
                int roundNo = Integer.parseInt(settingDao.getValue("selection_round"));
                selectionDao.insert(user.getId(), topicId, reason, roundNo, "pending");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/student/selections.action");
    }
}
