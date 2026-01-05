package stnw.controller.order;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import stnw.model.*;
import stnw.service.OrderService;
import stnw.service.VoucherService;
import stnw.service.impl.OrderServiceImpl;
import stnw.service.impl.VoucherServiceImpl;

@WebServlet(urlPatterns = {"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {

    private OrderService orderService;
    private VoucherService voucherService;

    @Override
    public void init() throws ServletException {
        orderService = new OrderServiceImpl();
        voucherService = new VoucherServiceImpl();
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> cart(HttpSession session) {
        var list = (List<CartItem>) session.getAttribute("CART");
        return list != null ? list : new ArrayList<>();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            session.setAttribute("redirectAfterLogin", req.getContextPath() + "/checkout");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        var cartItems = cart(session);
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        // Load danh sách voucher khả dụng
        try {
            var availableVouchers = voucherService.getAvailableVouchers(cartItems);
            req.setAttribute("availableVouchers", availableVouchers);
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi khi load voucher, vẫn tiếp tục với danh sách rỗng
            req.setAttribute("availableVouchers", new ArrayList<>());
        }

        req.getRequestDispatcher("/views/order/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            session.setAttribute("redirectAfterLogin", req.getContextPath() + "/checkout");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        var items = cart(session);
        if (items.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String note = req.getParameter("note");
        String voucherCode = req.getParameter("voucher");
        String payment = Optional.ofNullable(req.getParameter("payment")).orElse("COD");

        Orders order;
        try {
            order = orderService.placeOrder(currentUser, items, fullname, phone, address, note, voucherCode, payment);

            // Đơn hàng đã được tạo thành công, chỉ lưu tên phương thức thanh toán
            String successMessage = "Đơn hàng #" + order.getOrder_id() + " đã được đặt thành công!";
            if ("COD".equals(payment)) {
                successMessage += " Bạn sẽ thanh toán khi nhận hàng.";
            } else {
                successMessage += " Phương thức thanh toán: " + payment;
            }
            
            session.setAttribute("orderSuccess", successMessage);
            session.removeAttribute("CART");
            resp.sendRedirect(req.getContextPath() + "/user/orders");

        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("checkoutError",
                    "Lỗi khi tạo đơn hàng. Vui lòng thử lại hoặc liên hệ hỗ trợ.");
            resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }

}
