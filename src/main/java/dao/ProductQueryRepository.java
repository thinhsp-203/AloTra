package dao;

import model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductQueryRepository {
    List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange);
    List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange, int offset, int limit);
    Map<String, Object> search(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword, int page, int size);
    long count(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword);
}