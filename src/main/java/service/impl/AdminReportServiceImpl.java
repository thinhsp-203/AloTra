package service.impl;

import java.util.List;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import service.AdminReportService;

public class AdminReportServiceImpl implements AdminReportService {
    
    @Override
    public List<Object[]> getMonthlyRevenue() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery(
                "SELECT YEAR(o.createdDate), MONTH(o.createdDate), SUM(o.total_amount) " +
                "FROM Orders o " +
                "GROUP BY YEAR(o.createdDate), MONTH(o.createdDate) " +
                "ORDER BY YEAR(o.createdDate), MONTH(o.createdDate)", Object[].class)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getTopProducts(int limit) {
        EntityManager em = JpaUtil.em();
        try {
            var query = em.createQuery(
                "SELECT d.product.product_name, SUM(d.quantity) " +
                "FROM OrderDetail d " +
                "GROUP BY d.product.product_name " +
                "ORDER BY SUM(d.quantity) DESC", Object[].class);
            
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Object[]> getStockReport() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery(
                "SELECT p.product_name, p.stock FROM Product p " +
                "ORDER BY p.stock ASC", Object[].class)
                .getResultList();
        } finally {
            em.close();
        }
    }
}