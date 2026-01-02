<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<fmt:setLocale value="vi_VN" />

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-chart-bar text-primary" style="margin-right: 10px;"></i>Báo cáo & Thống kê
        </h1>
        <p class="text-muted mb-0">Báo cáo chi tiết về doanh thu, sản phẩm, đơn hàng và khách hàng</p>
    </div>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<%-- Filter Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-primary text-white py-3">
        <h6 class="m-0 font-weight-bold">
            <i class="fas fa-filter" style="margin-right: 10px;"></i>Bộ lọc báo cáo
        </h6>
    </div>
    <div class="card-body p-4">
        <form method="get" action="${pageContext.request.contextPath}/admin/reports" class="row g-3 align-items-end">
            <div class="col-md-3">
                <label class="form-label fw-semibold mb-2">
                    <i class="fas fa-file-alt" style="margin-right: 10px;"></i>Loại báo cáo
                </label>
                <select class="form-select" name="type" id="reportType" onchange="this.form.submit();">
                    <option value="revenue" ${reportType eq 'revenue' ? 'selected' : ''}>Báo cáo Doanh thu</option>
                    <option value="products" ${reportType eq 'products' ? 'selected' : ''}>Báo cáo Sản phẩm</option>
                    <option value="orders" ${reportType eq 'orders' ? 'selected' : ''}>Báo cáo Đơn hàng</option>
                    <option value="customers" ${reportType eq 'customers' ? 'selected' : ''}>Báo cáo Khách hàng</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label fw-semibold mb-2">
                    <i class="fas fa-calendar-alt" style="margin-right: 10px;"></i>Từ ngày
                </label>
                <input type="date" class="form-control" name="startDate" value="${startDate}">
            </div>
            <div class="col-md-3">
                <label class="form-label fw-semibold mb-2">
                    <i class="fas fa-calendar-times" style="margin-right: 10px;"></i>Đến ngày
                </label>
                <input type="date" class="form-control" name="endDate" value="${endDate}">
            </div>
            <div class="col-md-3">
                <button type="submit" class="btn btn-primary w-100">
                    <i class="fas fa-search" style="margin-right: 10px;"></i>Xem báo cáo
                </button>
            </div>
        </form>
    </div>
</div>

<%-- Report Content --%>
<c:choose>
    <c:when test="${reportType eq 'revenue'}">
        <jsp:include page="/views/admin/reports/_revenue.jsp" />
    </c:when>
    <c:when test="${reportType eq 'products'}">
        <jsp:include page="/views/admin/reports/_products.jsp" />
    </c:when>
    <c:when test="${reportType eq 'orders'}">
        <jsp:include page="/views/admin/reports/_orders.jsp" />
    </c:when>
    <c:when test="${reportType eq 'customers'}">
        <jsp:include page="/views/admin/reports/_customers.jsp" />
    </c:when>
    <c:otherwise>
        <jsp:include page="/views/admin/reports/_revenue.jsp" />
    </c:otherwise>
</c:choose>
