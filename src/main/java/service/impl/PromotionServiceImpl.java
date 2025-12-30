package service.impl;

import config.JpaUtil;
import dao.PromotionRepository;
import dao.impl.PromotionRepositoryImpl;
import jakarta.persistence.EntityManager;
import model.Promotion;
import service.PromotionService;

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
}

