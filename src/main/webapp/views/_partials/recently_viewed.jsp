<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page import="config.JpaUtil" %>
<%@ page import="jakarta.persistence.EntityManager" %>
<%@ page import="model.Product" %>
<%@ page import="java.util.*" %>

<%
  // Đọc cookie "viewed"
  String viewedIds = "";
  if (request.getCookies() != null) {
    for (Cookie c : request.getCookies()) {
      if ("viewed".equals(c.getName())) {
        viewedIds = c.getValue();
        break;
      }
    }
  }
  
  List<Product> viewedProducts = new ArrayList<>();
  if (!viewedIds.isEmpty()) {
    String[] ids = viewedIds.split("-");
    EntityManager em = JpaUtil.em();
    try {
      // Lấy tối đa 4 sản phẩm gần nhất
      int count = 0;
      for (int i = ids.length - 1; i >= 0 && count < 4; i--) {
        try {
          int pid = Integer.parseInt(ids[i]);
          Product p = em.find(Product.class, pid);
          if (p != null && p.getIsActive() != null && p.getIsActive()) {
            viewedProducts.add(p);
            count++;
          }
        } catch (Exception ignore) {}
      }
    } finally {
      em.close();
    }
  }
  
  request.setAttribute("recentlyViewed", viewedProducts);
%>

<c:if test="${not empty recentlyViewed}">
  <section class="mt-5">
    <h5 class="mb-3">Đã xem gần đây</h5>
    <div class="row row-cols-2 row-cols-md-4 g-3">
      <c:forEach var="product" items="${recentlyViewed}">
        <div class="col">
          <div class="card h-100">
            <img src="${product.thumbnail}" class="card-img-top" alt="${product.product_name}" 
                 style="height:150px;object-fit:cover;">
            <div class="card-body">
              <h6 class="card-title small mb-1">${product.product_name}</h6>
              <div class="text-primary fw-bold small">
                <fmt:formatNumber value="${product.price}" pattern="#,##0₫"/>
              </div>
            </div>
            <div class="card-footer bg-transparent border-0 pt-0">
              <a href="${pageContext.request.contextPath}/p?id=${product.product_id}" 
                 class="btn btn-sm btn-outline-primary w-100">Xem lại</a>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </section>
</c:if>