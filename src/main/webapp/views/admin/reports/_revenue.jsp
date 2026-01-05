<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- Revenue Comparison Card --%>
<c:if test="${not empty revenueComparison}">
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h6 class="text-muted mb-2">Kỳ này</h6>
                    <h3 class="text-primary mb-0">
                        <fmt:formatNumber value="${revenueComparison[0]}" pattern="#,##0"/> ₫
                    </h3>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h6 class="text-muted mb-2">Kỳ trước</h6>
                    <h3 class="text-secondary mb-0">
                        <fmt:formatNumber value="${revenueComparison[1]}" pattern="#,##0"/> ₫
                    </h3>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h6 class="text-muted mb-2">Tăng trưởng</h6>
                    <h3 class="${revenueComparison[2] >= 0 ? 'text-success' : 'text-danger'} mb-0">
                        <c:choose>
                            <c:when test="${revenueComparison[2] >= 0}">
                                <i class="fas fa-arrow-up me-1"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-arrow-down me-1"></i>
                            </c:otherwise>
                        </c:choose>
                        <fmt:formatNumber value="${revenueComparison[2]}" pattern="#,##0.00"/>%
                    </h3>
                </div>
            </div>
        </div>
    </div>
</c:if>

<div class="row g-4">

    <%-- Daily Revenue --%>
    <div class="col-12">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-calendar-day me-2"></i>
                    Doanh thu theo ngày
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4" style="width: 200px;">Ngày</th>
                                <th class="text-end pe-4">Doanh thu</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:choose>
                                <c:when test="${empty dailyRevenue}">
                                    <tr>
                                        <td colspan="2" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">
                                                    Chưa có doanh thu trong khoảng thời gian này
                                                </p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="r" items="${dailyRevenue}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <span class="fw-semibold">${r[0]}</span>
                                            </td>
                                            <td class="text-end pe-4">
                                                <span class="fw-bold text-success fs-5">
                                                    <fmt:formatNumber value="${r[1]}" pattern="#,##0"/> ₫
                                                </span>
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
    </div>

    <%-- Monthly Revenue --%>
    <div class="col-12">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-calendar-alt me-2"></i>
                    Doanh thu theo tháng
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4" style="width: 150px;">Năm</th>
                                <th style="width: 150px;">Tháng</th>
                                <th class="text-end pe-4">Doanh thu</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:choose>
                                <c:when test="${empty monthlyRevenue}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">Chưa có doanh thu nào được ghi nhận</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="r" items="${monthlyRevenue}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <span class="fw-semibold fs-5">${r[0]}</span>
                                            </td>
                                            <td>
                                                <span class="text-muted">Tháng ${r[1]}</span>
                                            </td>
                                            <td class="text-end pe-4">
                                                <span class="fw-semibold text-success fs-5">
                                                    <fmt:formatNumber value="${r[2]}" pattern="#,##0"/> ₫
                                                </span>
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
    </div>

</div>
