package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.OrderDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Orders createOrder(User user, String fullname, String phone, String address, String note,
                              BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
                              List<CartItem> items) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            
            Orders o = new Orders();
            o.setUser(user);
            o.setFullname(fullname);
            o.setPhone(phone);
            o.setAddress(address);
            o.setNote(note);
            o.setTotal_amount(totalAmount);
            o.setPayment_method(paymentMethod);
            o.setPayment_status(paymentStatus);
            o.setOrder_status(orderStatus);
            o.setCreatedDate(java.time.LocalDateTime.now());
            o.setUpdatedDate(java.time.LocalDateTime.now());

            em.persist(o);
            for (CartItem ci : items) {
                Product p = em.find(Product.class, ci.getProductId());
                OrderDetail d = new OrderDetail();
                d.setOrder(o);
                d.setProduct(p);
                d.setProduct_name(ci.getProductName());
                d.setSize_name(ci.getSizeName());
                d.setQuantity(ci.getQuantity());

                var unit = (ci.getUnitPrice() == null ? BigDecimal.ZERO : ci.getUnitPrice())
                        .add(ci.getSizeAdj() == null ? BigDecimal.ZERO : ci.getSizeAdj())
                        .add(ci.getToppingsCost() == null ? BigDecimal.ZERO : ci.getToppingsCost());
                d.setPrice(unit);
                d.setToppings(ci.getToppingsCsv());
                em.persist(d);
            }
            
            trans.commit();
            return o;
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean hasUserPurchasedProduct(Integer userId, Integer productId) {
        EntityManager em = JpaUtils.em();
        try {
            String ql = "SELECT COUNT(o.order_id) FROM Orders o " +
                    "JOIN o.orderDetails od " +
                    "WHERE o.user.id = :userId AND od.product.product_id = :productId " +
                    "AND o.order_status = 'Hoàn thành'";
            TypedQuery<Long> query = em.createQuery(ql, Long.class);
            query.setParameter("userId", userId);
            query.setParameter("productId", productId);

            try {
                return query.getSingleResult() > 0;
            } catch (jakarta.persistence.NoResultException e) {
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } finally {
            em.close();
        }
    }

    @Override
    public Orders findById(int orderId) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Orders.class, orderId);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Orders order) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(order);
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Orders> findByUserId(int userId, String status, String keyword) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT o FROM Orders o " +
                "LEFT JOIN FETCH o.orderDetails od " +
                "LEFT JOIN FETCH od.product " +
                "WHERE o.user.id = :uid "
            );

            if (status != null && !status.isEmpty() && !status.equals("Tất cả")) {
                jpql.append("AND o.order_status = :status ");
            }
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                jpql.append("AND (o.order_id = :orderId OR o.fullname LIKE :kw OR o.phone LIKE :kw) ");
            }
            
            jpql.append("ORDER BY o.createdDate DESC");

            TypedQuery<Orders> query = em.createQuery(jpql.toString(), Orders.class)
                .setParameter("uid", userId);

            if (status != null && !status.isEmpty() && !status.equals("Tất cả")) {
                query.setParameter("status", status);
            }
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                try {
                    query.setParameter("orderId", Integer.parseInt(keyword));
                } catch (NumberFormatException e) {
                    query.setParameter("orderId", -1);
                }
                query.setParameter("kw", "%" + keyword.trim() + "%");
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Orders findByIdWithDetails(int orderId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Orders> query = em.createQuery(
                "SELECT o FROM Orders o " +
                "LEFT JOIN FETCH o.orderDetails od " +
                "LEFT JOIN FETCH od.product p " +
                "WHERE o.order_id = :orderId",
                Orders.class);
            query.setParameter("orderId", orderId);
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Orders> searchOrders(String keyword, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder("SELECT o FROM Orders o WHERE 1=1");
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                jpql.append(" AND (o.fullname LIKE :kw OR o.phone LIKE :kw)");
            }
            if (status != null && !status.trim().isEmpty()) {
                jpql.append(" AND o.order_status = :status");
            }
            jpql.append(" ORDER BY o.createdDate DESC");
            
            TypedQuery<Orders> query = em.createQuery(jpql.toString(), Orders.class);
            
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
    public Orders findByIdWithDetailsForAdmin(int orderId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Orders> query = em.createQuery(
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
    public long countOrdersByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(od) FROM OrderDetail od WHERE od.product.product_id = :productId",
                Long.class
            ).setParameter("productId", productId).getSingleResult();
            return count != null ? count : 0;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteByUserId(Integer userId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            // Xóa OrderDetail trước (vì có foreign key với Orders)
            em.createQuery("DELETE FROM OrderDetail od WHERE od.order.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            // Xóa Orders
            em.createQuery("DELETE FROM Orders o WHERE o.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteOrderDetailsByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.createQuery("DELETE FROM OrderDetail od WHERE od.product.product_id = :productId")
              .setParameter("productId", productId)
              .executeUpdate();
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    // Reporting methods
    @Override
    public List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), SUM(o.total_amount) " +
                "FROM Orders o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                       "ORDER BY YEAR(o.createdDate) DESC, MONTH(o.createdDate) DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
    public List<Object[]> getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.createdDate, o.total_amount " +
                "FROM Orders o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            List<Object[]> orders = query.getResultList();
            
            // Nhóm theo ngày
            Map<LocalDate, BigDecimal> dailyMap = new HashMap<>();
            for (Object[] row : orders) {
                LocalDateTime orderDate = (LocalDateTime) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                LocalDate date = orderDate.toLocalDate();
                dailyMap.put(date, dailyMap.getOrDefault(date, BigDecimal.ZERO).add(amount));
            }
            
            // Chuyển sang List<Object[]>
            List<Object[]> result = new ArrayList<>();
            for (Map.Entry<LocalDate, BigDecimal> entry : dailyMap.entrySet()) {
                result.add(new Object[]{entry.getKey(), entry.getValue()});
            }
            
            // Sắp xếp theo ngày giảm dần
            result.sort((a, b) -> ((LocalDate) b[0]).compareTo((LocalDate) a[0]));
            
            return result;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopProducts(LocalDateTime startDate, LocalDateTime endDate, String status, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT od.product.product_name, SUM(od.quantity) as total " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY od.product.product_name " +
                       "ORDER BY total DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
    public List<Object[]> getProductRevenue(LocalDateTime startDate, LocalDateTime endDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT od.product.product_name, " +
                "       SUM(od.quantity) as totalQuantity, " +
                "       SUM(od.price * od.quantity) as totalRevenue " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY od.product.product_name " +
                       "ORDER BY totalRevenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
        EntityManager em = JpaUtils.em();
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
    public List<Object[]> getOrderStatsByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.payment_method, COUNT(o.order_id), SUM(o.total_amount) " +
                "FROM Orders o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.payment_method " +
                       "ORDER BY SUM(o.total_amount) DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
    public List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate, String status, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.user.fullname, o.user.email, o.user.phone, " +
                "       COUNT(o.order_id) as orderCount, " +
                "       SUM(o.total_amount) as totalRevenue " +
                "FROM Orders o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.user.id, o.user.fullname, o.user.email, o.user.phone " +
                       "ORDER BY totalRevenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
    public List<Object[]> getTopCustomersByOrderCount(LocalDateTime startDate, LocalDateTime endDate, String status, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o.user.fullname, o.user.email, o.user.phone, " +
                "       COUNT(o.order_id) as orderCount, " +
                "       SUM(o.total_amount) as totalRevenue " +
                "FROM Orders o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY o.user.id, o.user.fullname, o.user.email, o.user.phone " +
                       "ORDER BY orderCount DESC, totalRevenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
                                         LocalDateTime previousStart, LocalDateTime previousEnd, String status) {
        EntityManager em = JpaUtils.em();
        try {
            // Doanh thu kỳ hiện tại
            BigDecimal currentRevenue = (BigDecimal) em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start AND o.createdDate <= :end")
                .setParameter("status", status)
                .setParameter("start", currentStart)
                .setParameter("end", currentEnd)
                .getSingleResult();
            
            // Doanh thu kỳ trước
            BigDecimal previousRevenue = (BigDecimal) em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start AND o.createdDate <= :end")
                .setParameter("status", status)
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
    
    @Override
    public BigDecimal getTotalRevenue(String status) {
        EntityManager em = JpaUtils.em();
        try {
            Object result = em.createQuery(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o WHERE o.order_status = :status")
                .setParameter("status", status)
                .getSingleResult();
            return result != null ? (BigDecimal) result : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    @Override
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT COALESCE(SUM(o.total_amount), 0) FROM Orders o " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            TypedQuery<BigDecimal> query = em.createQuery(jpql.toString(), BigDecimal.class)
                .setParameter("status", status);
            
            if (startDate != null) {
                query.setParameter("startDate", startDate);
            }
            if (endDate != null) {
                query.setParameter("endDate", endDate);
            }
            
            BigDecimal result = query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Orders> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT o FROM Orders o WHERE 1=1 "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("ORDER BY o.createdDate DESC");
            
            TypedQuery<Orders> query = em.createQuery(jpql.toString(), Orders.class);
            
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
    public long getOrdersByStatus(String status) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Orders> getRecentOrders(int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Orders> query = em.createQuery(
                "SELECT o FROM Orders o ORDER BY o.createdDate DESC", Orders.class);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopProductsByDateRange(LocalDateTime startDate, LocalDateTime endDate, String status, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT p.product_name, SUM(od.quantity) as total " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "JOIN od.product p " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY p.product_name " +
                       "ORDER BY total DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
    public List<Object[]> getCategoryStats(LocalDateTime startDate, LocalDateTime endDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder jpql = new StringBuilder(
                "SELECT c.name, SUM(od.quantity) as total, SUM(od.price * od.quantity) as revenue " +
                "FROM OrderDetail od " +
                "JOIN od.order o " +
                "JOIN od.product p " +
                "JOIN p.category c " +
                "WHERE o.order_status = :status "
            );
            
            if (startDate != null) {
                jpql.append("AND o.createdDate >= :startDate ");
            }
            if (endDate != null) {
                jpql.append("AND o.createdDate <= :endDate ");
            }
            
            jpql.append("GROUP BY c.name " +
                       "ORDER BY revenue DESC");
            
            TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class)
                .setParameter("status", status);
            
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
    public List<Object[]> getMonthlyRevenueForDashboard(String status, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Object[]> query = em.createQuery(
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), COALESCE(SUM(o.total_amount), 0) " +
                "FROM Orders o " +
                "WHERE o.order_status = :status " +
                "GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                "ORDER BY YEAR(o.createdDate) DESC, MONTH(o.createdDate) DESC", Object[].class)
                .setParameter("status", status);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getDailyRevenueForDashboard(LocalDateTime startDate, String status) {
        EntityManager em = JpaUtils.em();
        try {
            List<Object[]> orders = em.createQuery(
                "SELECT o.createdDate, o.total_amount FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :startDate", Object[].class)
                .setParameter("status", status)
                .setParameter("startDate", startDate)
                .getResultList();
            
            // Xử lý dữ liệu theo ngày
            Map<LocalDate, BigDecimal> dailyMap = new HashMap<>();
            for (var row : orders) {
                LocalDateTime orderDate = (LocalDateTime) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                LocalDate date = orderDate.toLocalDate();
                dailyMap.put(date, dailyMap.getOrDefault(date, BigDecimal.ZERO).add(amount));
            }
            
            // Chuyển sang List<Object[]>
            List<Object[]> result = new ArrayList<>();
            for (Map.Entry<LocalDate, BigDecimal> entry : dailyMap.entrySet()) {
                result.add(new Object[]{entry.getKey(), entry.getValue()});
            }
            
            return result;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getHourlyRevenueForToday(LocalDateTime startOfToday, LocalDateTime endOfToday, String status) {
        EntityManager em = JpaUtils.em();
        try {
            List<Object[]> todayOrders = em.createQuery(
                "SELECT o.createdDate, o.total_amount FROM Orders o " +
                "WHERE o.order_status = :status " +
                "AND o.createdDate >= :start AND o.createdDate <= :end", Object[].class)
                .setParameter("status", status)
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
            List<Object[]> hourlyRevenue = new ArrayList<>();
            for (int h = 0; h < 24; h++) {
                hourlyRevenue.add(new Object[]{h, hourlyMap.getOrDefault(h, BigDecimal.ZERO)});
            }
            return hourlyRevenue;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getTotalOrders() {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery("SELECT COUNT(o) FROM Orders o", Long.class)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getOrdersToday(LocalDateTime startOfToday, LocalDateTime endOfToday) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM Orders o " +
                "WHERE o.createdDate >= :start AND o.createdDate <= :end", Long.class)
                .setParameter("start", startOfToday)
                .setParameter("end", endOfToday)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getProcessingOrders(String status1, String status2) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status IN (:status1, :status2)", Long.class)
                .setParameter("status1", status1)
                .setParameter("status2", status2)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getCompletedOrders(String status) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getCancelledOrders(String status1, String status2, String status3, String oldStatus) {
        EntityManager em = JpaUtils.em();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(o) FROM Orders o WHERE o.order_status IN (:status1, :status2, :status3, :oldStatus)", Long.class)
                .setParameter("status1", status1)
                .setParameter("status2", status2)
                .setParameter("status3", status3)
                .setParameter("oldStatus", oldStatus)
                .getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
}

