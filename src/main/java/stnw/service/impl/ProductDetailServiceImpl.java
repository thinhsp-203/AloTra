package stnw.service.impl;

import stnw.dao.OrderDao;
import stnw.dao.ProductDao;
import stnw.dao.ReviewDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.ReviewDaoImpl;
import stnw.model.Product;
import stnw.model.Review;
import stnw.service.ProductDetailService;

import java.util.List;
import java.util.Objects;

public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductDao productDao = new ProductDaoImpl();
    private final ReviewDao reviewDao = new ReviewDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();

    @Override
    public Product getProduct(int id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> getRelatedProducts(int categoryId, int excludeProductId, int limit) {
        return productDao.findRelatedProducts(categoryId, excludeProductId, limit);
    }

    @Override
    public List<Review> getApprovedReviews(int productId) {
        return reviewDao.findApprovedByProductId(productId);
    }

    @Override
    public boolean canUserReview(Integer userId, int productId, List<Review> existingReviews) {
        if (userId == null) return false;
        boolean hasReviewed = existingReviews.stream()
                .anyMatch(r -> r.getUser() != null && Objects.equals(r.getUser().getId(), userId));
        if (hasReviewed) return false;

        return orderDao.hasUserPurchasedProduct(userId, productId);
    }

    @Override
    public List<Product> findProductsByIds(List<Integer> ids) {
        return productDao.findProductsByIds(ids);
    }
}

