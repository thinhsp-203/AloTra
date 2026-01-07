package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.RewardDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.Reward;
import java.time.LocalDateTime;
import java.util.List;

public class RewardDaoImpl implements RewardDao {

    @Override
    public List<Reward> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Reward> query = em.createQuery(
                "SELECT r FROM Reward r ORDER BY r.points_required ASC", 
                Reward.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Reward> findAllActive() {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Reward> query = em.createQuery(
                "SELECT r FROM Reward r WHERE r.isActive = true ORDER BY r.points_required ASC", 
                Reward.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Reward findById(Integer id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Reward.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Reward reward) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            if (reward.getReward_id() == null) {
                reward.setCreatedDate(LocalDateTime.now());
                reward.setUpdatedDate(LocalDateTime.now());
                em.persist(reward);
            } else {
                reward.setUpdatedDate(LocalDateTime.now());
                em.merge(reward);
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
    public void update(Reward reward) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            reward.setUpdatedDate(LocalDateTime.now());
            em.merge(reward);
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
    public void delete(Integer id) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Reward reward = em.find(Reward.class, id);
            if (reward != null) {
                em.remove(reward);
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
    public long count() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT COUNT(r) FROM Reward r", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}

