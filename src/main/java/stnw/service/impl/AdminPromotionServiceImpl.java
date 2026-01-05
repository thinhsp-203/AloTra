package stnw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import stnw.model.Promotion;
import stnw.model.User;
import stnw.service.AdminPromotionService;
import stnw.service.NotificationService;
import stnw.service.impl.NotificationServiceImpl;
import stnw.utils.UploadType;
import stnw.utils.UploadUtil;

public class AdminPromotionServiceImpl implements AdminPromotionService {
    private final NotificationService notificationService = new NotificationServiceImpl();
    
    @Override
    public List<Promotion> getAllPromotions() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT p FROM Promotion p ORDER BY p.createdDate DESC", Promotion.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Promotion getPromotionById(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Promotion.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void savePromotion(Promotion promotion, jakarta.servlet.http.Part imageFile, String imageUrl, ServletContext context) {
        try {
            String finalImageUrl = handleImageUpload(promotion, imageFile, imageUrl, context);
            if (finalImageUrl != null) {
                promotion.setImageUrl(finalImageUrl);
            } else if (promotion.getId() == null) {
                throw new IllegalArgumentException("Bạn phải cung cấp ảnh!");
            }
            
            promotion.setUpdatedDate(LocalDateTime.now());
            if (promotion.getId() == null) {
                promotion.setCreatedDate(LocalDateTime.now());
            }
            
            EntityManager em = JpaUtil.em();
            boolean isNew = promotion.getId() == null;
            try {
                em.getTransaction().begin();
                
                if (isNew) {
                    em.persist(promotion);
                } else {
                    em.merge(promotion);
                }
                
                em.getTransaction().commit();
                
                // Tạo notification cho tất cả users khi có promotion mới và active
                if (isNew && promotion.isActive()) {
                    try {
                        em = JpaUtil.em();
                        List<User> users = em.createQuery("SELECT u FROM User u WHERE u.isActive = true", User.class).getResultList();
                        String message = "Khuyến mãi mới: " + promotion.getTitle() + "! Xem ngay!";
                        String link = "/promotions/detail?id=" + promotion.getId();
                        for (User user : users) {
                            notificationService.createNotification(user.getId(), message, link);
                        }
                    } catch (Exception e) {
                        // Log lỗi nhưng không ảnh hưởng đến việc lưu promotion
                        System.err.println("Lỗi khi tạo notification cho promotion: " + e.getMessage());
                    } finally {
                        if (em != null && em.isOpen()) {
                            em.close();
                        }
                    }
                }
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu khuyến mãi: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deletePromotion(int id, ServletContext context) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Promotion promotion = em.find(Promotion.class, id);
            if (promotion != null) {
                // Xóa file ảnh
                if (promotion.getImageUrl() != null) {
                    UploadUtil.deleteOldImage(promotion.getImageUrl(), context);
                }
                em.remove(promotion);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xóa khuyến mãi: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    private String handleImageUpload(Promotion promotion, jakarta.servlet.http.Part imageFile, String imageUrl, ServletContext context) {
        try {
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            String uploadedPath = UploadUtil.save(imageFile, UploadType.PROMOTIONS, context);
            if (uploadedPath != null) {
                // Xóa ảnh cũ nếu đang edit
                if (promotion.getId() != null && promotion.getImageUrl() != null) {
                    UploadUtil.deleteOldImage(promotion.getImageUrl(), context);
                }
                return uploadedPath;
            }
            
            // TRƯỜNG HỢP 2: URL từ text input
            if (imageUrl != null && !imageUrl.isEmpty()) {
                return imageUrl;
            }
            
            // TRƯỜNG HỢP 3: Giữ nguyên ảnh cũ
            return null;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
}
