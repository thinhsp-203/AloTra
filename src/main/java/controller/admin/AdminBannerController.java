package controller.admin;

import config.JpaUtil;
import dao.jpa.BannerRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Banner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = "/admin/banners")
@MultipartConfig
public class AdminBannerController extends HttpServlet {

    private BannerRepository bannerRepo;
    private static final String UPLOAD_DIR_RELATIVE = "uploads/banners";
    private String uploadDirPhysical;

    @Override
    public void init() throws ServletException {
        bannerRepo = new BannerRepository();
        
        uploadDirPhysical = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR_RELATIVE.replace("/", File.separator);
        
        // Tạo thư mục này nếu nó chưa tồn tại
        File uploadDir = new File(uploadDirPhysical);
        if (!uploadDir.exists()) uploadDir.mkdirs();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        try {
            List<Banner> banners = bannerRepo.findAll(em);
            req.setAttribute("banners", banners);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        req.getRequestDispatcher("/views/admin/banners.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();

            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                bannerRepo.findById(id, em).ifPresent(banner -> {
                    if (banner.getImageUrl() != null && !banner.getImageUrl().startsWith("http")) {
                        try {
                            String fileName = Paths.get(banner.getImageUrl()).getFileName().toString();
                            File fileToDelete = new File(uploadDirPhysical, fileName);
                            if (fileToDelete.exists()) {
                                fileToDelete.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace(); // Log lỗi xóa file
                        }
                    }
                    bannerRepo.delete(banner, em);
                });
                
            } else if ("add".equals(action)) {

                Part filePart = req.getPart("bannerFile"); 
                String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String imageUrlFromText = req.getParameter("imageUrl");
                
                String finalImageUrl = null;

                if (originalFileName != null && !originalFileName.isEmpty()) {
                    // --- Ưu tiên upload file ---
                    String extension = "";
                    int i = originalFileName.lastIndexOf('.');
                    if (i > 0) {
                        extension = originalFileName.substring(i); // .png
                    }
                    String finalFileName = "banner-" + UUID.randomUUID().toString() + extension;

                    // Lưu file vào đường dẫn vật lý
                    File fileToSave = new File(uploadDirPhysical, finalFileName);
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    // Lưu đường dẫn tương đối (dùng /)
                    finalImageUrl = UPLOAD_DIR_RELATIVE + "/" + finalFileName;
                    
                } else if (imageUrlFromText != null && !imageUrlFromText.isEmpty()) {
                    finalImageUrl = imageUrlFromText;
                }

                if (finalImageUrl != null && !finalImageUrl.isEmpty()) {
                    Banner newBanner = new Banner();
                    newBanner.setImageUrl(finalImageUrl);
                    newBanner.setLinkUrl(req.getParameter("linkUrl"));
                    
                    String sortOrderStr = req.getParameter("sortOrder");
                    newBanner.setSortOrder( (sortOrderStr == null || sortOrderStr.isEmpty()) ? 0 : Integer.parseInt(sortOrderStr) );
                    
                    newBanner.setActive(req.getParameter("isActive") != null);
                    
                    bannerRepo.save(newBanner, em);
                } else {
                    // Báo lỗi nếu cả 2 đều rỗng
                    req.getSession().setAttribute("error", "Bạn phải cung cấp ảnh bằng cách upload hoặc dán URL.");
                    em.getTransaction().rollback(); // Hủy transaction
                    resp.sendRedirect(req.getContextPath() + "/admin/banners");
                    return; 
                }
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}