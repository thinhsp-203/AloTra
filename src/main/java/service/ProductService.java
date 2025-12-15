package service;

import model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts(int page, int pageSize);
    Product getProductById(int id);
    void saveProduct(Product product); 
    void deleteProduct(int id);
    int getTotalPages(int pageSize);
}