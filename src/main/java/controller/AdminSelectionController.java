package controller;

import dao.FinalAssignmentDao;
import dao.TopicDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员查看全校选题/分配结果（只读）。
 * 选题确认、强制分配由专业负责人在 /director/ 下完成。
 */
public class AdminSelectionController extends HttpServlet {
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();
    private TopicDao topicDao = new TopicDao();
    private static final String JSP_PAGE = "/WEB-INF/jsp/admin/selections.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("assignments", assignmentDao.findAll());
            request.setAttribute("topics", topicDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }
}
