package controller;

import bean.User;
import dao.FinalAssignmentDao;
import dao.SystemSettingDao;
import dao.TopicDao;
import dao.UserDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 专业负责人强制分配：第二轮后仍未匹配时，手工指定剩余学生与剩余题目。
 * 在系统界面内完成，不需要管理员去数据库改。
 */
public class DirectorAssignController extends HttpServlet {

    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private UserDao userDao = new UserDao();
    private TopicDao topicDao = new TopicDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/director/assign.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/director/assign.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("manualOpen", settingDao.isOpen(Stage.MANUAL_ASSIGN_OPEN));
            request.setAttribute("unassignedStudents",
                    userDao.findUnassignedStudents(user.getCollege(), user.getMajor()));
            // 剩余可分配题目 = 本专业 approved 且未被最终分配
            request.setAttribute("availableTopics",
                    topicDao.findSelectableByMajor(user.getCollege(), user.getMajor()));
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
        try {
            if (settingDao.isOpen(Stage.MANUAL_ASSIGN_OPEN)) {
                Integer studentId = ParamUtil.getInt(request, "studentId");
                Integer topicId = ParamUtil.getInt(request, "topicId");
                if (studentId != null && topicId != null) {
                    FinalAssignmentDao.AssignResult result = assignmentDao.manualAssign(
                            studentId, topicId, user.getId(), "强制分配",
                            user.getCollege(), user.getMajor());
                    request.getSession().setAttribute("flash", result.message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
