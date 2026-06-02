package filter;

import bean.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录验证与权限控制过滤器
 * 1. 检查用户是否已登录，未登录则跳转到登录页
 * 2. 根据URL前缀判断角色权限，防止越权访问
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();

        // 放行不需要登录的资源：登录页面、登录请求、静态资源
        if (path.equals("/login.jsp") || path.equals("/login.action") || path.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否已登录
        HttpSession session = req.getSession(false);
        User loginUser = null;
        if (session != null) {
            loginUser = (User) session.getAttribute("loginUser");
        }

        if (loginUser == null) {
            // 未登录，重定向到登录页
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 权限控制：根据URL前缀判断是否有权访问
        String role = loginUser.getRole();
        if (path.startsWith("/admin/") && !"admin".equals(role)) {
            // 非管理员访问管理员页面，跳转回仪表盘
            resp.sendRedirect(req.getContextPath() + "/dashboard.action");
            return;
        }
        if (path.startsWith("/teacher/") && !"teacher".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard.action");
            return;
        }
        if (path.startsWith("/student/") && !"student".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard.action");
            return;
        }

        // 验证通过，放行
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}