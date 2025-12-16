package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.AdminUserService;
import service.impl.AdminUserServiceImpl;

import java.io.IOException;


@WebServlet(urlPatterns = {
    "/admin/users/create", 
    "/admin/users/edit", 
    "/admin/users/save", 
    "/admin/users/delete", 
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
                // Hiển thị form tạo user
                req.getRequestDispatcher("/views/admin/user_form.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/edit")) {
                // Hiển thị form sửa user
                int userId = Integer.parseInt(req.getParameter("id"));
                User user = userService.getUserById(userId);
                
                if (user == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/admin/user_form.jsp").forward(req, resp);
                
            } else {
                // GET không hợp lệ cho delete/toggle-status
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp lệ!");
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
            } else if (uri.endsWith("/toggle-status")) {
                handleToggleStatus(req, resp);
            }
        } catch (IllegalArgumentException e) {
            // Lỗi validation từ service
            req.getSession().setAttribute("error", e.getMessage());
            
            // Redirect về form edit nếu có userId
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/admin/users/edit?id=" + idParam);
            } else {
                resp.sendRedirect(req.getContextPath() + "/admin/users/create");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
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
        
        // Lấy thông tin từ form
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String password = req.getParameter("password");
        
        String roleIdParam = req.getParameter("roleid");
        Integer roleId = (roleIdParam != null && !roleIdParam.isEmpty()) 
            ? Integer.parseInt(roleIdParam) 
            : 3;
        
        boolean isActive = "on".equals(req.getParameter("isActive"));
        
        if (userId == null) {
            // TẠO MỚI
            userService.createUser(username, email, password, fullname, 
                                  phone, address, roleId, isActive);
            req.getSession().setAttribute("success", "Đã tạo người dùng mới thành công!");
        } else {
            // CẬP NHẬT
            userService.updateUser(userId, email, fullname, phone, address, 
                                  roleId, isActive, password);
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
    
    private void handleToggleStatus(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        int userId = Integer.parseInt(req.getParameter("id"));
        
        userService.toggleUserStatus(userId);
        req.getSession().setAttribute("success", "Đã cập nhật trạng thái hoạt động!");
        
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}