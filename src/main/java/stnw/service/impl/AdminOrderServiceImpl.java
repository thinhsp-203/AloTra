package stnw.service.impl;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import stnw.model.*;
import stnw.service.*;
import stnw.utils.OrderStatus;
import stnw.utils.PaymentStatus;
import stnw.utils.Roles;

import java.time.LocalDateTime;
import java.util.*;

public class AdminOrderServiceImpl implements AdminOrderService {
    
    private final LoyaltyService loyaltyService = new LoyaltyServiceImpl();
    private final NotificationService notificationService = new NotificationServiceImpl();
    
    /**
     * Kiểm tra xem payment_method có phải là phương thức thanh toán online không
     * (ATM, MOMO, VNPAY, Online)
     */
    private boolean isOnlinePaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }
        String method = paymentMethod.trim().toUpperCase();
        return "ATM".equals(method) || 
               "MOMO".equals(method) || 
               "VNPAY".equals(method) || 
               "ONLINE".equals(method);
    }
    
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
                    // Nếu đơn hàng đã thanh toán online thì cần hoàn tiền
                    if ("Đã thanh toán".equals(currentPaymentStatus) && 
                        "Online".equals(order.getPayment_method())) {
                        order.setPayment_status("Đã hoàn tiền");
                    }
                    // Nếu là COD hoặc chưa thanh toán thì giữ "Chưa thanh toán"
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
    
    @Override
    public void confirmOrder(int orderId, User adminUser) {
        // Validate admin role
        if (adminUser == null || (adminUser.getRoleid() != Roles.ADMIN && adminUser.getRoleid() != Roles.STAFF)) {
            throw new IllegalArgumentException("Chỉ admin/staff mới được xác nhận đơn hàng!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Đơn hàng không tồn tại!");
            }
            
            // Chỉ cho phép xác nhận khi status = CHO_XAC_NHAN
            OrderStatus currentStatus = OrderStatus.fromOldString(order.getOrder_status());
            if (currentStatus != OrderStatus.CHO_XAC_NHAN) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Chỉ có thể xác nhận đơn hàng đang ở trạng thái 'Chờ xác nhận'!");
            }
            
            order.setOrder_status(OrderStatus.DANG_CHUAN_BI.getDisplayName());
            order.setUpdatedDate(LocalDateTime.now());
            em.merge(order);
            em.getTransaction().commit();
            
            // Tạo notification cho user
            try {
                if (order.getUser() != null) {
                    String message = "Đơn hàng #" + orderId + " đã được xác nhận và đang được chuẩn bị";
                    String link = "/user/orders";
                    notificationService.createNotification(order.getUser().getId(), message, link);
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tạo notification cho đơn hàng #" + orderId + ": " + e.getMessage());
            }
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xác nhận đơn hàng: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void rejectOrder(int orderId, User adminUser) {
        // Validate admin role
        if (adminUser == null || (adminUser.getRoleid() != Roles.ADMIN && adminUser.getRoleid() != Roles.STAFF)) {
            throw new IllegalArgumentException("Chỉ admin/staff mới được từ chối đơn hàng!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Đơn hàng không tồn tại!");
            }
            
            // Chỉ cho phép từ chối khi status = CHO_XAC_NHAN
            OrderStatus currentStatus = OrderStatus.fromOldString(order.getOrder_status());
            if (currentStatus != OrderStatus.CHO_XAC_NHAN) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Chỉ có thể từ chối đơn hàng đang ở trạng thái 'Chờ xác nhận'!");
            }
            
            order.setOrder_status(OrderStatus.TU_CHOI.getDisplayName());
            order.setUpdatedDate(LocalDateTime.now());
            
            // Xử lý payment status khi từ chối
            // Nếu đơn hàng có payment_method là ATM, MOMO, VNPAY và đã thanh toán thì chuyển sang Hoàn tiền
            String currentPaymentStatus = order.getPayment_status();
            if ("Đã thanh toán".equals(currentPaymentStatus) && isOnlinePaymentMethod(order.getPayment_method())) {
                order.setPayment_status(PaymentStatus.HOAN_TIEN.getDisplayName());
            } else {
                order.setPayment_status(PaymentStatus.CHUA_THANH_TOAN.getDisplayName());
            }
            
            em.merge(order);
            em.getTransaction().commit();
            
            // Tạo notification cho user
            try {
                if (order.getUser() != null) {
                    String message = "Đơn hàng #" + orderId + " đã bị từ chối bởi cửa hàng.";
                    String link = "/user/orders";
                    notificationService.createNotification(order.getUser().getId(), message, link);
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tạo notification cho đơn hàng #" + orderId + ": " + e.getMessage());
            }
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi từ chối đơn hàng: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updateOrderStatusByAdmin(int orderId, String newStatus, User adminUser) {
        // Validate admin role
        if (adminUser == null || (adminUser.getRoleid() != Roles.ADMIN && adminUser.getRoleid() != Roles.STAFF)) {
            throw new IllegalArgumentException("Chỉ admin/staff mới được cập nhật trạng thái đơn hàng!");
        }
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Đơn hàng không tồn tại!");
            }
            
            OrderStatus currentStatusEnum = OrderStatus.fromOldString(order.getOrder_status());
            OrderStatus newStatusEnum = OrderStatus.fromOldString(newStatus);
            
            // Không cho phép chuyển sang final state nếu đã là final state
            if (currentStatusEnum.isFinalState()) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Không thể cập nhật đơn hàng đã ở trạng thái kết thúc!");
            }
            
            // Validation theo workflow:
            // 1. Từ DANG_CHUAN_BI có thể chuyển sang: DANG_GIAO, HOAN_THANH, HUY_BOI_SHOP
            // 2. Từ DANG_GIAO có thể chuyển sang: HOAN_THANH, HUY_BOI_SHOP
            if (currentStatusEnum == OrderStatus.DANG_CHUAN_BI) {
                // Từ "Đang chuẩn bị" có thể chuyển sang: Đang giao, Hoàn thành, Hủy bởi shop
                if (newStatusEnum != OrderStatus.DANG_GIAO && 
                    newStatusEnum != OrderStatus.HOAN_THANH && 
                    newStatusEnum != OrderStatus.HUY_BOI_SHOP) {
                    em.getTransaction().rollback();
                    throw new IllegalArgumentException("Trạng thái không hợp lệ! Từ 'Đang chuẩn bị' chỉ có thể chuyển sang: Đang giao, Hoàn thành, hoặc Hủy bởi shop.");
                }
            } else if (currentStatusEnum == OrderStatus.DANG_GIAO) {
                // Từ "Đang giao" có thể chuyển sang: Hoàn thành, Hủy bởi shop
                if (newStatusEnum != OrderStatus.HOAN_THANH && 
                    newStatusEnum != OrderStatus.HUY_BOI_SHOP) {
                    em.getTransaction().rollback();
                    throw new IllegalArgumentException("Trạng thái không hợp lệ! Từ 'Đang giao' chỉ có thể chuyển sang: Hoàn thành hoặc Hủy bởi shop.");
                }
            } else {
                // Không cho phép cập nhật từ các trạng thái khác
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Chỉ có thể cập nhật trạng thái từ 'Đang chuẩn bị' hoặc 'Đang giao'!");
            }
            
            order.setOrder_status(newStatus);
            order.setUpdatedDate(LocalDateTime.now());
            
            // Logic: Khi đơn hàng được set "Hoàn thành", tự động set payment_status = "Đã thanh toán"
            if (newStatusEnum == OrderStatus.HOAN_THANH) {
                order.setPayment_status(PaymentStatus.DA_THANH_TOAN.getDisplayName());
                
                // Tích điểm thành viên khi đơn hàng hoàn thành
                try {
                    if (order.getUser() != null && order.getTotal_amount() != null) {
                        loyaltyService.earnPointsFromOrder(order.getUser(), order.getTotal_amount(), order.getOrder_id());
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi tích điểm cho đơn hàng #" + orderId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Logic: Khi đơn hàng bị hủy bởi shop, xử lý payment status
            // Nếu đơn hàng có payment_method là ATM, MOMO, VNPAY và đã thanh toán thì chuyển sang Hoàn tiền
            if (newStatusEnum == OrderStatus.HUY_BOI_SHOP) {
                String currentPaymentStatus = order.getPayment_status();
                if ("Đã thanh toán".equals(currentPaymentStatus) && isOnlinePaymentMethod(order.getPayment_method())) {
                    order.setPayment_status(PaymentStatus.HOAN_TIEN.getDisplayName());
                } else {
                    order.setPayment_status(PaymentStatus.CHUA_THANH_TOAN.getDisplayName());
                }
            }
            
            em.merge(order);
            em.getTransaction().commit();
            
            // Tạo notification cho user
            try {
                if (order.getUser() != null) {
                    String message = "Đơn hàng #" + orderId + " của bạn đã được cập nhật: " + newStatus;
                    String link = "/user/orders";
                    notificationService.createNotification(order.getUser().getId(), message, link);
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tạo notification cho đơn hàng #" + orderId + ": " + e.getMessage());
            }
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật trạng thái đơn hàng: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
