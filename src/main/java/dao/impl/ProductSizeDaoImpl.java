package dao.impl;

import dao.ProductSizeDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.ProductSize;
import java.util.List;

public class ProductSizeDaoImpl implements ProductSizeDao {
    
    private final EntityManager em;
    
    public ProductSizeDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(ProductSize productSize) {
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
        }
    }

    @Override
    public List<ProductSize> findByProductId(int productId) {
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
    }

    @Override
    public void delete(int sizeId) {
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
        }
    }

    @Override
    public void deleteByProductId(int productId) {
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
        }
    }

    @Override
    public boolean exists(int productId, String sizeName) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(ps) FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sizeName",
                Long.class);
        query.setParameter("pid", productId);
        query.setParameter("sizeName", sizeName);
        return query.getSingleResult() > 0;
    }
}

