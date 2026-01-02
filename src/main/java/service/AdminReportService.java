package service;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminReportService {
    /**
     * Lấy doanh thu theo tháng (tất cả hoặc trong khoảng thời gian)
     */
    List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Lấy doanh thu theo ngày trong khoảng thời gian
     */
    List<Object[]> getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Top sản phẩm bán chạy với filter thời gian và limit
     */
    List<Object[]> getTopProducts(LocalDateTime startDate, LocalDateTime endDate, int limit);
    
    /**
     * Doanh thu từng sản phẩm trong khoảng thời gian
     */
    List<Object[]> getProductRevenue(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Thống kê đơn hàng theo trạng thái trong khoảng thời gian
     */
    List<Object[]> getOrderStatsByStatus(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Thống kê đơn hàng theo phương thức thanh toán
     */
    List<Object[]> getOrderStatsByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Top khách hàng theo tổng giá trị đơn hàng
     */
    List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate, int limit);
    
    /**
     * Top khách hàng theo số lượng đơn hàng
     */
    List<Object[]> getTopCustomersByOrderCount(LocalDateTime startDate, LocalDateTime endDate, int limit);
    
    /**
     * So sánh doanh thu giữa 2 kỳ (kỳ này vs kỳ trước)
     * Returns: [currentRevenue, previousRevenue, growthRate]
     */
    Object[] compareRevenuePeriods(LocalDateTime currentStart, LocalDateTime currentEnd, 
                                   LocalDateTime previousStart, LocalDateTime previousEnd);
}