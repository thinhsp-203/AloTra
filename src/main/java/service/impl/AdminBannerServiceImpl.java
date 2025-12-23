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
    public void saveBanner(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl) {
        try {
            String finalImageUrl = handleImageUpload(banner, imageFile, imageUrl);
            if (finalImageUrl != null) {
                banner.setImageUrl(finalImageUrl);
            } else if (banner.getId() == null) {
                throw new IllegalArgumentException("Bạn phải cung cấp ảnh!");
            }
            
            EntityManager em = JpaUtil.em();
            try {
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
    public void deleteBanner(int id) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Banner banner = em.find(Banner.class, id);
            if (banner != null) {
                // Xóa file ảnh
                if (banner.getImageUrl() != null && !banner.getImageUrl().startsWith("http")) {
                    try {
                        String fileName = Paths.get(banner.getImageUrl()).getFileName().toString();
                        String uploadDirPhysical = Paths.get(Constant.UPLOAD_DIRECTORY, BANNER_SUBDIR)
                            .toFile().getAbsolutePath();
                        File fileToDelete = new File(uploadDirPhysical, fileName);
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
    
    private String handleImageUpload(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl) {
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
                String finalFileName = "banner-" + UUID.randomUUID().toString() + extension;
                
                String uploadDirPhysical = Paths.get(Constant.UPLOAD_DIRECTORY, BANNER_SUBDIR)
                    .toFile().getAbsolutePath();
                File uploadDir = new File(uploadDirPhysical);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                File fileToSave = new File(uploadDirPhysical, finalFileName);
                
                try (InputStream input = imageFile.getInputStream()) {
                    Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                return BANNER_SUBDIR + "/" + finalFileName;
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