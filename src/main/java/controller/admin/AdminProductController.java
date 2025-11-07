package controller.admin;

import jakarta.servlet.*; 
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException; 
import java.math.BigDecimal;
import java.util.List;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;

@WebServlet(urlPatterns = {"/admin/products", "/admin/products/*"})
public class AdminProductController extends HttpServlet {

  @Override 
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
      throws ServletException, IOException {
    String path = req.getPathInfo();
    EntityManager em = JpaUtil.em();
    try {
      if (path == null || "/".equals(path)){
        List<Product> list = em.createQuery(
          "select p from Product p order by p.updatedDate desc", Product.class)
          .getResultList();
        req.setAttribute("list", list);
        req.getRequestDispatcher("/views/admin/products.jsp").forward(req, resp);
        
      } else if ("/create".equals(path)){
        req.setAttribute("categories", em.createQuery(
          "select c from Category c order by c.id", Category.class).getResultList());
        req.setAttribute("suppliers", em.createQuery(
          "select s from Supplier s where s.isActive=true order by s.supplier_name", 
          Supplier.class).getResultList());
        req.getRequestDispatcher("/views/admin/product_form.jsp").forward(req, resp);
        
      } else if ("/edit".equals(path)){
        int id = parseInt(req.getParameter("id"), 0);
        if (id <= 0) { 
          resp.sendError(400, "Invalid product ID"); 
          return; 
        }
        Product p = em.find(Product.class, id);
        if (p == null) { 
          resp.sendError(404, "Product not found"); 
          return; 
        }
        req.setAttribute("p", p);
        req.setAttribute("categories", em.createQuery(
          "select c from Category c order by c.id", Category.class).getResultList());
        req.setAttribute("suppliers", em.createQuery(
          "select s from Supplier s where s.isActive=true order by s.supplier_name", 
          Supplier.class).getResultList());
        req.getRequestDispatcher("/views/admin/product_form.jsp").forward(req, resp);
        
      } else if ("/delete".equals(path)){
        // CHUYỂN HƯỚNG NẾU DÙNG GET
        resp.sendRedirect(req.getContextPath() + "/admin/products");
        
      } else {
        resp.sendError(404);
      }
    } finally { 
      em.close(); 
    }
  }

  @Override 
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
      throws IOException, ServletException {
    String path = req.getPathInfo();
    EntityManager em = JpaUtil.em();

    try {
        if ("/save".equals(path)) {
            // Logic lưu sản phẩm (Thêm/Sửa)
            int id = parseInt(req.getParameter("id"), 0);
            String name = req.getParameter("product_name");
            String desc = req.getParameter("description");
            BigDecimal price = parseDec(req.getParameter("price"));
            BigDecimal discount = parseDec(req.getParameter("discount"));
            Integer stock = parseInt(req.getParameter("stock"), 0);
            String thumbnail = req.getParameter("thumbnail");
            Integer cateId = parseIntObj(req.getParameter("cate_id"));
            Integer supId = parseIntObj(req.getParameter("supplier_id"));
            boolean active = "on".equals(req.getParameter("isActive"));
            boolean featured = "on".equals(req.getParameter("isFeatured"));
    
            var tx = em.getTransaction(); 
            tx.begin();
            try {
              Product p = (id > 0) ? em.find(Product.class, id) : new Product();
              if (p == null) { 
                tx.rollback(); 
                resp.sendError(404, "Product not found"); 
                return; 
              }
              
              p.setProduct_name(name);
              p.setDescription(desc);
              p.setPrice(price);
              p.setDiscount(discount);
              p.setStock(stock);
              p.setThumbnail(thumbnail);
              
              if (cateId != null) {
                Category cat = em.find(Category.class, cateId);
                if (cat != null) {
                  p.setCategory(cat);
                }
              }
              
              if (supId != null) {
                Supplier sup = em.find(Supplier.class, supId);
                if (sup != null) {
                  p.setSupplier(sup);
                }
              }
              
              p.setIsActive(active);
              p.setIsFeatured(featured);
              
              java.time.LocalDateTime now = java.time.LocalDateTime.now();
              if (p.getProduct_id() == null) {
                p.setCreatedDate(now);
                em.persist(p);
              } else {
                em.merge(p);
              }
              p.setUpdatedDate(now);
              
              tx.commit();
              req.getSession().setAttribute("success", "Đã lưu sản phẩm thành công!");
            } catch(Exception ex) { 
              if(tx.isActive()) tx.rollback(); 
              req.getSession().setAttribute("error", "Lỗi khi lưu sản phẩm: " + ex.getMessage());
              throw ex; 
            }
            
        } else if ("/delete".equals(path)) {
            // Logic xóa sản phẩm (đã chuyển sang POST)
            int id = parseInt(req.getParameter("id"), 0);
            if (id > 0) {
              var tx = em.getTransaction(); 
              tx.begin();
              try {
                Product p = em.find(Product.class, id);
                if (p != null) {
                  em.remove(p);
                  req.getSession().setAttribute("success", "Đã xóa sản phẩm thành công!");
                }
                tx.commit();
              } catch(Exception ex) { 
                if(tx.isActive()) tx.rollback(); 
                req.getSession().setAttribute("error", "Lỗi khi xóa sản phẩm: " + ex.getMessage());
                throw ex; 
              }
            }
        } else {
            resp.sendError(404);
            return;
        }
    } finally {
        if (em.isOpen()) em.close();
    }
    
    // Redirect chung về trang danh sách
    resp.sendRedirect(req.getContextPath() + "/admin/products");
  }

  // --- (Giữ nguyên các hàm helper: parseInt, parseIntObj, parseDec) ---
  private static int parseInt(String s, int defaultValue) { 
    try {
      return Integer.parseInt(s);
    } catch(Exception e) {
      return defaultValue;
    } 
  }
  
  private static Integer parseIntObj(String s) { 
    try {
      return Integer.valueOf(s);
    } catch(Exception e) {
      return null;
    } 
  }
  
  private static BigDecimal parseDec(String s) { 
    try {
      return new BigDecimal(s);
    } catch(Exception e) {
      return null;
    } 
  }
}