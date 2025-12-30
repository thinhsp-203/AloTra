package service;

import model.Banner;
import model.Category;
import model.Product;
import model.Promotion;

import java.util.List;

public interface CatalogService {
    List<Product> getFeaturedProducts(int limit);
    List<Product> getNewestProducts(int limit);
    List<Category> getAllCategories();
    List<Banner> getActiveBanners();
    List<Promotion> getActivePromotions(int limit);
}

