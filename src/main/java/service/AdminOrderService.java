package service;

import java.util.List;
import model.Orders;
import model.User;

public interface AdminOrderService {
    List<Orders> searchOrders(String keyword, String status);
    Orders getOrderDetails(int orderId);
    
    /**
     * @deprecated Sử dụng confirmOrder hoặc rejectOrder thay thế
     */
    @Deprecated
    void updateOrderStatus(int orderId, String newStatus);
    
    void updatePaymentStatus(int orderId, String paymentStatus);
    
    /**
     * Admin xác nhận đơn hàng (chỉ cho phép khi status = CHO_XAC_NHAN)
     * @throws IllegalArgumentException nếu không thể xác nhận
     */
    void confirmOrder(int orderId, User adminUser);
    
    /**
     * Admin từ chối đơn hàng (chỉ cho phép khi status = CHO_XAC_NHAN)
     * @throws IllegalArgumentException nếu không thể từ chối
     */
    void rejectOrder(int orderId, User adminUser);
    
    /**
     * Admin cập nhật trạng thái đơn hàng (chỉ cho phép từ DANG_CHUAN_BI)
     * @throws IllegalArgumentException nếu không thể cập nhật
     */
    void updateOrderStatusByAdmin(int orderId, String newStatus, User adminUser);
}