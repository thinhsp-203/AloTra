package stnw.service.impl;

import stnw.dao.PromotionDao;
import stnw.dao.UserDao;
import stnw.dao.impl.PromotionDaoImpl;
import stnw.dao.impl.UserDaoImpl;
import jakarta.servlet.ServletContext;
import stnw.model.Promotion;
import stnw.model.User;
import stnw.service.AdminPromotionService;
import stnw.service.NotificationService;
import stnw.enums.UploadType;
import stnw.utils.UploadUtils;

import java.time.LocalDateTime;
import java.util.List;

public class AdminPromotionServiceImpl implements AdminPromotionService {
    private final NotificationService notificationService = new NotificationServiceImpl();
    private final PromotionDao promotionDao = new PromotionDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    
    @Override
    public List<Promotion> getAllPromotions() {
        return promotionDao.findAll();
    }
    
    @Override
    public Promotion getPromotionById(int id) {
        return promotionDao.findById(id);
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
            
            boolean isNew = promotion.getId() == null;
            if (isNew) {
                promotionDao.save(promotion);
            } else {
                promotionDao.update(promotion);
            }
            
            // Tạo notification cho tất cả users khi có promotion mới và active
            if (isNew && promotion.isActive()) {
                try {
                    // Lấy tất cả users active - cần thêm method vào UserDao
                    // Tạm thời dùng cách cũ
                    List<User> users = userDao.findAllActive();
                    String message = "Khuyến mãi mới: " + promotion.getTitle() + "! Xem ngay!";
                    String link = "/promotions/detail?id=" + promotion.getId();
                    for (User user : users) {
                        notificationService.createNotification(user.getId(), message, link);
                    }
                } catch (Exception e) {
                    // Log lỗi nhưng không ảnh hưởng đến việc lưu promotion
                    System.err.println("Lỗi khi tạo notification cho promotion: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu khuyến mãi: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deletePromotion(int id, ServletContext context) {
        Promotion promotion = promotionDao.findById(id);
        if (promotion != null) {
            // Xóa file ảnh
            if (promotion.getImageUrl() != null) {
                UploadUtils.deleteOldImage(promotion.getImageUrl(), context);
            }
            promotionDao.delete(promotion);
        }
    }
    
    private String handleImageUpload(Promotion promotion, jakarta.servlet.http.Part imageFile, String imageUrl, ServletContext context) {
        try {
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            String uploadedPath = UploadUtils.save(imageFile, UploadType.PROMOTIONS, context);
            if (uploadedPath != null) {
                // Xóa ảnh cũ nếu đang edit
                if (promotion.getId() != null && promotion.getImageUrl() != null) {
                    UploadUtils.deleteOldImage(promotion.getImageUrl(), context);
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
