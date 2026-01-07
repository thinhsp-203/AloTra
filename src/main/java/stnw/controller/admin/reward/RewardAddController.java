package stnw.controller.admin.reward;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.Reward;
import stnw.service.AdminRewardService;
import stnw.service.impl.AdminRewardServiceImpl;
import stnw.utils.UploadUtils;
import stnw.enums.UploadType;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/rewards/add")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class RewardAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminRewardService rewardService;
    
    @Override
    public void init() throws ServletException {
        rewardService = new AdminRewardServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            Reward reward = new Reward();
            
            reward.setName(req.getParameter("name"));
            reward.setDescription(req.getParameter("description"));
            reward.setPoints_required(Integer.parseInt(req.getParameter("points_required")));
            
            Part imagePart = req.getPart("imageFile");
            String imageUrl = req.getParameter("image_url");
            
            if (imagePart != null && imagePart.getSize() > 0) {
                String uploadedPath = UploadUtils.save(imagePart, UploadType.GIFTS, req.getServletContext());
                if (uploadedPath != null) {
                    reward.setImage_url(uploadedPath);
                }
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                reward.setImage_url(imageUrl.trim());
            } else {
                reward.setImage_url(null);
            }
            
            String stockStr = req.getParameter("stock");
            if (stockStr != null && !stockStr.trim().isEmpty()) {
                reward.setStock(Integer.parseInt(stockStr));
            } else {
                reward.setStock(null);
            }
            
            reward.setIsActive(req.getParameter("isActive") != null);
            
            rewardService.saveReward(reward);
            req.getSession().setAttribute("success", "Đã thêm quà tặng thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/rewards");
    }
}

