package service.impl;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Product;
import model.WishlistItem;
import service.WishlistService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WishlistServiceImpl implements WishlistService {

    @Override
    public List<WishlistItem> listItems(int userId) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<WishlistItem> query = em.createQuery(
                    "SELECT w FROM WishlistItem w JOIN FETCH w.product WHERE w.user.id = :userId ORDER BY w.addedDate DESC",
                    WishlistItem.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public ToggleResult toggleItem(int userId, int productId) {
        EntityManager em = JpaUtil.em();
        try {
            Product product = em.find(Product.class, productId);
            if (product == null) {
                return new ToggleResult(false, "error", "Sản phẩm không tồn tại.");
            }

            TypedQuery<WishlistItem> query = em.createQuery(
                    "SELECT w FROM WishlistItem w WHERE w.user.id = :userId AND w.product.id = :productId",
                    WishlistItem.class);
            query.setParameter("userId", userId);
            query.setParameter("productId", productId);

            WishlistItem existing = query.getResultStream().findFirst().orElse(null);

            em.getTransaction().begin();
            if (existing != null) {
                em.remove(existing);
                em.getTransaction().commit();
                return new ToggleResult(true, "removed", "Đã xóa khỏi danh sách yêu thích.");
            } else {
                WishlistItem item = new WishlistItem();
                item.setUser(em.getReference(model.User.class, userId));
                item.setProduct(product);
                item.setAddedDate(LocalDateTime.now());
                em.persist(item);
                em.getTransaction().commit();
                return new ToggleResult(true, "added", "Đã thêm vào danh sách yêu thích.");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return new ToggleResult(false, "error", "Lỗi máy chủ: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public Set<Integer> getWishlistProductIds(int userId) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT w.product.id FROM WishlistItem w WHERE w.user.id = :userId", Integer.class);
            query.setParameter("userId", userId);
            return query.getResultStream().collect(Collectors.toSet());
        } finally {
            em.close();
        }
    }
}

