package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.PaymentConfigDao;
import stnw.dao.impl.PaymentConfigDaoImpl;
import jakarta.persistence.EntityManager;
import stnw.model.PaymentConfig;
import stnw.service.PaymentConfigService;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentConfigServiceImpl implements PaymentConfigService {
    
    @Override
    public List<PaymentConfig> getAllPaymentConfigs() {
        EntityManager em = JpaUtil.em();
        try {
            PaymentConfigDao dao = new PaymentConfigDaoImpl(em);
            return dao.findAll();
        } finally {
            em.close();
        }
    }
    
    @Override
    public PaymentConfig getPaymentConfigById(Integer id) {
        EntityManager em = JpaUtil.em();
        try {
            PaymentConfigDao dao = new PaymentConfigDaoImpl(em);
            return dao.findById(id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void savePaymentConfig(Integer id, String paymentMethod, String displayName, 
                                 String apiEndpoint, String merchantId, String secretKey, 
                                 String accessKey, String configJson, boolean isActive) {
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            PaymentConfigDao dao = new PaymentConfigDaoImpl(em);
            
            PaymentConfig config;
            if (id == null) {
                config = new PaymentConfig();
                config.setCreatedDate(LocalDateTime.now());
            } else {
                config = dao.findById(id);
                if (config == null) {
                    throw new IllegalArgumentException("Payment config not found");
                }
            }
            
            config.setPayment_method(paymentMethod);
            config.setDisplay_name(displayName);
            config.setApi_endpoint(apiEndpoint);
            config.setMerchant_id(merchantId);
            config.setSecret_key(secretKey);
            config.setAccess_key(accessKey);
            config.setConfig_json(configJson);
            config.setIsActive(isActive);
            config.setUpdatedDate(LocalDateTime.now());
            
            if (id == null) {
                dao.save(config);
            } else {
                dao.update(config);
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error saving payment config: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void togglePaymentConfigStatus(Integer id) {
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            PaymentConfigDao dao = new PaymentConfigDaoImpl(em);
            
            PaymentConfig config = dao.findById(id);
            if (config != null) {
                config.setIsActive(!config.getIsActive());
                config.setUpdatedDate(LocalDateTime.now());
                dao.update(config);
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error toggling payment config status: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

