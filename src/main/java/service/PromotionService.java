package service;

import model.Promotion;
import java.util.List;

public interface PromotionService {
    List<Promotion> getAllActivePromotions();
    Promotion getPromotionById(int id);
}

