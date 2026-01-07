package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.ProductSizeDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.ProductSize;
import java.util.List;

public class ProductSizeDaoImpl implements ProductSizeDao {

    @Override
    public void save(ProductSize productSize) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            if (productSize.getSize_id() == null) {
                em.persist(productSize);
            } else {
                em.merge(productSize);
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
    public List<ProductSize> findByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        try {
            // Sort theo thứ tự logic: S (1) -> M (2) -> L (3) -> các size khác (99)
            TypedQuery<ProductSize> query = em.createQuery(
                    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid " +
                    "ORDER BY CASE ps.size_name " +
                    "    WHEN 'S' THEN 1 " +
                    "    WHEN 'M' THEN 2 " +
                    "    WHEN 'L' THEN 3 " +
                    "    ELSE 99 " +
                    "END",
                    ProductSize.class);
            query.setParameter("pid", productId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int sizeId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            ProductSize size = em.find(ProductSize.class, sizeId);
            if (size != null) {
                em.remove(size);
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
    public void deleteByProductId(int productId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            TypedQuery<ProductSize> query = em.createQuery(
                    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid",
                    ProductSize.class);
            query.setParameter("pid", productId);
            List<ProductSize> sizes = query.getResultList();
            for (ProductSize size : sizes) {
                em.remove(size);
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
    public boolean exists(int productId, String sizeName) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(ps) FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sizeName",
                    Long.class);
            query.setParameter("pid", productId);
            query.setParameter("sizeName", sizeName);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public ProductSize findByProductIdAndSizeName(int productId, String sizeName) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<ProductSize> query = em.createQuery(
                    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sizeName",
                    ProductSize.class);
            query.setParameter("pid", productId);
            query.setParameter("sizeName", sizeName);
            query.setMaxResults(1);
            List<ProductSize> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        } finally {
            em.close();
        }
    }
}

