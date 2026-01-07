package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.PointTransactionDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.PointTransaction;
import java.time.LocalDateTime;
import java.util.List;

public class PointTransactionDaoImpl implements PointTransactionDao {

    @Override
    public void save(PointTransaction transaction) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            transaction.setCreatedDate(LocalDateTime.now());
            em.persist(transaction);
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
    public List<PointTransaction> findByUserId(Integer userId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<PointTransaction> query = em.createQuery(
                "SELECT pt FROM PointTransaction pt WHERE pt.user.id = :userId ORDER BY pt.createdDate DESC",
                PointTransaction.class
            );
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<PointTransaction> findByUserIdOrderByDateDesc(Integer userId) {
        return findByUserId(userId); // Same implementation
    }

    @Override
    public PointTransaction findById(Integer id) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<PointTransaction> query = em.createQuery(
                "SELECT pt FROM PointTransaction pt LEFT JOIN FETCH pt.reward WHERE pt.id = :id",
                PointTransaction.class
            );
            query.setParameter("id", id);
            PointTransaction transaction = query.getSingleResult();
            
            // Initialize reward proxy if exists (to avoid LazyInitializationException)
            if (transaction != null && transaction.getReward() != null) {
                transaction.getReward().getName(); // Force initialization
            }
            
            return transaction;
        } catch (jakarta.persistence.NoResultException e) {
            return null;
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
            em.createQuery("DELETE FROM PointTransaction pt WHERE pt.user.id = :userId")
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

