package controller;

import bean.User;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

 




public class LoginController extends HttpServlet {

    private UserDao userDao = new UserDao();
    private static final String LOGIN_PAGE = "/login.jsp";
    private static final String DASHBOARD_PAGE = "/dashboard.action";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userDao.validate(username, password);
            if (user != null) {
                loginSuccess(request, response, user);
            } else {
                loginFail(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.getSession().invalidate();
        response.sendRedirect(request.getContextPath() + LOGIN_PAGE);
    }

    private void loginSuccess(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        request.getSession().setAttribute("loginUser", user);
        response.sendRedirect(request.getContextPath() + DASHBOARD_PAGE);
    }

    private void loginFail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("error", "用户名或密码错误");
        request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
    }
}
