package stnw.service.impl;

import stnw.dao.CategoryDao;
import stnw.dao.impl.CategoryDaoImpl;
import stnw.model.Category;
import stnw.service.CategoryService;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryDao categoryDao = new CategoryDaoImpl();
    
    @Override
    public void insert(Category category) {
        categoryDao.insert(category);
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
        categoryDao.update(category);
    }
    
    @Override
    public void delete(int id) {
        categoryDao.delete(id);
    }
    
    @Override
    public Category get(int id) {
        return categoryDao.findById(id);
    }
    
    @Override
    public Category get(String name) {
        return categoryDao.findByName(name);
    }
    
    @Override
    public List<Category> getAll() {
        return categoryDao.findAll();
    }
    
    @Override
    public List<Category> search(String keyword) {
        return categoryDao.search(keyword);
    }
}
