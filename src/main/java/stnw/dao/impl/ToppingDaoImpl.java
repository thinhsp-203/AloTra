package stnw.dao.impl;

import stnw.utils.JpaUtils;
import stnw.dao.ToppingDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import stnw.model.Topping;

import java.util.List;

public class ToppingDaoImpl implements ToppingDao {

    @Override
    public List<Topping> findAll() {
        EntityManager em = JpaUtils.em();
        try {
            return em.createQuery("SELECT t FROM Topping t ORDER BY t.topping_name", Topping.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Topping> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        EntityManager em = JpaUtils.em();
        try {
            TypedQuery<Topping> query = em.createQuery(
                    "SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class);
            query.setParameter("ids", ids);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Topping findById(int id) {
        EntityManager em = JpaUtils.em();
        try {
            return em.find(Topping.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Topping topping) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(topping);
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
    public void update(Topping topping) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.merge(topping);
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
    public void delete(int id) {
        EntityManager em = JpaUtils.em();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            Topping topping = em.find(Topping.class, id);
            if (topping != null) {
                topping.setIsAvailable(false); // Soft delete
                em.merge(topping);
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

