package controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Banner;
import service.AdminBannerService;
import service.impl.AdminBannerServiceImpl;

@WebServlet(urlPatterns = "/admin/banners")
@MultipartConfig
public class AdminBannerController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminBannerService bannerService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        req.setAttribute("banners", bannerService.getAllBanners());
        req.getRequestDispatcher("/views/admin/banners.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                bannerService.deleteBanner(id);
                
            } else if ("add".equals(action)) {
                Banner newBanner = new Banner();
                
                Part filePart = req.getPart("bannerFile");
                String imageUrlFromText = req.getParameter("imageUrl");
                
                newBanner.setLinkUrl(req.getParameter("linkUrl"));
                
                String sortOrderStr = req.getParameter("sortOrder");
                newBanner.setSortOrder((sortOrderStr == null || sortOrderStr.isEmpty()) 
                    ? 0 
                    : Integer.parseInt(sortOrderStr));
                
                newBanner.setActive(req.getParameter("isActive") != null);
                
                bannerService.saveBanner(newBanner, filePart, imageUrlFromText);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}