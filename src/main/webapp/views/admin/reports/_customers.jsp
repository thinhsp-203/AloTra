<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="row g-4">

    <%-- Top Customers by Revenue --%>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-star me-2"></i>
                    Top khách hàng theo doanh thu
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Khách hàng</th>
                                <th class="text-end" style="width: 100px;">Số đơn</th>
                                <th class="text-end pe-4" style="width: 150px;">Tổng giá trị</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty topCustomersByRevenue}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">Chưa có khách hàng nào</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="tc" items="${topCustomersByRevenue}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <div class="fw-semibold">
                                                    ${empty tc[0] ? 'N/A' : tc[0]}
                                                </div>
                                                <small class="text-muted">${tc[1]}</small>
                                            </td>

                                            <td class="text-end">
                                                <span class="badge bg-info text-white px-3 py-2">
                                                    <fmt:formatNumber value="${tc[3]}" groupingUsed="true"/>
                                                </span>
                                            </td>

                                            <td class="text-end pe-4">
                                                <span class="fw-bold text-success">
                                                    <fmt:formatNumber value="${tc[4]}" pattern="#,##0"/> ₫
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

    <%-- Top Customers by Order Count --%>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-shopping-cart me-2"></i>
                    Top khách hàng theo số đơn hàng
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Khách hàng</th>
                                <th class="text-end" style="width: 100px;">Số đơn</th>
                                <th class="text-end pe-4" style="width: 150px;">Tổng giá trị</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty topCustomersByOrderCount}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">Chưa có khách hàng nào</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="tc" items="${topCustomersByOrderCount}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <div class="fw-semibold">
                                                    ${empty tc[0] ? 'N/A' : tc[0]}
                                                </div>
                                                <small class="text-muted">${tc[1]}</small>
                                            </td>

                                            <td class="text-end">
                                                <span class="badge bg-primary text-white px-3 py-2">
                                                    <fmt:formatNumber value="${tc[3]}" groupingUsed="true"/>
                                                </span>
                                            </td>

                                            <td class="text-end pe-4">
                                                <span class="fw-bold text-success">
                                                    <fmt:formatNumber value="${tc[4]}" pattern="#,##0"/> ₫
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
