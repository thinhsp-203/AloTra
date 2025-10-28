<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h5 mb-3">${p.product_name}</h1>
<div class="row g-4">
  <div class="col-md-5"><img class="img-fluid rounded border" src="${p.thumbnail}" alt="${p.product_name}"/></div>
  <div class="col-md-7">
    <div class="mb-2"><strong>Giá:</strong> ${p.price}</div>
    <div class="mb-3"><c:out value="${p.description}" escapeXml="false"/></div>
    <form method="post" action="${pageContext.request.contextPath}/cart/add">
      <input type="hidden" name="productId" value="${p.product_id}"/>
      <button class="btn btn-primary">Thêm vào giỏ</button>
    </form>
  </div>
</div>

<%-- SỬA LỖI LẶP LẠI Ở ĐÂY --%>
<h2 class="h6 mt-5">Cùng loại</h2>
<div class="row row-cols-2 row-cols-md-4 g-3">
  <c:forEach var="x" items="${sameCate}">
    <%-- Dùng c:set để truyền đúng đối tượng 'x' vào file con --%>
    <c:set var="p" value="${x}" scope="request" />
    <jsp:include page="/views/_partials/product_card.jsp" />
  </c:forEach>
</div>

<%-- SỬA TƯƠNG TỰ CHO PHẦN CÙNG NHÀ CUNG CẤP --%>
<h2 class="h6 mt-4">Cùng nhà cung cấp</h2>
<div class="row row-cols-2 row-cols-md-4 g-3">
  <c:forEach var="x" items="${sameSup}">
    <c:set var="p" value="${x}" scope="request" />
    <jsp:include page="/views/_partials/product_card.jsp" />
  </c:forEach>
</div>

<jsp:include page="/views/_partials/recently_viewed.jsp"/>