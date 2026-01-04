package controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PointTransaction;
import model.Reward;
import model.User;
import service.LoyaltyService;
import service.impl.LoyaltyServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/user/loyalty", "/user/rewards", "/user/point-history"})
public class LoyaltyController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private LoyaltyService loyaltyService;
    
    @Override
    public void init() throws ServletException {
        loyaltyService = new LoyaltyServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            req.getSession().setAttribute("redirectAfterLogin", req.getRequestURI());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/loyalty")) {
                // Trang chính hội viên - hiển thị điểm và quà tặng
                Integer points = loyaltyService.getUserPoints(currentUser.getId());
                List<Reward> rewards = loyaltyService.getActiveRewards();
                req.setAttribute("points", points);
                req.setAttribute("rewards", rewards);
                req.getRequestDispatcher("/views/user/loyalty.jsp").forward(req, resp);
            } else if (uri.endsWith("/rewards")) {
                // Trang danh sách quà tặng
                List<Reward> rewards = loyaltyService.getActiveRewards();
                Integer points = loyaltyService.getUserPoints(currentUser.getId());
                req.setAttribute("rewards", rewards);
                req.setAttribute("points", points);
                req.getRequestDispatcher("/views/user/rewards.jsp").forward(req, resp);
            } else if (uri.endsWith("/point-history")) {
                // Trang lịch sử giao dịch điểm
                List<PointTransaction> transactions = loyaltyService.getPointHistory(currentUser.getId());
                Integer points = loyaltyService.getUserPoints(currentUser.getId());
                req.setAttribute("transactions", transactions);
                req.setAttribute("points", points);
                req.getRequestDispatcher("/views/user/point_history.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            req.getRequestDispatcher("/views/user/loyalty.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String action = req.getParameter("action");
        
        try {
            if ("redeem".equals(action)) {
                Integer rewardId = Integer.parseInt(req.getParameter("rewardId"));
                loyaltyService.redeemReward(currentUser, rewardId);
                
                // Refresh user từ DB để cập nhật điểm trong session
                jakarta.persistence.EntityManager em = config.JpaUtil.em();
                try {
                    User refreshedUser = em.find(User.class, currentUser.getId());
                    if (refreshedUser != null) {
                        req.getSession().setAttribute("currentUser", refreshedUser);
                    }
                } finally {
                    em.close();
                }
                
                req.getSession().setAttribute("success", "Đổi quà thành công!");
                resp.sendRedirect(req.getContextPath() + "/user/rewards");
            }
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/user/rewards");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/user/rewards");
        }
    }
}

