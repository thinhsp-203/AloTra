package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.ProductQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.Product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    @Override
    public List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange) {
        return findProducts(cateId, keyword, sortBy, priceRange, 0, -1);
    }

    @Override
    public List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange, int offset, int limit) {
        EntityManager em = JpaUtils.em();
        try {
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
                        qlString.append(" ORDER BY p.createdDate DESC");
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
            if (offset >= 0) query.setFirstResult(offset);
            if (limit > 0) query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Map<String, Object> search(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword, int page, int size) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder ql = new StringBuilder("SELECT p FROM Product p WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (cateId != null) {
                ql.append(" AND p.category.id = :cateId");
                params.put("cateId", cateId);
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
        } finally {
            em.close();
        }
    }

    @Override
    public long count(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword) {
        EntityManager em = JpaUtils.em();
        try {
            StringBuilder ql = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (cateId != null) {
                ql.append(" AND p.category.id = :cateId");
                params.put("cateId", cateId);
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
        } finally {
            em.close();
        }
    }
}

