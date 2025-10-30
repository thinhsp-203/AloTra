package service.impl;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.Orders;
import model.PaymentConfig;
import service.PaymentGatewayService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * VNPay Payment Gateway Integration Service
 * Documentation: https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.html
 */
public class VNPayService implements PaymentGatewayService {
    
    private static final String VERSION = "2.1.0";
    private static final String COMMAND = "pay";
    private static final String CURRENCY_CODE = "VND";
    private static final String ORDER_TYPE = "other";
    private static final String LOCALE = "vn";
    
    @Override
    public String createPaymentUrl(Orders order, String method, String returnUrl) {
        if (!"VNPAY".equals(method)) {
            return null;
        }
        
        EntityManager em = JpaUtil.em();
        try {
            // Get payment config
            PaymentConfig config = getPaymentConfig(em, "VNPAY");
            if (config == null) {
                System.err.println("VNPAY config not found or not active");
                return null;
            }
            
            String vnp_TmnCode = config.getMerchant_id();
            String vnp_HashSecret = config.getSecret_key();
            String vnp_Url = config.getApi_endpoint();
            
            if (vnp_TmnCode == null || vnp_HashSecret == null || vnp_Url == null) {
                System.err.println("VNPAY config incomplete");
                return null;
            }
            
            // Build payment parameters
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", VERSION);
            vnp_Params.put("vnp_Command", COMMAND);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            
            // Amount in smallest unit (VND * 100)
            long amount = order.getTotal_amount()
                              .multiply(java.math.BigDecimal.valueOf(100))
                              .longValue();
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            
            vnp_Params.put("vnp_CurrCode", CURRENCY_CODE);
            vnp_Params.put("vnp_TxnRef", String.valueOf(order.getOrder_id()));
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang #" + order.getOrder_id());
            vnp_Params.put("vnp_OrderType", ORDER_TYPE);
            vnp_Params.put("vnp_Locale", LOCALE);
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1"); // Should get real IP in production
            
            // Timestamps
            LocalDateTime now = LocalDateTime.now();
            String createDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String expireDate = now.plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            vnp_Params.put("vnp_CreateDate", createDate);
            vnp_Params.put("vnp_ExpireDate", expireDate);
            
            // Build hash data and query string
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            boolean isFirst = true;
            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    if (!isFirst) {
                        hashData.append('&');
                        query.append('&');
                    }
                    
                    hashData.append(fieldName).append('=')
                           .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                         .append('=')
                         .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    
                    isFirst = false;
                }
            }
            
            // Generate secure hash
            String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);
            
            String paymentUrl = vnp_Url + "?" + query.toString();
            System.out.println("Generated VNPay URL: " + paymentUrl);
            
            return paymentUrl;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    @Override
    public boolean verifyPayment(Map<String, String> params) {
        EntityManager em = JpaUtil.em();
        try {
            PaymentConfig config = getPaymentConfig(em, "VNPAY");
            if (config == null) {
                return false;
            }
            
            String vnp_SecureHash = params.get("vnp_SecureHash");
            if (vnp_SecureHash == null) {
                return false;
            }
            
            // Remove secure hash params for verification
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("vnp_SecureHash");
            verifyParams.remove("vnp_SecureHashType");
            
            // Build hash data
            StringBuilder hashData = new StringBuilder();
            boolean isFirst = true;
            
            for (Map.Entry<String, String> entry : verifyParams.entrySet()) {
                String fieldValue = entry.getValue();
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    if (!isFirst) {
                        hashData.append('&');
                    }
                    hashData.append(entry.getKey()).append('=')
                           .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    isFirst = false;
                }
            }
            
            String signValue = hmacSHA512(config.getSecret_key(), hashData.toString());
            
            // Verify signature and response code
            boolean signatureValid = signValue.equals(vnp_SecureHash);
            boolean responseSuccess = "00".equals(params.get("vnp_ResponseCode"));
            
            System.out.println("VNPay verification - Signature valid: " + signatureValid + 
                             ", Response success: " + responseSuccess);
            
            return signatureValid && responseSuccess;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    @Override
    public boolean isPaymentMethodEnabled(String method) {
        EntityManager em = JpaUtil.em();
        try {
            PaymentConfig config = getPaymentConfig(em, method);
            return config != null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Get payment configuration from database
     */
    private PaymentConfig getPaymentConfig(EntityManager em, String method) {
        try {
            return em.createQuery(
                "SELECT p FROM PaymentConfig p WHERE p.payment_method = :m AND p.isActive = true",
                PaymentConfig.class)
                .setParameter("m", method)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Generate HMAC SHA512 signature
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Get VNPay response code description
     */
    public static String getResponseDescription(String responseCode) {
        Map<String, String> responseCodes = new HashMap<>();
        responseCodes.put("00", "Giao dịch thành công");
        responseCodes.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        responseCodes.put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        responseCodes.put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        responseCodes.put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodes.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        responseCodes.put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodes.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
        responseCodes.put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        responseCodes.put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        responseCodes.put("75", "Ngân hàng thanh toán đang bảo trì.");
        responseCodes.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch");
        responseCodes.put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");
        
        return responseCodes.getOrDefault(responseCode, "Lỗi không xác định");
    }
}