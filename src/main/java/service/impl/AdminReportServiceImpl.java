package service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import service.AdminReportService;

public class AdminReportServiceImpl implements AdminReportService {
    
    @Override
    public List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), SUM(o.total_amount) " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                       "ORDER BY YEAR(o.createdDate) DESC, MONTH(o.createdDate) DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.em();
        try {
            // Lấy tất cả orders trong khoảng thời gian
            StringBuilder jpql = new StringBuilder(
                "SELECT o.createdDate, o.total_amount " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            List<Object[]> orders = query.getResultList();
            
            // Nhóm theo ngày
            Map<java.time.LocalDate, BigDecimal> dailyMap = new HashMap<>();
            for (Object[] row : orders) {
                LocalDateTime orderDate = (LocalDateTime) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                java.time.LocalDate date = orderDate.toLocalDate();
                dailyMap.put(date, dailyMap.getOrDefault(date, BigDecimal.ZERO).add(amount));
            }
            
            // Chuyển sang List<Object[]>
            List<Object[]> result = new ArrayList<>();
            for (Map.Entry<java.time.LocalDate, BigDecimal> entry : dailyMap.entrySet()) {
                result.add(new Object[]{entry.getKey(), entry.getValue()});
            }
            
            // Sắp xếp theo ngày giảm dần
            result.sort((a, b) -> ((java.time.LocalDate) b[0]).compareTo((java.time.LocalDate) a[0]));
            
            return result;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT od.product.product_name, SUM(od.quantity) as total " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY od.product.product_name " +
                       "ORDER BY total DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getProductRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT od.product.product_name, " +
                "       SUM(od.quantity) as totalQuantity, " +
                "       SUM(od.price * od.quantity) as totalRevenue " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY od.product.product_name " +
                       "ORDER BY totalRevenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getOrderStatsByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.order_status, COUNT(o.order_id), SUM(o.total_amount) " +
                "FROM Orders o " +
                "WHERE 1=1 "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.order_status " +
                       "ORDER BY COUNT(o.order_id) DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getOrderStatsByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.payment_method, COUNT(o.order_id), SUM(o.total_amount) " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.payment_method " +
                       "ORDER BY SUM(o.total_amount) DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.user.fullname, o.user.email, o.user.phone, " +
                "       COUNT(o.order_id) as orderCount, " +
                "       SUM(o.total_amount) as totalRevenue " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.user.id, o.user.fullname, o.user.email, o.user.phone " +
                       "ORDER BY totalRevenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopCustomersByOrderCount(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.user.fullname, o.user.email, o.user.phone, " +
                "       COUNT(o.order_id) as orderCount, " +
                "       SUM(o.total_amount) as totalRevenue " +
                "FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.user.id, o.user.fullname, o.user.email, o.user.phone " +
                       "ORDER BY orderCount DESC, totalRevenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Object[] compareRevenuePeriods(LocalDateTime currentStart, LocalDateTime currentEnd,
                                         LocalDateTime previousStart, LocalDateTime previousEnd) {
        EntityManager em = JpaUtil.em();
        try {
            // Doanh thu kỳ hiện tại
            BigDecimal currentRevenue = (BigDecimal) em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' " +
                "AND o.createdDate >= :start AND o.createdDate <= :end")
                .setParameter("start", currentStart)
                .setParameter("end", currentEnd)
                .getSingleResult();
            
            // Doanh thu kỳ trước
            BigDecimal previousRevenue = (BigDecimal) em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = 'Hoàn thành' " +
                "AND o.createdDate >= :start AND o.createdDate <= :end")
                .setParameter("start", previousStart)
                .setParameter("end", previousEnd)
                .getSingleResult();
            
            // Tính tỷ lệ tăng trưởng
            double growthRate = 0.0;
            if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal diff = currentRevenue.subtract(previousRevenue);
                growthRate = diff.divide(previousRevenue, 4, RoundingMode.HALF_UP)
                                 .multiply(BigDecimal.valueOf(100))
                                 .doubleValue();
            } else if (currentRevenue.compareTo(BigDecimal.ZERO) > 0) {
                growthRate = 100.0; // Tăng 100% nếu kỳ trước = 0
            }
            
            return new Object[]{currentRevenue, previousRevenue, growthRate};
        } finally {
            em.close();
        }
    }
}