package stnw.service.impl;

import java.util.List;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import stnw.model.Topping;
import stnw.service.AdminToppingService;

public class AdminToppingServiceImpl implements AdminToppingService {
    
    @Override
    public List<Topping> getAllToppings() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT t FROM Topping t", Topping.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Topping getToppingById(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Topping.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void saveTopping(Topping topping) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            if (topping.getTopping_id() == null) {
                em.persist(topping);
            } else {
                em.merge(topping);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi lưu topping: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteTopping(int id) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Topping t = em.find(Topping.class, id);
            if (t != null) {
                em.remove(t);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xóa topping: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}