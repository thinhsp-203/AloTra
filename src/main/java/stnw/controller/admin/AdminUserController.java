package stnw.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.User;
import stnw.service.AdminUserService;
import stnw.service.impl.AdminUserServiceImpl;

import java.io.IOException;


@WebServlet(urlPatterns = {
    "/admin/users/create", 
    "/admin/users/edit", 
    "/admin/users/save", 
    "/admin/users/delete", 
    "/admin/users/delete-permanent",
    "/admin/users/toggle-status"
})
public class AdminUserController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminUserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new AdminUserServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/create")) {
                // Hiển th�?form tạo user
                req.getRequestDispatcher("/views/admin/user-form.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/edit")) {
                // Hiển th�?form sửa user
                int userId = Integer.parseInt(req.getParameter("id"));
                User user = userService.getUserById(userId);
                
                if (user == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/admin/user-form.jsp").forward(req, resp);
                
            } else {
                // GET không hợp l�?cho delete/toggle-status
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp l�?");
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/save")) {
                handleSaveUser(req, resp);
            } else if (uri.endsWith("/delete")) {
                handleDeleteUser(req, resp);
            } else if (uri.endsWith("/delete-permanent")) {
                handleHardDeleteUser(req, resp);
            } else if (uri.endsWith("/toggle-status")) {
                handleToggleStatus(req, resp);
            }
        } catch (IllegalArgumentException e) {
            // Lỗi validation t�?service
            req.getSession().setAttribute("error", e.getMessage());
            
            // Redirect v�?form edit nếu có userId
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + idParam);
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/users/create");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi h�?thống: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }
    
    // ==================== PRIVATE METHODS ====================
    
    private void handleSaveUser(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        String idParam = req.getParameter("id");
        Integer userId = (idParam != null && !idParam.isEmpty()) 
            ? Integer.parseInt(idParam) 
            : null;
        
        // Lấy thông tin t�?form
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String password = req.getParameter("password");
        
        boolean isActive = "on".equals(req.getParameter("isActive"));
        
        if (userId == null) {
            // TẠO MỚI - Mặc định role là User (roleId = 3)
            userService.createUser(username, email, password, fullname, 
                                  phone, address, 3, isActive); // Luôn tạo với roleId = 3 (User)
            req.getSession().setAttribute("success", "Đã tạo người dùng mới thành công!");
        } else {
            // CẬP NHẬT - Không cho phép thay đổi role, giữ nguyên role hiện tại
            // Truyền null cho roleId để service giữ nguyên role hiện tại
            userService.updateUser(userId, email, fullname, phone, address, 
                                  null, isActive, password); // null = không thay đổi role
            req.getSession().setAttribute("success", "Đã cập nhật thông tin người dùng!");
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
    
    private void handleDeleteUser(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        int userId = Integer.parseInt(req.getParameter("id"));
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        Integer currentUserId = (currentUser != null) ? currentUser.getId() : null;
        
        userService.softDeleteUser(userId, currentUserId);
        req.getSession().setAttribute("success", "Đã vô hiệu hóa người dùng thành công!");
        
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
    
    private void handleHardDeleteUser(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        int userId = Integer.parseInt(req.getParameter("id"));
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        Integer currentUserId = (currentUser != null) ? currentUser.getId() : null;
        
        userService.hardDeleteUser(userId, currentUserId);
        req.getSession().setAttribute("success", "Đã xóa vĩnh viễn người dùng thành công!");
        
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
    
    private void handleToggleStatus(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        int userId = Integer.parseInt(req.getParameter("id"));
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        Integer currentUserId = (currentUser != null) ? currentUser.getId() : null;
        
        userService.toggleUserStatus(userId, currentUserId);
        req.getSession().setAttribute("success", "Đã cập nhật trạng thái hoạt động!");
        
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}