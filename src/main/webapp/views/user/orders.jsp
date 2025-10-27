<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<h2 class="h4 mb-4">Đơn hàng của tôi</h2>

<c:choose>
  <c:when test="${empty orders}">
    <div class="alert alert-info">
      <i class="bi bi-inbox"></i> Bạn chưa có đơn hàng nào.
      <a href="${pageContext.request.contextPath}/products" class="alert-link">Đặt hàng ngay</a>
    </div>
  </c:when>
  <c:otherwise>
    <div class="row g-3">
      <c:forEach var="order" items="${orders}">
        <div class="col-12">
          <div class="card">
            <div class="card-body">
              <div class="row align-items-center">
                <div class="col-md-2">
                  <h6 class="mb-1">Đơn hàng #${order.order_id}</h6>
                  <small class="text-muted">
                    <fmt:formatDate value="${order.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                  </small>
                </div>
                
                <div class="col-md-3">
                  <div class="small text-muted">Khách hàng</div>
                  <div>${order.fullname}</div>
                  <div class="small">${order.phone}</div>
                </div>
                
                <div class="col-md-2">
                  <div class="small text-muted">Tổng tiền</div>
                  <div class="fw-bold text-primary">
                    <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                  </div>
                </div>
                
                <div class="col-md-2">
                  <div class="small text-muted">Thanh toán</div>
                  <span class="badge bg-${order.payment_status eq 'Đã thanh toán' ? 'success' : 'warning'}">
                    ${order.payment_status}
                  </span>
                </div>
                
                <div class="col-md-2">
                  <div class="small text-muted">Trạng thái</div>
                  <c:choose>
                    <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                      <span class="badge bg-secondary">${order.order_status}</span>
                    </c:when>
                    <c:when test="${order.order_status eq 'Đang chuẩn bị'}">
                      <span class="badge bg-info">${order.order_status}</span>
                    </c:when>
                    <c:when test="${order.order_status eq 'Đang giao'}">
                      <span class="badge bg-primary">${order.order_status}</span>
                    </c:when>
                    <c:when test="${order.order_status eq 'Hoàn thành'}">
                      <span class="badge bg-success">${order.order_status}</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge bg-danger">${order.order_status}</span>
                    </c:otherwise>
                  </c:choose>
                </div>
                
                <div class="col-md-1 text-end">
                  <button class="btn btn-sm btn-outline-secondary" 
                          type="button" 
                          data-bs-toggle="collapse" 
                          data-bs-target="#order-${order.order_id}">
                    <i class="bi bi-chevron-down"></i>
                  </button>
                </div>
              </div>
              
              <!-- Chi tiết đơn hàng (collapse) -->
              <div class="collapse mt-3" id="order-${order.order_id}">
                <hr>
                <div class="row">
                  <div class="col-md-6">
                    <h6 class="small text-muted">Địa chỉ giao hàng</h6>
                    <p class="mb-2">${order.address}</p>
                    <c:if test="${not empty order.note}">
                      <h6 class="small text-muted mt-3">Ghi chú</h6>
                      <p class="mb-0"><em>${order.note}</em></p>
                    </c:if>
                  </div>
                  
                  <div class="col-md-6">
                    <h6 class="small text-muted">Phương thức thanh toán</h6>
                    <p>${order.payment_method}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<div class="mt-4">
  <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary">
    <i class="bi bi-arrow-left"></i> Quay lại trang cá nhân
  </a>
</div>