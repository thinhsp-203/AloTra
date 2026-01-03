package service.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.Banner;
import service.AdminBannerService;
import utils.Constant;

public class AdminBannerServiceImpl implements AdminBannerService {
    private static final String BANNER_SUBDIR = "banners";
    
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
                if (banner.getImageUrl() != null && !banner.getImageUrl().startsWith("http")) {
                    try {
                        String fileName = Paths.get(banner.getImageUrl()).getFileName().toString();
                        String uploadBaseDir = Constant.getUploadPath(servletContext);
                        File uploadDir = new File(uploadBaseDir, BANNER_SUBDIR);
                        File fileToDelete = new File(uploadDir, fileName);
                        if (fileToDelete.exists()) {
                            fileToDelete.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            // Kiểm tra file upload
            if (imageFile != null && imageFile.getSize() > 0) {
                String submittedFileName = imageFile.getSubmittedFileName();
                if (submittedFileName != null && !submittedFileName.trim().isEmpty()) {
                    String originalFileName = Paths.get(submittedFileName).getFileName().toString();
                    
                    if (originalFileName != null && !originalFileName.isEmpty()) {
                        String extension = "";
                        int i = originalFileName.lastIndexOf('.');
                        if (i > 0) {
                            extension = originalFileName.substring(i);
                        }
                        String finalFileName = "banner-" + UUID.randomUUID().toString() + extension;
                        
                        // Sử dụng ServletContext để lấy đường dẫn đúng từ webapp/uploads/banners
                        String uploadBaseDir = Constant.getUploadPath(servletContext);
                        File uploadDir = new File(uploadBaseDir, BANNER_SUBDIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        
                        // Xóa ảnh cũ nếu đang edit và có ảnh cũ
                        if (banner.getId() != null && banner.getImageUrl() != null && !banner.getImageUrl().startsWith("http")) {
                            try {
                                String oldFileName = Paths.get(banner.getImageUrl()).getFileName().toString();
                                File oldFile = new File(uploadDir, oldFileName);
                                if (oldFile.exists()) {
                                    oldFile.delete();
                                }
                            } catch (Exception e) {
                                // Ignore error when deleting old file
                            }
                        }
                        
                        File fileToSave = new File(uploadDir, finalFileName);
                        
                        try (InputStream input = imageFile.getInputStream()) {
                            Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                        
                        return BANNER_SUBDIR + "/" + finalFileName;
                    }
                }
            }
            
            // Kiểm tra URL ảnh
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                return imageUrl.trim();
            }
            
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
}