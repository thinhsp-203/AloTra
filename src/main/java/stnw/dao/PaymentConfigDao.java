package stnw.dao;

import stnw.model.PaymentConfig;
import java.util.List;

public interface PaymentConfigDao {
    List<PaymentConfig> findAll();
    PaymentConfig findById(Integer id);
    void save(PaymentConfig config);
    void update(PaymentConfig config);
    void delete(Integer id);
}

