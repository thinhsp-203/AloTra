package stnw.controller.admin.banner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.Banner;
import stnw.service.AdminBannerService;
import stnw.service.AdminSettingsService;
import stnw.service.impl.AdminBannerServiceImpl;
import stnw.service.impl.AdminSettingsServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = {"/admin/banners/save"})
@MultipartConfig
public class BannerSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminBannerService bannerService;
    private AdminSettingsService settingsService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
        settingsService = new AdminSettingsServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("updateLogo".equals(action)) {
                String logoUrl = null;
                
                // Ưu tiên: Upload file
                Part logoFile = req.getPart("logoFile");
                if (logoFile != null && logoFile.getSize() > 0) {
                    try {
                        // Upload file sử dụng UploadUtils
                        String uploadedPath = stnw.utils.UploadUtils.save(
                            logoFile, 
                            stnw.enums.UploadType.BANNERS, 
                            req.getServletContext()
                        );
                        if (uploadedPath != null) {
                            // UploadUtils trả về "uploads/banners/xxx.jpg" (không có / ở đầu)
                            // Giữ nguyên format này để tương thích với cách JSP xử lý path
                            logoUrl = uploadedPath;
                            
                            // Xóa logo cũ nếu có (nếu là file local, không phải URL external)
                            var allSettings = settingsService.getAllSettings();
                            String oldLogoUrl = allSettings.get("LOGO_URL");
                            if (oldLogoUrl != null && !oldLogoUrl.startsWith("http") && !oldLogoUrl.startsWith("https")) {
                                // Xử lý cả 2 trường hợp: "uploads/..." và "/uploads/..."
                                String pathToDelete = oldLogoUrl.startsWith("/") 
                                    ? oldLogoUrl.substring(1) 
                                    : oldLogoUrl;
                                stnw.utils.UploadUtils.deleteOldImage(pathToDelete, req.getServletContext());
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        req.getSession().setAttribute("error", "Lỗi upload file: " + e.getMessage());
                        resp.sendRedirect(req.getContextPath() + "/admin/banners");
                        return;
                    } catch (Exception e) {
                        req.getSession().setAttribute("error", "Lỗi khi upload file: " + e.getMessage());
                        resp.sendRedirect(req.getContextPath() + "/admin/banners");
                        return;
                    }
                }
                
                // Nếu không có file upload, dùng URL từ input
                if (logoUrl == null) {
                    logoUrl = req.getParameter("LOGO_URL");
                }
                
                // Lưu vào settings
                if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                    var settings = new java.util.HashMap<String, String>();
                    settings.put("LOGO_URL", logoUrl.trim());
                    settingsService.updateSettings(settings);
                    stnw.config.AppContextListener.loadSiteSettings(req.getServletContext());
                    req.getSession().setAttribute("success", "Đã cập nhật logo!");
                } else {
                    req.getSession().setAttribute("error", "Vui lòng chọn file hoặc nhập URL logo!");
                }
            } else {
                String idParam = req.getParameter("id");
                Integer id = (idParam != null && !idParam.isEmpty()) 
                    ? Integer.parseInt(idParam) 
                    : null;
                
                Banner banner = (id != null) 
                    ? bannerService.getBannerById(id) 
                    : new Banner();
                
                banner.setLinkUrl(null);
                
                if (id != null) {
                    String sortOrderStr = req.getParameter("sortOrder");
                    if (sortOrderStr != null && !sortOrderStr.isEmpty()) {
                        banner.setSortOrder(Integer.parseInt(sortOrderStr));
                    }
                }
                
                banner.setActive(req.getParameter("isActive") != null);
                
                Part filePart = req.getPart("bannerFile");
                String imageUrl = req.getParameter("imageUrl");
                
                bannerService.saveBanner(banner, filePart, imageUrl, req.getServletContext());
                req.getSession().setAttribute("success", "Đã lưu banner thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}

