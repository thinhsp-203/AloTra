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

    String rel = null;
    Part part = req.getPart("icon");
    if (part != null && part.getSize() > 0) {
      String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
      String realPath = getServletContext().getRealPath("/uploads");
      File dir = new File(realPath);
      if (!dir.exists()) dir.mkdirs();
      File out = new File(dir, fileName);
      part.write(out.getPath());
      rel = "uploads/" + fileName;     
    } else {
      rel = old.getIcon();             
    }

    Category c = new Category();
    c.setId(id);
    c.setName(name);
    c.setIcon(rel);
    service.edit(c);                  

    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}
