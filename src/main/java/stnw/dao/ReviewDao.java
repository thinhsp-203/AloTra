package stnw.dao;

import stnw.model.Review;
import java.util.List;

public interface ReviewDao {
    void save(Review review);
    List<Review> findByProductId(int productId);
    List<Review> findApprovedByProductId(int productId);
    Double getAverageRatingByProductId(int productId);
    void deleteByProductId(int productId);
    void deleteByUserId(Integer userId);
}

