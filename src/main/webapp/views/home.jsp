<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h2 class="h5 mb-3">Sản phẩm nổi bật</h2>
<div class="row row-cols-2 row-cols-md-4 g-3 mb-4">
  <c:forEach var="product" items="${featured}">
    <jsp:include page="/views/_partials/product_card.jsp">
      <jsp:param name="product" value="product" />
    </jsp:include>
  </c:forEach>
</div>

<h2 class="h5 mb-3">Sản phẩm mới</h2>
<div class="row row-cols-2 row-cols-md-4 g-3">
  <c:forEach var="product" items="${newest}">
    <jsp:include page="/views/_partials/product_card.jsp">
      <jsp:param name="product" value="product" />
    </jsp:include>
  </c:forEach>
</div>
