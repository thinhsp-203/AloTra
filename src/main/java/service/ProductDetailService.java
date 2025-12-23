package service;

import model.Product;
import model.Review;

import java.util.List;

public interface ProductDetailService {
    Product getProduct(int id);
    List<Product> getRelatedProducts(int categoryId, int excludeProductId, int limit);
    List<Review> getApprovedReviews(int productId);
    boolean canUserReview(Integer userId, int productId, List<Review> existingReviews);
    List<Product> findProductsByIds(List<Integer> ids);
}

