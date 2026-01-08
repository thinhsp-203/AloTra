package stnw.controller.admin.voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Voucher;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/vouchers/create")
public class VoucherAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        req.setAttribute("v", new Voucher());
        req.getRequestDispatcher("/views/admin/voucher-form.jsp").forward(req, resp);
    }
}

