package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.WishlistDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.WishlistItem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WishlistDaoImpl implements WishlistDao {

    @Override
    public List<WishlistItem> findByUserId(int userId) {
        EntityManager em = JpaUtils.em();
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
    public WishlistItem findByUserIdAndProductId(int userId, int productId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<WishlistItem> query = em.createQuery(
                    "SELECT w FROM WishlistItem w WHERE w.user.id = :userId AND w.product.id = :productId",
                    WishlistItem.class);
            query.setParameter("userId", userId);
            query.setParameter("productId", productId);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(WishlistItem item) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(item);
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
    public void delete(WishlistItem item) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            WishlistItem managedItem = em.find(WishlistItem.class, item.getId());
            if (managedItem != null) {
                em.remove(managedItem);
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
    public Set<Integer> findProductIdsByUserId(int userId) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT w.product.id FROM WishlistItem w WHERE w.user.id = :userId", Integer.class);
            query.setParameter("userId", userId);
            return query.getResultStream().collect(Collectors.toSet());
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
            em.createQuery("DELETE FROM WishlistItem wi WHERE wi.product.product_id = :productId")
              .setParameter("productId", productId)
              .executeUpdate();
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
    public void deleteByUserId(Integer userId) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.createQuery("DELETE FROM WishlistItem w WHERE w.user.id = :userId")
              .setParameter("userId", userId)
              .executeUpdate();
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
}

