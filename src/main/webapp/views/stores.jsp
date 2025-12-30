<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container mb-5">
    <div class="text-center mb-4">
        <h1 class="h3 mb-2">Danh sách cửa hàng</h1>
        <p class="text-muted">Tìm cửa hàng AloTra gần bạn nhất</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-warning alert-dismissible fade show">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <%-- Search form --%>
    <div class="row mb-4">
        <div class="col-md-8 mx-auto">
            <form method="GET" action="${pageContext.request.contextPath}/stores" class="d-flex">
                <input type="text" 
                       class="form-control me-2" 
                       name="keyword" 
                       placeholder="Tìm kiếm theo tên, địa chỉ, quận/huyện, thành phố..." 
                       value="${keyword}">
                <button class="btn btn-primary" type="submit">
                    <i class="bi bi-search me-2"></i>
                </button>
            </form>
        </div>
    </div>

    <c:choose>
        <c:when test="${not empty stores}">
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                <c:forEach items="${stores}" var="store">
                    <div class="col">
                        <div class="card h-100 shadow-sm" style="transition: transform 0.3s ease;" 
                             onmouseover="this.style.transform='translateY(-5px)'"
                             onmouseout="this.style.transform='translateY(0)'">
                            <div class="card-body">
                                <h5 class="card-title text-primary">
                                    <i class="bi bi-geo-alt-fill me-2"></i>${store.store_name}
                                </h5>
                                
                                <div class="mb-2">
                                    <small class="text-muted d-block">
                                        <i class="bi bi-geo me-1"></i>
                                        <strong>Địa chỉ:</strong>
                                    </small>
                                    <p class="mb-1">${store.address}</p>
                                    <c:if test="${not empty store.ward || not empty store.province}">
                                        <small class="text-muted">
                                            <c:if test="${not empty store.ward}">${store.ward}<c:if test="${not empty store.province}">, </c:if></c:if>
                                            <c:if test="${not empty store.province}">${store.province}</c:if>
                                        </small>
                                    </c:if>
                                </div>
                                
                                <c:if test="${not empty store.phone}">
                                    <div class="mb-2">
                                        <small class="text-muted d-block">
                                            <i class="bi bi-telephone me-1"></i>
                                            <strong>Số điện thoại:</strong>
                                        </small>
                                        <a href="tel:${store.phone}" class="text-decoration-none">${store.phone}</a>
                                    </div>
                                </c:if>
                                
                                <c:if test="${not empty store.opening_hours}">
                                    <div class="mb-2">
                                        <small class="text-muted d-block">
                                            <i class="bi bi-clock me-1"></i>
                                            <strong>Giờ mở cửa:</strong>
                                        </small>
                                        <small>${store.opening_hours}</small>
                                    </div>
                                </c:if>
                                
                                <div class="mt-3">
                                    <a href="${pageContext.request.contextPath}/stores?id=${store.store_id}" 
                                       class="btn btn-primary btn-sm w-100">
                                        <i class="bi bi-eye me-2"></i>Xem chi tiết
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info text-center">
                <i class="bi bi-info-circle me-2"></i>
                <c:choose>
                    <c:when test="${not empty keyword}">
                        Không tìm thấy cửa hàng nào với từ khóa "${keyword}".
                    </c:when>
                    <c:otherwise>
                        Hiện chưa có cửa hàng nào.
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
</div>

