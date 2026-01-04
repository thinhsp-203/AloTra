package service.impl;

import java.util.List;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.Banner;
import service.AdminBannerService;
import utils.UploadType;
import utils.UploadUtil;

public class AdminBannerServiceImpl implements AdminBannerService {
    
    @Override
    public List<Banner> getAllBanners() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT b FROM Banner b", Banner.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Banner getBannerById(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Banner.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public int getMaxSortOrder() {
        EntityManager em = JpaUtil.em();
        try {
            Object result = em.createQuery("SELECT MAX(b.sortOrder) FROM Banner b").getSingleResult();
            return result != null ? ((Integer) result) : -1;
        } finally {
            em.close();
        }
    }
    
    @Override
    public void saveBanner(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            EntityManager em = JpaUtil.em();
            try {
                // Tự động set thứ tự khi tạo mới
                if (banner.getId() == null) {
                    Object maxResult = em.createQuery("SELECT MAX(b.sortOrder) FROM Banner b").getSingleResult();
                    int maxSortOrder = (maxResult != null) ? ((Integer) maxResult) : -1;
                    banner.setSortOrder(maxSortOrder + 1);
                }
                
                String finalImageUrl = handleImageUpload(banner, imageFile, imageUrl, servletContext);
                if (finalImageUrl != null) {
                    banner.setImageUrl(finalImageUrl);
                } else if (banner.getId() == null) {
                    throw new IllegalArgumentException("Bạn phải cung cấp ảnh!");
                }
                
                em.getTransaction().begin();
                
                if (banner.getId() == null) {
                    em.persist(banner);
                } else {
                    em.merge(banner);
                }
                
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu banner: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteBanner(int id, jakarta.servlet.ServletContext servletContext) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Banner banner = em.find(Banner.class, id);
            if (banner != null) {
                // Xóa file ảnh
                if (banner.getImageUrl() != null) {
                    UploadUtil.deleteOldImage(banner.getImageUrl(), servletContext);
                }
                em.remove(banner);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xóa banner: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    private String handleImageUpload(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            String uploadedPath = UploadUtil.save(imageFile, UploadType.BANNERS, servletContext);
            if (uploadedPath != null) {
                // Xóa ảnh cũ nếu đang edit
                if (banner.getId() != null && banner.getImageUrl() != null) {
                    UploadUtil.deleteOldImage(banner.getImageUrl(), servletContext);
                }
                return uploadedPath;
            }
            
            // TRƯỜNG HỢP 2: URL từ text input
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                return imageUrl.trim();
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