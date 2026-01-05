package stnw.controller.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import stnw.dto.NotificationDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Notification;
import stnw.model.User;
import stnw.service.NotificationService;
import stnw.service.impl.NotificationServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/api/notifications/recent", asyncSupported = false)
public class NotificationApiController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private NotificationService notificationService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        notificationService = new NotificationServiceImpl();
        gson = new GsonBuilder()
            .setLenient()
            .disableHtmlEscaping() // Don't escape Unicode characters
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
                }
            })
            .create();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(error));
            out.flush();
            return;
        }
        
        try {
            List<Notification> notifications = notificationService.getRecentNotifications(currentUser.getId(), 5);
            long unreadCount = notificationService.getUnreadCount(currentUser.getId());
            
            // Convert Notification entities to DTOs to avoid Hibernate proxy serialization issues
            List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(n -> new NotificationDTO(
                    n.getId(),
                    n.getMessage(),
                    n.getLink(),
                    n.getIsRead(),
                    n.getCreatedDate()
                ))
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notificationDTOs);
            response.put("unreadCount", unreadCount);
            
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(response));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Internal server error");
            PrintWriter out = resp.getWriter();
            out.print(gson.toJson(error));
            out.flush();
        }
    }
}

