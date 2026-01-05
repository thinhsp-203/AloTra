package stnw.service;

import stnw.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductQueryService {
    List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange, int offset, int limit);
    Map<String, Object> search(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword, int page, int size);
    Product getById(int id);
    List<stnw.model.ProductSize> getSizes(int productId);
    List<stnw.model.Topping> getAvailableToppingsForCategory(String categoryName);
}

