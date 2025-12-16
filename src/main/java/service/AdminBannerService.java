package service;

import java.util.List;
import model.Banner;

public interface AdminBannerService {
    List<Banner> getAllBanners();
    Banner getBannerById(int id);
    void saveBanner(Banner banner, jakarta.servlet.http.Part imageFile, String imageUrl);
    void deleteBanner(int id);
}