package controller;

import dao.TopicDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员查看全校题目（只读）。题目审核由专业负责人在 /director/ 下完成，
 * 管理员不直接审某个专业的题。
 */
public class AdminTopicReviewController extends HttpServlet {

    private TopicDao topicDao = new TopicDao();
    private static final String JSP_PAGE = "/WEB-INF/jsp/admin/topics.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("topics", topicDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }
}
