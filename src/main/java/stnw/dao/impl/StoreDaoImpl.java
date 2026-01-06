package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.StoreDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.Store;

import java.util.List;

public class StoreDaoImpl implements StoreDao {

    @Override
    public List<Store> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Store> query = em.createQuery(
                "SELECT s FROM Store s WHERE s.isActive = true ORDER BY s.store_name",
                Store.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Store> searchByAddress(String keyword) {
        EntityManager em = JpaUtils.em();
        try {
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            TypedQuery<Store> query = em.createQuery(
                "SELECT s FROM Store s WHERE s.isActive = true " +
                "AND (LOWER(s.address) LIKE :keyword " +
                "OR LOWER(s.ward) LIKE :keyword " +
                "OR LOWER(s.province) LIKE :keyword " +
                "OR LOWER(s.store_name) LIKE :keyword) " +
                "ORDER BY s.store_name",
                Store.class
            );
            query.setParameter("keyword", searchPattern);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Store findById(Integer id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Store.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Store> findByCity(String city) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Store> query = em.createQuery(
                "SELECT s FROM Store s WHERE s.isActive = true AND LOWER(s.province) = LOWER(:province) ORDER BY s.store_name",
                Store.class
            );
            query.setParameter("province", city);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Store> findByDistrict(String district) {
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Store> query = em.createQuery(
                "SELECT s FROM Store s WHERE s.isActive = true AND LOWER(s.ward) = LOWER(:ward) ORDER BY s.store_name",
                Store.class
            );
            query.setParameter("ward", district);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Store store) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(store);
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
    public void update(Store store) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(store);
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
    public void delete(Integer id) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Store store = em.find(Store.class, id);
            if (store != null) {
                store.setIsActive(false); // Soft delete
                em.merge(store);
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
}

