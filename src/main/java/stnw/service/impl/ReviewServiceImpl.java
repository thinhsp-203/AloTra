package stnw.service.impl;

import stnw.dao.OrderDao;
import stnw.dao.ProductDao;
import stnw.dao.ReviewDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.ReviewDaoImpl;
import stnw.model.Product;
import stnw.model.Review;
import stnw.model.User;
import stnw.service.ReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReviewServiceImpl implements ReviewService {

    private final OrderDao orderDao = new OrderDaoImpl();
    private final ProductDao productDao = new ProductDaoImpl();
    private final ReviewDao reviewDao = new ReviewDaoImpl();

    @Override
    public boolean submitReview(User user, int productId, int rating, String comment) {
        if (user == null) return false;
        
        boolean hasPurchased = orderDao.hasUserPurchasedProduct(user.getId(), productId);
        if (!hasPurchased) {
            return false;
        }

        Product product = productDao.findById(productId);
        if (product == null) {
            return false;
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedDate(LocalDateTime.now());
        review.setIsApproved(true);
        reviewDao.save(review);

        // Update product rating
        Double avgRating = reviewDao.getAverageRatingByProductId(productId);
        if (avgRating != null) {
            product.setRating(BigDecimal.valueOf(Math.round(avgRating * 10.0) / 10.0));
            productDao.update(product);
        }

        return true;
    }
}

