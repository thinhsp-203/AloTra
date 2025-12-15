package dao;

import jakarta.persistence.EntityManager;
import model.Banner;
import java.util.List;
import java.util.Optional;

public class BannerRepository {

    // (Stateless constructor)
    public BannerRepository() { }

    public List<Banner> findAllActive(EntityManager em) {
        return em.createQuery("SELECT b FROM Banner b WHERE b.isActive = true ORDER BY b.sortOrder ASC", Banner.class)
                 .getResultList();
    }
    
    public List<Banner> findAll(EntityManager em) {
        return em.createQuery("SELECT b FROM Banner b ORDER BY b.sortOrder ASC", Banner.class)
                 .getResultList();
    }

    public Optional<Banner> findById(int id, EntityManager em) {
        Banner banner = em.find(Banner.class, id);
        return Optional.ofNullable(banner);
    }

    public void save(Banner banner, EntityManager em) {
        em.persist(banner);
    }

    public void delete(Banner banner, EntityManager em) {
        em.remove(banner);
    }
}