package stnw.dao;

import jakarta.persistence.EntityManager;
import stnw.model.Promotion;
import java.util.List;
import java.util.Optional;

public interface PromotionDao {
    List<Promotion> findAllActive(EntityManager em);
    List<Promotion> findAll(EntityManager em);
    Optional<Promotion> findById(int id, EntityManager em);
    void save(Promotion promotion, EntityManager em);
    void delete(Promotion promotion, EntityManager em);
}

