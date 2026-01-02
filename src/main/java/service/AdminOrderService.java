package service;

import java.util.List;
import model.Orders;

public interface AdminOrderService {
    List<Orders> searchOrders(String keyword, String status);
    Orders getOrderDetails(int orderId);
    void updateOrderStatus(int orderId, String newStatus);
    void updatePaymentStatus(int orderId, String paymentStatus);
    void acceptOrder(int orderId); // Nhận đơn: Chờ xác nhận -> Đang chuẩn bị
    void cancelOrder(int orderId, String cancellationReason); // Hủy đơn: Chờ xác nhận -> Đã hủy
}