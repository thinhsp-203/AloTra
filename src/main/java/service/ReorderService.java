
package service;

import model.*;
import java.util.List;
import java.util.Map;

public interface ReorderService {
    /**
     * @return Map với "addedItems" và "unavailableItems"
     */
    Map<String, Integer> reorder(int userId, int orderId, List<CartItem> currentCart);
}