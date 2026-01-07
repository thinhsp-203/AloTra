package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.VoucherDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import stnw.model.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VoucherDaoImpl implements VoucherDao {
    
    @Override
    public Optional<Voucher> findActiveByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        EntityManager em = JpaUtils.em();
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Voucher> list = em.createQuery(
                            "select v from Voucher v where v.isActive = true and v.code = :c " +
                                    "and (v.start_date is null or v.start_date <= :now) " +
                                    "and (v.end_date   is null or v.end_date   >= :now) " +
                                    "and (v.usage_limit is null or v.used_count < v.usage_limit)", Voucher.class)
                    .setParameter("c", code)
                    .setParameter("now", now)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Voucher> findAvailableVouchers() {
        EntityManager em = JpaUtils.em();
        try {
            LocalDateTime now = LocalDateTime.now();
            return em.createQuery(
                    "select v from Voucher v where v.isActive = true " +
                            "and (v.start_date is null or v.start_date <= :now) " +
                            "and (v.end_date is null or v.end_date >= :now) " +
                            "and (v.usage_limit is null or v.used_count < v.usage_limit) " +
                            "order by v.discount_value desc", Voucher.class)
                    .setParameter("now", now)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Voucher findById(int id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Voucher.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Voucher voucher) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(voucher);
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
    public void update(Voucher voucher) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(voucher);
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
    public void delete(int id) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Voucher voucher = em.find(Voucher.class, id);
            if (voucher != null) {
                em.remove(voucher);
            }
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
    public List<Voucher> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT v FROM Voucher v ORDER BY v.start_date DESC", Voucher.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
