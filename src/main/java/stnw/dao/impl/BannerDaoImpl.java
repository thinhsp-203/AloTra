package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.BannerDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import stnw.model.Banner;

import java.util.List;

public class BannerDaoImpl implements BannerDao {

    @Override
    public List<Banner> findAllActive() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT b FROM Banner b WHERE b.isActive = true ORDER BY b.sortOrder ASC", Banner.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Banner> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT b FROM Banner b ORDER BY b.sortOrder ASC", Banner.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Banner findById(int id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Banner.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Banner banner) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(banner);
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
    public void update(Banner banner) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(banner);
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
            Banner banner = em.find(Banner.class, id);
            if (banner != null) {
                em.remove(banner);
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
    public int getMaxSortOrder() {
        EntityManager em = JpaUtils.em();
        try {
            Object result = em.createQuery("SELECT MAX(b.sortOrder) FROM Banner b").getSingleResult();
            return result != null ? ((Integer) result) : -1;
        } finally {
            em.close();
        }
    }
}
