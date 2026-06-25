package controller;

import bean.User;
import dao.FinalAssignmentDao;
import dao.SelectionDao;
import dao.SystemSettingDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 专业负责人选题确认（第一轮 / 第二轮）。
 * 按题目聚合显示本专业本轮志愿，负责人选定 学生<->题目 写入最终分配；
 * 写入由 FinalAssignmentDao 在事务内保证一人一题、一题一人。
 */
public class DirectorConfirmController extends HttpServlet {

    private SelectionDao selectionDao = new SelectionDao();
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/director/confirm.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/director/confirm.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            int round = settingDao.getInt(Stage.CURRENT_ROUND, 1);
            request.setAttribute("round", round);
            request.setAttribute("confirmOpen", settingDao.isOpen(Stage.CONFIRM_OPEN));
            request.setAttribute("choices",
                    selectionDao.findChoicesForConfirm(user.getCollege(), user.getMajor(), round));
            request.setAttribute("assignments",
                    assignmentDao.findByMajor(user.getCollege(), user.getMajor()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String action = request.getParameter("opttype");
        try {
            if ("confirm".equals(action) && settingDao.isOpen(Stage.CONFIRM_OPEN)) {
                confirm(request, user);
            } else if ("revoke".equals(action) && settingDao.isOpen(Stage.CONFIRM_OPEN)) {
                Integer studentId = ParamUtil.getInt(request, "studentId");
                if (studentId != null) {
                    boolean ok = assignmentDao.revoke(studentId, user.getCollege(), user.getMajor());
                    request.getSession().setAttribute("flash", ok ? "已撤销分配" : "撤销失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }

    private void confirm(HttpServletRequest request, User user) throws Exception {
        Integer studentId = ParamUtil.getInt(request, "studentId");
        Integer topicId = ParamUtil.getInt(request, "topicId");
        Integer choiceRank = ParamUtil.getInt(request, "choiceRank");
        if (studentId == null || topicId == null) {
            return;
        }
        int round = settingDao.getInt(Stage.CURRENT_ROUND, 1);
        String source = round == 2 ? "round2" : "round1";
        FinalAssignmentDao.AssignResult result = assignmentDao.confirm(
                studentId, topicId, source, choiceRank, user.getId(),
                "第" + round + "轮确认", user.getCollege(), user.getMajor());
        request.getSession().setAttribute("flash", result.message);
    }
}
