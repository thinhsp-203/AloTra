package controller.api;

import config.JpaUtil;
import dao.jpa.VoucherRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CartItem;
import model.Voucher;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class VoucherApiController extends HttpServlet {

    private VoucherRepository voucherRepo;

    @Override
    public void init() throws ServletException {
        voucherRepo = new VoucherRepository();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String code = req.getParameter("code");

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) req.getSession().getAttribute("CART");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        BigDecimal total = cart.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (code == null || code.isBlank()) {
            resp.getWriter().print("{\"ok\":false, \"message\":\"Vui lòng nhập mã voucher.\"}");
            return;
        }

        EntityManager em = JpaUtil.em(); 
        try {
            Optional<Voucher> vopt = voucherRepo.findActiveByCode(code.trim(), em);

            if (vopt.isPresent()) { 
                Voucher v = vopt.get(); 
                
                if (v.getMin_order_value() != null && total.compareTo(v.getMin_order_value()) < 0) {
                    resp.getWriter().print("{\"ok\":false, \"message\":\"Đơn hàng chưa đủ điều kiện áp dụng mã.\"}");
                    return;
                }

                BigDecimal discountAmount;
                if ("Percent".equalsIgnoreCase(v.getDiscount_type())) {
                    discountAmount = total.multiply(v.getDiscount_value().divide(BigDecimal.valueOf(100)));
                } else {
                    discountAmount = v.getDiscount_value();
                }

                if (v.getMax_discount() != null && discountAmount.compareTo(v.getMax_discount()) > 0) {
                    discountAmount = v.getMax_discount();
                }

                BigDecimal newTotal = total.subtract(discountAmount);
                newTotal = newTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newTotal;

                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                String responseJson = String.format(
                        "{\"ok\":true, \"discount\":%s, \"newTotal\":%s, \"discountFormatted\":\"%s\", \"newTotalFormatted\":\"%s\", \"message\":\"Áp dụng mã thành công!\"}",
                        discountAmount, newTotal, currencyFormatter.format(discountAmount), currencyFormatter.format(newTotal)
                );
                resp.getWriter().print(responseJson);

            } else {
                resp.getWriter().print("{\"ok\":false, \"message\":\"Mã giảm giá không hợp lệ hoặc đã hết hạn.\"}");
            }
        } finally {
            em.close();
        }
    }
}