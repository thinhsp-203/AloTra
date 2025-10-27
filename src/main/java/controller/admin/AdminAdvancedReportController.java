package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/admin/dashboard")
public class AdminDashboardReportController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Tổng doanh thu
            Object revenue = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o WHERE o.order_status = 'Hoàn thành'")
                .getSingleResult();
            stats.put("totalRevenue", revenue);
            
            // Tổng đơn hàng
            Long totalOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o", Long.class)
                .getSingleResult();
            stats.put("totalOrders", totalOrders);
            
            // Tổng khách hàng
            Long totalCustomers = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.roleid = 3", Long.class)
                .getSingleResult();
            stats.put("totalCustomers", totalCustomers);
            
            // Tổng sản phẩm
            Long totalProducts = em.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.isActive = true", Long.class)
                .getSingleResult();
            stats.put("totalProducts", totalProducts);
            
            // Đơn hàng chờ xử lý
            Long pendingOrders = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status = 'Chờ xác nhận'", Long.class)
                .getSingleResult();
            stats.put("pendingOrders", pendingOrders);
            
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
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), SUM(o.total_amount) " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' " +
                "GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                "ORDER BY YEAR(o.createdDate) DESC, MONTH(o.createdDate) DESC", Object[].class)
                .setMaxResults(6)
                .getResultList();
            stats.put("monthlyRevenue", monthlyRevenue);
            
            // Sản phẩm sắp hết hàng (< 10)
            var lowStock = em.createQuery(
                "SELECT p.product_name, p.stock FROM Product p " +
                "WHERE p.stock < 10 AND p.isActive = true " +
                "ORDER BY p.stock ASC", Object[].class)
                .setMaxResults(10)
                .getResultList();
            stats.put("lowStock", lowStock);
            
            req.setAttribute("stats", stats);
            req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);
            
        } finally {
            em.close();
        }
    }
}