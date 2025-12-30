package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Reward;
import service.AdminRewardService;
import service.impl.AdminRewardServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/rewards")
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
                
                if ("edit".equals(action)) {
                    int id = Integer.parseInt(req.getParameter("id"));
                    reward = rewardService.getRewardById(id);
                    if (reward == null) {
                        req.getSession().setAttribute("error", "Không tìm thấy quà tặng!");
                        resp.sendRedirect(req.getContextPath() + "/admin/rewards");
                        return;
                    }
                } else {
                    reward = new Reward();
                }
                
                reward.setName(req.getParameter("name"));
                reward.setDescription(req.getParameter("description"));
                reward.setPoints_required(Integer.parseInt(req.getParameter("points_required")));
                reward.setImage_url(req.getParameter("image_url"));
                
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

