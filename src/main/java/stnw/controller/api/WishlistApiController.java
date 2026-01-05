package stnw.controller.api;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import stnw.model.User;
import stnw.service.WishlistService;
import stnw.service.impl.WishlistServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@WebServlet(urlPatterns = {"/api/wishlist/toggle", "/api/wishlist/ids"})
public class WishlistApiController extends HttpServlet {
    
    private final Gson gson = new Gson();
    private WishlistService wishlistService;

    @Override
    public void init() throws ServletException {
        wishlistService = new WishlistServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/toggle")) {
            toggleWishlistItem(req, resp);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/ids")) {
            getWishlistIds(req, resp);
        }
    }

    private void toggleWishlistItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        try {
            HttpSession session = req.getSession();
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                resp.setStatus(401); // Unauthorized
                responseData.put("status", "error");
                responseData.put("message", "Bạn cần đăng nhập để thực hiện việc này.");
                resp.getWriter().write(gson.toJson(responseData));
                return;
            }

            int productId = Integer.parseInt(req.getParameter("productId"));
            var result = wishlistService.toggleItem(currentUser.getId(), productId);
            resp.setStatus(result.ok() ? 200 : 500);
            responseData.put("status", result.status());
            responseData.put("message", result.message());
        } catch (Exception e) {
            resp.setStatus(500); // Internal Server Error
            responseData.put("status", "error");
            responseData.put("message", "Lỗi máy chủ " + e.getMessage());
            e.printStackTrace();
        } finally {
            resp.getWriter().write(gson.toJson(responseData));
        }
    }
    
    private void getWishlistIds(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            responseData.put("wishlistIds", Set.of()); // Trả về set rỗng
            resp.getWriter().write(gson.toJson(responseData));
            return;
        }

        try {
            User currentUser = (User) session.getAttribute("currentUser");
            Set<Integer> wishlistIds = wishlistService.getWishlistProductIds(currentUser.getId());
            responseData.put("wishlistIds", wishlistIds);
            resp.getWriter().write(gson.toJson(responseData));
            
        } catch (Exception e) {
             resp.setStatus(500);
             responseData.put("error", e.getMessage());
             resp.getWriter().write(gson.toJson(responseData));
        }
    }
}
