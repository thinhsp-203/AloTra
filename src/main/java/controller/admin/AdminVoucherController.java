package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Voucher;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Sửa từ LocalDate sang LocalDateTime

@WebServlet(urlPatterns = {
    "/admin/vouchers",
    "/admin/vouchers/create",
    "/admin/vouchers/edit",
    "/admin/vouchers/save",
    "/admin/vouchers/delete"
})
public class AdminVoucherController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/admin/vouchers")) {
            this.showVoucherList(req, resp);
        } else if (uri.endsWith("/admin/vouchers/create")) {
            this.showVoucherForm(req, resp, null);
        } else if (uri.endsWith("/admin/vouchers/edit")) {
            // SỬA LỖI: Dùng voucher_id
            int id = Integer.parseInt(req.getParameter("id"));
            this.showVoucherForm(req, resp, id);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/admin/vouchers/save")) {
            this.saveVoucher(req, resp);
        } else if (uri.endsWith("/admin/vouchers/delete")) {
            this.deleteVoucher(req, resp);
        }
    }

    private void showVoucherList(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        TypedQuery<Voucher> query = em.createQuery("SELECT v FROM Voucher v ORDER BY v.end_date DESC", Voucher.class);
        req.setAttribute("vouchers", query.getResultList());
        req.getRequestDispatcher("/views/admin/vouchers.jsp").forward(req, resp);
    }

    private void showVoucherForm(HttpServletRequest req, HttpServletResponse resp, Integer id)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        Voucher v = new Voucher();
        if (id != null) {
            v = em.find(Voucher.class, id);
        }
        req.setAttribute("v", v);
        req.getRequestDispatcher("/views/admin/voucher_form.jsp").forward(req, resp);
    }

    private void saveVoucher(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            String idParam = req.getParameter("id");
            Voucher v;
            if (idParam != null && !idParam.isEmpty()) {
                v = em.find(Voucher.class, Integer.parseInt(idParam));
            } else {
                v = new Voucher();
            }

            v.setCode(req.getParameter("code").toUpperCase());
            v.setDescription(req.getParameter("description"));
            
            // SỬA LỖI: Dùng đúng tên trường trong Model
            v.setDiscount_type(req.getParameter("discount_type")); 
            v.setDiscount_value(new BigDecimal(req.getParameter("discount_value")));
            
            // Xử lý các trường số mới (có thể null)
            String minOrder = req.getParameter("min_order_value");
            v.setMin_order_value((minOrder == null || minOrder.isEmpty()) ? null : new BigDecimal(minOrder));
            
            String maxDiscount = req.getParameter("max_discount");
            v.setMax_discount((maxDiscount == null || maxDiscount.isEmpty()) ? null : new BigDecimal(maxDiscount));
            
            String usageLimit = req.getParameter("usage_limit");
            v.setUsage_limit((usageLimit == null || usageLimit.isEmpty()) ? null : Integer.parseInt(usageLimit));
            
            // SỬA LỖI: Dùng LocalDateTime.parse
            v.setStart_date(LocalDateTime.parse(req.getParameter("start_date")));
            v.setEnd_date(LocalDateTime.parse(req.getParameter("end_date")));
            
            v.setIsActive(req.getParameter("isActive") != null);

            if (v.getVoucher_id() == null) {
                v.setUsed_count(0); // Khởi tạo
                em.persist(v);
            } else {
                em.merge(v);
            }
            
            em.getTransaction().commit();
            req.getSession().setAttribute("success", "Đã lưu voucher thành công!");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
    }

    private void deleteVoucher(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            int id = Integer.parseInt(req.getParameter("id"));
            Voucher v = em.find(Voucher.class, id);
            if (v != null) {
                em.remove(v);
                req.getSession().setAttribute("success", "Đã xóa voucher!");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
    }
}