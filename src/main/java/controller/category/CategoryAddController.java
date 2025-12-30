package controller.category;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import model.Category;
import service.CategoryService;
import service.impl.CategoryServiceImpl;
import utils.Constant; // Import Constant

@WebServlet(urlPatterns = {"/admin/category/add"})
@MultipartConfig(fileSizeThreshold = 2*1024*1024, maxFileSize = 10*1024*1024, maxRequestSize = 50*1024*1024)
public class CategoryAddController extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private final CategoryService service = new CategoryServiceImpl();

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/views/admin/add-category.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String name = req.getParameter("name");

    String finalFileName = null; // Tên file sẽ lưu vào DB
    Part part = req.getPart("icon");
    
    if (part != null && part.getSize() > 0) {
      String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
      
      // Tạo tên file duy nhất để tránh trùng lặp
      String extension = "";
      int i = originalFileName.lastIndexOf('.');
      if (i > 0) {
          extension = originalFileName.substring(i); // Lấy cả dấu . (vd: .png)
      }
      finalFileName = "category-" + UUID.randomUUID().toString() + extension;
      
      // Lưu file vào thư mục uploads/categories
      String uploadPath = Constant.getUploadPath(req.getServletContext());
      File categoriesDir = new File(uploadPath, "categories");
      if (!categoriesDir.exists()) categoriesDir.mkdirs();
      
      File fileToSave = new File(categoriesDir, finalFileName);
      
      try (InputStream input = part.getInputStream()) {
          Files.copy(input, fileToSave.toPath());
      }
    }

    Category c = new Category();
    c.setName(name);
    c.setIcon(finalFileName); // Chỉ lưu tên file duy nhất
    service.insert(c);

    req.getSession().setAttribute("success", "Đã thêm danh mục thành công!");
    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}