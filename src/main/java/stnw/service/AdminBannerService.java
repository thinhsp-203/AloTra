package stnw.service;

import java.util.List;
import stnw.model.Banner;

public interface AdminBannerService {
    List<Banner> getAllBanners();
    Banner getBannerById(int id);
    int getMaxSortOrder();
    void saveBanner(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext);
    void deleteBanner(int id, jakarta.servlet.ServletContext servletContext);
}