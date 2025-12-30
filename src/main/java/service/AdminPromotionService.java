package service;

import java.util.List;
import jakarta.servlet.ServletContext;
import model.Promotion;

public interface AdminPromotionService {
    List<Promotion> getAllPromotions();
    Promotion getPromotionById(int id);
    void savePromotion(Promotion promotion, jakarta.servlet.http.Part imageFile, String imageUrl, ServletContext context);
    void deletePromotion(int id, ServletContext context);
}

