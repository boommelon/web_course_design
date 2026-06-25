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

        
        if (path.equals("/login.jsp") || path.equals("/login.action") || path.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        
        HttpSession session = req.getSession(false);
        User loginUser = null;
        if (session != null) {
            loginUser = (User) session.getAttribute("loginUser");
        }

        if (loginUser == null) {
            
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        
        String role = loginUser.getRole();
        if (path.startsWith("/admin/") && !"admin".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard.action");
            return;
        }
        if (path.startsWith("/api/") && !"admin".equals(role)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"success\":false,\"message\":\"\\u65e0\\u6743\\u9650\"}");
            return;
        }
        if (path.startsWith("/director/") && !"director".equals(role)) {
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

        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
