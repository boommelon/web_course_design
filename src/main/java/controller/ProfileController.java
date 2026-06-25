package controller;

import bean.User;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 


public class ProfileController extends HttpServlet {

    private UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        String opttype = request.getParameter("opttype");

        try {
            if ("profile".equals(opttype)) {
                User user = new User();
                user.setId(loginUser.getId());
                user.setName(request.getParameter("name"));
                user.setEmail(request.getParameter("email"));
                user.setPhone(request.getParameter("phone"));
                userDao.updateProfile(user);

                User refreshed = userDao.findById(loginUser.getId());
                request.getSession().setAttribute("loginUser", refreshed);
                request.setAttribute("message", "个人资料已保存");
            } else if ("password".equals(opttype)) {
                String oldPassword = request.getParameter("oldPassword");
                String newPassword = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                if (newPassword == null || newPassword.length() < 6) {
                    request.setAttribute("error", "新密码长度至少为6位");
                } else if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("error", "两次输入的新密码不一致");
                } else if (!userDao.checkPassword(loginUser.getId(), oldPassword)) {
                    request.setAttribute("error", "原密码不正确");
                } else {
                    userDao.updatePassword(loginUser.getId(), newPassword);
                    request.setAttribute("message", "密码已修改");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(request, response);
    }
}
