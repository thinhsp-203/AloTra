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
    
    <%-- Đã xóa dòng mô tả bị lặp --%>
    <div class.mb-4"><c:out value="${p.description}" escapeXml="false"/></div>

    <%-- Chỉ giữ lại một form duy nhất để thêm sản phẩm và topping --%>
    <form id="addToCartForm">
        <input type="hidden" name="productId" value="${p.product_id}"/>

        <%-- Phần chọn Topping --%>
        <c:if test="${not empty toppings}">
            <div class="mb-3">
                <h6 class="h6">Chọn topping (tùy chọn)</h6>
                <c:forEach var="top" items="${toppings}">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="topping" value="${top.topping_id}" id="top-${top.topping_id}">
                        <label class="form-check-label" for="top-${top.topping_id}">
                            ${top.topping_name}
                            <span class="text-muted small">+<fmt:formatNumber value="${top.price}" pattern="#,##0₫"/></span>
                        </label>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <button type="submit" class="btn btn-primary btn-lg">
            <i class="bi bi-cart-plus-fill me-2"></i> Thêm vào giỏ
        </button>
    </form>
    <%-- Đã xóa form thừa ở đây --%>
  </div>
</div>

<%-- Phần sản phẩm liên quan --%>
<h2 class="h6 mt-5">Cùng loại</h2>
<div class="row row-cols-2 row-cols-md-4 g-3">
  <c:forEach var="x" items="${sameCate}">
    <c:set var="p" value="${x}" scope="request" />
    <jsp:include page="/views/_partials/product_card.jsp" />
  </c:forEach>
</div>

<h2 class="h6 mt-4">Cùng nhà cung cấp</h2>
<div class="row row-cols-2 row-cols-md-4 g-3">
  <c:forEach var="x" items="${sameSup}">
    <c:set var="p" value="${x}" scope="request" />
    <jsp:include page="/views/_partials/product_card.jsp" />
  </c:forEach>
</div>

<jsp:include page="/views/_partials/recently_viewed.jsp"/>