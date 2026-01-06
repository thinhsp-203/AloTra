package stnw.controller.user.profile;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.CartItem;
import stnw.model.Orders;
import stnw.model.User;
import stnw.service.ReorderService;
import stnw.service.UserProfileService;
import stnw.service.impl.ReorderServiceImpl;
import stnw.service.impl.UserProfileServiceImpl;

import java.io.IOException;
import java.util.List;


@MultipartConfig(
    fileSizeThreshold = 2*1024*1024, 
    maxFileSize = 10*1024*1024, 
    maxRequestSize = 50*1024*1024
)
@WebServlet(urlPatterns = {"/user/profile", "/user/orders", "/user/change-password", "/user/reorder"}, asyncSupported = false)
public class UserProfileController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserProfileService profileService;
	private ReorderService reorderService;
    
    @Override
    public void init() throws ServletException {
        profileService = new UserProfileServiceImpl();
        reorderService = new ReorderServiceImpl();
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
        
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/profile")) {
                showProfile(req, resp, currentUser);
            } else if (uri.endsWith("/orders")) {
                showOrders(req, resp, currentUser);
            } else if (uri.endsWith("/change-password")) {
                req.getRequestDispatcher("/views/user/change-password.jsp").forward(req, resp);
            } else if (uri.endsWith("/reorder")) {
                handleReorder(req, resp, currentUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
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
            if ("cancelOrder".equals(action)) {
                handleCancelOrder(req, resp, currentUser);
            } else if ("updateProfile".equals(action)) {
                handleUpdateProfile(req, resp, currentUser);
            } else if ("changePassword".equals(action)) {
                handleChangePassword(req, resp, currentUser);
            } else if ("changeAvatar".equals(action)) {
                handleChangeAvatar(req, resp, currentUser);
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            String uri = req.getRequestURI();
            if (uri != null && uri.endsWith("/change-password")) {
                req.getRequestDispatcher("/views/user/change-password.jsp").forward(req, resp);
            } else {
                User user = profileService.getUserById(currentUser.getId());
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            String uri = req.getRequestURI();
            if (uri != null && uri.endsWith("/change-password")) {
                req.getRequestDispatcher("/views/user/change-password.jsp").forward(req, resp);
            } else {
                User user = profileService.getUserById(currentUser.getId());
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
            }
        }
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private void showProfile(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws ServletException, IOException {
        User user = profileService.getUserById(currentUser.getId());
        if (user == null) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("user", user);
        req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
    }
    
    private void showOrders(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws ServletException, IOException {
        String status = req.getParameter("status");
        String keyword = req.getParameter("keyword");
        
        List<Orders> orders = profileService.getUserOrders(currentUser.getId(), status, keyword);
        
        req.setAttribute("orders", orders);
        req.setAttribute("currentStatus", status);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/views/user/orders.jsp").forward(req, resp);
    }
    
    private void handleCancelOrder(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws IOException {
        int orderId = Integer.parseInt(req.getParameter("orderId"));
        
        profileService.cancelOrder(currentUser.getId(), orderId);
        req.getSession().setAttribute("orderSuccess", "Hủy đơn hàng #" + orderId);
        
        resp.sendRedirect(req.getContextPath() + "/user/orders");
    }
    
    private void handleUpdateProfile(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws ServletException, IOException {
        String fullname = sanitize(req.getParameter("fullname"));
        String phone = sanitize(req.getParameter("phone"));
        String address = sanitize(req.getParameter("address"));
        
        profileService.updateProfile(currentUser.getId(), fullname, phone, address);
        
        // Update session
        User updatedUser = profileService.getUserById(currentUser.getId());
        req.getSession().setAttribute("currentUser", updatedUser);
        
        req.setAttribute("success", "Cập nhật thông tin thành công!");
        req.setAttribute("user", updatedUser);
        req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
    }
    
    private void handleChangePassword(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws ServletException, IOException {
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        
        profileService.changePassword(currentUser.getId(), oldPassword, newPassword, confirmPassword);
        
        req.setAttribute("success", "Đổi mật khẩu thành công!");
        req.getRequestDispatcher("/views/user/change-password.jsp").forward(req, resp);
    }
    
    private void handleChangeAvatar(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws ServletException, IOException {
        Part avatarFile = req.getPart("avatar");
        
        profileService.changeAvatar(currentUser.getId(), avatarFile, req.getServletContext());
        
        // Update session
        User updatedUser = profileService.getUserById(currentUser.getId());
        req.getSession().setAttribute("currentUser", updatedUser);
        
        req.setAttribute("success", "Cập nhật ảnh đại diện thành công!");
        req.setAttribute("user", updatedUser);
        req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
    }
    
    private void handleReorder(HttpServletRequest req, HttpServletResponse resp, User currentUser) 
            throws IOException {
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) req.getSession().getAttribute("CART");
            if (cart == null) {
                cart = new java.util.ArrayList<>();
                req.getSession().setAttribute("CART", cart);
            }
            
            var result = reorderService.reorder(currentUser.getId(), orderId, cart);
            
            int added = result.get("addedItems");
            int unavailable = result.get("unavailableItems");
            
            if (added > 0 && unavailable == 0) {
                req.getSession().setAttribute("orderSuccess", 
                    "Đã thêm " + added + " sản phẩm từ đơn hàng #" + orderId + " vào giỏ hàng!");
            } else if (added > 0 && unavailable > 0) {
                req.getSession().setAttribute("orderSuccess", 
                    "Đã thêm " + added + " sản phẩm. " + unavailable + " sản phẩm không còn khả dụng.");
            } else {
                req.getSession().setAttribute("orderError", 
                    "Không thể thêm sản phẩm. Tất cả sản phẩm trong đơn hàng đã hết hàng hoặc không còn bán.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/checkout");
            
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("orderError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("orderError", "Không thể thêm lại sản phẩm. Vui lòng thử lại!");
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        }
    }
    
    private String sanitize(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("<[^>]*>", "");
    }
}

