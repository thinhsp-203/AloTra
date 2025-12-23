package service;

import model.User;

public interface ReviewService {
    boolean submitReview(User user, int productId, int rating, String comment);
}

