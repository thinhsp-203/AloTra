package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.OrderDetail;
import model.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/orders", "/admin/orders/*"})
public class AdminOrderController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderController.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        EntityManager em = JpaUtil.em();
        
        try {
            if (path == null || "/".equals(path)) {
                // Danh sách đơn hàng
                String keyword = req.getParameter("keyword");
                String status = req.getParameter("status");
                
                StringBuilder jpql = new StringBuilder("SELECT o FROM Orders o WHERE 1=1");
                
                if (keyword != null && !keyword.trim().isEmpty()) {
                    jpql.append(" AND (o.fullname LIKE :kw OR o.phone LIKE :kw)");
                }
                if (status != null && !status.trim().isEmpty()) {
                    jpql.append(" AND o.order_status = :status");
                }
                jpql.append(" ORDER BY o.createdDate DESC");
                
                var query = em.createQuery(jpql.toString(), Orders.class);
                if (keyword != null && !keyword.trim().isEmpty()) {
                    query.setParameter("kw", "%" + keyword.trim() + "%");
                }
                if (status != null && !status.trim().isEmpty()) {
                    query.setParameter("status", status);
                }
                
                List<Orders> orders = query.getResultList();
                req.setAttribute("orders", orders);
                req.setAttribute("keyword", keyword);
                req.setAttribute("selectedStatus", status);
                req.getRequestDispatcher("/views/admin/orders.jsp").forward(req, resp);
                
            } else if ("/detail".equals(path)) {
                // Chi tiết đơn hàng
                int orderId = Integer.parseInt(req.getParameter("id"));
                Orders order = em.find(Orders.class, orderId);
                
                if (order == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                    return;
                }
                
                List<OrderDetail> details = em.createQuery(
                    "SELECT od FROM OrderDetail od WHERE od.order.order_id = :oid", 
                    OrderDetail.class)
                    .setParameter("oid", orderId)
                    .getResultList();
                
                req.setAttribute("order", order);
                req.setAttribute("details", details);
                req.getRequestDispatcher("/views/admin/order_detail.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error processing order request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing order request: " + e.getMessage());
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
            
            if (order == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            if ("updateStatus".equals(action)) {
                String newStatus = req.getParameter("status");
                order.setOrder_status(newStatus);
                order.setUpdatedDate(java.time.LocalDateTime.now());
                em.merge(order);
                
            } else if ("updatePayment".equals(action)) {
                String paymentStatus = req.getParameter("paymentStatus");
                order.setPayment_status(paymentStatus);
                order.setUpdatedDate(java.time.LocalDateTime.now());
                em.merge(order);
            }
            
            tx.commit();
            resp.sendRedirect(req.getContextPath() + "/admin/orders/detail?id=" + orderId);
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Error updating order", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error updating order: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}