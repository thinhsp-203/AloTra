package stnw.controller.admin.reward;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminRewardService;
import stnw.service.impl.AdminRewardServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/rewards/delete")
public class RewardDeleteController extends HttpServlet {
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
            rewardService.deleteReward(id);
            req.getSession().setAttribute("success", "Đã xóa quà tặng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/rewards");
    }
}

