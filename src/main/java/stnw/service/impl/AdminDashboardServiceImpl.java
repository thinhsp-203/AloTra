package stnw.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import stnw.service.AdminDashboardService;
import stnw.utils.OrderStatus;

public class AdminDashboardServiceImpl implements AdminDashboardService {
    
    @Override
    public Map<String, Object> getDashboardStats() {
        EntityManager em = JpaUtil.em();
        try {
            Map<String, Object> stats = new HashMap<>();
            
            LocalDate today = LocalDate.now();
            LocalDateTime startOfToday = today.atStartOfDay();
            LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
            
            LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1); // Monday
            LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
            
            LocalDate startOfMonth = today.withDayOfMonth(1);
            LocalDateTime startOfMonthDateTime = startOfMonth.atStartOfDay();
            
            // ============ DOANH THU ============
            // Tổng doanh thu (tất cả thời gian)
            Object totalRevenue = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o WHERE o.order_status = :status")
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .getSingleResult();
            stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            
            // Doanh thu hôm nay
            Object revenueToday = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start AND o.createdDate <= :end")
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("start", startOfToday)
                .setParameter("end", endOfToday)
                .getSingleResult();
            stats.put("revenueToday", revenueToday != null ? revenueToday : BigDecimal.ZERO);
            
            // Doanh thu tuần này
            Object revenueWeek = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start")
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("start", startOfWeekDateTime)
                .getSingleResult();
            stats.put("revenueWeek", revenueWeek != null ? revenueWeek : BigDecimal.ZERO);
            
            // Doanh thu tháng này
            Object revenueMonth = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start")
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("start", startOfMonthDateTime)
                .getSingleResult();
            stats.put("revenueMonth", revenueMonth != null ? revenueMonth : BigDecimal.ZERO);
            
            // Doanh thu theo giờ trong ngày (cho F&B)
            // Query tất cả orders hôm nay và xử lý trong Java
            var todayOrders = em.createQuery(
                "SELECT o.createdDate, o.total_amount FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start AND o.createdDate <= :end", Object[].class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("start", startOfToday)
                .setParameter("end", endOfToday)
                .getResultList();
            
            // Xử lý dữ liệu theo giờ
            Map<Integer, BigDecimal> hourlyMap = new HashMap<>();
            for (var row : todayOrders) {
                LocalDateTime orderDate = (LocalDateTime) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                int hour = orderDate.getHour();
                hourlyMap.put(hour, hourlyMap.getOrDefault(hour, BigDecimal.ZERO).add(amount));
            }
            
            // Chuyển sang List<Object[]>
            List<Object[]> hourlyRevenue = new java.util.ArrayList<>();
            for (int h = 0; h < 24; h++) {
                hourlyRevenue.add(new Object[]{h, hourlyMap.getOrDefault(h, BigDecimal.ZERO)});
            }
            stats.put("hourlyRevenue", hourlyRevenue);
            
            // ============ ĐƠN HÀNG ============
            // Tổng đơn hàng
            Long totalOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o", Long.class)
                .getSingleResult();
            stats.put("totalOrders", totalOrders != null ? totalOrders : 0L);
            
            // Đơn hàng hôm nay
            Long ordersToday = em.createQuery(
                "SELECT COUNT(o) FROM Orders o " +
                "WHERE o.createdDate >= :start AND o.createdDate <= :end", Long.class)
                .setParameter("start", startOfToday)
                .setParameter("end", endOfToday)
                .getSingleResult();
            stats.put("ordersToday", ordersToday != null ? ordersToday : 0L);
            
            // Đơn hàng theo trạng thái
            // Clear any cache trước khi query
            em.clear();
            
            // Sử dụng OrderStatus enum để tránh lỗi encoding
            Long pendingOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status = :status", Long.class)
                .setParameter("status", OrderStatus.CHO_XAC_NHAN.getDisplayName())
                .getSingleResult();
            stats.put("pendingOrders", pendingOrders != null ? pendingOrders : 0L);
            
            // Đếm đơn đang xử lý: Đang chuẩn bị + Đang giao
            Long processingOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status IN (:status1, :status2)", Long.class)
                .setParameter("status1", OrderStatus.DANG_CHUAN_BI.getDisplayName())
                .setParameter("status2", OrderStatus.DANG_GIAO.getDisplayName())
                .getSingleResult();
            stats.put("processingOrders", processingOrders != null ? processingOrders : 0L);
            
            Long completedOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status = :status", Long.class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .getSingleResult();
            stats.put("completedOrders", completedOrders != null ? completedOrders : 0L);
            
            // Đếm tất cả đơn đã hủy (bao gồm "Hủy Đơn" cũ để tương thích)
            Long cancelledOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status IN (:status1, :status2, :status3, :oldStatus)", Long.class)
                .setParameter("status1", OrderStatus.HUY_BOI_KHACH.getDisplayName())
                .setParameter("status2", OrderStatus.HUY_BOI_SHOP.getDisplayName())
                .setParameter("status3", OrderStatus.TU_CHOI.getDisplayName())
                .setParameter("oldStatus", "Hủy Đơn")
                .getSingleResult();
            stats.put("cancelledOrders", cancelledOrders != null ? cancelledOrders : 0L);
            
            // Đơn hàng mới nhất (10 đơn)
            var recentOrders = em.createQuery(
                "SELECT o FROM Orders o " +
                "ORDER BY o.createdDate DESC", stnw.model.Orders.class)
                .setMaxResults(10)
                .getResultList();
            stats.put("recentOrders", recentOrders);
            
            // ============ KHÁCH HÀNG ============
            // Tổng khách hàng
            Long totalCustomers = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.roleid = 3", Long.class)
                .getSingleResult();
            stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0L);
            
            // Khách hàng mới tháng này
            Long newCustomersThisMonth = em.createQuery(
                "SELECT COUNT(u) FROM User u " +
                "WHERE u.roleid = 3 " +
                "AND YEAR(u.createdDate) = YEAR(CURRENT_DATE) " +
                "AND MONTH(u.createdDate) = MONTH(CURRENT_DATE)", Long.class)
                .getSingleResult();
            stats.put("newCustomersThisMonth", newCustomersThisMonth != null ? newCustomersThisMonth : 0L);
            
            // ============ SẢN PHẨM ============
            // Tổng sản phẩm
            Long totalProducts = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.isActive = true", Long.class)
                .getSingleResult();
            stats.put("totalProducts", totalProducts != null ? totalProducts : 0L);
            
            // Top 10 sản phẩm bán chạy (tháng này)
            var topProducts = em.createQuery(
                "SELECT p.product_name, SUM(od.quantity) as total " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "JOIN od.product p " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :startMonth " +
                "GROUP BY p.product_name " +
                "ORDER BY total DESC", Object[].class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("startMonth", startOfMonthDateTime)
                .setMaxResults(10)
                .getResultList();
            stats.put("topProducts", topProducts);
            
            // Top 5 sản phẩm bán chạy (tất cả thời gian)
            var topProductsAllTime = em.createQuery(
                "SELECT p.product_name, SUM(od.quantity) as total " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "JOIN od.product p " +
                "WHERE o.order_status = :status " +
                "GROUP BY p.product_name " +
                "ORDER BY total DESC", Object[].class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setMaxResults(5)
                .getResultList();
            stats.put("topProductsAllTime", topProductsAllTime);
            
            // Thống kê theo danh mục (tháng này)
            var categoryStats = em.createQuery(
                "SELECT c.name, SUM(od.quantity) as total, SUM(od.price * od.quantity) as revenue " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "JOIN od.product p " +
                "JOIN p.category c " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :startMonth " +
                "GROUP BY c.name " +
                "ORDER BY revenue DESC", Object[].class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("startMonth", startOfMonthDateTime)
                .getResultList();
            stats.put("categoryStats", categoryStats);
            
            // ============ BIỂU ĐỒ ============
            // Doanh thu theo tháng (6 tháng gần nhất)
            var monthlyRevenue = em.createQuery(
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), COALESCE(SUM(o.total_amount), 0) " +
                "FROM Orders o " +
                "WHERE o.order_status = :status " +
                "GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                "ORDER BY YEAR(o.createdDate) DESC, MONTH(o.createdDate) DESC", Object[].class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setMaxResults(6)
                .getResultList();
            stats.put("monthlyRevenue", monthlyRevenue);
            
            // Doanh thu 7 ngày qua
            // Query tất cả orders 7 ngày qua và xử lý trong Java
            LocalDateTime startDate7Days = today.minusDays(6).atStartOfDay();
            var last7DaysOrders = em.createQuery(
                "SELECT o.createdDate, o.total_amount FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :startDate", Object[].class)
                .setParameter("status", OrderStatus.HOAN_THANH.getDisplayName())
                .setParameter("startDate", startDate7Days)
                .getResultList();
            
            // Xử lý dữ liệu theo ngày
            Map<LocalDate, BigDecimal> dailyMap = new HashMap<>();
            for (var row : last7DaysOrders) {
                LocalDateTime orderDate = (LocalDateTime) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                LocalDate date = orderDate.toLocalDate();
                dailyMap.put(date, dailyMap.getOrDefault(date, BigDecimal.ZERO).add(amount));
            }
            
            // Chuyển sang List<Object[]> với đủ 7 ngày
            List<Object[]> dailyRevenue = new java.util.ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate date = today.minusDays(6 - i);
                dailyRevenue.add(new Object[]{date, dailyMap.getOrDefault(date, BigDecimal.ZERO)});
            }
            stats.put("dailyRevenue", dailyRevenue);
            
            return stats;
            
        } finally {
            em.close();
        }
    }
}
