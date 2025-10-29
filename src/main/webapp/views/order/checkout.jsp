<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="model.CartItem" %>
<fmt:setLocale value="vi_VN"/>
<%
  @SuppressWarnings("unchecked")
  List<CartItem> items = (List<CartItem>) session.getAttribute("CART");
  if (items == null) items = new java.util.ArrayList<>();
  java.math.BigDecimal total = java.math.BigDecimal.ZERO;
  for (CartItem ci : items) total = total.add(ci.getLineTotal());
%>
<h1 class="h5 mb-3">Thanh toán</h1>

<%-- ADDED: Display checkout errors --%>
<c:if test="${not empty sessionScope.checkoutError}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    ${sessionScope.checkoutError}
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
  <c:remove var="checkoutError" scope="session"/>
</c:if>

<c:if test="${empty sessionScope.CART}">
  <div class="alert alert-warning">Không có sản phẩm. <a href="${pageContext.request.contextPath}/cart/view" class="alert-link">Về giỏ hàng</a></div>
</c:if>

<%-- ... rest of the file remains the same ... --%>