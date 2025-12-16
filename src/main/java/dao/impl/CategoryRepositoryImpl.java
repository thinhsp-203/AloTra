package dao.impl;

import config.JpaUtil;
import dao.CategoryRepository;
import jakarta.persistence.EntityManager;
import model.Category;

import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepository {
    private final EntityManager em;

    public CategoryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    private EntityManager em() {
        return em != null ? em : JpaUtil.em();
    }

    @Override
    public void insert(Category category) {
        EntityManager manager = em();
        var tx = manager.getTransaction();
        tx.begin();
        try {
            manager.persist(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public void update(Category category) {
        EntityManager manager = em();
        var tx = manager.getTransaction();
        tx.begin();
        try {
            manager.merge(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager manager = em();
        var tx = manager.getTransaction();
        tx.begin();
        try {
            Category category = manager.find(Category.class, id);
            if (category != null) {
                manager.remove(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public Category findById(int id) {
        EntityManager manager = em();
        try {
            return manager.find(Category.class, id);
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public Category findByName(String name) {
        EntityManager manager = em();
        try {
            List<Category> results = manager.createQuery(
                            "SELECT c FROM Category c WHERE c.name = :name", Category.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager manager = em();
        try {
            return manager.createQuery(
                            "SELECT c FROM Category c ORDER BY c.id", Category.class)
                    .getResultList();
        } finally {
            if (manager != em) manager.close();
        }
    }

    @Override
    public List<Category> search(String keyword) {
        EntityManager manager = em();
        try {
            return manager.createQuery(
                            "SELECT c FROM Category c WHERE c.name LIKE :kw ORDER BY c.name",
                            Category.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .getResultList();
        } finally {
            if (manager != em) manager.close();
        }
    }
}

