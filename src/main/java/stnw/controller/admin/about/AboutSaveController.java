package stnw.controller.admin.about;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.AboutUs;
import stnw.service.AdminAboutService;
import stnw.service.impl.AdminAboutServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/about/save")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class AboutSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminAboutService aboutService;
    
    @Override
    public void init() throws ServletException {
        aboutService = new AdminAboutServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            Integer id = (idParam != null && !idParam.isEmpty()) 
                ? Integer.parseInt(idParam) 
                : null;
            
            AboutUs about = (id != null) 
                ? aboutService.getAboutUsById(id) 
                : new AboutUs();
            
            about.setTitle(req.getParameter("title"));
            about.setContent(req.getParameter("content"));
            
            String sortOrderParam = req.getParameter("sortOrder");
            about.setSortOrder((sortOrderParam == null || sortOrderParam.isEmpty()) 
                ? 0 
                : Integer.parseInt(sortOrderParam));
            
            about.setIsActive(req.getParameter("isActive") != null);
            
            Part imageFile = req.getPart("imageFile");
            String imageUrl = req.getParameter("imageUrl");
            
            aboutService.saveAboutUs(about, imageFile, imageUrl, req.getServletContext());
            
            req.getSession().setAttribute("success", "Đã lưu bài viết thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/about");
    }
}

