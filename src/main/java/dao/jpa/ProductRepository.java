package dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery; // <-- Thêm import này
import model.Product;
import java.util.List;

public class ProductRepository {
    public ProductRepository() {
    }
    public List<Product> findFeatured(int limit, EntityManager em) { 
        return em.createQuery(
                "select p from Product p where p.isActive = true and p.isFeatured = true order by p.createdDate desc", Product.class
        ).setMaxResults(limit).getResultList();
    }
    public List<Product> findNewest(int limit, EntityManager em) {

        return em.createQuery(
                "select p from Product p where p.isActive = true order by p.createdDate desc", Product.class
        ).setMaxResults(limit).getResultList();
    }

    public Product findById(int id, EntityManager em) {
        return em.find(Product.class, id);
    }

    public List<Product> findRelatedProducts(int categoryId, Integer currentProductId, EntityManager em) {
        String ql = "SELECT p FROM Product p " +
                    "WHERE p.category.id = :cid AND p.product_id != :pid " +
                    "AND p.isActive = true " +
                    "ORDER BY p.createdDate DESC"; // Lấy sp liên quan mới nhất

        TypedQuery<Product> query = em.createQuery(ql, Product.class);
        query.setParameter("cid", categoryId);
        query.setParameter("pid", currentProductId);
        query.setMaxResults(4); // Giới hạn 4 sản phẩm liên quan

        return query.getResultList();
    }
}