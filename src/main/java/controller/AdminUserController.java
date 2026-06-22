package controller;

import bean.User;
import dao.UserDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员-用户管理控制器
 * 实现用户的增删改查功能
 */
public class AdminUserController extends HttpServlet {

    private UserDao userDao = new UserDao();
    private static final String LIST_PAGE = "/admin/users.action";
    private static final String JSP_PAGE = "/WEB-INF/jsp/admin/users.jsp";
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("opttype");

        try {
            if ("delete".equals(action)) {
                deleteUser(request);
                redirectToList(request, response);
                return;
            }

            if ("resetPassword".equals(action)) {
                resetPassword(request);
                redirectToList(request, response);
                return;
            }

            showUserList(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher(JSP_PAGE).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("opttype");

        try {
            if ("add".equals(action)) {
                addUser(request);
            }

            if ("edit".equals(action)) {
                editUser(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showUserList(HttpServletRequest request) throws Exception {
        String roleFilter = request.getParameter("role");

        if (roleFilter != null && !roleFilter.isEmpty()) {
            request.setAttribute("users", userDao.findByRole(roleFilter));
        } else {
            request.setAttribute("users", userDao.findAll());
        }
        request.setAttribute("roleFilter", roleFilter);
    }

    private void addUser(HttpServletRequest request) throws Exception {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        fillUserInfo(request, user);
        userDao.insert(user);
    }

    private void editUser(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            return;
        }

        User user = new User();
        user.setId(id);
        fillUserInfo(request, user);
        userDao.update(user);
    }

    private void fillUserInfo(HttpServletRequest request, User user) {
        user.setName(request.getParameter("name"));
        user.setRole(request.getParameter("role"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
    }

    private void deleteUser(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id != null) {
            userDao.delete(id);
        }
    }

    private void resetPassword(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id != null) {
            userDao.updatePassword(id, DEFAULT_PASSWORD);
        }
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + LIST_PAGE);
    }
}
