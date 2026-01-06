package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.CategoryDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import stnw.model.Category;

import java.util.List;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public void insert(Category category) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(category);
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
    public void update(Category category) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(category);
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
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
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
    public Category findById(int id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Category findByName(String name) {
        EntityManager em = JpaUtils.em();
        try {
            List<Category> results = em.createQuery(
                            "SELECT c FROM Category c WHERE c.name = :name", Category.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery(
                            "SELECT c FROM Category c ORDER BY c.id", Category.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> search(String keyword) {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery(
                            "SELECT c FROM Category c WHERE c.name LIKE :kw ORDER BY c.name",
                            Category.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }
}

