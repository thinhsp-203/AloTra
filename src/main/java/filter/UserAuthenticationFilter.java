package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.User;
import service.NotificationService;
import service.impl.NotificationServiceImpl;
import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*", "/checkout", "/cart/*"}, asyncSupported = false)
public class UserAuthenticationFilter implements Filter {
    
    private NotificationService notificationService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        notificationService = new NotificationServiceImpl();
    }

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
        
        // Set unread notification count
        try {
            long unreadCount = notificationService.getUnreadCount(user.getId());
            req.setAttribute("unreadNotifications", unreadCount);
            session.setAttribute("unreadNotifications", unreadCount);
        } catch (Exception e) {
            // Ignore notification errors to not block request
        }

        chain.doFilter(request, response);
    }
}