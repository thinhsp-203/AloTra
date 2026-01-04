<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-credit-card text-primary" style="margin-right: 10px;"></i>Cấu hình thanh toán
        </h1>
        <p class="text-muted mb-0">Quản lý các cổng thanh toán và phương thức thanh toán</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/payment-config/create" class="btn btn-primary">
        <i class="fas fa-plus" style="margin-right: 10px;"></i>Thêm phương thức
    </a>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<%-- Payment Configs List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Phương thức thanh toán
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 80px;" class="ps-4">#</th>
                        <th>Phương thức</th>
                        <th>Tên hiển thị</th>
                        <th>API Endpoint</th>
                        <th style="width: 150px;" class="text-center">Trạng thái</th>
                        <th style="width: 200px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty configs}">
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có cấu hình thanh toán nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm phương thức thanh toán mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${configs}" var="config" varStatus="st">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <span class="fw-semibold fs-5">${st.index + 1}</span>
                                    </td>
                                    <td>
                                        <div class="fw-semibold fs-5">${config.payment_method}</div>
                                        <c:choose>
                                            <c:when test="${config.payment_method eq 'VNPAY'}">
                                                <span class="badge bg-primary text-white px-2 py-1">
                                                    <i class="fas fa-shield-alt" style="margin-right: 5px;"></i>Cổng thanh toán
                                                </span>
                                            </c:when>
                                            <c:when test="${config.payment_method eq 'MOMO'}">
                                                <span class="badge bg-danger text-white px-2 py-1">
                                                    <i class="fas fa-wallet" style="margin-right: 5px;"></i>Ví điện tử
                                                </span>
                                            </c:when>
                                            <c:when test="${config.payment_method eq 'COD'}">
                                                <span class="badge bg-success text-white px-2 py-1">
                                                    <i class="fas fa-money-bill-wave" style="margin-right: 5px;"></i>Tiền mặt
                                                </span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="fw-semibold">${config.display_name}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty config.api_endpoint}">
                                                <small class="text-muted">
                                                    <i class="fas fa-link" style="margin-right: 5px;"></i>${config.api_endpoint}
                                                </small>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/payment-config/toggle" style="display: inline;">
                                            <input type="hidden" name="id" value="${config.config_id}">
                                            <button type="submit" class="btn btn-sm ${config.isActive ? 'btn-success' : 'btn-secondary'} text-white px-3 py-2">
                                                <i class="fas ${config.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                                ${config.isActive ? 'Đang bật' : 'Đã tắt'}
                                            </button>
                                        </form>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/payment-config/edit?id=${config.config_id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <button type="button" 
                                                    class="btn btn-sm btn-outline-info" 
                                                    title="Xem chi tiết" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#detailModal-${config.config_id}"
                                                    style="margin: 0 7.5px;">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                
                                <%-- Detail Modal --%>
                                <div class="modal fade" id="detailModal-${config.config_id}" tabindex="-1">
                                    <div class="modal-dialog modal-lg">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title">
                                                    <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Chi tiết cấu hình: ${config.payment_method}
                                                </h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                            </div>
                                            <div class="modal-body">
                                                <table class="table table-sm table-bordered">
                                                    <tr>
                                                        <th style="width: 200px;" class="bg-light">Merchant ID:</th>
                                                        <td><code>${config.merchant_id}</code></td>
                                                    </tr>
                                                    <tr>
                                                        <th class="bg-light">Secret Key:</th>
                                                        <td>
                                                            <code>••••••••</code> 
                                                            <small class="text-muted">
                                                                <i class="fas fa-lock" style="margin-right: 5px;"></i>(ẩn vì bảo mật)
                                                            </small>
                                                        </td>
                                                    </tr>
                                                    <c:if test="${not empty config.access_key}">
                                                        <tr>
                                                            <th class="bg-light">Access Key:</th>
                                                            <td><code>••••••••</code></td>
                                                        </tr>
                                                    </c:if>
                                                    <c:if test="${not empty config.config_json}">
                                                        <tr>
                                                            <th class="bg-light">Config JSON:</th>
                                                            <td>
                                                                <pre class="small mb-0 bg-light p-2 rounded"><code>${config.config_json}</code></pre>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </table>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                                    <i class="fas fa-times" style="margin-right: 10px;"></i>Đóng
                                                </button>
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
