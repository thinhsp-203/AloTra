package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.User;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Cho phép cả Admin (roleId=1) và Manager (roleId=2)
        if (user == null || (user.getRoleid() != 1 && user.getRoleid() != 2)) {
            session = req.getSession(true);
            session.setAttribute("redirectAfterLogin", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}