<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.util.*" %>
<%@ page import="model.CartItem" %>
<%
  @SuppressWarnings("unchecked")
  List<CartItem> items = (List<CartItem>) session.getAttribute("CART");
  if (items == null) items = new java.util.ArrayList<>();
  java.math.BigDecimal total = java.math.BigDecimal.ZERO;
  for (CartItem ci : items) total = total.add(ci.getLineTotal());
%>
<h1 class="h5 mb-3">Giỏ hàng</h1>
<c:if test="${empty sessionScope.CART}">
  <div class="alert alert-info">Giỏ hàng trống.</div>
</c:if>
<c:if test="${not empty sessionScope.CART}">
  <table class="table table-sm align-middle">
    <thead><tr><th>Sản phẩm</th><th>Size</th><th>Topping</th><th>SL</th><th>Đơn giá</th><th>Tổng</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="ci" items="${sessionScope.CART}">
      <tr>
        <td>${ci.productName}</td>
        <td>${ci.sizeName}</td>
        <td>${ci.toppingsCsv}</td>
        <td>${ci.quantity}</td>
        <td>${ci.unitPrice}</td>
        <td>${ci.lineTotal}</td>
        <td>
          <form method="post" action="${pageContext.request.contextPath}/cart/remove">
            <input type="hidden" name="productId" value="${ci.productId}"/>
            <input type="hidden" name="size" value="${ci.sizeName}"/>
            <input type="hidden" name="toppings" value="${ci.toppingsCsv}"/>
            <button class="btn btn-sm btn-outline-danger">Xóa</button>
          </form>
        </td>
      </tr>
    </c:forEach>
    </tbody>
    <tfoot>
      <tr><th colspan="5" class="text-end">Tạm tính</th><th colspan="2"><%= total %></th></tr>
    </tfoot>
  </table>
  <a class="btn btn-primary" href="${pageContext.request.contextPath}/checkout">Đặt hàng</a>
</c:if>
