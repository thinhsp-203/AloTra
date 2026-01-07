package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.ReviewDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.Review;

import java.util.List;

public class ReviewDaoImpl implements ReviewDao {

    @Override
    public void save(Review review) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(review);
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
    public List<Review> findByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r WHERE r.product.product_id = :pid ORDER BY r.createdDate DESC",
                    Review.class);
            query.setParameter("pid", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> findApprovedByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r JOIN FETCH r.user WHERE r.product.product_id = :pid AND r.isApproved = true ORDER BY r.createdDate DESC",
                    Review.class);
            query.setParameter("pid", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Double getAverageRatingByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Double> query = em.createQuery(
                    "SELECT AVG(r.rating) FROM Review r WHERE r.product.product_id = :pid AND r.isApproved = true",
                    Double.class);
            query.setParameter("pid", productId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.createQuery("DELETE FROM Review r WHERE r.product.product_id = :productId")
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

    @Override
    public void deleteByUserId(Integer userId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.createQuery("DELETE FROM Review r WHERE r.user.id = :userId")
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
}

