package dao;

import jakarta.persistence.EntityManager;
import model.Banner;
import java.util.List;
import java.util.Optional;

public interface BannerRepository {
    List<Banner> findAllActive(EntityManager em);
    List<Banner> findAll(EntityManager em);
    Optional<Banner> findById(int id, EntityManager em);
    void save(Banner banner, EntityManager em);
    void delete(Banner banner, EntityManager em);
}