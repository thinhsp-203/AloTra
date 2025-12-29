<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<style>
.user-sidebar {
    background: #f8f9fa;
    border-radius: 12px;
    overflow: hidden;
}

.user-sidebar .list-group-item {
    border: none;
    border-left: 3px solid transparent;
    transition: all 0.2s ease;
}

.user-sidebar .list-group-item:hover {
    background-color: #e9ecef;
    border-left-color: var(--bs-primary);
    transform: translateX(3px);
}

.user-sidebar .list-group-item.active {
    background-color: var(--bs-primary);
    border-left-color: var(--bs-primary);
    color: white;
}

.user-sidebar .list-group-item.active i {
    color: white;
}

.user-profile-header {
    background: linear-gradient(135deg, var(--bs-primary) 0%, #004d26 100%);
    color: white;
    padding: 1.5rem;
    text-align: center;
}

.user-avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 1rem;
    font-size: 2rem;
    color: var(--bs-primary);
    border: 3px solid white;
}
</style>

<div class="user-sidebar">
    <div class="user-profile-header">
        <div class="user-avatar">
            <c:choose>
                <c:when test="${not empty sessionScope.currentUser.avatar}">
                    <img src="${pageContext.request.contextPath}/uploads/${sessionScope.currentUser.avatar}" 
                         alt="Avatar" class="img-fluid rounded-circle" 
                         style="width: 100%; height: 100%; object-fit: cover;">
                </c:when>
                <c:otherwise>
                    <i class="bi bi-person-circle"></i>
                </c:otherwise>
            </c:choose>
        </div>
        <h6 class="mb-1">${sessionScope.currentUser.fullname}</h6>
        <small class="opacity-75">@${sessionScope.currentUser.username}</small>
    </div>

     <div class="list-group list-group-flush">
        <a href="${pageContext.request.contextPath}/user/profile"
           class="list-group-item list-group-item-action ${fn:endsWith(pageContext.request.requestURI, '/profile') ? 'active' : ''}">
            <i class="bi bi-person-fill me-2"></i> Hồ Sơ
        </a>

        <a href="${pageContext.request.contextPath}/user/orders"
           class="list-group-item list-group-item-action ${fn:endsWith(pageContext.request.requestURI, '/orders') ? 'active' : ''}">
            <i class="bi bi-receipt me-2"></i> Đơn Mua
        </a>

        <a href="${pageContext.request.contextPath}/user/wishlist"
           class="list-group-item list-group-item-action ${fn:endsWith(pageContext.request.requestURI, '/wishlist') ? 'active' : ''}">
            <i class="bi bi-heart me-2"></i> Yêu Thích
        </a>

        <a href="${pageContext.request.contextPath}/user/notifications"
           class="list-group-item list-group-item-action ${fn:endsWith(pageContext.request.requestURI, '/notifications') ? 'active' : ''}">
            <i class="bi bi-bell me-2"></i> Thông Báo
            <c:if test="${not empty unreadNotifications && unreadNotifications > 0}">
                <span class="badge bg-danger float-end">${unreadNotifications}</span>
            </c:if>
        </a>

        <hr class="my-0">

        <a href="${pageContext.request.contextPath}/logout"
           class="list-group-item list-group-item-action text-danger">
            <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
        </a>
    </div>
</div>