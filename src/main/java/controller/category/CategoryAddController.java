package controller.category;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.Paths;
import model.Category;
import service.CategoryService;
import service.impl.CategoryServiceImpl;

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

    String rel = null;
    Part part = req.getPart("icon");
    if (part != null && part.getSize() > 0) {
      String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
      String realPath = getServletContext().getRealPath("/uploads");
      File dir = new File(realPath);
      if (!dir.exists()) dir.mkdirs();
      part.write(new File(dir, fileName).getPath());
      rel = "uploads/" + fileName; // đường dẫn tương đối để hiển thị
    }

    Category c = new Category();
    c.setName(name);
    c.setIcon(rel);
    service.insert(c);

    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}
