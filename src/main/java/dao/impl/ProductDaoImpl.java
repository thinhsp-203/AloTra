package dao.impl;

import config.JpaUtil;
import dao.ProductDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import model.Product;
import java.util.List;

public class ProductDaoImpl implements ProductDao {

    @Override
    public List<Product> findAll(int offset, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
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
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void insert(Product product) {
        EntityManager em = JpaUtil.em();
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
        EntityManager em = JpaUtil.em();
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
        EntityManager em = JpaUtil.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
            	// Ưu tiên xoá mềm
                p.setIsActive(false);
                em.merge(p);
//                // Hoặc xóa cứng:
//                em.remove(p);
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
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Product> findByCategory(int categoryId) {
        EntityManager em = JpaUtil.em();
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
		EntityManager em = JpaUtil.em();
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
        EntityManager em = JpaUtil.em();
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
        EntityManager em = JpaUtil.em();
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
}