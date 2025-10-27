package controller.admin;

import jakarta.servlet.*; 
import jakarta.servlet.http.*;
import java.io.IOException; 
import java.math.BigDecimal;
import java.util.List;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;

public class AdminProductController extends HttpServlet {

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getPathInfo(); // /, /create, /edit, /delete
    EntityManager em = JpaUtil.em();
    try {
      if (path == null || "/".equals(path)){
        List<Product> list = em.createQuery("select p from Product p order by p.updatedDate desc", Product.class).getResultList();
        req.setAttribute("list", list);
        req.getRequestDispatcher("/views/admin/products.jsp").forward(req, resp);
      } else if ("/create".equals(path)){
        req.setAttribute("categories", em.createQuery("select c from Category c order by c.displayOrder", Category.class).getResultList());
        req.setAttribute("suppliers",  em.createQuery("select s from Supplier s where s.isActive=true order by s.supplier_name", Supplier.class).getResultList());
        req.getRequestDispatcher("/views/admin/product_form.jsp").forward(req, resp);
      } else if ("/edit".equals(path)){
        int id = Integer.parseInt(req.getParameter("id"));
        Product p = em.find(Product.class, id);
        if (p == null){ resp.sendError(404); return; }
        req.setAttribute("p", p);
        req.setAttribute("categories", em.createQuery("select c from Category c order by c.displayOrder", Category.class).getResultList());
        req.setAttribute("suppliers",  em.createQuery("select s from Supplier s where s.isActive=true order by s.supplier_name", Supplier.class).getResultList());
        req.getRequestDispatcher("/views/admin/product_form.jsp").forward(req, resp);
      } else if ("/delete".equals(path)){
        int id = Integer.parseInt(req.getParameter("id"));
        var tx = em.getTransaction(); tx.begin();
        try {
          Product p = em.find(Product.class, id);
          if (p != null) em.remove(p);
          tx.commit();
        } catch(Exception ex){ if(tx.isActive()) tx.rollback(); throw ex; }
        resp.sendRedirect(req.getContextPath()+"/admin/products");
      } else {
        resp.sendError(404);
      }
    } finally { em.close(); }
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String path = req.getPathInfo(); // /save
    if (!"/save".equals(path)){ resp.sendError(404); return; }

    int id = parseInt(req.getParameter("id"), 0);
    String name = req.getParameter("product_name");
    String desc = req.getParameter("description");
    BigDecimal price = parseDec(req.getParameter("price"));
    BigDecimal discount = parseDec(req.getParameter("discount"));
    Integer stock = parseInt(req.getParameter("stock"), 0);
    String thumbnail = req.getParameter("thumbnail");
    Integer cateId = parseInt(req.getParameter("cate_id"), null);
    Integer supId  = parseInt(req.getParameter("supplier_id"), null);
    boolean active = "on".equals(req.getParameter("isActive"));
    boolean featured = "on".equals(req.getParameter("isFeatured"));

    EntityManager em = JpaUtil.em();
    var tx = em.getTransaction(); tx.begin();
    try {
      Product p = id>0 ? em.find(Product.class, id) : new Product();
      if (p == null){ tx.rollback(); resp.sendError(404); return; }
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
      if (supId  != null) p.setSupplier(em.find(Supplier.class, supId));
      p.setIsActive(active);
      p.setIsFeatured(featured);
      if (p.getProduct_id()==null) p.setCreatedDate(java.time.LocalDateTime.now());
      p.setUpdatedDate(java.time.LocalDateTime.now());
      if (p.getProduct_id()==null) em.persist(p); else em.merge(p);
      tx.commit();
    } catch(Exception ex){ if(tx.isActive()) tx.rollback(); throw ex; }
    finally { em.close(); }

    resp.sendRedirect(req.getContextPath()+"/admin/products");
  }

  private static Integer parseInt(String s, Integer d){ try{return Integer.valueOf(s);}catch(Exception e){return d;} }
  private static int parseInt(String s, int d){ try{return Integer.parseInt(s);}catch(Exception e){return d;} }
  private static java.math.BigDecimal parseDec(String s){ try{return new java.math.BigDecimal(s);}catch(Exception e){return null;} }
}
