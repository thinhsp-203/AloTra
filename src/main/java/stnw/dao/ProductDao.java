package stnw.dao;

import stnw.model.Product;
import java.util.List;

public interface ProductDao {
    List<Product> findAll(int offset, int limit);
    Product findById(int id);
    void insert(Product product);
    void update(Product product);
    void delete(int id);
    long count();
    List<Product> findByCategory(int categoryId);
	void save(Product product);
    List<Product> findFeatured(int limit);
    List<Product> findNewest(int limit);
}