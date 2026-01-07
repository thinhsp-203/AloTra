package stnw.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import stnw.model.User;
import stnw.enums.Roles; 

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/api/admin/*"}, asyncSupported = false)
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = (session != null)
                ? (User) session.getAttribute("currentUser")
                : null;

        // Chưa đăng nhập
        if (user == null) {
            req.getSession(true)
               .setAttribute("redirectAfterLogin", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login?message=login_required");
            return;
        }

        // ADMIN: vào tất cả
        if (user.getRoleid() == Roles.ADMIN) {
            chain.doFilter(request, response);
            return;
        }

        // CUSTOMER và STAFF: cấm admin (quyền ngang nhau)
        if (user.getRoleid() == Roles.CUSTOMER || user.getRoleid() == Roles.STAFF) {
            resp.sendRedirect(req.getContextPath() + "/home?alert=access_denied");
            return;
        }

        // Role không xác định: chuyển về login
        resp.sendRedirect(req.getContextPath() + "/login");;
    }
}
