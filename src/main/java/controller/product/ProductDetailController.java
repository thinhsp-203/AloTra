package controller.product;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
import config.JpaUtil; import jakarta.persistence.EntityManager; import model.Product;

public class ProductDetailController extends HttpServlet {
  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    int id = Integer.parseInt(req.getParameter("id"));
    EntityManager em = JpaUtil.em();
    try {
      Product p = em.find(Product.class, id);
      if (p==null){ resp.sendError(404); return; }
      req.setAttribute("p", p);
      var sameCate = em.createQuery(
        "select x from Product x where x.category.cate_id=:c and x.product_id<>:id order by x.createdDate desc", Product.class)
        .setParameter("c", p.getCategory().getCate_id()).setParameter("id", p.getProduct_id())
        .setMaxResults(8).getResultList();
      var sameSup  = em.createQuery(
        "select x from Product x where x.supplier.supplier_id=:s and x.product_id<>:id order by x.createdDate desc", Product.class)
        .setParameter("s", p.getSupplier().getSupplier_id()).setParameter("id", p.getProduct_id())
        .setMaxResults(8).getResultList();
      req.setAttribute("sameCate", sameCate);
      req.setAttribute("sameSup", sameSup);
      trackViewed(req, resp, id);
      req.getRequestDispatcher("/views/product/detail.jsp").forward(req, resp);
    } finally { em.close(); }
  }
  private void trackViewed(HttpServletRequest req, HttpServletResponse resp, int id){
    String name="viewed", v="";
    if (req.getCookies()!=null) for (var c:req.getCookies()) if (name.equals(c.getName())) v=c.getValue();
    var set=new java.util.LinkedHashSet<>(java.util.Arrays.asList(v.split("-")));
    set.removeIf(String::isBlank); set.add(String.valueOf(id));
    while(set.size()>20) set.remove(set.iterator().next());
    var c=new Cookie(name,String.join("-",set)); c.setPath(req.getContextPath()); c.setMaxAge(30*24*3600); resp.addCookie(c);
  }
}
