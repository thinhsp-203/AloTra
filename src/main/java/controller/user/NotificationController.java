package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Notification;
import model.User;
import service.NotificationService;
import service.impl.NotificationServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/user/notifications", asyncSupported = false)
public class NotificationController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        notificationService = new NotificationServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            req.getSession().setAttribute("redirectAfterLogin", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            List<Notification> notifications = notificationService.getUserNotifications(currentUser.getId());
            req.setAttribute("notifications", notifications);
            req.getRequestDispatcher("/views/user/notifications.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.getRequestDispatcher("/views/user/notifications.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String action = req.getParameter("action");
        
        try {
            if ("markAsRead".equals(action)) {
                Integer notificationId = Integer.parseInt(req.getParameter("id"));
                notificationService.markAsRead(notificationId);
            } else if ("markAllAsRead".equals(action)) {
                notificationService.markAllAsRead(currentUser.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        resp.sendRedirect(req.getContextPath() + "/user/notifications");
    }
}

