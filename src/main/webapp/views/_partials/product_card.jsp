<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="card h-100 product-card">
    
    <button class="btn btn-outline-danger btn-sm btn-wishlist" 
            data-product-id="${p.product_id}"
            title="Thêm vào yêu thích">
        <i class="bi bi-heart"></i>
    </button>
    
    <a href="${pageContext.request.contextPath}/p?id=${p.product_id}" class="card-link text-decoration-none text-dark">
        <div class="card-img-container">
            <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
            <c:if test="${not empty thumbnailSrc and not fn:startsWith(thumbnailSrc, 'http')}">
               <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/${p.thumbnail}"/>
            </c:if>
            <c:if test="${empty thumbnailSrc}">
               <c:set var="thumbnailSrc" value="https://via.placeholder.com/200"/>
            </c:if>
            
            <img class="card-img-top" src="${thumbnailSrc}" alt="${p.product_name}">
        </div>
        <div class="card-body d-flex flex-column text-center">
            <h6 class="card-title">${p.product_name}</h6>
            <p class="card-text fw-bold text-primary mt-auto">
            <fmt:formatNumber value="${p.price}" pattern="#,##0"/> đ
            </p>
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