package controller.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CartItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@WebServlet(urlPatterns = "/api/cart/update")
public class CartUpdateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        HttpSession session = req.getSession();

        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            String size = req.getParameter("size");
            String toppings = req.getParameter("toppings");
            String action = req.getParameter("action");

            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("CART");
            if (cart == null) {
                resp.getWriter().print("{\"ok\":false, \"message\":\"Giỏ hàng không tồn tại.\"}");
                return;
            }

            Optional<CartItem> itemOpt = cart.stream()
                .filter(item -> item.getProductId() == productId &&
                                Objects.equals(item.getSizeName(), size) &&
                                Objects.equals(item.getToppingsCsv(), toppings))
                .findFirst();

            if (itemOpt.isEmpty()) {
                resp.getWriter().print("{\"ok\":false, \"message\":\"Sản phẩm không có trong giỏ.\"}");
                return;
            }

            CartItem item = itemOpt.get();
            int newQuantity = item.getQuantity();

            if ("increase".equals(action)) newQuantity++;
            else if ("decrease".equals(action)) newQuantity--;
            else if ("remove".equals(action)) newQuantity = 0;

            if (newQuantity <= 0) {
                cart.remove(item);
            } else {
                item.setQuantity(newQuantity);
            }

            BigDecimal subTotal = cart.stream()
                                      .map(CartItem::getLineTotal)
                                      .reduce(BigDecimal.ZERO, BigDecimal::add);

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            String response = String.format(
                "{\"ok\":true, \"newQuantity\":%d, \"lineTotal\":%s, \"lineTotalFormatted\":\"%s\", \"subTotal\":%s, \"subTotalFormatted\":\"%s\", \"cartSize\":%d}",
                newQuantity,
                item.getLineTotal(),
                currencyFormatter.format(item.getLineTotal()),
                subTotal,
                currencyFormatter.format(subTotal),
                cart.size()
            );
            resp.getWriter().print(response);

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false, \"message\":\"Lỗi máy chủ: " + e.getMessage() + "\"}");
        }
    }
}