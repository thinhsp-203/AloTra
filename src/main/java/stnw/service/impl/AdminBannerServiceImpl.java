package stnw.service.impl;

import stnw.dao.BannerDao;
import stnw.dao.impl.BannerDaoImpl;
import stnw.model.Banner;
import stnw.service.AdminBannerService;
import stnw.utils.UploadType;
import stnw.utils.UploadUtils;

import java.util.List;

public class AdminBannerServiceImpl implements AdminBannerService {
    
    private final BannerDao bannerDao = new BannerDaoImpl();
    
    @Override
    public List<Banner> getAllBanners() {
        return bannerDao.findAll();
    }
    
    @Override
    public Banner getBannerById(int id) {
        return bannerDao.findById(id);
    }
    
    @Override
    public int getMaxSortOrder() {
        return bannerDao.getMaxSortOrder();
    }
    
    @Override
    public void saveBanner(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            // Tự động set thứ tự khi tạo mới
            if (banner.getId() == null) {
                int maxSortOrder = bannerDao.getMaxSortOrder();
                banner.setSortOrder(maxSortOrder + 1);
            }
            
            String finalImageUrl = handleImageUpload(banner, imageFile, imageUrl, servletContext);
            if (finalImageUrl != null) {
                banner.setImageUrl(finalImageUrl);
            } else if (banner.getId() == null) {
                throw new IllegalArgumentException("Bạn phải cung cấp ảnh!");
            }
            
            if (banner.getId() == null) {
                bannerDao.save(banner);
            } else {
                bannerDao.update(banner);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu banner: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteBanner(int id, jakarta.servlet.ServletContext servletContext) {
        Banner banner = bannerDao.findById(id);
        if (banner != null) {
            // Xóa file ảnh
            if (banner.getImageUrl() != null) {
                UploadUtils.deleteOldImage(banner.getImageUrl(), servletContext);
            }
            bannerDao.delete(id);
        }
    }
    
    private String handleImageUpload(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            String uploadedPath = UploadUtils.save(imageFile, UploadType.BANNERS, servletContext);
            if (uploadedPath != null) {
                // Xóa ảnh cũ nếu đang edit
                if (banner.getId() != null && banner.getImageUrl() != null) {
                    UploadUtils.deleteOldImage(banner.getImageUrl(), servletContext);
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
