package controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Voucher;
import service.AdminVoucherService;
import service.impl.AdminVoucherServiceImpl;

@WebServlet(urlPatterns = {
	    "/admin/vouchers",
	    "/admin/vouchers/create",
	    "/admin/vouchers/edit",
	    "/admin/vouchers/save",
	    "/admin/vouchers/delete"
	})
	public class AdminVoucherController extends HttpServlet {
	    
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private AdminVoucherService voucherService;
	    
	    @Override
	    public void init() throws ServletException {
	        voucherService = new AdminVoucherServiceImpl();
	    }
	    
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	        String uri = req.getRequestURI();
	        
	        if (uri.endsWith("/admin/vouchers")) {
	            req.setAttribute("vouchers", voucherService.getAllVouchers());
	            req.getRequestDispatcher("/views/admin/vouchers.jsp").forward(req, resp);
	            
	        } else if (uri.endsWith("/admin/vouchers/create")) {
	            req.setAttribute("v", new Voucher());
	            req.getRequestDispatcher("/views/admin/voucher_form.jsp").forward(req, resp);
	            
	        } else if (uri.endsWith("/admin/vouchers/edit")) {
	            int id = Integer.parseInt(req.getParameter("id"));
	            req.setAttribute("v", voucherService.getVoucherById(id));
	            req.getRequestDispatcher("/views/admin/voucher_form.jsp").forward(req, resp);
	        }
	    }
	    
	    @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	        String uri = req.getRequestURI();
	        
	        try {
	            if (uri.endsWith("/admin/vouchers/save")) {
	                String idParam = req.getParameter("id");
	                Voucher v = (idParam != null && !idParam.isEmpty()) 
	                    ? voucherService.getVoucherById(Integer.parseInt(idParam)) 
	                    : new Voucher();
	                
	                v.setCode(req.getParameter("code").toUpperCase());
	                v.setDescription(req.getParameter("description"));
	                v.setDiscount_type(req.getParameter("discount_type"));
	                v.setDiscount_value(new BigDecimal(req.getParameter("discount_value")));
	                
	                String minOrder = req.getParameter("min_order_value");
	                v.setMin_order_value((minOrder == null || minOrder.isEmpty()) ? null : new BigDecimal(minOrder));
	                
	                String maxDiscount = req.getParameter("max_discount");
	                v.setMax_discount((maxDiscount == null || maxDiscount.isEmpty()) ? null : new BigDecimal(maxDiscount));
	                
	                String usageLimit = req.getParameter("usage_limit");
	                v.setUsage_limit((usageLimit == null || usageLimit.isEmpty()) ? null : Integer.parseInt(usageLimit));
	                
	                v.setStart_date(LocalDateTime.parse(req.getParameter("start_date")));
	                v.setEnd_date(LocalDateTime.parse(req.getParameter("end_date")));
	                v.setIsActive(req.getParameter("isActive") != null);
	                
	                voucherService.saveVoucher(v);
	                req.getSession().setAttribute("success", "Đã lưu voucher thành công!");
	                
	            } else if (uri.endsWith("/admin/vouchers/delete")) {
	                int id = Integer.parseInt(req.getParameter("id"));
	                voucherService.deleteVoucher(id);
	                req.getSession().setAttribute("success", "Đã xóa voucher!");
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
	        }
	        
	        resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
	    }
	}
