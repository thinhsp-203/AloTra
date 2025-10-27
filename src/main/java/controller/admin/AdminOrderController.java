package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Orders;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/orders", "/admin/orders/*"})
public class AdminOrderController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        EntityManager em = JpaUtil.em();
        
        try {
            if (path == null || "/".equals(path)) {
                // Danh sách đơn hàng
                String status = req.getParameter("status");
                String keyword = req.getParameter("keyword");
                
                StringBuilder jpql = new StringBuilder("SELECT o FROM Orders o WHERE 1=1");
                if (status != null && !status.isBlank()) {
                    jpql.append(" AND o.order_status = :status");
                }
                if (keyword != null && !keyword.isBlank()) {
                    jpql.append(" AND (o.fullname LIKE :kw OR o.phone LIKE :kw)");
                }
                jpql.append(" ORDER BY o.createdDate DESC");
                
                var query = em.createQuery(jpql.toString(), Orders.class);
                if (status != null && !status.isBlank()) {
                    query.setParameter("status", status);
                }
                if (keyword != null && !keyword.isBlank()) {
                    query.setParameter("kw", "%" + keyword + "%");
                }
                
                List<Orders> orders = query.getResultList();
                req.setAttribute("orders", orders);
                req.setAttribute("selectedStatus", status);
                req.setAttribute("keyword", keyword);
                req.getRequestDispatcher("/views/admin/orders.jsp").forward(req, resp);
                
            } else if ("/detail".equals(path)) {
                // Chi tiết đơn hàng
                int id = Integer.parseInt(req.getParameter("id"));
                Orders order = em.createQuery(
                    "SELECT o FROM Orders o LEFT JOIN FETCH o.user WHERE o.order_id = :id", 
                    Orders.class)
                    .setParameter("id", id)
                    .getSingleResult();
                
                var details = em.createQuery(
                    "SELECT d FROM OrderDetail d LEFT JOIN FETCH d.product WHERE d.order.order_id = :oid",
                    model.OrderDetail.class)
                    .setParameter("oid", id)
                    .getResultList();
                
                req.setAttribute("order", order);
                req.setAttribute("details", details);
                req.getRequestDispatcher("/views/admin/order_detail.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Lỗi xử lý đơn hàng");
        } finally {
            em.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = req.getParameter("action");
        int orderId = Integer.parseInt(req.getParameter("orderId"));
        
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            Orders order = em.find(Orders.class, orderId);
            
            if ("updateStatus".equals(action)) {
                String newStatus = req.getParameter("status");
                order.setOrder_status(newStatus);
                order.setUpdatedDate(java.time.LocalDateTime.now());
            } else if ("updatePayment".equals(action)) {
                String payStatus = req.getParameter("paymentStatus");
                order.setPayment_status(payStatus);
                order.setUpdatedDate(java.time.LocalDateTime.now());
            } else if ("cancel".equals(action)) {
                order.setOrder_status("Đã hủy");
                order.setUpdatedDate(java.time.LocalDateTime.now());
            }
            
            em.merge(order);
            tx.commit();
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders/detail?id=" + orderId);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendError(500, "Lỗi cập nhật đơn hàng");
        } finally {
            em.close();
        }
    }
}