package controller;

import bean.User;
import dao.SystemSettingDao;
import dao.TopicDao;
import util.ParamUtil;
import util.Stage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 专业负责人审题：只处理本人 college+major 的题目。
 * 通过 -> approved（题目进入可选）；退回 -> rejected（教师可改后重提）。
 */
public class DirectorTopicReviewController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private SystemSettingDao settingDao = new SystemSettingDao();
    private static final String LIST_PAGE = "/director/topics.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/director/topics.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            request.setAttribute("topics", topicDao.findByMajor(user.getCollege(), user.getMajor()));
            request.setAttribute("reviewOpen", settingDao.isOpen(Stage.TOPIC_REVIEW_OPEN));
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
        Integer id = ParamUtil.getInt(request, "id");
        try {
            if (id != null && settingDao.isOpen(Stage.TOPIC_REVIEW_OPEN)) {
                String comment = request.getParameter("comment");
                int rows = 0;
                if ("approve".equals(action)) {
                    rows = topicDao.review(id, "approved", comment, user.getId(), user.getCollege(), user.getMajor());
                } else if ("reject".equals(action)) {
                    rows = topicDao.review(id, "rejected", comment, user.getId(), user.getCollege(), user.getMajor());
                }
                if (rows == 0) {
                    request.getSession().setAttribute("flash", "只能审核待审核题目，已审核或已分配题目不可重复操作。");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
