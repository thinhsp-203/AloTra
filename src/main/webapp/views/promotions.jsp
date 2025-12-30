<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container mb-5">
    <div class="text-center mb-4">
        <h1 class="h3 mb-2">Khuyến mãi & Ưu đãi</h1>
        <p class="text-muted">Các chương trình khuyến mãi hấp dẫn dành cho bạn</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-warning alert-dismissible fade show">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty promotions}">
            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                <c:forEach items="${promotions}" var="promo">
                    <div class="col">
                        <div class="card h-100 news-card" style="cursor: pointer; transition: transform 0.3s ease;" 
                             onclick="window.location.href='${pageContext.request.contextPath}/promotions?id=${promo.id}'"
                             onmouseover="this.style.transform='translateY(-5px)'"
                             onmouseout="this.style.transform='translateY(0)'">
                            <div class="card-img-container" style="height: 280px; overflow: hidden;">
                                <c:choose>
                                    <c:when test="${fn:startsWith(promo.imageUrl, 'http')}">
                                        <img src="${promo.imageUrl}" class="card-img-top" alt="${promo.title}" 
                                             style="width: 100%; height: 100%; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/uploads/${promo.imageUrl}" 
                                             class="card-img-top" alt="${promo.title}"
                                             style="width: 100%; height: 100%; object-fit: cover;">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title">${promo.title}</h5>
                                <c:if test="${not empty promo.description}">
                                    <p class="card-text text-muted">${promo.description}</p>
                                </c:if>
                                <div class="mt-auto">
                                    <small class="text-muted">
                                        <fmt:formatDate value="${promo.createdDateAsDate}" pattern="dd/MM/yyyy" />
                                    </small>
                                    <div class="mt-2">
                                        <a href="${pageContext.request.contextPath}/promotions?id=${promo.id}" 
                                           class="btn btn-primary btn-sm">Xem chi tiết</a>
                                    </div>
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
                Hiện chưa có chương trình khuyến mãi nào.
            </div>
        </c:otherwise>
    </c:choose>
</div>

