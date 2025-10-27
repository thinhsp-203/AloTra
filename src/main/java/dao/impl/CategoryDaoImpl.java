package dao.impl;

import java.util.List;
import config.JpaUtil;
import dao.CategoryDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Category;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public void insert(Category category) {
        // Implementation for inserting a category
    }

    @Override
    public void edit(Category category) {
        // Implementation for editing a category
    }

    @Override
    public void delete(int id) {
        // Implementation for deleting a category
    }

    @Override
    public Category get(int id) {
        // Implementation for getting a category by ID
        return null;
    }

    @Override
    public Category get(String name) {
        // Implementation for getting a category by name
        return null;
    }

    @Override
    public List<Category> getAll() {
        EntityManager em = JpaUtil.em();
        String jpql = "SELECT c FROM Category c";
        TypedQuery<Category> query = em.createQuery(jpql, Category.class);
        return query.getResultList();
    }

    @Override
    public List<Category> search(String keyword) {
        // Implementation for searching categories
        return null;
    }
}