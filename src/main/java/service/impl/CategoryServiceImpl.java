package service.impl;

import config.JpaUtil;
import dao.CategoryRepository;
import dao.impl.CategoryRepositoryImpl;
import jakarta.persistence.EntityManager;
import model.Category;
import service.CategoryService;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    
    @Override
    public void insert(Category category) {
        EntityManager em = JpaUtil.em();
        try {
            new CategoryRepositoryImpl(em).insert(category);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void edit(Category category) {
        EntityManager em = JpaUtil.em();
        try {
            new CategoryRepositoryImpl(em).update(category);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(int id) {
        EntityManager em = JpaUtil.em();
        try {
            new CategoryRepositoryImpl(em).delete(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Category get(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepositoryImpl(em).findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Category get(String name) {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepositoryImpl(em).findByName(name);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Category> getAll() {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepositoryImpl(em).findAll();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Category> search(String keyword) {
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepositoryImpl(em).search(keyword);
        } finally {
            em.close();
        }
    }
}
