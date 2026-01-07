<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="row g-4">

    <%-- Orders by Status --%>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-list-alt me-2"></i>
                    Thống kê đơn hàng theo trạng thái
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Trạng thái</th>
                                <th class="text-end" style="width: 120px;">Số lượng</th>
                                <th class="text-end pe-4" style="width: 150px;">Tổng giá trị</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty orderStatsByStatus}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">Chưa có đơn hàng nào</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="os" items="${orderStatsByStatus}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <span class="fw-semibold">${os[0]}</span>
                                            </td>

                                            <td class="text-end">
                                                <span class="badge bg-info text-white px-3 py-2">
                                                    <fmt:formatNumber value="${os[1]}" groupingUsed="true"/>
                                                </span>
                                            </td>

                                            <td class="text-end pe-4">
                                                <span class="fw-bold text-success">
                                                    <fmt:formatNumber value="${os[2]}" pattern="#,##0"/> ₫
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

    <%-- Orders by Payment Method --%>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-credit-card me-2"></i>
                    Thống kê theo phương thức thanh toán
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Phương thức</th>
                                <th class="text-end" style="width: 120px;">Số lượng</th>
                                <th class="text-end pe-4" style="width: 150px;">Tổng giá trị</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty orderStatsByPayment}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">
                                                    Chưa có đơn hàng đã hoàn thành
                                                </p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="op" items="${orderStatsByPayment}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <span class="fw-semibold">${op[0]}</span>
                                            </td>

                                            <td class="text-end">
                                                <span class="badge bg-primary text-white px-3 py-2">
                                                    <fmt:formatNumber value="${op[1]}" groupingUsed="true"/>
                                                </span>
                                            </td>

                                            <td class="text-end pe-4">
                                                <span class="fw-bold text-success">
                                                    <fmt:formatNumber value="${op[2]}" pattern="#,##0"/> ₫
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
