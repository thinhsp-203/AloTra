package service.impl;

import config.JpaUtil;
import dao.ProductDao;
import dao.ProductQueryRepository;
import dao.impl.ProductDaoImpl;
import dao.impl.ProductQueryRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Product;
import model.ProductSize;
import model.Topping;
import service.ProductQueryService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductDao productDao = new ProductDaoImpl();

    @Override
    public List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange, int offset, int limit) {
        ProductQueryRepository repo = new ProductQueryRepositoryImpl();
        return repo.findProducts(cateId, keyword, sortBy, priceRange, offset, limit);
    }

    @Override
    public Map<String, Object> search(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword, int page, int size) {
        ProductQueryRepository repo = new ProductQueryRepositoryImpl();
        return repo.search(cateId, suppId, minPrice, maxPrice, keyword, page, size);
    }

    @Override
    public Product getById(int id) {
        return productDao.findById(id);
    }

    @Override
    public List<ProductSize> getSizes(int productId) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<ProductSize> query = em.createQuery(
                    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid ORDER BY ps.size_name",
                    ProductSize.class);
            query.setParameter("pid", productId);
            List<ProductSize> sizes = query.getResultList();
            if (sizes.isEmpty()) {
                ProductSize defaultSize = new ProductSize();
                defaultSize.setSize_name("Mặc định");
                defaultSize.setPrice_adjustment(BigDecimal.ZERO);
                return Collections.singletonList(defaultSize);
            }
            return sizes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Topping> getAvailableToppingsForCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return List.of();
        }
        if (!categoryName.toLowerCase().contains("trà")) {
            return List.of();
        }
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT t FROM Topping t WHERE t.isAvailable = true ORDER BY t.topping_name", Topping.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}

