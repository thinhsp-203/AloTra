package controller.category;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.Category;
import service.CategoryService;
import service.impl.CategoryServiceImpl;
import utils.UploadType;
import utils.UploadUtil;

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
    String isDrinkParam = req.getParameter("isDrink");
    Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);

    String iconPath = null; // Relative path sẽ lưu vào DB: "uploads/categories/filename"
    Part part = req.getPart("icon");
    
    if (part != null && part.getSize() > 0) {
      iconPath = UploadUtil.save(part, UploadType.CATEGORIES, req.getServletContext());
    }

    Category c = new Category();
    c.setName(name);
    c.setIcon(iconPath); // Lưu relative path: "uploads/categories/filename" hoặc null
    c.setIsDrink(isDrink);
    service.insert(c);

    req.getSession().setAttribute("success", "Đã thêm danh mục thành công!");
    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}