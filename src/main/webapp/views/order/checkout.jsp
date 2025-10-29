<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<h1 class="h5 mb-4">Thanh toán</h1>

<c:if test="${not empty sessionScope.checkoutError}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    ${sessionScope.checkoutError}
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
  <c:remove var="checkoutError" scope="session"/>
</c:if>

<c:if test="${empty sessionScope.CART}">
  <div class="alert alert-info">Giỏ hàng của bạn đang trống. <a href="${pageContext.request.contextPath}/products" class="alert-link">Bắt đầu mua sắm</a></div>
</c:if>

<c:if test="${not empty sessionScope.CART}">
    <c:set var="total" value="${0}" />
    <c:forEach var="item" items="${sessionScope.CART}">
        <c:set var="total" value="${total + item.lineTotal}" />
    </c:forEach>

    <div class="row g-4">
        <div class="col-md-7">
            <div class="card">
                <div class="card-header"><h5 class="card-title mb-0">Thông tin giao hàng</h5></div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/checkout">
                        <div class="mb-3"><label class="form-label">Họ tên</label>
                            <input class="form-control" name="fullname" required value="${sessionScope.currentUser.fullname}"/>
                        </div>
                        <div class="mb-3"><label class="form-label">Điện thoại</label>
                            <input class="form-control" name="phone" required value="${sessionScope.currentUser.phone}"/>
                        </div>
                        <div class="mb-3"><label class="form-label">Địa chỉ</label>
                            <textarea class="form-control" name="address" required rows="3">${sessionScope.currentUser.address}</textarea>
                        </div>
                        <div class="mb-3"><label class="form-label">Ghi chú</label>
                            <textarea class="form-control" name="note" rows="2"></textarea>
                        </div>

                        <hr>
                        <h6 class="mb-3">Thanh toán & Mã giảm giá</h6>
                        <div class="row g-3 align-items-end">
                            <div class="col-md-6">
                                <label class="form-label">Hình thức thanh toán</label>
                                <select class="form-select" name="payment">
                                    <option value="COD">Thanh toán khi nhận hàng (COD)</option>
                                    <option value="Bank">Chuyển khoản Ngân hàng</option>
                                    <option value="MoMo">Ví điện tử MoMo</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Mã giảm giá</label>
                                <input class="form-control" name="voucher" id="voucher-code" placeholder="Nhập mã để tự động áp dụng"/>
                            </div>
                        </div>
                        <div id="voucher-message" class="mt-2 small"></div>
                        <hr>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">Xác nhận đặt hàng</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-md-5">
            <div class="card position-sticky" style="top: 20px;">
                <div class="card-header"><h5 class="card-title mb-0">Tóm tắt đơn hàng</h5></div>
                <div class="card-body">
                    <ul class="list-group list-group-flush">
                        <c:forEach var="ci" items="${sessionScope.CART}">
                            <li class="list-group-item d-flex justify-content-between align-items-center px-0">
                                <div>
                                    <div>${ci.productName} <span class="text-muted">x${ci.quantity}</span></div>
                                    <c:if test="${not empty ci.toppingsCsv}"><small class="text-muted">Topping: ${ci.toppingsCsv}</small></c:if>
                                </div>
                                <span class="fw-bold"><fmt:formatNumber value="${ci.lineTotal}" pattern="#,##0₫"/></span>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <div class="card-footer">
                    <div class="d-flex justify-content-between">
                        <span>Tạm tính</span>
                        <strong id="subtotal-display"><fmt:formatNumber value="${total}" pattern="#,##0₫"/></strong>
                    </div>
                    <div class="d-flex justify-content-between text-danger">
                        <span>Giảm giá</span>
                        <strong id="discount-display">0₫</strong>
                    </div>
                    <hr>
                    <div class="d-flex justify-content-between h5">
                        <strong>Tổng cộng</strong>
                        <strong class="text-primary" id="grand-total-display"><fmt:formatNumber value="${total}" pattern="#,##0₫"/></strong>
                    </div>
                </div>
            </div>
             <div class="mt-3 d-grid">
                <a href="${pageContext.request.contextPath}/cart/view" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Quay lại giỏ hàng</a>
            </div>
        </div>
    </div>
</c:if>