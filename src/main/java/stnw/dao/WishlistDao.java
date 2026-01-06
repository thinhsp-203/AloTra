package stnw.dao;

import stnw.model.WishlistItem;
import java.util.List;
import java.util.Set;

public interface WishlistDao {
    List<WishlistItem> findByUserId(int userId);
    WishlistItem findByUserIdAndProductId(int userId, int productId);
    void save(WishlistItem item);
    void delete(WishlistItem item);
    void deleteByProductId(int productId);
    void deleteByUserId(Integer userId);
    Set<Integer> findProductIdsByUserId(int userId);
}

