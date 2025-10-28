<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<h1 class="h4 mb-4">Quản lý đơn hàng</h1>

<!-- Filter -->
<div class="card mb-4">
  <div class="card-body">
    <form method="get" action="${pageContext.request.contextPath}/admin/orders" class="row g-3">
      <div class="col-md-4">
        <input type="text" class="form-control" name="keyword" placeholder="Tìm theo tên, SĐT..." value="${keyword}">
      </div>
      <div class="col-md-3">
        <select class="form-select" name="status">
          <option value="">-- Tất cả trạng thái --</option>
          <option value="Chờ xác nhận" ${selectedStatus eq 'Chờ xác nhận' ? 'selected' : ''}>Chờ xác nhận</option>
          <option value="Đang chuẩn bị" ${selectedStatus eq 'Đang chuẩn bị' ? 'selected' : ''}>Đang chuẩn bị</option>
          <option value="Đang giao" ${selectedStatus eq 'Đang giao' ? 'selected' : ''}>Đang giao</option>
          <option value="Hoàn thành" ${selectedStatus eq 'Hoàn thành' ? 'selected' : ''}>Hoàn thành</option>
          <option value="Đã hủy" ${selectedStatus eq 'Đã hủy' ? 'selected' : ''}>Đã hủy</option>
        </select>
      </div>
      <div class="col-md-2">
        <button type="submit" class="btn btn-primary w-100">Lọc</button>
      </div>
      <div class="col-md-2">
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary w-100">Đặt lại</a>
      </div>
    </form>
  </div>
</div>

<!-- Orders Table -->
<div class="table-responsive">
  <table class="table table-hover align-middle">
    <thead class="table-light">
      <tr>
        <th>Mã ĐH</th>
        <th>Khách hàng</th>
        <th>Điện thoại</th>
        <th>Tổng tiền</th>
        <th>Thanh toán</th>
        <th>Trạng thái</th>
        <th>Ngày đặt</th>
        <th>Thao tác</th>
      </tr>
    </thead>
    <tbody>
      <c:choose>
        <c:when test="${empty orders}">
          <tr>
            <td colspan="8" class="text-center text-muted py-4">Không có đơn hàng nào</td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="order" items="${orders}">
            <tr>
              <td><strong>#${order.order_id}</strong></td>
              <td>${order.fullname}</td>
              <td>${order.phone}</td>
              <td class="text-primary fw-bold">
                <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
              </td>
              <td>
                <span class="badge bg-${order.payment_status eq 'Đã thanh toán' ? 'success' : 'warning'}">
                  ${order.payment_status}
                </span>
              </td>
              <td>
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
              </td>
              <td>
                <fmt:formatDate value="${order.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
              </td>
              <td>
                <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${order.order_id}" 
                   class="btn btn-sm btn-outline-primary">
                  <i class="bi bi-eye"></i> Chi tiết
                </a>
              </td>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </tbody>
  </table>
</div>

<style>
  .badge { font-size: 0.85rem; padding: 0.35rem 0.65rem; }
</style>