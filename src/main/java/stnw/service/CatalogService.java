package stnw.service;

import stnw.model.Banner;
import stnw.model.Category;
import stnw.model.Product;
import stnw.model.Promotion;
import stnw.model.Store;

import java.util.List;

public interface CatalogService {
    List<Product> getFeaturedProducts(int limit);
    List<Product> getNewestProducts(int limit);
    List<Category> getAllCategories();
    List<Banner> getActiveBanners();
    List<Promotion> getActivePromotions(int limit);
    List<Store> getActiveStores(int limit);
}

