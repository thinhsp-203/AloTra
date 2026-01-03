package service.impl;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;
import service.*;

import java.time.LocalDateTime;
import java.util.*;

public class AdminOrderServiceImpl implements AdminOrderService {
    
    private final LoyaltyService loyaltyService = new LoyaltyServiceImpl();
    private final NotificationService notificationService = new NotificationServiceImpl();
    
    @Override
    public List<Orders> searchOrders(String keyword, String status) {
        EntityManager em = JpaUtil.em();
        try {
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
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Orders getOrderDetails(int orderId) {
        EntityManager em = JpaUtil.em();
        try {
            // Sử dụng JOIN FETCH để eager load orderDetails và product
            jakarta.persistence.TypedQuery<Orders> query = em.createQuery(
                "SELECT DISTINCT o FROM Orders o " +
                "LEFT JOIN FETCH o.orderDetails od " +
                "LEFT JOIN FETCH od.product " +
                "WHERE o.order_id = :orderId",
                Orders.class
            );
            query.setParameter("orderId", orderId);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updateOrderStatus(int orderId, String newStatus) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Đơn hàng không tồn tại!");
            }
            
            // Không cho phép cập nhật nếu đơn hàng đã hủy
            if ("Hủy Đơn".equals(order.getOrder_status())) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Không thể cập nhật đơn hàng đã bị hủy!");
            }
            
            String oldStatus = order.getOrder_status();
            order.setOrder_status(newStatus);
            order.setUpdatedDate(LocalDateTime.now());
            
            // Logic: Khi đơn hàng bị hủy, tự động cập nhật payment_status
            if ("Hủy Đơn".equals(newStatus) && !"Hủy Đơn".equals(oldStatus)) {
                String currentPaymentStatus = order.getPayment_status();
                
                // Chỉ cập nhật nếu chưa phải "Đã hoàn tiền" (tránh ghi đè)
                if (!"Đã hoàn tiền".equals(currentPaymentStatus)) {
                    // Nếu đơn hàng đã thanh toán online → cần hoàn tiền
                    if ("Đã thanh toán".equals(currentPaymentStatus) && 
                        "Online".equals(order.getPayment_method())) {
                        order.setPayment_status("Đã hoàn tiền");
                    }
                    // Nếu là COD hoặc chưa thanh toán → giữ "Chưa thanh toán"
                    else if ("COD".equals(order.getPayment_method()) || 
                             "Chưa thanh toán".equals(currentPaymentStatus)) {
                        order.setPayment_status("Chưa thanh toán");
                    }
                }
            }
            
            // Logic: Khi đơn hàng được set "Hoàn thành", tự động set payment_status = "Đã thanh toán"
            if ("Hoàn thành".equals(newStatus) && !"Hoàn thành".equals(oldStatus)) {
                order.setPayment_status("Đã thanh toán");
                
                // Tích điểm thành viên khi đơn hàng hoàn thành
                try {
                    if (order.getUser() != null && order.getTotal_amount() != null) {
                        loyaltyService.earnPointsFromOrder(order.getUser(), order.getTotal_amount(), order.getOrder_id());
                    }
                } catch (Exception e) {
                    // Log lỗi nhưng không rollback transaction vì đơn hàng vẫn cần được cập nhật
                    System.err.println("Lỗi khi tích điểm cho đơn hàng #" + orderId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            em.merge(order);
            em.getTransaction().commit();
            
            // Tạo notification cho user về cập nhật trạng thái đơn hàng
            try {
                if (order.getUser() != null) {
                    String message = "Đơn hàng #" + orderId + " của bạn đã được cập nhật: " + newStatus;
                    String link = "/user/orders";
                    notificationService.createNotification(order.getUser().getId(), message, link);
                }
            } catch (Exception e) {
                // Log lỗi nhưng không ảnh hưởng đến cập nhật đơn hàng
                System.err.println("Lỗi khi tạo notification cho đơn hàng #" + orderId + ": " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Re-throw để controller xử lý
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật trạng thái: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updatePaymentStatus(int orderId, String paymentStatus) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Đơn hàng không tồn tại!");
            }
            
            // Không cho phép cập nhật nếu đơn hàng đã hủy
            if ("Hủy Đơn".equals(order.getOrder_status())) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Không thể cập nhật thanh toán cho đơn hàng đã bị hủy!");
            }
            
            // Logic: Không cho phép đặt "Đã thanh toán" nếu đơn hàng đã bị hủy (double check)
            if ("Hủy Đơn".equals(order.getOrder_status()) && "Đã thanh toán".equals(paymentStatus)) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Không thể đặt 'Đã thanh toán' cho đơn hàng đã bị hủy.");
            }
            
            order.setPayment_status(paymentStatus);
            order.setUpdatedDate(LocalDateTime.now());
            em.merge(order);
            
            em.getTransaction().commit();
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Re-throw để controller xử lý
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật thanh toán: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}