package dao.jpa;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.Product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductQueryRepository {
    private final EntityManager em;

    public ProductQueryRepository(EntityManager em) {
        this.em = em;
    }

    // HÀM MỚI (TỪ TÔI) ĐỂ LỌC VÀ SẮP XẾP
    public List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder qlString = new StringBuilder("SELECT p FROM Product p WHERE p.isActive = true");

        if (cateId != null) {
            qlString.append(" AND p.category.id = :cateId");
            params.put("cateId", cateId);
        }

        if (keyword != null && !keyword.isEmpty()) {
            qlString.append(" AND p.product_name LIKE :keyword");
            params.put("keyword", "%" + keyword + "%");
        }
        
        if (priceRange != null && !priceRange.isEmpty()) {
            switch (priceRange) {
                case "0-50000":
                    qlString.append(" AND p.price < 50000");
                    break;
                case "50000-100000":
                    qlString.append(" AND p.price >= 50000 AND p.price <= 100000");
                    break;
                case "100000+":
                    qlString.append(" AND p.price > 100000");
                    break;
            }
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "price-asc":
                    qlString.append(" ORDER BY p.price ASC");
                    break;
                case "price-desc":
                    qlString.append(" ORDER BY p.price DESC");
                    break;
                case "newest":
                    qlString.append(" ORDER BY p.createdDate DESC"); // Giả sử bạn có cột createdDate
                    break;
                default:
                     qlString.append(" ORDER BY p.product_id DESC");
            }
        } else {
            qlString.append(" ORDER BY p.product_id DESC");
        }

        TypedQuery<Product> query = em.createQuery(qlString.toString(), Product.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }

    // HÀM CŨ CỦA BẠN (GIỮ NGUYÊN)
    public Map<String, Object> search(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword, int page, int size) {
        StringBuilder ql = new StringBuilder("SELECT p FROM Product p WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (cateId != null) {
            ql.append(" AND p.category.id = :cateId");
            params.put("cateId", cateId);
        }
        if (suppId != null) {
            ql.append(" AND p.supplier.id = :suppId");
            params.put("suppId", suppId);
        }
        if (minPrice != null) {
            ql.append(" AND p.price >= :minPrice");
            params.put("minPrice", minPrice);
        }
        if (maxPrice != null) {
            ql.append(" AND p.price <= :maxPrice");
            params.put("maxPrice", maxPrice);
        }
        if (keyword != null && !keyword.isEmpty()) {
            ql.append(" AND p.product_name LIKE :keyword");
            params.put("keyword", "%" + keyword + "%");
        }

        TypedQuery<Product> query = em.createQuery(ql.toString(), Product.class);
        params.forEach(query::setParameter);

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        List<Product> products = query.getResultList();
        long total = count(cateId, suppId, minPrice, maxPrice, keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("total", total);
        return result;
    }

    // HÀM CŨ CỦA BẠN (GIỮ NGUYÊN)
    public long count(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword) {
        StringBuilder ql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (cateId != null) {
            ql.append(" AND p.category.id = :cateId");
            params.put("cateId", cateId);
        }
        if (suppId != null) {
            ql.append(" AND p.supplier.id = :suppId");
            params.put("suppId", suppId);
        }
        if (minPrice != null) {
            ql.append(" AND p.price >= :minPrice");
            params.put("minPrice", minPrice);
        }
        if (maxPrice != null) {
            ql.append(" AND p.price <= :maxPrice");
            params.put("maxPrice", maxPrice);
        }
        if (keyword != null && !keyword.isEmpty()) {
            ql.append(" AND p.product_name LIKE :keyword");
            params.put("keyword", "%" + keyword + "%");
        }

        TypedQuery<Long> query = em.createQuery(ql.toString(), Long.class);
        params.forEach(query::setParameter);

        return query.getSingleResult();
    }
}