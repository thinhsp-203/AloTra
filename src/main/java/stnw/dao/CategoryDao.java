package stnw.dao;

import stnw.model.Category;
import java.util.List;

public interface CategoryDao {
    void insert(Category category);
    void update(Category category);
    void delete(int id);
    Category findById(int id);
    Category findByName(String name);
    List<Category> findAll();
    List<Category> search(String keyword);
}

