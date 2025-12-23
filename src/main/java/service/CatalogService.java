package service;

import model.Banner;
import model.Category;
import model.Product;

import java.util.List;

public interface CatalogService {
    List<Product> getFeaturedProducts(int limit);
    List<Product> getNewestProducts(int limit);
    List<Category> getAllCategories();
    List<Banner> getActiveBanners();
}

