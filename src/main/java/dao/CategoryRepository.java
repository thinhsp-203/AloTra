package dao;

import jakarta.persistence.EntityManager;
import model.Category;
import java.util.List;

public class CategoryRepository {
    private final EntityManager em;
    
    public CategoryRepository(EntityManager em) {
        this.em = em;
    }
    
    public void insert(Category category) {
        var tx = em.getTransaction();
        tx.begin();
        try {
            em.persist(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    
    public void update(Category category) {
        var tx = em.getTransaction();
        tx.begin();
        try {
            em.merge(category);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    
    public void delete(int id) {
        var tx = em.getTransaction();
        tx.begin();
        try {
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
    
    public Category findById(int id) {
        return em.find(Category.class, id);
    }
    
    public Category findByName(String name) {
        List<Category> results = em.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name", Category.class)
            .setParameter("name", name)
            .setMaxResults(1)
            .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<Category> findAll() {
        return em.createQuery(
            "SELECT c FROM Category c ORDER BY c.id", Category.class)
            .getResultList();
    }
    
    public List<Category> search(String keyword) {
        return em.createQuery(
            "SELECT c FROM Category c WHERE c.name LIKE :kw ORDER BY c.name", 
            Category.class)
            .setParameter("kw", "%" + keyword + "%")
            .getResultList();
    }
}
