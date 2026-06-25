package controller;

import bean.User;
import dao.UserDao;
import util.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 



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
                if (!addUser(request)) {
                    showUserList(request);
                    keepAddForm(request);
                    request.getRequestDispatcher(JSP_PAGE).forward(request, response);
                    return;
                }
            }

            if ("edit".equals(action)) {
                if (!editUser(request)) {
                    showUserList(request);
                    request.setAttribute("openModal", "editUserModal" + request.getParameter("id"));
                    request.setAttribute("errorUserId", ParamUtil.getInt(request, "id"));
                    request.getRequestDispatcher(JSP_PAGE).forward(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        redirectToList(request, response);
    }

    private void showUserList(HttpServletRequest request) throws Exception {
        int fixedCount = userDao.fixDuplicatePhones();
        if (fixedCount > 0) {
            request.setAttribute("message", "已自动修复 " + fixedCount + " 个重复手机号");
        }

        String roleFilter = request.getParameter("role");
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            roleFilter = request.getParameter("roleFilter");
        }

        if (roleFilter != null && !roleFilter.isEmpty()) {
            request.setAttribute("users", userDao.findByRole(roleFilter));
        } else {
            request.setAttribute("users", userDao.findAll());
        }
        request.setAttribute("roleFilter", roleFilter);
    }

    private boolean addUser(HttpServletRequest request) throws Exception {
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));
        fillUserInfo(request, user);

        if (phoneUsed(user.getPhone(), null)) {
            request.setAttribute("error", "手机号已存在，请更换");
            return false;
        }

        userDao.insert(user);
        return true;
    }

    private boolean editUser(HttpServletRequest request) throws Exception {
        Integer id = ParamUtil.getInt(request, "id");
        if (id == null) {
            return true;
        }

        User user = new User();
        user.setId(id);
        fillUserInfo(request, user);

        if (phoneUsed(user.getPhone(), id)) {
            request.setAttribute("error", "手机号已存在，请更换");
            return false;
        }

        userDao.update(user);
        return true;
    }

    private void fillUserInfo(HttpServletRequest request, User user) {
        user.setName(request.getParameter("name"));
        user.setRole(request.getParameter("role"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));
    }

    private void keepAddForm(HttpServletRequest request) {
        request.setAttribute("openModal", "addUserModal");
        request.setAttribute("formUsername", request.getParameter("username"));
        request.setAttribute("formPassword", request.getParameter("password"));
        request.setAttribute("formName", request.getParameter("name"));
        request.setAttribute("formRole", request.getParameter("role"));
        request.setAttribute("formEmail", request.getParameter("email"));
        request.setAttribute("formPhone", request.getParameter("phone"));
    }

    private boolean phoneUsed(String phone, Integer userId) throws Exception {
        if (phone == null || phone.trim().length() == 0) {
            return false;
        }

        if (userId == null) {
            return userDao.phoneExists(phone.trim());
        }
        return userDao.phoneExistsForOtherUser(phone.trim(), userId);
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
