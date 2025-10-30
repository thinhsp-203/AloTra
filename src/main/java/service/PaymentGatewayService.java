package service;

import model.Orders;
import java.util.Map;

/**
 * Payment Gateway Service Interface
 * Provides common methods for payment gateway integrations
 */
public interface PaymentGatewayService {
    
    /**
     * Create payment URL for redirecting user to payment gateway
     * 
     * @param order The order to create payment for
     * @param method Payment method code (VNPAY, MOMO, etc.)
     * @param returnUrl URL to redirect user after payment completion
     * @return Payment URL string, or null if creation failed
     */
    String createPaymentUrl(Orders order, String method, String returnUrl);
    
    /**
     * Verify payment callback/webhook signature and result
     * 
     * @param params Request parameters from payment gateway callback
     * @return true if payment is valid and successful, false otherwise
     */
    boolean verifyPayment(Map<String, String> params);
    
    /**
     * Check if a payment method is enabled in system
     * 
     * @param method Payment method code to check
     * @return true if payment method is configured and enabled
     */
    boolean isPaymentMethodEnabled(String method);
}