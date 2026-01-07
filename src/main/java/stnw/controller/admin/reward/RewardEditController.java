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

@WebServlet(urlPatterns = "/admin/rewards/edit")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class RewardEditController extends HttpServlet {
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
            int id = Integer.parseInt(req.getParameter("id"));
            Reward reward = rewardService.getRewardById(id);
            
            if (reward == null) {
                req.getSession().setAttribute("error", "Không tìm thấy quà tặng!");
                resp.sendRedirect(req.getContextPath() + "/admin/rewards");
                return;
            }
            
            String oldImagePath = reward.getImage_url();
            
            reward.setName(req.getParameter("name"));
            reward.setDescription(req.getParameter("description"));
            reward.setPoints_required(Integer.parseInt(req.getParameter("points_required")));
            
            Part imagePart = req.getPart("imageFile");
            String imageUrl = req.getParameter("image_url");
            
            if (imagePart != null && imagePart.getSize() > 0) {
                String uploadedPath = UploadUtils.save(imagePart, UploadType.GIFTS, req.getServletContext());
                if (uploadedPath != null) {
                    reward.setImage_url(uploadedPath);
                    if (oldImagePath != null && !oldImagePath.isEmpty() && !oldImagePath.startsWith("http")) {
                        UploadUtils.deleteOldImage(oldImagePath, req.getServletContext());
                    }
                }
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                reward.setImage_url(imageUrl.trim());
            } else if (oldImagePath != null) {
                reward.setImage_url(oldImagePath);
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
            
            rewardService.updateReward(reward);
            req.getSession().setAttribute("success", "Đã cập nhật quà tặng thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/rewards");
    }
}

