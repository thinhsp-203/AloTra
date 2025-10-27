package dao.impl;

import java.util.List;
import config.JpaUtil;
import dao.CategoryDao;
import dao.jpa.CategoryRepository;
import jakarta.persistence.EntityManager;
import model.Category;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public void insert(Category category) {
        EntityManager em = JpaUtil.em();
        try {
            new CategoryRepository(em).insert(category);
        } finally {
            em.close();
        }
    }

    @Override
    public void edit(Category category) {
        EntityManager em = JpaUtil.em();
        try {
            new CategoryRepository(em).update(category);
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = JpaUtil.em();
        try {
            new CategoryRepository(em).delete(id);
        } finally {
            em.close();
        }
    }

    @Override
    public Category get(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepository(em).findById(id);
        } finally {
            em.close();
        }
    }

    @Override
    public Category get(String name) {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepository(em).findByName(name);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> getAll() {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepository(em).findAll();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category> search(String keyword) {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepository(em).search(keyword);
        } finally {
            em.close();
        }
    }
}