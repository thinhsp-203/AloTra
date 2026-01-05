package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.BannerRepository;
import stnw.dao.ProductDao;
import stnw.dao.StoreDao;
import stnw.dao.impl.BannerRepositoryImpl;
import stnw.dao.impl.CategoryRepositoryImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.StoreDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.Banner;
import stnw.model.Category;
import stnw.model.Product;
import stnw.model.Promotion;
import stnw.model.Store;
import stnw.service.CatalogService;

import java.util.List;

public class CatalogServiceImpl implements CatalogService {

    private final ProductDao productDao = new ProductDaoImpl();
    private final BannerRepository bannerRepository = new BannerRepositoryImpl();
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

