package service.impl;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;
import service.*;

import java.time.LocalDateTime;
import java.util.*;

public class AdminOrderServiceImpl implements AdminOrderService {
    
    @Override
    public List<Orders> searchOrders(String keyword, String status) {
        EntityManager em = JpaUtil.em();
        try {
            StringBuilder jpql = new StringBuilder("SELECT o FROM Orders o WHERE 1=1");
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                jpql.append(" AND (o.fullname LIKE :kw OR o.phone LIKE :kw)");
            }
            if (status != null && !status.trim().isEmpty()) {
                jpql.append(" AND o.order_status = :status");
            }
            jpql.append(" ORDER BY o.createdDate DESC");
            
            var query = em.createQuery(jpql.toString(), Orders.class);
            
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
    public Orders getOrderDetails(int orderId) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Orders.class, orderId);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updateOrderStatus(int orderId, String newStatus) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order != null) {
                order.setOrder_status(newStatus);
                order.setUpdatedDate(LocalDateTime.now());
                em.merge(order);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật trạng thái: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void updatePaymentStatus(int orderId, String paymentStatus) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Orders order = em.find(Orders.class, orderId);
            if (order != null) {
                order.setPayment_status(paymentStatus);
                order.setUpdatedDate(LocalDateTime.now());
                em.merge(order);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật thanh toán: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}