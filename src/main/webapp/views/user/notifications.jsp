<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h4 mb-0">
                <i class="bi bi-bell text-primary"></i> Thông báo
            </h2>
            <c:if test="${not empty notifications}">
                <form action="${pageContext.request.contextPath}/user/notifications" method="POST" style="display: inline;">
                    <input type="hidden" name="action" value="markAllAsRead">
                    <button type="submit" class="btn btn-sm btn-outline-primary">
                        <i class="bi bi-check-all me-1"></i>Đánh dấu tất cả đã đọc
                    </button>
                </form>
            </c:if>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty notifications}">
                <div class="card border-0 shadow-sm">
                    <div class="card-body text-center py-5">
                        <i class="bi bi-bell-slash fs-1 text-muted mb-3 d-block"></i>
                        <h5 class="text-muted">Chưa có thông báo nào</h5>
                        <p class="text-muted mb-0">Bạn sẽ nhận được thông báo về đơn hàng và khuyến mãi tại đây</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="list-group">
                    <c:forEach var="notif" items="${notifications}">
                        <div class="list-group-item ${not notif.isRead ? 'list-group-item-action bg-light' : ''} border-0 border-bottom">
                            <div class="d-flex w-100 justify-content-between align-items-start">
                                <div class="flex-grow-1">
                                    <c:if test="${not notif.isRead}">
                                        <span class="badge bg-primary rounded-pill me-2">Mới</span>
                                    </c:if>
                                    <p class="mb-1 ${not notif.isRead ? 'fw-bold' : ''}">${notif.message}</p>
                                    <small class="text-muted">
                                        <fmt:formatDate value="${notif.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </small>
                                </div>
                                <div class="ms-3">
                                    <c:if test="${not empty notif.link}">
                                        <a href="${pageContext.request.contextPath}${notif.link}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-arrow-right"></i> Xem
                                        </a>
                                    </c:if>
                                    <c:if test="${not notif.isRead}">
                                        <form action="${pageContext.request.contextPath}/user/notifications" method="POST" style="display: inline-block;" class="ms-2">
                                            <input type="hidden" name="action" value="markAsRead">
                                            <input type="hidden" name="id" value="${notif.id}">
                                            <button type="submit" class="btn btn-sm btn-link text-muted" title="Đánh dấu đã đọc">
                                                <i class="bi bi-check-circle"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

