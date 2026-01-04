package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Reward;
import service.AdminRewardService;
import service.impl.AdminRewardServiceImpl;
import utils.UploadUtil;
import utils.UploadType;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/rewards")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class AdminRewardController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private AdminRewardService rewardService;
    
    @Override
    public void init() throws ServletException {
        rewardService = new AdminRewardServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            // Edit mode - load reward by id
            try {
                int id = Integer.parseInt(idParam);
                Reward reward = rewardService.getRewardById(id);
                if (reward != null) {
                    req.setAttribute("reward", reward);
                } else {
                    req.getSession().setAttribute("error", "Không tìm thấy quà tặng!");
                }
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("error", "ID không hợp lệ!");
            }
        }
        req.setAttribute("rewards", rewardService.getAllRewards());
        req.getRequestDispatcher("/views/admin/rewards.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                rewardService.deleteReward(id);
                req.getSession().setAttribute("success", "Đã xóa quà tặng thành công!");
                
            } else if ("add".equals(action) || "edit".equals(action)) {
                Reward reward;
                String oldImagePath = null;
                
                if ("edit".equals(action)) {
                    int id = Integer.parseInt(req.getParameter("id"));
                    reward = rewardService.getRewardById(id);
                    if (reward == null) {
                        req.getSession().setAttribute("error", "Không tìm thấy quà tặng!");
                        resp.sendRedirect(req.getContextPath() + "/admin/rewards");
                        return;
                    }
                    oldImagePath = reward.getImage_url();
                } else {
                    reward = new Reward();
                }
                
                reward.setName(req.getParameter("name"));
                reward.setDescription(req.getParameter("description"));
                reward.setPoints_required(Integer.parseInt(req.getParameter("points_required")));
                
                // Xử lý upload ảnh
                Part imagePart = req.getPart("imageFile");
                String imageUrl = req.getParameter("image_url");
                
                if (imagePart != null && imagePart.getSize() > 0) {
                    // Ưu tiên upload file
                    String uploadedPath = UploadUtil.save(imagePart, UploadType.GIFTS, req.getServletContext());
                    if (uploadedPath != null) {
                        reward.setImage_url(uploadedPath);
                        // Xóa ảnh cũ nếu có
                        if (oldImagePath != null && !oldImagePath.isEmpty() && !oldImagePath.startsWith("http")) {
                            UploadUtil.deleteOldImage(oldImagePath, req.getServletContext());
                        }
                    }
                } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    // Nếu không upload file, dùng URL
                    reward.setImage_url(imageUrl.trim());
                } else if (oldImagePath != null) {
                    // Giữ nguyên ảnh cũ nếu không có thay đổi
                    reward.setImage_url(oldImagePath);
                } else {
                    reward.setImage_url(null);
                }
                
                String stockStr = req.getParameter("stock");
                if (stockStr != null && !stockStr.trim().isEmpty()) {
                    reward.setStock(Integer.parseInt(stockStr));
                } else {
                    reward.setStock(null); // Không giới hạn
                }
                
                reward.setIsActive(req.getParameter("isActive") != null);
                
                if ("edit".equals(action)) {
                    rewardService.updateReward(reward);
                } else {
                    rewardService.saveReward(reward);
                }
                
                req.getSession().setAttribute("success", "Đã " + ("edit".equals(action) ? "cập nhật" : "thêm") + " quà tặng thành công!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/rewards");
    }
}

