package stnw.dao;

import stnw.model.Promotion;
import java.util.List;

public interface PromotionDao {
    List<Promotion> findAllActive();
    List<Promotion> findAll();
    Promotion findById(int id);
    void save(Promotion promotion);
    void update(Promotion promotion);
    void delete(int id);
    void delete(Promotion promotion);
    List<Promotion> findRelatedPromotions(int excludeId, int limit);
}

