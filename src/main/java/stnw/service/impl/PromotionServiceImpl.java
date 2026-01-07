package stnw.service.impl;

import stnw.dao.PromotionDao;
import stnw.dao.impl.PromotionDaoImpl;
import stnw.model.Promotion;
import stnw.service.PromotionService;

import java.util.List;

public class PromotionServiceImpl implements PromotionService {

    private final PromotionDao promotionDao = new PromotionDaoImpl();

    @Override
    public List<Promotion> getAllActivePromotions() {
        return promotionDao.findAllActive();
    }

    @Override
    public Promotion getPromotionById(int id) {
        return promotionDao.findById(id);
    }

    @Override
    public List<Promotion> getRelatedPromotions(int excludeId, int limit) {
        return promotionDao.findRelatedPromotions(excludeId, limit);
    }
}

