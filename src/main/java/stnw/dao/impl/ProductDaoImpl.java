package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.ProductDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.Product;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public List<Product> findAll(int offset, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
            // Disable query cache để đảm bảo lấy dữ liệu mới nhất từ DB
            query.setHint("jakarta.persistence.cache.retrieveMode", "BYPASS");
            query.setHint("jakarta.persistence.cache.storeMode", "BYPASS");
            if (offset >= 0 && limit > 0) {
                query.setFirstResult(offset);
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Product findById(int id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void insert(Product product) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(product);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Product product) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(product);
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int id) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
                // Mặc định dùng soft delete cho các luồng cũ
                p.setIsActive(false);
                em.merge(p);
            }
            trans.commit();
        } catch (Exception e) {
            trans.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Product> findByCategory(int categoryId) {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.category.id = :cid", Product.class)
                     .setParameter("cid", categoryId)
                     .getResultList();
        } finally {
            em.close();
        }
    }

	@Override
	public void save(Product product) {
		EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            if (product.getProduct_id() == null) {
                em.persist(product);
            } else {
                em.merge(product);
            }
            trans.commit();
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
	}	

    @Override
    public List<Product> findFeatured(int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.isActive = true AND p.isFeatured = true ORDER BY p.createdDate DESC",
                    Product.class);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findNewest(int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdDate DESC",
                    Product.class);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findRelatedProducts(int categoryId, int excludeProductId, int limit) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.category.id = :cid AND p.product_id <> :pid AND p.isActive = true ORDER BY p.createdDate DESC",
                    Product.class);
            query.setParameter("cid", categoryId);
            query.setParameter("pid", excludeProductId);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product> findProductsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p WHERE p.product_id IN :ids",
                    Product.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteProduct(int productId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            System.out.println("[ProductDao] Bắt đầu xóa Product ID: " + productId);
            trans.begin();
            
            Product p = em.find(Product.class, productId);
            if (p == null) {
                trans.rollback();
                throw new IllegalArgumentException("Sản phẩm không tồn tại!");
            }
            
            System.out.println("[ProductDao] Tìm thấy Product: " + p.getProduct_name());
            
            // Xóa tất cả các bản ghi liên quan trong CÙNG MỘT TRANSACTION
            // Thứ tự xóa: OrderDetail -> Review -> WishlistItem -> ProductSize -> ViewHistory -> Product
            
            // 1. Xóa OrderDetail (phải xóa trước vì có FK đến Product)
            int deletedOrderDetails = em.createQuery("DELETE FROM OrderDetail od WHERE od.product.product_id = :productId")
              .setParameter("productId", productId)
              .executeUpdate();
            System.out.println("[ProductDao] Đã xóa " + deletedOrderDetails + " OrderDetail");
            
            // 2. Xóa Review
            int deletedReviews = em.createQuery("DELETE FROM Review r WHERE r.product.product_id = :productId")
              .setParameter("productId", productId)
              .executeUpdate();
            System.out.println("[ProductDao] Đã xóa " + deletedReviews + " Review");
            
            // 3. Xóa WishlistItem
            int deletedWishlist = em.createQuery("DELETE FROM WishlistItem wi WHERE wi.product.product_id = :productId")
              .setParameter("productId", productId)
              .executeUpdate();
            System.out.println("[ProductDao] Đã xóa " + deletedWishlist + " WishlistItem");
            
            // 4. Xóa ProductSize
            int deletedSizes = em.createQuery("DELETE FROM ProductSize ps WHERE ps.product.product_id = :productId")
              .setParameter("productId", productId)
              .executeUpdate();
            System.out.println("[ProductDao] Đã xóa " + deletedSizes + " ProductSize");
            
            // 5. Xóa ViewHistory (nếu bảng tồn tại)
            try {
                int deletedViews = em.createQuery("DELETE FROM ViewHistory vh WHERE vh.product.product_id = :productId")
                  .setParameter("productId", productId)
                  .executeUpdate();
                System.out.println("[ProductDao] Đã xóa " + deletedViews + " ViewHistory");
            } catch (Exception ignore) {
                // Bảng ViewHistory có thể không tồn tại trong schema hiện tại
                System.out.println("[ProductDao] ViewHistory không tồn tại hoặc đã được xóa");
            }
            
            // 6. Xóa Product (sau khi đã xóa tất cả các bảng con)
            // Sử dụng native DELETE query để đảm bảo xóa trực tiếp trong DB
            System.out.println("[ProductDao] Xóa Product bằng native DELETE query...");
            int deletedCount = em.createNativeQuery("DELETE FROM Product WHERE product_id = ?")
              .setParameter(1, productId)
              .executeUpdate();
            System.out.println("[ProductDao] Số lượng Product đã xóa: " + deletedCount);
            
            if (deletedCount == 0) {
                trans.rollback();
                throw new RuntimeException("Không thể xóa Product. Có thể sản phẩm không tồn tại hoặc đã bị xóa.");
            }
            
            // Flush để đảm bảo thay đổi được ghi vào DB ngay lập tức
            em.flush();
            System.out.println("[ProductDao] Flush completed");
            
            trans.commit();
            System.out.println("[ProductDao] Transaction committed thành công");
            
            // Kiểm tra xem Product đã thực sự bị xóa chưa (sử dụng native query để tránh cache)
            EntityManager checkEm = JpaUtils.em();
            try {
                // Sử dụng native query để kiểm tra trực tiếp trong DB
                // COUNT(*) có thể trả về Integer, Long, hoặc BigInteger tùy database
                Object countResult = checkEm.createNativeQuery(
                    "SELECT COUNT(*) FROM Product WHERE product_id = ?")
                    .setParameter(1, productId)
                    .getSingleResult();
                
                // Xử lý nhiều kiểu dữ liệu có thể trả về
                long count = 0;
                if (countResult instanceof Number) {
                    count = ((Number) countResult).longValue();
                } else {
                    count = Long.parseLong(countResult.toString());
                }
                
                if (count > 0) {
                    System.err.println("[ProductDao] CẢNH BÁO: Product vẫn còn tồn tại trong DB! ID: " + productId + ", Count: " + count);
                    // Thử xóa lại bằng native query với transaction riêng
                    EntityTransaction checkTrans = checkEm.getTransaction();
                    try {
                        checkTrans.begin();
                        int nativeDeleted = checkEm.createNativeQuery(
                            "DELETE FROM Product WHERE product_id = ?")
                            .setParameter(1, productId)
                            .executeUpdate();
                        checkTrans.commit();
                        System.out.println("[ProductDao] Đã xóa lại bằng native query: " + nativeDeleted + " bản ghi");
                    } catch (Exception e) {
                        if (checkTrans.isActive()) {
                            checkTrans.rollback();
                        }
                        System.err.println("[ProductDao] Lỗi khi xóa lại: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("[ProductDao] Xác nhận: Product đã bị xóa khỏi DB. ID: " + productId);
                }
            } finally {
                checkEm.close();
            }
        } catch (IllegalArgumentException e) {
            if (trans.isActive()) {
                trans.rollback();
                System.err.println("[ProductDao] Transaction rolled back do IllegalArgumentException");
            }
            throw e;
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback();
                System.err.println("[ProductDao] Transaction rolled back do Exception: " + e.getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa sản phẩm: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public long getTotalProducts(boolean isActive) {
        EntityManager em = JpaUtils.em();
        try {
            String jpql = "SELECT COUNT(p) FROM Product p";
            if (isActive) {
                jpql += " WHERE p.isActive = true";
            }
            Long count = em.createQuery(jpql, Long.class).getSingleResult();
            return count != null ? count : 0L;
        } finally {
            em.close();
        }
    }
}