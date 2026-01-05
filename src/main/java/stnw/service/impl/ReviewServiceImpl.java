package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.OrderRepository;
import stnw.dao.impl.OrderRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.Product;
import stnw.model.Review;
import stnw.model.User;
import stnw.service.ReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReviewServiceImpl implements ReviewService {

    private final OrderRepository orderRepository = new OrderRepositoryImpl();

    @Override
    public boolean submitReview(User user, int productId, int rating, String comment) {
        if (user == null) return false;
        EntityManager em = JpaUtil.em();
        try {
            boolean hasPurchased = orderRepository.hasUserPurchasedProduct(user.getId(), productId, em);
            if (!hasPurchased) {
                return false;
            }

            Product product = em.find(Product.class, productId);
            if (product == null) {
                return false;
            }

            em.getTransaction().begin();

            Review review = new Review();
            review.setProduct(product);
            review.setUser(user);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedDate(LocalDateTime.now());
            review.setIsApproved(true);
            em.persist(review);

            TypedQuery<Double> avgQuery = em.createQuery(
                    "SELECT AVG(r.rating) FROM Review r WHERE r.product.product_id = :pid AND r.isApproved = true",
                    Double.class);
            avgQuery.setParameter("pid", productId);
            Double avgRating = avgQuery.getSingleResult();

            if (avgRating != null) {
                product.setRating(BigDecimal.valueOf(Math.round(avgRating * 10.0) / 10.0));
                em.merge(product);
            }

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

