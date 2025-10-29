package controller.user;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.Orders;
import utils.PasswordUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/user/profile", "/user/orders"})
public class UserProfileController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String uri = req.getRequestURI();
        EntityManager em = JpaUtil.em();
        
        try {
            if (uri.endsWith("/profile")) {
                User user = em.find(User.class, currentUser.getId());
                req.setAttribute("user", user);
                req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
                
            } else if (uri.endsWith("/orders")) {
                String status = req.getParameter("status");
                
                String jpql = "SELECT DISTINCT o FROM Orders o " +
                              "LEFT JOIN FETCH o.orderDetails od " +
                              "LEFT JOIN FETCH od.product " +
                              "WHERE o.user.id = :uid ";

                if (status != null && !status.isEmpty() && !status.equals("Tất cả")) {
                    jpql += "AND o.order_status = :status ";
                }
                
                jpql += "ORDER BY o.createdDate DESC";

                TypedQuery<Orders> query = em.createQuery(jpql, Orders.class)
                                             .setParameter("uid", currentUser.getId());

                if (status != null && !status.isEmpty() && !status.equals("Tất cả")) {
                    query.setParameter("status", status);
                }
                
                List<Orders> orders = query.getResultList();
                
                req.setAttribute("orders", orders);
                req.setAttribute("currentStatus", status);
                req.getRequestDispatcher("/views/user/orders.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
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
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            
            if ("cancelOrder".equals(action)) {
                int orderId = Integer.parseInt(req.getParameter("orderId"));
                Orders order = em.find(Orders.class, orderId);

                if (order != null && order.getUser().getId().equals(currentUser.getId()) && "Chờ xác nhận".equals(order.getOrder_status())) {
                    order.setOrder_status("Đã hủy");
                    order.setUpdatedDate(java.time.LocalDateTime.now());
                    em.merge(order);
                    req.getSession().setAttribute("orderSuccess", "Đã hủy đơn hàng #" + orderId + " thành công.");
                } else {
                    req.getSession().setAttribute("orderError", "Không thể hủy đơn hàng #" + orderId + ".");
                }
                tx.commit();
                resp.sendRedirect(req.getContextPath() + "/user/orders");
                return;
            }
            
            User user = em.find(User.class, currentUser.getId());
            if ("updateProfile".equals(action)) {
                user.setFullname(req.getParameter("fullname"));
                user.setPhone(req.getParameter("phone"));
                user.setAddress(req.getParameter("address"));
                em.merge(user);
                tx.commit();
                req.getSession().setAttribute("currentUser", user);
                req.setAttribute("success", "Cập nhật thông tin thành công!");
            } else if ("changePassword".equals(action)) {
                String oldPassword = req.getParameter("oldPassword");
                String newPassword = req.getParameter("newPassword");
                String confirmPassword = req.getParameter("confirmPassword");
                
                if (!PasswordUtil.verify(oldPassword, user.getPassword())) {
                    req.setAttribute("error", "Mật khẩu cũ không đúng!");
                } else if (!newPassword.equals(confirmPassword)) {
                    req.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                } else if (newPassword.length() < 6) {
                    req.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                } else {
                    user.setPassword(PasswordUtil.hash(newPassword));
                    em.merge(user);
                    tx.commit();
                    req.setAttribute("success", "Đổi mật khẩu thành công!");
                }
}
            
            req.setAttribute("user", em.find(User.class, currentUser.getId()));
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.setAttribute("user", em.find(User.class, currentUser.getId()));
            req.getRequestDispatcher("/views/user/profile.jsp").forward(req, resp);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}