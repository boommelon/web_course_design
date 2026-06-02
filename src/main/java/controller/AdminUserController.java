package controller;

import bean.User;
import dao.UserDao;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            // 删除操作
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                userDao.delete(id);
                response.sendRedirect(request.getContextPath() + "/admin/users.action");
                return;
            }

            // 查询用户列表（支持按角色筛选）
            String roleFilter = request.getParameter("role");
            if (roleFilter != null && !roleFilter.isEmpty()) {
                request.setAttribute("users", userDao.findByRole(roleFilter));
            } else {
                request.setAttribute("users", userDao.findAll());
            }
            request.setAttribute("roleFilter", roleFilter);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                // 新增用户
                User user = new User();
                user.setUsername(request.getParameter("username"));
                user.setPassword(request.getParameter("password"));
                user.setName(request.getParameter("name"));
                user.setRole(request.getParameter("role"));
                user.setEmail(request.getParameter("email"));
                user.setPhone(request.getParameter("phone"));
                userDao.insert(user);
            } else if ("edit".equals(action)) {
                // 修改用户
                User user = new User();
                user.setId(Integer.parseInt(request.getParameter("id")));
                user.setName(request.getParameter("name"));
                user.setRole(request.getParameter("role"));
                user.setEmail(request.getParameter("email"));
                user.setPhone(request.getParameter("phone"));
                userDao.update(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        response.sendRedirect(request.getContextPath() + "/admin/users.action");
    }
}