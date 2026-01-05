package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.impl.CategoryRepositoryImpl;
import jakarta.persistence.EntityManager;
import stnw.model.Category;
import stnw.service.CategoryService;
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
    public void insertFromParams(String name, String iconPath, Boolean isDrink) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(iconPath);
        category.setIsDrink(isDrink);
        insert(category);
    }
    
    @Override
    public void editFromParams(Integer id, String name, String iconPath, Boolean isDrink) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setIcon(iconPath);
        category.setIsDrink(isDrink);
        edit(category);
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
