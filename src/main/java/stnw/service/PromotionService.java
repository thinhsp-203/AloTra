package stnw.service;

import stnw.model.Promotion;
import java.util.List;

public interface PromotionService {
    List<Promotion> getAllActivePromotions();
    Promotion getPromotionById(int id);
    List<Promotion> getRelatedPromotions(int excludeId, int limit);
}

