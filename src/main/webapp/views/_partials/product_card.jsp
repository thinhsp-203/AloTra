<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="card h-100 product-card">
    <a href="${pageContext.request.contextPath}/p?id=${p.product_id}" class="card-link text-decoration-none text-dark">
        <div class="card-img-container">
            <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
            <c:if test="${not empty thumbnailSrc}">
                <c:choose>
                    <c:when test="${fn:startsWith(thumbnailSrc, 'http')}">
                        <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                    </c:when>
                    <c:when test="${fn:startsWith(thumbnailSrc, 'uploads/')}">
                        <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/${p.thumbnail}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/products/${p.thumbnail}"/>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <c:if test="${empty thumbnailSrc}">
               <c:set var="thumbnailSrc" value="https://via.placeholder.com/200"/>
            </c:if>
            
            <img class="card-img-top" src="${thumbnailSrc}" alt="${p.product_name}">
        </div>
        <div class="card-body d-flex flex-column text-center">
            <h6 class="card-title">${p.product_name}</h6>
            <c:if test="${p.hasDiscount()}">
                <span class="badge bg-danger text-white mb-2">-<fmt:formatNumber value="${p.discount}" pattern="#,##0"/>%</span>
            </c:if>
            <div class="d-flex align-items-center justify-content-center gap-2 mt-auto">
                <div class="text-center">
                    <c:choose>
                        <c:when test="${p.hasDiscount()}">
                            <p class="card-text fw-bold text-danger mb-0 fs-5">
                                <fmt:formatNumber value="${p.finalPrice}" pattern="#,##0"/>₫
                            </p>
                            <p class="card-text text-muted mb-0" style="text-decoration: line-through; font-size: 0.85rem;">
                                <fmt:formatNumber value="${p.price}" pattern="#,##0"/>₫
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="card-text fw-bold text-primary mb-0">
                                <fmt:formatNumber value="${p.price}" pattern="#,##0"/>₫
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>
                <button class="btn btn-outline-danger btn-sm btn-wishlist" 
                        data-product-id="${p.product_id}"
                        title="Thêm vào yêu thích"
                        type="button">
                    <i class="bi bi-heart"></i>
                </button>
            </div>
        </div>
    </a>
    <div class="card-footer bg-transparent border-0 pb-3">
        <button class="btn btn-primary w-100" 
                data-bs-toggle="modal" 
                data-bs-target="#productModal" 
                data-product-id="${p.product_id}">
            Đặt mua
        </button>
    </div>
</div>