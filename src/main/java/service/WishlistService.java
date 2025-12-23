package service;

import model.WishlistItem;

import java.util.List;
import java.util.Set;

public interface WishlistService {
    record ToggleResult(boolean ok, String status, String message) {}

    List<WishlistItem> listItems(int userId);
    ToggleResult toggleItem(int userId, int productId);
    Set<Integer> getWishlistProductIds(int userId);
}

