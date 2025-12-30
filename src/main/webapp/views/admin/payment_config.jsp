<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<h1 class="h3 mb-2 text-gray-800">Cấu hình thanh toán</h1>
<p class="mb-4">Quản lý các cổng thanh toán và phương thức thanh toán.</p>

<c:if test="${not empty sessionScope.success}">
  <div class="alert alert-success alert-dismissible fade show">
    <i class="bi bi-check-circle"></i> ${sessionScope.success}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="success" scope="session"/>
</c:if>

<div class="card shadow mb-4">
  <div class="card-header py-3 d-flex justify-content-between align-items-center">
    <h6 class="m-0 font-weight-bold text-primary">Phương thức thanh toán</h6>
    <a href="${pageContext.request.contextPath}/admin/payment-config/create" class="btn btn-primary btn-sm">
      <i class="fas fa-plus fa-sm"></i> Thêm phương thức
    </a>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th style="width: 60px;">#</th>
            <th>Phương thức</th>
            <th>Tên hiển thị</th>
            <th>API Endpoint</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center" style="width: 200px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty configs}">
              <tr>
                <td colspan="6" class="text-center text-muted py-4">
                  <i class="bi bi-inbox fs-3"></i>
                  <p class="mb-0 mt-2">Chưa có cấu hình thanh toán nào</p>
                </td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach items="${configs}" var="config" varStatus="st">
                <tr>
                  <td><strong>${st.index + 1}</strong></td>
                  <td>
                    <strong>${config.payment_method}</strong>
                    <c:choose>
                      <c:when test="${config.payment_method eq 'VNPAY'}">
                        <span class="badge text-bg-primary ms-2">Cổng thanh toán</span>
                      </c:when>
                      <c:when test="${config.payment_method eq 'MOMO'}">
                        <span class="badge text-bg-danger ms-2">Ví điện tử</span>
                      </c:when>
                      <c:when test="${config.payment_method eq 'COD'}">
                        <span class="badge text-bg-success ms-2">Tiền mặt</span>
                      </c:when>
                    </c:choose>
                  </td>
                  <td>${config.display_name}</td>
                  <td>
                    <c:choose>
                      <c:when test="${not empty config.api_endpoint}">
                        <small class="text-muted">${config.api_endpoint}</small>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">-</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-center">
                    <form method="post" action="${pageContext.request.contextPath}/admin/payment-config/toggle" style="display: inline;">
                      <input type="hidden" name="id" value="${config.config_id}">
                      <button type="submit" class="btn btn-sm btn-${config.isActive ? 'success' : 'secondary'}">
                        <i class="bi bi-${config.isActive ? 'check-circle' : 'x-circle'}"></i>
                        ${config.isActive ? 'Đang bật' : 'Đã tắt'}
                      </button>
                    </form>
                  </td>
                  <td class="text-center">
                    <div class="btn-group btn-group-sm">
                      <a href="${pageContext.request.contextPath}/admin/payment-config/edit?id=${config.config_id}" 
                         class="btn btn-outline-primary" 
                         title="Chỉnh sửa">
                        <i class="fas fa-pencil-alt"></i>
                      </a>
                      <button type="button" class="btn btn-outline-info" title="Xem chi tiết" data-bs-toggle="modal" data-bs-target="#detailModal-${config.config_id}">
                        <i class="fas fa-eye"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                <div class="modal fade" id="detailModal-${config.config_id}" tabindex="-1">
                  <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h5 class="modal-title">Chi tiết cấu hình: ${config.payment_method}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                      </div>
                      <div class="modal-body">
                        <table class="table table-sm">
                          <tr>
                            <th style="width: 200px;">Merchant ID:</th>
                            <td><code>${config.merchant_id}</code></td>
                          </tr>
                          <tr>
                            <th>Secret Key:</th>
                            <td><code>••••••••</code> <small class="text-muted">(ẩn vì bảo mật)</small></td>
                          </tr>
                          <c:if test="${not empty config.access_key}">
                            <tr>
                              <th>Access Key:</th>
                              <td><code>••••••••</code></td>
                            </tr>
                          </c:if>
                          <c:if test="${not empty config.config_json}">
                            <tr>
                              <th>Config JSON:</th>
                              <td><pre class="small mb-0">${config.config_json}</pre></td>
                            </tr>
                          </c:if>
                        </table>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                      </div>
                    </div>
                  </div>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
</div>