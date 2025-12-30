<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container mb-5">
    <c:if test="${not empty error}">
        <div class="alert alert-warning alert-dismissible fade show">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty store}">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/stores" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-2"></i>Quay lại danh sách
            </a>
        </div>

        <article class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="card-title mb-3">
                    <i class="bi bi-geo-alt-fill text-primary me-2"></i>${store.store_name}
                </h1>
                
                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-3">
                            <h5 class="text-muted mb-2">
                                <i class="bi bi-geo me-2"></i>Địa chỉ
                            </h5>
                            <p class="mb-1">${store.address}</p>
                            <c:if test="${not empty store.ward || not empty store.province}">
                                <small class="text-muted">
                                    <c:if test="${not empty store.ward}">${store.ward}<c:if test="${not empty store.province}">, </c:if></c:if>
                                    <c:if test="${not empty store.province}">${store.province}</c:if>
                                </small>
                            </c:if>
                        </div>
                        
                        <c:if test="${not empty store.phone}">
                            <div class="mb-3">
                                <h5 class="text-muted mb-2">
                                    <i class="bi bi-telephone me-2"></i>Số điện thoại
                                </h5>
                                <a href="tel:${store.phone}" class="text-decoration-none">${store.phone}</a>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty store.email}">
                            <div class="mb-3">
                                <h5 class="text-muted mb-2">
                                    <i class="bi bi-envelope me-2"></i>Email
                                </h5>
                                <a href="mailto:${store.email}" class="text-decoration-none">${store.email}</a>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty store.opening_hours}">
                            <div class="mb-3">
                                <h5 class="text-muted mb-2">
                                    <i class="bi bi-clock me-2"></i>Giờ mở cửa
                                </h5>
                                <p>${store.opening_hours}</p>
                            </div>
                        </c:if>
                    </div>
                    
                    <c:if test="${not empty store.mapIframe}">
                        <div class="col-md-6">
                            <h5 class="text-muted mb-3">
                                <i class="bi bi-map me-2"></i>Vị trí trên bản đồ
                            </h5>
                            <div class="mb-3" style="border-radius: 8px; overflow: hidden;">
                                <c:out value="${store.mapIframe}" escapeXml="false" />
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </article>
    </c:if>
</div>

