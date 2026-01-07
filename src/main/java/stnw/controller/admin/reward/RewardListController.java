package stnw.controller.admin.reward;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminRewardService;
import stnw.service.impl.AdminRewardServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/rewards")
public class RewardListController extends HttpServlet {
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
            try {
                int id = Integer.parseInt(idParam);
                stnw.model.Reward reward = rewardService.getRewardById(id);
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
}

