<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-receipt text-primary me-2"></i>Quản lý đơn hàng
        </h1>
        <p class="text-muted mb-0">Theo dõi và quản lý tất cả đơn hàng của khách hàng</p>
    </div>
    <div class="text-end">
        <span class="badge bg-primary fs-6 px-3 py-2">
            <i class="fas fa-shopping-cart me-2"></i>Tổng: ${fn:length(orders)} đơn
        </span>
    </div>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle me-2"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<%-- Filter Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-primary text-white py-3">
        <h6 class="m-0 font-weight-bold">
            <i class="fas fa-filter me-2"></i>Tìm kiếm & Lọc đơn hàng
        </h6>
    </div>
    <div class="card-body p-4">
        <form method="get" action="${pageContext.request.contextPath}/admin/orders" id="filterForm">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-search me-2"></i>Tìm kiếm
                    </label>
                    <input type="text" class="form-control" name="keyword" 
                           placeholder="Nhập tên hoặc số điện thoại khách hàng..." 
                           value="${keyword}"
                           onkeyup="if(event.key === 'Enter') document.getElementById('filterForm').submit();">
                </div>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-tag me-2"></i>Trạng thái
                    </label>
                    <select class="form-select" name="status" id="statusFilter" onchange="document.getElementById('filterForm').submit();">
                        <option value="">-- Tất cả trạng thái --</option>
                        <option value="Chờ xác nhận" ${selectedStatus eq 'Chờ xác nhận' ? 'selected' : ''}>
                            Chờ xác nhận
                        </option>
                        <option value="Đang chuẩn bị" ${selectedStatus eq 'Đang chuẩn bị' ? 'selected' : ''}>
                            Đang chuẩn bị
                        </option>
                        <option value="Đang giao" ${selectedStatus eq 'Đang giao' ? 'selected' : ''}>
                            Đang giao
                        </option>
                        <option value="Hoàn thành" ${selectedStatus eq 'Hoàn thành' ? 'selected' : ''}>
                            Hoàn thành
                        </option>
                        <option value="Đã hủy" ${selectedStatus eq 'Đã hủy' ? 'selected' : ''}>
                            Đã hủy
                        </option>
                    </select>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Orders Table --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt me-2"></i>Danh sách đơn hàng
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 100px;" class="ps-4">Mã ĐH</th>
                        <th style="width: 200px;">Thông tin khách hàng</th>
                        <th style="width: 130px;">Điện thoại</th>
                        <th style="width: 140px;" class="text-end">Tổng tiền</th>
                        <th style="width: 140px;" class="text-center">Thanh toán</th>
                        <th style="width: 150px;" class="text-center">Trạng thái</th>
                        <th style="width: 160px;">Ngày đặt</th>
                        <th style="width: 120px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty orders}">
                            <tr>
                                <td colspan="8" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có đơn hàng nào</h5>
                                        <p class="text-muted small mb-0">Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="order" items="${orders}">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <strong class="text-primary">#${order.order_id}</strong>
                                    </td>
                                    <td style="width: 200px;">
                                        <div class="fw-semibold mb-1">
                                            <i class="fas fa-user text-muted me-4"></i>${order.fullname}
                                        </div>
                                        <small class="text-muted">
                                            <i class="fas fa-map-marker-alt me-4"></i>
                                            ${fn:length(order.address) > 30 ? fn:substring(order.address, 0, 30).concat('...') : order.address}
                                        </small>
                                    </td>
                                    <td>
                                        <span class="badge bg-light text-dark">
                                            <i class="fas fa-phone me-2"></i>${order.phone}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <strong class="text-success fs-5">
                                            <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                        </strong>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${order.payment_status eq 'Đã thanh toán'}">
                                                <span class="badge text-bg-success px-3 py-2">
                                                    <i class="fas fa-check-circle me-2"></i>${order.payment_status}
                                                </span>
                                            </c:when>
                                            <c:when test="${order.payment_status eq 'Đã hoàn tiền'}">
                                                <span class="badge text-bg-info px-3 py-2">
                                                    <i class="fas fa-undo me-2"></i>${order.payment_status}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge text-bg-warning px-3 py-2">
                                                    <i class="fas fa-clock me-2"></i>${order.payment_status}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                                                <span class="badge text-bg-secondary px-3 py-2">
                                                    <i class="fas fa-hourglass-half me-2"></i>${order.order_status}
                                                </span>
                                            </c:when>
                                            <c:when test="${order.order_status eq 'Đang chuẩn bị'}">
                                                <span class="badge text-bg-info px-3 py-2">
                                                    <i class="fas fa-utensils me-2"></i>${order.order_status}
                                                </span>
                                            </c:when>
                                            <c:when test="${order.order_status eq 'Đang giao'}">
                                                <span class="badge text-bg-primary px-3 py-2">
                                                    <i class="fas fa-motorcycle me-2"></i>${order.order_status}
                                                </span>
                                            </c:when>
                                            <c:when test="${order.order_status eq 'Hoàn thành'}">
                                                <span class="badge text-bg-success px-3 py-2">
                                                    <i class="fas fa-check-circle me-2"></i>${order.order_status}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge text-bg-danger px-3 py-2">
                                                    <i class="fas fa-times-circle me-2"></i>${order.order_status}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <small class="text-muted">
                                            <i class="far fa-calendar me-2"></i>
                                            <fmt:formatDate value="${order.createdDateAsDate}" pattern="dd/MM/yyyy"/>
                                        </small>
                                        <br>
                                        <small class="text-muted">
                                            <i class="far fa-clock me-2"></i>
                                            <fmt:formatDate value="${order.createdDateAsDate}" pattern="HH:mm"/>
                                        </small>
                                    </td>
                                    <td class="text-center pe-4">
                                        <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${order.order_id}" 
                                           class="btn btn-sm btn-primary" title="Xem chi tiết">
                                            <i class="fas fa-eye me-1"></i>Chi tiết
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>
