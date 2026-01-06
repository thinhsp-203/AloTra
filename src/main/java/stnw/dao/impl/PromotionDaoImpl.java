package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.PromotionDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.Promotion;

import java.util.List;

public class PromotionDaoImpl implements PromotionDao {

    @Override
    public List<Promotion> findAllActive() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT p FROM Promotion p WHERE p.isActive = true ORDER BY p.createdDate DESC", Promotion.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT p FROM Promotion p ORDER BY p.createdDate DESC", Promotion.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Promotion findById(int id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Promotion.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Promotion promotion) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(promotion);
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
    public void update(Promotion promotion) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(promotion);
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
            Promotion promotion = em.find(Promotion.class, id);
            if (promotion != null) {
                em.remove(promotion);
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
    public void delete(Promotion promotion) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Promotion managedPromotion = em.find(Promotion.class, promotion.getId());
            if (managedPromotion != null) {
                em.remove(managedPromotion);
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
    public List<Promotion> findRelatedPromotions(int excludeId, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                "SELECT p FROM Promotion p WHERE p.isActive = true AND p.id != :excludeId ORDER BY p.createdDate DESC", 
                Promotion.class
            );
            query.setParameter("excludeId", excludeId);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

