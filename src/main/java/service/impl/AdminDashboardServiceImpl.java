package service.impl;

import java.util.HashMap;
import java.util.Map;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import service.AdminDashboardService;

public class AdminDashboardServiceImpl implements AdminDashboardService {
    
    @Override
    public Map<String, Object> getDashboardStats() {
        EntityManager em = JpaUtil.em();
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Tổng doanh thu
            Object revenue = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o WHERE o.order_status = 'Hoàn thành'")
                .getSingleResult();
            stats.put("totalRevenue", revenue != null ? revenue : 0);
            
            // Tổng đơn hàng
            Long totalOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o", Long.class)
                .getSingleResult();
            stats.put("totalOrders", totalOrders != null ? totalOrders : 0L);
            
            // Tổng khách hàng
            Long totalCustomers = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.roleid = 3", Long.class)
                .getSingleResult();
            stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0L);
            
            // Tổng sản phẩm
            Long totalProducts = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.isActive = true", Long.class)
                .getSingleResult();
            stats.put("totalProducts", totalProducts != null ? totalProducts : 0L);
            
            // Đơn hàng chờ xử lý
            Long pendingOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status = 'Chờ xác nhận'", Long.class)
                .getSingleResult();
            stats.put("pendingOrders", pendingOrders != null ? pendingOrders : 0L);
            
            // Top 5 sản phẩm bán chạy
            var topProducts = em.createQuery(
                "SELECT d.product.product_name, SUM(d.quantity) as total " +
                "FROM OrderDetail d " +
                "GROUP BY d.product.product_name " +
                "ORDER BY total DESC", Object[].class)
                .setMaxResults(5)
                .getResultList();
            stats.put("topProducts", topProducts);
            
            // Doanh thu theo tháng (6 tháng gần nhất)
            var monthlyRevenue = em.createQuery(
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), COALESCE(SUM(o.total_amount), 0) " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' " +
                "GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                "ORDER BY YEAR(o.createdDate) DESC, MONTH(o.createdDate) DESC", Object[].class)
                .setMaxResults(6)
                .getResultList();
            stats.put("monthlyRevenue", monthlyRevenue);
            
            // Sản phẩm sắp hết hàng
            var lowStock = em.createQuery(
                "SELECT p.product_name, p.stock FROM Product p " +
                "WHERE p.stock < 10 AND p.isActive = true " +
                "ORDER BY p.stock ASC", Object[].class)
                .setMaxResults(10)
                .getResultList();
            stats.put("lowStock", lowStock);
            
            return stats;
            
        } finally {
            em.close();
        }
    }
}
