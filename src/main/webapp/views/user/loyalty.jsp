<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Hội Viên - Tích Điểm Đổi Quà" scope="request"/>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">
            <i class="bi bi-star-fill text-warning"></i> Chương trình Hội Viên
        </h2>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Điểm tích lũy -->
        <div class="card mb-4 shadow-sm border-0" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <div class="card-body text-white p-4">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="mb-2">Điểm tích lũy của bạn</h5>
                        <h2 class="mb-0"><fmt:formatNumber value="${points}" pattern="#,##0"/> điểm</h2>
                    </div>
                    <div class="text-end">
                        <i class="bi bi-star-fill fs-1 opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>

        <!-- Thông tin chương trình -->
        <div class="card mb-4">
            <div class="card-body">
                <h5 class="card-title">
                    <i class="bi bi-info-circle text-primary"></i> Cách tích điểm
                </h5>
                <ul class="mb-0">
                    <li>Mua hàng 1.000₫ = 1 điểm</li>
                    <li>Điểm sẽ được cộng tự động sau khi đơn hàng được xác nhận</li>
                    <li>Điểm không có thời hạn sử dụng</li>
                </ul>
            </div>
        </div>

        <!-- Danh sách quà tặng -->
        <div class="card">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="bi bi-gift text-primary"></i> Quà tặng có sẵn
                </h5>
                <a href="${pageContext.request.contextPath}/user/rewards" class="btn btn-sm btn-primary">
                    Xem tất cả <i class="bi bi-arrow-right"></i>
                </a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty rewards}">
                        <div class="alert alert-info text-center">
                            Hiện tại chưa có quà tặng nào.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row row-cols-1 row-cols-md-3 g-4">
                            <c:forEach var="reward" items="${rewards}" begin="0" end="5">
                                <div class="col">
                                    <div class="card h-100">
                                        <c:if test="${not empty reward.image_url}">
                                            <img src="${reward.image_url}" class="card-img-top" alt="${reward.name}" 
                                                 style="height: 200px; object-fit: cover;">
                                        </c:if>
                                        <div class="card-body d-flex flex-column">
                                            <h6 class="card-title">${reward.name}</h6>
                                            <p class="card-text small text-muted flex-grow-1">${reward.description}</p>
                                            <div class="d-flex justify-content-between align-items-center mt-auto">
                                                <span class="badge bg-warning text-dark">
                                                    <fmt:formatNumber value="${reward.points_required}" pattern="#,##0"/> điểm
                                                </span>
                                                <c:choose>
                                                    <c:when test="${points >= reward.points_required}">
                                                        <a href="${pageContext.request.contextPath}/user/rewards?id=${reward.reward_id}" 
                                                           class="btn btn-sm btn-primary">Đổi quà</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted small">Chưa đủ điểm</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Liên kết nhanh -->
        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/user/point-history" class="btn btn-outline-primary">
                <i class="bi bi-clock-history"></i> Xem lịch sử giao dịch điểm
            </a>
        </div>
    </div>
</div>

