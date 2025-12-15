package service.impl;

import config.JpaUtil;
import dao.BannerRepository;
import dao.CategoryRepository;
import dao.ProductDao;
import dao.impl.ProductDaoImpl;
import jakarta.persistence.EntityManager;
import model.Banner;
import model.Category;
import model.Product;
import service.CatalogService;

import java.util.List;

public class CatalogServiceImpl implements CatalogService {

    private final ProductDao productDao = new ProductDaoImpl();
    private final BannerRepository bannerRepository = new BannerRepository();

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
            return new CategoryRepository(em).findAll();
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
}

