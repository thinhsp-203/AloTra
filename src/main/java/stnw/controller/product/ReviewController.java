package stnw.controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import stnw.model.User;
import stnw.service.ReviewService;
import stnw.service.impl.ReviewServiceImpl;

import java.io.IOException;

@WebServlet("/submit-review")
public class ReviewController extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() throws ServletException {
        reviewService = new ReviewServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        
        String productIdStr = req.getParameter("productId");
        int productId = Integer.parseInt(productIdStr);

        if (currentUser == null) {
            req.getSession().setAttribute("error", "Vui lòng đăng nhập để đánh giá.");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getContextPath() + "/p?id=" + productId);
            return;
        }

        try {
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            boolean ok = reviewService.submitReview(currentUser, productId, rating, comment);
            if (ok) {
                req.getSession().setAttribute("success", "Cảm ơn bạn đã đánh giá sản phẩm!");
            } else {
                req.getSession().setAttribute("error", "Bạn chỉ có thể đánh giá sản phẩm đã mua hoặc sản phẩm không tồn tại.");
            }
            resp.sendRedirect(req.getContextPath() + "/p?id=" + productId);

        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Đã xảy ra lỗi khi gửi đánh giá.");
            resp.sendRedirect(req.getContextPath() + "/p?id=" + productIdStr);
        }
    }
}
