<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<div class="row g-4">
  <div class="col-md-5">
    <img class="img-fluid rounded border shadow-sm" src="${p.thumbnail}" alt="${p.product_name}"/>
  </div>
  <div class="col-md-7">
    <h1 class="h3 mb-3">${p.product_name}</h1>
    
    <div class="mb-3 p-3 bg-light rounded">
        <span class="h4 text-primary fw-bold">
            <fmt:formatNumber value="${p.price}" pattern="#,##0₫"/>
        </span>
        <%-- Optional: Hiển thị giá cũ nếu có giảm giá --%>
        <c:if test="${p.discount > 0}">
            <span class="ms-2 text-muted small text-decoration-line-through">
                <fmt:formatNumber value="${p.price / (1 - p.discount/100)}" pattern="#,##0₫"/>
            </span>
            <span class="ms-2 badge bg-danger">${p.discount}% OFF</span>
        </c:if>
    </div>
    
    <div class="mb-4"><c:out value="${p.description}" escapeXml="false"/></div>
    
    <form method="post" action="${pageContext.request.contextPath}/cart/add">
      <input type="hidden" name="productId" value="${p.product_id}"/>
      <button class="btn btn-primary btn-lg">
          <i class="bi bi-cart-plus-fill me-2"></i> Thêm vào giỏ
      </button>
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