<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-ticket-alt text-primary" style="margin-right: 10px;"></i>Quản lý Voucher
        </h1>
        <p class="text-muted mb-0">Tạo và quản lý các mã giảm giá cho cửa hàng</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/vouchers/create" class="btn btn-primary">
        <i class="fas fa-plus" style="margin-right: 10px;"></i>Tạo voucher mới
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

<%-- Vouchers Table Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Voucher
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">Mã Voucher</th>
                        <th style="width: 120px;">Loại</th>
                        <th style="width: 120px;" class="text-end">Giá trị</th>
                        <th style="width: 120px;" class="text-end">Đơn tối thiểu</th>
                        <th style="width: 120px;" class="text-end">Giảm tối đa</th>
                        <th style="width: 180px;">Hiệu lực</th>
                        <th style="width: 100px;" class="text-center">Sử dụng</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 180px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty vouchers}">
                            <tr>
                                <td colspan="9" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có voucher nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm voucher mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="v" items="${vouchers}">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <div class="fw-semibold fs-5 text-primary">${v.code}</div>
                                        <c:if test="${not empty v.description}">
                                            <small class="text-muted">${v.description}</small>
                                        </c:if>
                                    </td>
                                    <td>
                                        <span class="badge ${v.discount_type eq 'PERCENT' ? 'bg-info' : 'bg-warning'} text-white">
                                            <c:if test="${v.discount_type eq 'PERCENT'}">%</c:if>
                                            <c:if test="${v.discount_type eq 'AMOUNT'}">₫</c:if>
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <strong class="text-success fs-5">
                                            <c:if test="${v.discount_type eq 'PERCENT'}">${v.discount_value}%</c:if>
                                            <c:if test="${v.discount_type eq 'AMOUNT'}">
                                                <fmt:formatNumber value="${v.discount_value}" pattern="#,##0₫"/>
                                            </c:if>
                                        </strong>
                                    </td>
                                    <td class="text-end">
                                        <c:if test="${empty v.min_order_value}">
                                            <span class="text-muted">-</span>
                                        </c:if>
                                        <c:if test="${not empty v.min_order_value}">
                                            <fmt:formatNumber value="${v.min_order_value}" pattern="#,##0₫"/>
                                        </c:if>
                                    </td>
                                    <td class="text-end">
                                        <c:if test="${empty v.max_discount}">
                                            <span class="text-muted">-</span>
                                        </c:if>
                                        <c:if test="${not empty v.max_discount}">
                                            <fmt:formatNumber value="${v.max_discount}" pattern="#,##0₫"/>
                                        </c:if>
                                    </td>
                                    <td>
                                        <small>
                                            <fmt:formatDate value="${v.start_dateAsDate}" pattern="dd/MM/yyyy HH:mm"/><br>
                                            <i class="fas fa-arrow-down fa-xs text-muted"></i><br>
                                            <fmt:formatDate value="${v.end_dateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                        </small>
                                    </td>
                                    <td class="text-center">
                                        <c:if test="${v.usage_limit != null}">
                                            <span class="badge bg-secondary text-white">
                                                ${v.used_count != null ? v.used_count : 0}/${v.usage_limit}
                                            </span>
                                        </c:if>
                                        <c:if test="${v.usage_limit == null}">
                                            <span class="text-muted">∞</span>
                                        </c:if>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${v.isActive ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${v.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${v.isActive ? 'Kích hoạt' : 'Vô hiệu'}
                                        </span>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/vouchers/edit?id=${v.voucher_id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/vouchers/delete" method="post" 
                                                  style="display: inline; margin: 0 7.5px;" 
                                                  onsubmit="return confirm('Xác nhận xóa voucher \'${v.code}\'?')">
                                                <input type="hidden" name="id" value="${v.voucher_id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
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
