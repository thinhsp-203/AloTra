package controller.product;
import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException; import java.math.BigDecimal; import java.util.*;
import config.JpaUtil; import dao.jpa.ProductQueryRepository; import model.Product;

public class ProductPageController extends HttpServlet {
  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int page = p(req.getParameter("page"),0), size = p(req.getParameter("size"),12);
    Integer cate = po(req.getParameter("cate")), sup = po(req.getParameter("supplier"));
    BigDecimal min = pd(req.getParameter("min")), max = pd(req.getParameter("max"));
    String q = req.getParameter("q");
    var em = JpaUtil.em();
    try {
      var repo = new ProductQueryRepository(em);
      var items = repo.search(cate, sup, min, max, q, page, size);
      long total = repo.count(cate, sup, min, max, q);
      boolean more = (long)(page+1)*size < total;
      resp.setContentType("application/json; charset=UTF-8");
      resp.getWriter().print(json(items, total, more));
    } finally { em.close(); }
  }
  private static int p(String s,int d){try{return Integer.parseInt(s);}catch(Exception e){return d;}}
  private static Integer po(String s){try{return Integer.valueOf(s);}catch(Exception e){return null;}}
  private static BigDecimal pd(String s){try{return new BigDecimal(s);}catch(Exception e){return null;}}
  private static String esc(String s){return s==null?"":s.replace("\\","\\\\").replace("\"","\\\"");}
  private static String json(java.util.List<Product> items,long total,boolean more){
    var sb=new StringBuilder("{\"total\":").append(total).append(",\"hasMore\":").append(more).append(",\"items\":[");
    for(int i=0;i<items.size();i++){
      var p=items.get(i);
      sb.append("{\"id\":").append(p.getProduct_id()).append(",\"name\":\"").append(esc(p.getProduct_name()))
        .append("\",\"price\":").append(p.getPrice()).append(",\"thumb\":\"").append(esc(p.getThumbnail())).append("\"}");
      if(i<items.size()-1) sb.append(",");
    }
    return sb.append("]}").toString();
  }
}
