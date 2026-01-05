package stnw.service.impl;

import java.util.List;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import stnw.model.Voucher;
import stnw.service.AdminVoucherService;

public class AdminVoucherServiceImpl implements AdminVoucherService {
    
    @Override
    public List<Voucher> getAllVouchers() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT v FROM Voucher v ORDER BY v.end_date DESC", Voucher.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Voucher getVoucherById(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Voucher.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void saveVoucher(Voucher voucher) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            if (voucher.getVoucher_id() == null) {
                voucher.setUsed_count(0);
                em.persist(voucher);
            } else {
                em.merge(voucher);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi lưu voucher: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteVoucher(int id) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Voucher v = em.find(Voucher.class, id);
            if (v != null) {
                em.remove(v);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xóa voucher: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
