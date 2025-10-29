package controller.product;

import config.JpaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/products")
public class ProductsListController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var em = JpaUtil.em();
        try {
            // Lấy danh sách categories để hiển thị filter
            var categories = em.createQuery("SELECT c FROM Category c ORDER BY c.id", model.Category.class)
                              .getResultList();
            req.setAttribute("categories", categories);
            
            // Lấy các tham số filter
            String keyword = req.getParameter("q");
            String cateParam = req.getParameter("cate");
            String supplierParam = req.getParameter("supplier");
            
            // Set lại các giá trị đã chọn để giữ trạng thái filter
            req.setAttribute("searchKeyword", keyword != null ? keyword.trim() : "");
            req.setAttribute("selectedCate", cateParam != null ? cateParam : "");
            req.setAttribute("selectedSupplier", supplierParam != null ? supplierParam : "");
            
            // Lấy thông tin category nếu có filter
            if (cateParam != null && !cateParam.isEmpty()) {
                try {
                    int cateId = Integer.parseInt(cateParam);
                    var category = em.find(model.Category.class, cateId);
                    req.setAttribute("currentCategory", category);
                } catch (NumberFormatException e) {
                    // Ignore invalid category ID
                }
            }

            req.getRequestDispatcher("/views/product/list.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }
}