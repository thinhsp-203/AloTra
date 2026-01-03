package service.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import model.Promotion;
import model.User;
import service.AdminPromotionService;
import service.NotificationService;
import service.impl.NotificationServiceImpl;
import utils.Constant;

public class AdminPromotionServiceImpl implements AdminPromotionService {
    private static final String PROMOTION_SUBDIR = "promotions";
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
                if (promotion.getImageUrl() != null && !promotion.getImageUrl().startsWith("http")) {
                    try {
                        String fileName = Paths.get(promotion.getImageUrl()).getFileName().toString();
                        String uploadDirPhysical = new File(Constant.getUploadPath(context), PROMOTION_SUBDIR).getAbsolutePath();
                        File fileToDelete = new File(uploadDirPhysical, fileName);
                        if (fileToDelete.exists()) {
                            fileToDelete.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            String originalFileName = (imageFile != null) 
                ? Paths.get(imageFile.getSubmittedFileName()).getFileName().toString() 
                : null;
            
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String extension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) {
                    extension = originalFileName.substring(i);
                }
                String finalFileName = "promotion-" + UUID.randomUUID().toString() + extension;
                
                // Sử dụng ServletContext để lấy đường dẫn đúng từ webapp/uploads/promotions
                String uploadBaseDir = Constant.getUploadPath(context);
                File uploadDir = new File(uploadBaseDir, PROMOTION_SUBDIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                File fileToSave = new File(uploadDir, finalFileName);
                
                try (InputStream input = imageFile.getInputStream()) {
                    Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                return PROMOTION_SUBDIR + "/" + finalFileName;
            }
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                return imageUrl;
            }
            
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
}

