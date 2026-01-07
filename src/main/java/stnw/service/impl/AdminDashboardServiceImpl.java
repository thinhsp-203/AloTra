package stnw.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stnw.dao.OrderDao;
import stnw.dao.ProductDao;
import stnw.dao.UserDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import stnw.service.AdminDashboardService;
import stnw.enums.OrderStatus;

public class AdminDashboardServiceImpl implements AdminDashboardService {
    
    private final OrderDao orderDao = new OrderDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final ProductDao productDao = new ProductDaoImpl();
    
    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
        
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDateTime startOfMonthDateTime = startOfMonth.atStartOfDay();
        
        String completedStatus = OrderStatus.HOAN_THANH.getDisplayName();
        
        // ============ DOANH THU ============
        // QUAN TRỌNG: Chỉ tính doanh thu từ đơn hàng có status = "Hoàn thành"
        // Đơn hàng đã thanh toán nhưng chưa hoàn thành KHÔNG được tính
        // Đơn hàng bị hủy (Hủy bởi khách, Hủy bởi shop, Từ chối) KHÔNG được tính
        
        // Tổng doanh thu (tất cả thời gian) - CHỈ đơn "Hoàn thành"
        stats.put("totalRevenue", orderDao.getTotalRevenue(completedStatus));
        
        // Doanh thu hôm nay - CHỈ đơn "Hoàn thành" hôm nay
        stats.put("revenueToday", orderDao.getRevenueByDateRange(startOfToday, endOfToday, completedStatus));
        
        // Doanh thu tuần này - CHỈ đơn "Hoàn thành" tuần này
        stats.put("revenueWeek", orderDao.getRevenueByDateRange(startOfWeekDateTime, null, completedStatus));
        
        // Doanh thu tháng này - CHỈ đơn "Hoàn thành" tháng này
        stats.put("revenueMonth", orderDao.getRevenueByDateRange(startOfMonthDateTime, null, completedStatus));
        
        // Doanh thu theo giờ trong ngày (cho F&B) - CHỈ đơn "Hoàn thành" hôm nay
        List<Object[]> hourlyRevenue = orderDao.getHourlyRevenueForToday(startOfToday, endOfToday, completedStatus);
        stats.put("hourlyRevenue", hourlyRevenue);
        
        // ============ ĐƠN HÀNG ============
        // Tổng đơn hàng (tất cả thời gian)
        stats.put("totalOrders", orderDao.getTotalOrders());
        
        // Đơn hàng hôm nay - Tổng đơn hàng trong ngày (tất cả trạng thái)
        stats.put("ordersToday", orderDao.getOrdersToday(startOfToday, endOfToday));
        
        // Đơn hàng theo trạng thái
        stats.put("pendingOrders", orderDao.getOrdersByStatus(OrderStatus.CHO_XAC_NHAN.getDisplayName()));
        
        // Đếm đơn đang xử lý: Đang chuẩn bị + Đang giao
        stats.put("processingOrders", orderDao.getProcessingOrders(
            OrderStatus.DANG_CHUAN_BI.getDisplayName(), 
            OrderStatus.DANG_GIAO.getDisplayName()));
        
        stats.put("completedOrders", orderDao.getCompletedOrders(completedStatus));
        
        // Đếm tất cả đơn đã hủy (bao gồm "Hủy Đơn" cũ để tương thích)
        stats.put("cancelledOrders", orderDao.getCancelledOrders(
            OrderStatus.HUY_BOI_KHACH.getDisplayName(),
            OrderStatus.HUY_BOI_SHOP.getDisplayName(),
            OrderStatus.TU_CHOI.getDisplayName(),
            "Hủy Đơn"));
        
        // Đơn hàng mới nhất (10 đơn)
        stats.put("recentOrders", orderDao.getRecentOrders(10));
        
        // ============ KHÁCH HÀNG ============
        // Tổng khách hàng
        stats.put("totalCustomers", userDao.getTotalCustomers(3));
        
        // Khách hàng mới tháng này
        stats.put("newCustomersThisMonth", userDao.getNewCustomersThisMonth(3));
        
        // ============ SẢN PHẨM ============
        // Tổng sản phẩm
        stats.put("totalProducts", productDao.getTotalProducts(true));
        
        // Top 10 sản phẩm bán chạy (tháng này) - CHỈ từ đơn "Hoàn thành"
        stats.put("topProducts", orderDao.getTopProductsByDateRange(
            startOfMonthDateTime, null, completedStatus, 10));
        
        // Top 5 sản phẩm bán chạy (tất cả thời gian) - CHỈ từ đơn "Hoàn thành"
        stats.put("topProductsAllTime", orderDao.getTopProductsByDateRange(
            null, null, completedStatus, 5));
        
        // Thống kê theo danh mục (tháng này) - CHỈ từ đơn "Hoàn thành"
        stats.put("categoryStats", orderDao.getCategoryStats(
            startOfMonthDateTime, null, completedStatus));
        
        // ============ BIỂU ĐỒ ============
        // Doanh thu theo tháng (6 tháng gần nhất) - CHỈ đơn "Hoàn thành"
        stats.put("monthlyRevenue", orderDao.getMonthlyRevenueForDashboard(completedStatus, 6));
        
        // Doanh thu 7 ngày qua - CHỈ đơn "Hoàn thành"
        LocalDateTime startDate7Days = today.minusDays(6).atStartOfDay();
        List<Object[]> last7DaysRevenue = orderDao.getDailyRevenueForDashboard(startDate7Days, completedStatus);
        
        // Xử lý dữ liệu theo ngày
        Map<LocalDate, BigDecimal> dailyMap = new HashMap<>();
        for (var row : last7DaysRevenue) {
            LocalDate date = (LocalDate) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            dailyMap.put(date, amount);
        }
        
        // Chuyển sang List<Object[]> với đủ 7 ngày
        List<Object[]> dailyRevenue = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            dailyRevenue.add(new Object[]{date, dailyMap.getOrDefault(date, BigDecimal.ZERO)});
        }
        stats.put("dailyRevenue", dailyRevenue);
        
        return stats;
    }
}
