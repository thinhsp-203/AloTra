package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Category;
import model.Product;
import model.Supplier;
import utils.Constant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal; // THÊM IMPORT NÀY
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {
    "/admin/products", 
    "/admin/products/create", 
    "/admin/products/edit", 
    "/admin/products/save", 
    "/admin/products/delete"
})
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024, // 2MB
    maxFileSize = 10 * 1024 * 1024,      // 10MB
    maxRequestSize = 50 * 1024 * 1024     // 50MB
)
public class AdminProductController extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String uri = req.getRequestURI();
    if (uri.endsWith("/admin/products")) {
      this.showProductList(req, resp);
    } else if (uri.endsWith("/admin/products/create")) {
      this.showProductForm(req, resp, null);
    } else if (uri.endsWith("/admin/products/edit")) {
      int id = Integer.parseInt(req.getParameter("id"));
      this.showProductForm(req, resp, id);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String uri = req.getRequestURI();
    if (uri.endsWith("/admin/products/save")) {
      this.saveProduct(req, resp);
    } else if (uri.endsWith("/admin/products/delete")) {
      this.deleteProduct(req, resp);
    }
  }

  private void showProductList(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    EntityManager em = JpaUtil.em();
    TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p ORDER BY p.product_id DESC", Product.class);
    req.setAttribute("list", query.getResultList());
    req.getRequestDispatcher("/views/admin/products.jsp").forward(req, resp);
  }

  private void showProductForm(HttpServletRequest req, HttpServletResponse resp, Integer id)
      throws ServletException, IOException {
    EntityManager em = JpaUtil.em();
    Product p = new Product(); 
    
    if (id != null) {
      p = em.find(Product.class, id); 
    }
    
    // Lấy danh sách Categories và Suppliers
    TypedQuery<Category> cateQuery = em.createQuery("SELECT c FROM Category c", Category.class);
    TypedQuery<Supplier> suppQuery = em.createQuery("SELECT s FROM Supplier s", Supplier.class);
    
    req.setAttribute("p", p);
    req.setAttribute("categories", cateQuery.getResultList());
    req.setAttribute("suppliers", suppQuery.getResultList());
    req.getRequestDispatcher("/views/admin/product_form.jsp").forward(req, resp);
  }
	  private static final String PRODUCT_SUBDIR = "products";
	  private String uploadDirPhysical;
	
	  @Override
	  public void init() throws ServletException {
	      uploadDirPhysical = Paths.get(Constant.UPLOAD_DIRECTORY, PRODUCT_SUBDIR).toFile().getAbsolutePath();
	      File uploadDir = new File(uploadDirPhysical);
	      if (!uploadDir.exists()) uploadDir.mkdirs();
	  }
  private void saveProduct(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    EntityManager em = JpaUtil.em();
    
    try {
      em.getTransaction().begin();
      
      String idParam = req.getParameter("id");
      Product p;
      if (idParam != null && !idParam.isEmpty()) {
        p = em.find(Product.class, Integer.parseInt(idParam));
      } else {
        p = new Product();
        p.setCreatedDate(java.time.LocalDateTime.now()); 
      }
      p.setUpdatedDate(java.time.LocalDateTime.now()); 

      // 1. Lấy thông tin từ form
      p.setProduct_name(req.getParameter("product_name"));
      p.setDescription(req.getParameter("description"));
      
      
      p.setPrice(new BigDecimal(req.getParameter("price")));
      
      String discountParam = req.getParameter("discount");
      if (discountParam == null || discountParam.isEmpty()) {
          p.setDiscount(BigDecimal.ZERO);
      } else {
          p.setDiscount(new BigDecimal(discountParam));
      }
      
      String stockParam = req.getParameter("stock");
      p.setStock( (stockParam == null || stockParam.isEmpty()) ? 0 : Integer.parseInt(stockParam) );

      // Lấy thông tin liên kết
      p.setCategory(em.find(Category.class, Integer.parseInt(req.getParameter("cate_id"))));
      p.setSupplier(em.find(Supplier.class, Integer.parseInt(req.getParameter("supplier_id"))));

      p.setIsActive(req.getParameter("isActive") != null);
      p.setIsFeatured(req.getParameter("isFeatured") != null);

      // 2. XỬ LÝ UPLOAD ẢNH 
      String thumbnailUrlFromUrl = req.getParameter("thumbnailUrl"); 
      Part filePart = req.getPart("thumbnailFile"); // Lấy từ ô File
      String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

      if (originalFileName != null && !originalFileName.isEmpty()) {
          // --- Ưu tiên upload file ---
          String extension = "";
          int i = originalFileName.lastIndexOf('.');
          if (i > 0) {
              extension = originalFileName.substring(i); // .png
          }
          String finalFileName = "product-" + UUID.randomUUID().toString() + extension;
          
          File uploadDir = new File(Constant.UPLOAD_DIRECTORY);
          if (!uploadDir.exists()) uploadDir.mkdirs();
          
          File fileToSave = new File(uploadDirPhysical, finalFileName);

          // Xóa ảnh cũ
          if (p.getThumbnail() != null && !p.getThumbnail().isEmpty() && !p.getThumbnail().startsWith("http")) {
              File oldFile = new File(uploadDir, p.getThumbnail());
              if (oldFile.exists()) {
                  oldFile.delete();
              }
          }

          // Lưu file mới
          try (InputStream input = filePart.getInputStream()) {
              Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
          }
          
          // Lưu tên file vào DB
          p.setThumbnail(PRODUCT_SUBDIR + "/" + finalFileName);
          
      } else if (thumbnailUrlFromUrl != null && !thumbnailUrlFromUrl.isEmpty()) {
          p.setThumbnail(thumbnailUrlFromUrl);
      }

      
      // 3. Lưu vào DB
      if (p.getProduct_id() == null) {
        em.persist(p);
      } else {
        em.merge(p);
      }
      
      em.getTransaction().commit();
      req.getSession().setAttribute("success", "Đã lưu sản phẩm thành công!");
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      e.printStackTrace();
      req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
    } finally {
      em.close();
    }
    
    resp.sendRedirect(req.getContextPath() + "/admin/products");
  }

  private void deleteProduct(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    EntityManager em = JpaUtil.em();
    try {
      em.getTransaction().begin();
      int id = Integer.parseInt(req.getParameter("id"));
      Product p = em.find(Product.class, id);
      if (p != null) {
        em.remove(p);
        
        // (Tùy chọn) Xóa file ảnh nếu tồn tại
        if (p.getThumbnail() != null && !p.getThumbnail().isEmpty() && !p.getThumbnail().startsWith("http")) {
            String fileName = Paths.get(p.getThumbnail()).getFileName().toString();
            File oldFile = new File(uploadDirPhysical, fileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
        
        req.getSession().setAttribute("success", "Đã xóa sản phẩm!");
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      e.printStackTrace();
      req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
    } finally {
      em.close();
    }
    resp.sendRedirect(req.getContextPath() + "/admin/products");
  }
}