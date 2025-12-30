package service.impl;

import config.JpaUtil;
import dao.BannerRepository;
import dao.PromotionRepository;
import dao.ProductDao;
import dao.StoreDao;
import dao.impl.BannerRepositoryImpl;
import dao.impl.PromotionRepositoryImpl;
import dao.impl.CategoryRepositoryImpl;
import dao.impl.ProductDaoImpl;
import dao.impl.StoreDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Banner;
import model.Category;
import model.Product;
import model.Promotion;
import model.Store;
import service.CatalogService;

import java.util.List;

public class CatalogServiceImpl implements CatalogService {

    private final ProductDao productDao = new ProductDaoImpl();
    private final BannerRepository bannerRepository = new BannerRepositoryImpl();
    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();
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
        EntityManager em = JpaUtil.em();
        try {
            return new CategoryRepositoryImpl(em).findAll();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Banner> getActiveBanners() {
        EntityManager em = JpaUtil.em();
        try {
            return bannerRepository.findAllActive(em);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> getActivePromotions(int limit) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                "SELECT p FROM Promotion p WHERE p.isActive = true ORDER BY p.createdDate DESC", 
                Promotion.class
            );
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
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

