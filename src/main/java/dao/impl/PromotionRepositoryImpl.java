package dao.impl;

import dao.PromotionRepository;
import jakarta.persistence.EntityManager;
import model.Promotion;

import java.util.List;
import java.util.Optional;

public class PromotionRepositoryImpl implements PromotionRepository {

    @Override
    public List<Promotion> findAllActive(EntityManager em) {
        return em.createQuery("SELECT p FROM Promotion p WHERE p.isActive = true ORDER BY p.createdDate DESC", Promotion.class)
                 .getResultList();
    }

    @Override
    public List<Promotion> findAll(EntityManager em) {
        return em.createQuery("SELECT p FROM Promotion p ORDER BY p.createdDate DESC", Promotion.class)
                 .getResultList();
    }

    @Override
    public Optional<Promotion> findById(int id, EntityManager em) {
        return Optional.ofNullable(em.find(Promotion.class, id));
    }

    @Override
    public void save(Promotion promotion, EntityManager em) {
        if (promotion.getId() == null) {
            em.persist(promotion);
        } else {
            em.merge(promotion);
        }
    }

    @Override
    public void delete(Promotion promotion, EntityManager em) {
        em.remove(promotion);
    }
}

