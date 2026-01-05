package stnw.service;

import stnw.model.PaymentConfig;
import java.util.List;

public interface PaymentConfigService {
    List<PaymentConfig> getAllPaymentConfigs();
    PaymentConfig getPaymentConfigById(Integer id);
    void savePaymentConfig(Integer id, String paymentMethod, String displayName, 
                          String apiEndpoint, String merchantId, String secretKey, 
                          String accessKey, String configJson, boolean isActive);
    void togglePaymentConfigStatus(Integer id);
}

