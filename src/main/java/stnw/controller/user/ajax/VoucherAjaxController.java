package stnw.controller.user.ajax;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.CartItem;
import stnw.service.VoucherService;
import stnw.service.impl.VoucherServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/api/voucher")
public class VoucherAjaxController extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VoucherService voucherService;

    @Override
    public void init() throws ServletException {
        voucherService = new VoucherServiceImpl();
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

        if (code == null || code.isBlank()) {
            resp.getWriter().print("{\"ok\":false, \"message\":\"Vui lòng nhập mã voucher.\"}");
            return;
        }

        var result = voucherService.applyVoucher(code, cart);
        if (!result.ok()) {
            resp.getWriter().print("{\"ok\":false, \"message\":\"" + escape(result.message()) + "\"}");
            return;
        }

        String responseJson = String.format(
                "{\"ok\":true, \"discount\":%s, \"newTotal\":%s, \"message\":\"%s\"}",
                result.discount(), result.newTotal(), escape(result.message())
        );
        resp.getWriter().print(responseJson);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }
}

