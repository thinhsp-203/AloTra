package stnw.service.impl;

import stnw.dao.ProductDao;
import stnw.dao.UserDao;
import stnw.dao.WishlistDao;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import stnw.dao.impl.WishlistDaoImpl;
import stnw.model.Product;
import stnw.model.User;
import stnw.model.WishlistItem;
import stnw.service.WishlistService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class WishlistServiceImpl implements WishlistService {

    private final WishlistDao wishlistDao = new WishlistDaoImpl();
    private final ProductDao productDao = new ProductDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public List<WishlistItem> listItems(int userId) {
        return wishlistDao.findByUserId(userId);
    }

    @Override
    public ToggleResult toggleItem(int userId, int productId) {
        Product product = productDao.findById(productId);
        if (product == null) {
            return new ToggleResult(false, "error", "Sản phẩm không tồn tại.");
        }

        WishlistItem existing = wishlistDao.findByUserIdAndProductId(userId, productId);

        if (existing != null) {
            wishlistDao.delete(existing);
            return new ToggleResult(true, "removed", "Đã xóa khỏi danh sách yêu thích.");
        } else {
            User user = userDao.findById(userId);
            if (user == null) {
                return new ToggleResult(false, "error", "Người dùng không tồn tại.");
            }
            
            WishlistItem item = new WishlistItem();
            item.setUser(user);
            item.setProduct(product);
            item.setAddedDate(LocalDateTime.now());
            wishlistDao.save(item);
            return new ToggleResult(true, "added", "Đã thêm vào danh sách yêu thích.");
        }
    }

    @Override
    public Set<Integer> getWishlistProductIds(int userId) {
        return wishlistDao.findProductIdsByUserId(userId);
    }
}
