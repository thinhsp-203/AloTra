package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.AboutUsDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.AboutUs;

import java.util.List;

public class AboutUsDaoImpl implements AboutUsDao {
    
    @Override
    public List<AboutUs> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<AboutUs> query = em.createQuery("SELECT a FROM AboutUs a ORDER BY a.sortOrder ASC, a.id DESC", AboutUs.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public AboutUs findById(Integer id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(AboutUs.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void save(AboutUs aboutUs) {
        EntityManager em = JpaUtils.em();
        try {
            em.getTransaction().begin();
            em.persist(aboutUs);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void update(AboutUs aboutUs) {
        EntityManager em = JpaUtils.em();
        try {
            em.getTransaction().begin();
            em.merge(aboutUs);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(Integer id) {
        EntityManager em = JpaUtils.em();
        try {
            em.getTransaction().begin();
            AboutUs aboutUs = em.find(AboutUs.class, id);
            if (aboutUs != null) {
                em.remove(aboutUs);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<AboutUs> findAllActiveOrderBySortOrder() {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<AboutUs> query = em.createQuery(
                "SELECT a FROM AboutUs a WHERE a.isActive = true ORDER BY a.sortOrder ASC, a.createdDate DESC", 
                AboutUs.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

