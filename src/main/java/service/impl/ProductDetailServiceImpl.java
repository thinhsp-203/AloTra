package service.impl;

import config.JpaUtil;
import dao.OrderRepository;
import dao.ProductDao;
import dao.impl.OrderRepositoryImpl;
import dao.impl.ProductDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Product;
import model.Review;
import service.ProductDetailService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDao productDao = new ProductDaoImpl();
    private final OrderRepository orderRepository = new OrderRepositoryImpl();

    @Override
    public Product getProduct(int id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> getRelatedProducts(int categoryId, int excludeProductId, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.category.id = :cid AND p.product_id <> :pid AND p.isActive = true ORDER BY p.createdDate DESC",
                    Product.class);
            query.setParameter("cid", categoryId);
            query.setParameter("pid", excludeProductId);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Review> getApprovedReviews(int productId) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r JOIN FETCH r.user WHERE r.product.product_id = :pid AND r.isApproved = true ORDER BY r.createdDate DESC",
                    Review.class);
            query.setParameter("pid", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean canUserReview(Integer userId, int productId, List<Review> existingReviews) {
        if (userId == null) return false;
        boolean hasReviewed = existingReviews.stream()
                .anyMatch(r -> r.getUser() != null && Objects.equals(r.getUser().getId(), userId));
        if (hasReviewed) return false;

        EntityManager em = JpaUtil.em();
        try {
            return orderRepository.hasUserPurchasedProduct(userId, productId, em);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findProductsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.product_id IN :ids",
                    Product.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

