package stnw.service.impl;

import stnw.dao.AboutUsDao;
import stnw.dao.impl.AboutUsDaoImpl;
import jakarta.servlet.http.Part;
import stnw.model.AboutUs;
import stnw.service.AdminAboutService;
import stnw.utils.Constant;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AdminAboutServiceImpl implements AdminAboutService {
    
    private static final String ABOUT_SUBDIR = "about";
    private final AboutUsDao aboutUsDao = new AboutUsDaoImpl();
    
    @Override
    public List<AboutUs> getAllAboutUs() {
        return aboutUsDao.findAll();
    }
    
    @Override
    public AboutUs getAboutUsById(Integer id) {
        return aboutUsDao.findById(id);
    }
    
    @Override
    public void saveAboutUs(AboutUs aboutUs, Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext) {
        // X�?lý ảnh
        String finalImage = handleImageUpload(aboutUs, imageFile, imageUrl, servletContext);
        if (finalImage != null) {
            aboutUs.setImage(finalImage);
        }
        
        // Set timestamps
        if (aboutUs.getId() == null) {
            aboutUs.setCreatedDate(LocalDateTime.now());
        }
        aboutUs.setUpdatedDate(LocalDateTime.now());
        
        // Lưu
        if (aboutUs.getId() == null) {
            aboutUsDao.save(aboutUs);
        } else {
            aboutUsDao.update(aboutUs);
        }
    }
    
    @Override
    public void deleteAboutUs(Integer id, jakarta.servlet.ServletContext servletContext) {
        AboutUs aboutUs = aboutUsDao.findById(id);
        if (aboutUs != null) {
            // Xóa ảnh
            deleteAboutImage(aboutUs.getImage(), servletContext);
            // Xóa record
            aboutUsDao.delete(id);
        }
    }
    
    private String handleImageUpload(AboutUs aboutUs, Part imageFile, String imageUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            String originalFileName = (imageFile != null && imageFile.getSize() > 0) 
                ? Paths.get(imageFile.getSubmittedFileName()).getFileName().toString() 
                : null;
            
            // Trường hợp 1: Upload file (ưu tiên)
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String extension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) {
                    extension = originalFileName.substring(i);
                }
                String finalFileName = "about-" + UUID.randomUUID().toString() + extension;
                
                // Lưu file vào thư mục uploads/about
                String uploadPath = Constant.getUploadPath(servletContext);
                File aboutDir = new File(uploadPath, ABOUT_SUBDIR);
                if (!aboutDir.exists()) aboutDir.mkdirs();
                
                File fileToSave = new File(aboutDir, finalFileName);
                
                // Xóa ảnh cũ nếu tồn tại
                if (aboutUs.getId() != null && aboutUs.getImage() != null && 
                    !aboutUs.getImage().isEmpty() && !aboutUs.getImage().startsWith("http")) {
                    deleteAboutImage(aboutUs.getImage(), servletContext);
                }
                
                // Lưu file mới
                try (InputStream input = imageFile.getInputStream()) {
                    Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                return ABOUT_SUBDIR + "/" + finalFileName;
            }
            
            // Trường hợp 2: URL t�?text input
            if (imageUrl != null && !imageUrl.isBlank()) {
                return imageUrl;
            }
            
            // Trường hợp 3: Gi�?nguyên ảnh cũ
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
    
    private void deleteAboutImage(String imagePath, jakarta.servlet.ServletContext servletContext) {
        if (imagePath == null || imagePath.isEmpty() || imagePath.startsWith("http")) {
            return;
        }
        
        try {
            String fileName = Paths.get(imagePath).getFileName().toString();
            String uploadPath = Constant.getUploadPath(servletContext);
            File aboutDir = new File(uploadPath, ABOUT_SUBDIR);
            File oldFile = new File(aboutDir, fileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        } catch (Exception e) {
            System.err.println("Không th�?xóa ảnh: " + e.getMessage());
        }
    }
}

