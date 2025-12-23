package dao.impl;

import dao.BannerRepository;
import jakarta.persistence.EntityManager;
import model.Banner;

import java.util.List;
import java.util.Optional;

public class BannerRepositoryImpl implements BannerRepository {

    @Override
    public List<Banner> findAllActive(EntityManager em) {
        return em.createQuery("SELECT b FROM Banner b WHERE b.isActive = true ORDER BY b.sortOrder ASC", Banner.class)
                 .getResultList();
    }

    @Override
    public List<Banner> findAll(EntityManager em) {
        return em.createQuery("SELECT b FROM Banner b ORDER BY b.sortOrder ASC", Banner.class)
                 .getResultList();
    }

    @Override
    public Optional<Banner> findById(int id, EntityManager em) {
        return Optional.ofNullable(em.find(Banner.class, id));
    }

    @Override
    public void save(Banner banner, EntityManager em) {
        em.persist(banner);
    }

    @Override
    public void delete(Banner banner, EntityManager em) {
        em.remove(banner);
    }
}

