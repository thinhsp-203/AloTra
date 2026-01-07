package stnw.service.impl;

import stnw.dao.BannerDao;
import stnw.dao.CategoryDao;
import stnw.dao.ProductDao;
import stnw.dao.PromotionDao;
import stnw.dao.StoreDao;
import stnw.dao.impl.BannerDaoImpl;
import stnw.dao.impl.CategoryDaoImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.PromotionDaoImpl;
import stnw.dao.impl.StoreDaoImpl;
import stnw.model.Banner;
import stnw.model.Category;
import stnw.model.Product;
import stnw.model.Promotion;
import stnw.model.Store;
import stnw.service.CatalogService;

import java.util.List;

public class CatalogServiceImpl implements CatalogService {

    private final ProductDao productDao = new ProductDaoImpl();
    private final CategoryDao categoryDao = new CategoryDaoImpl();
    private final PromotionDao promotionDao = new PromotionDaoImpl();
    private final BannerDao bannerDao = new BannerDaoImpl();
    private final StoreDao storeDao = new StoreDaoImpl();

    @Override
    public List<Product> getFeaturedProducts(int limit) {
        return productDao.findFeatured(limit);
    }

    @Override
    public List<Product> getNewestProducts(int limit) {
        return productDao.findNewest(limit);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public List<Banner> getActiveBanners() {
        return bannerDao.findAllActive();
    }

    @Override
    public List<Promotion> getActivePromotions(int limit) {
        List<Promotion> allPromotions = promotionDao.findAllActive();
        if (limit > 0 && allPromotions.size() > limit) {
            return allPromotions.subList(0, limit);
        }
        return allPromotions;
    }

    @Override
    public List<Store> getActiveStores(int limit) {
        List<Store> allStores = storeDao.findAll();
        if (limit > 0 && allStores.size() > limit) {
            return allStores.subList(0, limit);
        }
        return allStores;
    }
}

