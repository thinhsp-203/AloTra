package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.PromotionRepository;
import stnw.dao.impl.PromotionRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.Promotion;
import stnw.service.PromotionService;

import java.util.List;

public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository = new PromotionRepositoryImpl();

    @Override
    public List<Promotion> getAllActivePromotions() {
        EntityManager em = JpaUtil.em();
        try {
            return promotionRepository.findAllActive(em);
        } finally {
            em.close();
        }
    }

    @Override
    public Promotion getPromotionById(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return promotionRepository.findById(id, em).orElse(null);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Promotion> getRelatedPromotions(int excludeId, int limit) {
        EntityManager em = JpaUtil.em();
        try {
            TypedQuery<Promotion> query = em.createQuery(
                "SELECT p FROM Promotion p WHERE p.isActive = true AND p.id != :excludeId ORDER BY p.createdDate DESC", 
                Promotion.class
            );
            query.setParameter("excludeId", excludeId);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

