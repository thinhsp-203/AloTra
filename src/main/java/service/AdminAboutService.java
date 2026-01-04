package service;

import model.AboutUs;
import java.util.List;

public interface AdminAboutService {
    List<AboutUs> getAllAboutUs();
    AboutUs getAboutUsById(Integer id);
    void saveAboutUs(AboutUs aboutUs, jakarta.servlet.http.Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext);
    void deleteAboutUs(Integer id, jakarta.servlet.ServletContext servletContext);
}

