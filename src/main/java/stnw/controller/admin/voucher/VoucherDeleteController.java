package stnw.controller.admin.voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminVoucherService;
import stnw.service.impl.AdminVoucherServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/vouchers/delete")
public class VoucherDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminVoucherService voucherService;
    
    @Override
    public void init() throws ServletException {
        voucherService = new AdminVoucherServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            voucherService.deleteVoucher(id);
            req.getSession().setAttribute("success", "Đã xóa voucher!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
    }
}

