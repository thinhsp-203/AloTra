package stnw.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import stnw.model.User;
import stnw.utils.Roles; 

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

        // CUSTOMER: cấm admin
        if (user.getRoleid() == Roles.CUSTOMER) {
            resp.sendRedirect(req.getContextPath() + "/home?alert=access_denied");
            return;
        }

        // STAFF: chặn chức năng nhạy cảm
        if (user.getRoleid() == Roles.STAFF) {
            String uri = req.getRequestURI();

            if (uri.contains("/admin/users") ||
                uri.contains("/admin/reports") ||
                uri.contains("/admin/vouchers")) {

                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền truy cập mục này!");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
