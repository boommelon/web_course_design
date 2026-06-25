package controller;

import dao.FinalAssignmentDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员查看全校最终分配结果（只读）。
 */
public class AdminGroupController extends HttpServlet {
    private FinalAssignmentDao assignmentDao = new FinalAssignmentDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("assignments", assignmentDao.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/admin/groups.jsp").forward(request, response);
    }
}
