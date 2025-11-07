package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.User;
import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*", "/checkout", "/cart/*"})
public class UserAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            // Save the requested URL to redirect after login
            String requestedUrl = req.getRequestURI();
            String queryString = req.getQueryString();
            if (queryString != null) {
                requestedUrl += "?" + queryString;
            }
            
            session = req.getSession(true);
            session.setAttribute("redirectAfterLogin", requestedUrl);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Check if account is active
        if (user.getIsActive() == null || !user.getIsActive()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}