<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Đổi Quà Tặng - Hội Viên" scope="request"/>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h4 mb-0">
                <i class="bi bi-gift text-primary"></i> Đổi Quà Tặng
            </h2>
            <div class="badge bg-warning text-dark fs-6">
                Điểm của bạn: <fmt:formatNumber value="${points}" pattern="#,##0"/>
            </div>
        </div>

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

        <c:choose>
            <c:when test="${empty rewards}">
                <div class="alert alert-info text-center">
                    Hiện tại chưa có quà tặng nào.
                </div>
            </c:when>
            <c:otherwise>
                <div class="row row-cols-1 row-cols-md-3 g-4">
                    <c:forEach var="reward" items="${rewards}">
                        <div class="col">
                            <div class="card h-100 shadow-sm">
                                <c:if test="${not empty reward.image_url}">
                                    <img src="${reward.image_url}" class="card-img-top" alt="${reward.name}" 
                                         style="height: 200px; object-fit: cover;">
                                </c:if>
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">${reward.name}</h5>
                                    <p class="card-text small text-muted flex-grow-1">${reward.description}</p>
                                    <div class="mt-auto">
                                        <div class="d-flex justify-content-between align-items-center mb-3">
                                            <span class="badge bg-warning text-dark fs-6">
                                                <fmt:formatNumber value="${reward.points_required}" pattern="#,##0"/> điểm
                                            </span>
                                            <c:if test="${reward.stock != null}">
                                                <small class="text-muted">Còn lại: ${reward.stock}</small>
                                            </c:if>
                                        </div>
                                        <c:choose>
                                            <c:when test="${points >= reward.points_required && (reward.stock == null || reward.stock > 0)}">
                                                <form method="post" action="${pageContext.request.contextPath}/user/rewards" 
                                                      onsubmit="return confirm('Bạn có chắc chắn muốn đổi quà này?');">
                                                    <input type="hidden" name="action" value="redeem">
                                                    <input type="hidden" name="rewardId" value="${reward.reward_id}">
                                                    <button type="submit" class="btn btn-primary w-100">
                                                        <i class="bi bi-gift"></i> Đổi quà
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:when test="${points < reward.points_required}">
                                                <button type="button" class="btn btn-secondary w-100" disabled>
                                                    Chưa đủ điểm
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="btn btn-secondary w-100" disabled>
                                                    Hết hàng
                                                </button>
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

