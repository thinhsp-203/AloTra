<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="row g-4">

    <%-- Top Products --%>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-trophy me-2"></i>
                    Top sản phẩm bán chạy
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4" style="width: 60px;">#</th>
                                <th>Sản phẩm</th>
                                <th class="text-end pe-4">Số lượng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty topProducts}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">Chưa có sản phẩm nào được bán</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="t" items="${topProducts}" varStatus="loop">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <span class="badge bg-primary text-white px-3 py-2">
                                                    ${loop.index + 1}
                                                </span>
                                            </td>
                                            <td>
                                                <span class="fw-semibold fs-5">${t[0]}</span>
                                            </td>
                                            <td class="text-end pe-4">
                                                <span class="fw-semibold text-info fs-5">
                                                    <fmt:formatNumber value="${t[1]}" groupingUsed="true"/>
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

    <%-- Product Revenue --%>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 fw-bold text-primary">
                    <i class="fas fa-dollar-sign me-2"></i>
                    Doanh thu theo sản phẩm
                </h6>
            </div>

            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Sản phẩm</th>
                                <th class="text-end" style="width: 120px;">Số lượng</th>
                                <th class="text-end pe-4" style="width: 150px;">Doanh thu</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty productRevenue}">
                                    <tr>
                                        <td colspan="3" class="text-center py-5">
                                            <div class="py-4">
                                                <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                                <h5 class="text-muted mb-2">Không có dữ liệu</h5>
                                                <p class="text-muted small mb-0">Chưa có doanh thu từ sản phẩm</p>
                                            </div>
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="pr" items="${productRevenue}">
                                        <tr class="border-bottom">
                                            <td class="ps-4">
                                                <span class="fw-semibold">${pr[0]}</span>
                                            </td>
                                            <td class="text-end">
                                                <span class="text-muted">
                                                    <fmt:formatNumber value="${pr[1]}" groupingUsed="true"/>
                                                </span>
                                            </td>
                                            <td class="text-end pe-4">
                                                <span class="fw-bold text-success">
                                                    <fmt:formatNumber value="${pr[2]}" pattern="#,##0"/> ₫
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
