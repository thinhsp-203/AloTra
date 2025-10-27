package controller.admin;

import jakarta.servlet.*; 
import jakarta.servlet.http.*; 
import java.io.IOException; 
import java.util.*;
import config.JpaUtil; 
import jakarta.persistence.EntityManager;

public class AdminReportController extends HttpServlet {
  @Override 
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    EntityManager em = JpaUtil.em();
    try {
      // Doanh thu theo tháng (năm hiện tại)
      var rev = em.createQuery(
        "select YEAR(o.createdDate), MONTH(o.createdDate), sum(o.total_amount) " +
        "from Orders o group by YEAR(o.createdDate), MONTH(o.createdDate) " +
        "order by YEAR(o.createdDate), MONTH(o.createdDate)", Object[].class)
        .getResultList();

      // Top bán chạy
      var top = em.createQuery(
        "select d.product.product_name, sum(d.quantity) from OrderDetail d " +
        "group by d.product.product_name order by sum(d.quantity) desc", Object[].class)
        .setMaxResults(10).getResultList();

      // Tồn kho hiện tại
      var stock = em.createQuery(
        "select p.product_name, p.stock from Product p order by p.stock asc", Object[].class)
        .getResultList();

      req.setAttribute("rev", rev);
      req.setAttribute("top", top);
      req.setAttribute("stock", stock);
      req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);
    } finally { 
      em.close(); 
    }
  }
}