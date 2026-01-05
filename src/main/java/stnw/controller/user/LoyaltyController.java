package stnw.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.PointTransaction;
import stnw.model.Reward;
import stnw.model.User;
import stnw.service.LoyaltyService;
import stnw.service.UserService;
import stnw.service.impl.LoyaltyServiceImpl;
import stnw.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/user/loyalty", "/user/rewards", "/user/point-history", "/user/reward-detail"})
public class LoyaltyController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private LoyaltyService loyaltyService;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        loyaltyService = new LoyaltyServiceImpl();
        userService = new UserServiceImpl();
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
                req.getRequestDispatcher("/views/user/point-history.jsp").forward(req, resp);
            } else if (uri.endsWith("/reward-detail")) {
                // Trang chi tiết quà đã đổi
                String transactionIdParam = req.getParameter("transactionId");
                if (transactionIdParam == null || transactionIdParam.isEmpty()) {
                    req.getSession().setAttribute("error", "Không tìm thấy thông tin giao dịch!");
                    resp.sendRedirect(req.getContextPath() + "/user/point-history");
                    return;
                }
                
                try {
                    Integer transactionId = Integer.parseInt(transactionIdParam);
                    PointTransaction transaction = loyaltyService.getTransactionById(transactionId);
                    
                    if (transaction == null) {
                        req.getSession().setAttribute("error", "Giao dịch không tồn tại!");
                        resp.sendRedirect(req.getContextPath() + "/user/point-history");
                        return;
                    }
                    
                    // Kiểm tra transaction thuộc về user hiện tại
                    if (!transaction.getUser().getId().equals(currentUser.getId())) {
                        req.getSession().setAttribute("error", "Bạn không có quyền xem giao dịch này!");
                        resp.sendRedirect(req.getContextPath() + "/user/point-history");
                        return;
                    }
                    
                    // Kiểm tra transaction là loại REDEEM
                    if (!"REDEEM".equals(transaction.getType()) || transaction.getReward() == null) {
                        req.getSession().setAttribute("error", "Giao dịch không phải là đổi quà!");
                        resp.sendRedirect(req.getContextPath() + "/user/point-history");
                        return;
                    }
                    
                    req.setAttribute("transaction", transaction);
                    req.setAttribute("reward", transaction.getReward());
                    req.setAttribute("points", loyaltyService.getUserPoints(currentUser.getId()));
                    req.getRequestDispatcher("/views/user/reward-detail.jsp").forward(req, resp);
                } catch (NumberFormatException e) {
                    req.getSession().setAttribute("error", "ID giao dịch không hợp lệ!");
                    resp.sendRedirect(req.getContextPath() + "/user/point-history");
                }
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
                PointTransaction transaction = loyaltyService.redeemReward(currentUser, rewardId);
                
                // Refresh user từ DB để cập nhật điểm trong session
                User refreshedUser = userService.getUserById(currentUser.getId());
                if (refreshedUser != null) {
                    req.getSession().setAttribute("currentUser", refreshedUser);
                }
                
                // Redirect đến trang chi tiết quà đã đổi
                resp.sendRedirect(req.getContextPath() + "/user/reward-detail?transactionId=" + transaction.getTransaction_id());
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
