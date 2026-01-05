package stnw.dao.impl;

import jakarta.persistence.EntityManager;
import stnw.dao.PaymentConfigDao;
import stnw.model.PaymentConfig;
import java.util.List;

public class PaymentConfigDaoImpl implements PaymentConfigDao {
    
    private final EntityManager em;
    
    public PaymentConfigDaoImpl(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public List<PaymentConfig> findAll() {
        return em.createQuery(
            "SELECT p FROM PaymentConfig p ORDER BY p.display_order, p.payment_method",
            PaymentConfig.class)
            .getResultList();
    }
    
    @Override
    public PaymentConfig findById(Integer id) {
        return em.find(PaymentConfig.class, id);
    }
    
    @Override
    public void save(PaymentConfig config) {
        em.persist(config);
    }
    
    @Override
    public void update(PaymentConfig config) {
        em.merge(config);
    }
    
    @Override
    public void delete(Integer id) {
        PaymentConfig config = em.find(PaymentConfig.class, id);
        if (config != null) {
            em.remove(config);
        }
    }
}

