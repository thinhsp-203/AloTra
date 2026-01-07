package stnw.dao;
import stnw.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao {
    Orders createOrder(User user, String fullname, String phone, String address, String note,
                       BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
                       List<CartItem> items);

    boolean hasUserPurchasedProduct(Integer userId, Integer productId);
    Orders findById(int orderId);
    void update(Orders order);
    List<Orders> findByUserId(int userId, String status, String keyword);
    Orders findByIdWithDetails(int orderId);
    List<Orders> searchOrders(String keyword, String status);
    Orders findByIdWithDetailsForAdmin(int orderId);
    long countOrdersByProductId(int productId);
    void deleteByUserId(Integer userId);
    void deleteOrderDetailsByProductId(int productId);
    
    // Reporting methods
    List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Object[]> getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Object[]> getTopProducts(LocalDateTime startDate, LocalDateTime endDate, String status, int limit);
    List<Object[]> getProductRevenue(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Object[]> getOrderStatsByStatus(LocalDateTime startDate, LocalDateTime endDate);
    List<Object[]> getOrderStatsByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate, String status, int limit);
    List<Object[]> getTopCustomersByOrderCount(LocalDateTime startDate, LocalDateTime endDate, String status, int limit);
    Object[] compareRevenuePeriods(LocalDateTime currentStart, LocalDateTime currentEnd, 
                                   LocalDateTime previousStart, LocalDateTime previousEnd, String status);
    BigDecimal getTotalRevenue(String status);
    BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Orders> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    long getOrdersByStatus(String status);
    List<Orders> getRecentOrders(int limit);
    List<Object[]> getTopProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate, String status, int limit);
    List<Object[]> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate, String status);
    List<Object[]> getMonthlyRevenueForDashboard(String status, int limit);
    List<Object[]> getDailyRevenueForDashboard(LocalDateTime startDate, String status);
    List<Object[]> getHourlyRevenueForToday(LocalDateTime startOfToday, LocalDateTime endOfToday, String status);
    long getTotalOrders();
    long getOrdersToday(LocalDateTime startOfToday, LocalDateTime endOfToday);
    long getProcessingOrders(String status1, String status2);
    long getCompletedOrders(String status);
    long getCancelledOrders(String status1, String status2, String status3, String oldStatus);
}

