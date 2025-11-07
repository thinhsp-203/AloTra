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

@WebServlet(urlPatterns = {"/admin/category/edit"})
@MultipartConfig(fileSizeThreshold = 2*1024*1024, maxFileSize = 10*1024*1024, maxRequestSize = 50*1024*1024)
public class CategoryEditController extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private final CategoryService service = new CategoryServiceImpl();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      int id = Integer.parseInt(req.getParameter("id"));
      Category c = service.get(id);
      if (c == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
      req.setAttribute("category", c);
      req.getRequestDispatcher("/views/admin/edit-category.jsp").forward(req, resp);
    } catch (Exception e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    int id = Integer.parseInt(req.getParameter("id"));
    String name = req.getParameter("name");

    
    Category old = service.get(id);
    if (old == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

    String finalFileName = old.getIcon(); // Giữ lại ảnh cũ làm mặc định
    Part part = req.getPart("icon");
    
    if (part != null && part.getSize() > 0) {
      String originalFileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
      
      // Tạo tên file duy nhất
      String extension = "";
      int i = originalFileName.lastIndexOf('.');
      if (i > 0) {
          extension = originalFileName.substring(i);
      }
      finalFileName = "category-" + UUID.randomUUID().toString() + extension;
      
      // SỬA LỖI: Lưu file vào đường dẫn tuyệt đối
      File uploadDir = new File(Constant.UPLOAD_DIRECTORY);
      if (!uploadDir.exists()) uploadDir.mkdirs();
      
      File fileToSave = new File(uploadDir, finalFileName);
      
      try (InputStream input = part.getInputStream()) {
          Files.copy(input, fileToSave.toPath());
      }
      
      // (Tùy chọn: Xóa file ảnh cũ nếu tồn tại)
      if (old.getIcon() != null && !old.getIcon().isEmpty()) {
          File oldFile = new File(uploadDir, old.getIcon());
          if (oldFile.exists()) {
              oldFile.delete();
          }
      }
    }

    Category c = new Category();
    c.setId(id);
    c.setName(name);
    c.setIcon(finalFileName); // Cập nhật tên file mới (hoặc giữ tên file cũ)
    service.edit(c);                  

    req.getSession().setAttribute("success", "Đã cập nhật danh mục!");
    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}