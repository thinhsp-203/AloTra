package stnw.controller.admin.voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminVoucherService;
import stnw.service.impl.AdminVoucherServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/vouchers/edit")
public class VoucherEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminVoucherService voucherService;
    
    @Override
    public void init() throws ServletException {
        voucherService = new AdminVoucherServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            req.setAttribute("v", voucherService.getVoucherById(id));
            req.getRequestDispatcher("/views/admin/voucher-form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
        }
    }
}

