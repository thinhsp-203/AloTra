package stnw.dao.impl;

import stnw.config.JpaUtil;
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
        EntityManager em = JpaUtil.em();
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
        EntityManager em = JpaUtil.em();
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
}

