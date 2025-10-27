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
<h1 class="h5 mb-3">Thanh toán</h1>
<c:if test="${empty sessionScope.CART}">
  <div class="alert alert-warning">Không có sản phẩm. <a href="${pageContext.request.contextPath}/cart/view">Về giỏ hàng</a></div>
</c:if>
<c:if test="${not empty sessionScope.CART}">
  <div class="row g-4">
    <div class="col-md-7">
      <form method="post" action="${pageContext.request.contextPath}/checkout">
        <div class="mb-2"><label class="form-label">Họ tên</label>
          <input class="form-control" name="fullname" required /></div>
        <div class="mb-2"><label class="form-label">Điện thoại</label>
          <input class="form-control" name="phone" required /></div>
        <div class="mb-2"><label class="form-label">Địa chỉ</label>
          <textarea class="form-control" name="address" required></textarea></div>
        <div class="mb-2"><label class="form-label">Ghi chú</label>
          <textarea class="form-control" name="note"></textarea></div>

        <div class="mb-2"><label class="form-label">Mã giảm giá</label>
          <input class="form-control" name="voucher" placeholder="ALOTRA10 / FREESHIP ..."/></div>

        <div class="mb-3"><label class="form-label">Hình thức thanh toán</label>
          <select class="form-select" name="payment">
            <option value="COD">COD</option>
            <option value="Bank">Ngân hàng ảo</option>
            <option value="MoMo">MoMo (ảo)</option>
            <option value="VNPay">VNPay (ảo)</option>
          </select>
        </div>

        <button class="btn btn-primary">Đặt hàng</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/cart/view">Quay lại giỏ</a>
      </form>
    </div>
    <div class="col-md-5">
      <div class="card">
        <div class="card-header fw-semibold">Tóm tắt</div>
        <div class="card-body">
          <c:forEach var="ci" items="${sessionScope.CART}">
            <div class="d-flex justify-content-between">
              <div>${ci.productName} <span class="text-muted small">x${ci.quantity}</span></div>
              <div>${ci.lineTotal}</div>
            </div>
          </c:forEach>
          <hr/>
          <div class="d-flex justify-content-between"><div>Tạm tính</div><div><%= total %></div></div>
        </div>
      </div>
    </div>
  </div>
</c:if>
