<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<style>
    .promotion-detail-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 0.375rem 0 0 0.375rem;
    }
    
    .promotion-content {
        line-height: 1.8;
        font-size: 1.05rem;
        color: #495057;
    }
    
    .promotion-content h1, .promotion-content h2, .promotion-content h3 {
        margin-top: 1.5rem;
        margin-bottom: 1rem;
        font-weight: 600;
    }
    
    .promotion-content p {
        margin-bottom: 1rem;
    }
    
    .promotion-content ul, .promotion-content ol {
        margin-bottom: 1rem;
        padding-left: 2rem;
    }
    
    .promotion-content img {
        max-width: 100%;
        height: auto;
        border-radius: 0.375rem;
        margin: 1rem 0;
    }
    
    .promotion-meta {
        border-top: 1px solid #dee2e6;
        border-bottom: 1px solid #dee2e6;
        padding: 1rem 0;
    }
    
    .promotion-card-related {
        transition: transform 0.3s ease, box-shadow 0.3s ease;
        cursor: pointer;
        height: 100%;
    }
    
    .promotion-card-related:hover {
        transform: translateY(-5px);
        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
    }
    
    @media (max-width: 768px) {
        .promotion-detail-image {
            border-radius: 0.375rem 0.375rem 0 0;
        }
    }
</style>

<div class="container my-5">
    <c:if test="${not empty error}">
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle me-2"></i>
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:if test="${not empty promotion}">
        <%-- Breadcrumb --%>
        <nav aria-label="breadcrumb" class="mb-4">
            <ol class="breadcrumb">
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/" class="text-decoration-none">
                        <i class="bi bi-house-door me-1"></i>Trang chủ
                    </a>
                </li>
                <li class="breadcrumb-item">
                    <a href="${pageContext.request.contextPath}/promotions" class="text-decoration-none">Khuyến mãi</a>
                </li>
                <li class="breadcrumb-item active" aria-current="page">${promotion.title}</li>
            </ol>
        </nav>

        <%-- Main Promotion Detail --%>
        <article class="card shadow-lg border-0 mb-5">
            <div class="row g-0">
                <%-- Image Section --%>
                <div class="col-lg-6">
                    <div style="height: 100%; min-height: 500px; overflow: hidden; background-color: #f8f9fa;">
                        <c:set var="promoDetailImgSrc" value="${promotion.imageUrl}"/>
                        <c:if test="${not empty promoDetailImgSrc}">
                            <c:choose>
                                <c:when test="${fn:startsWith(promoDetailImgSrc, 'http')}">
                                    <c:set var="promoDetailImgSrc" value="${promotion.imageUrl}"/>
                                </c:when>
                                <c:when test="${fn:startsWith(promoDetailImgSrc, 'uploads/')}">
                                    <c:set var="promoDetailImgSrc" value="${pageContext.request.contextPath}/${promotion.imageUrl}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="promoDetailImgSrc" value="${pageContext.request.contextPath}/uploads/${promotion.imageUrl}"/>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <img src="${empty promoDetailImgSrc ? 'https://via.placeholder.com/500?text=No+Image' : promoDetailImgSrc}" 
                             class="promotion-detail-image" 
                             alt="${promotion.title}"
                             onerror="this.src='https://via.placeholder.com/500?text=No+Image'">
                    </div>
                </div>
                
                <%-- Content Section --%>
                <div class="col-lg-6">
                    <div class="card-body p-4 p-lg-5 h-100 d-flex flex-column">
                        <h1 class="card-title mb-4 fw-bold" style="color: #212529; font-size: 2rem;">
                            ${promotion.title}
                        </h1>
                        
                        <%-- Meta Information --%>
                        <div class="promotion-meta mb-4">
                            <div class="row g-3">
                                <div class="col-12">
                                    <div class="d-flex align-items-center text-muted">
                                        <i class="bi bi-calendar3 me-2 fs-5"></i>
                                        <div>
                                            <div class="small fw-semibold">Ngày đăng</div>
                                            <div><fmt:formatDate value="${promotion.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm" /></div>
                                        </div>
                                    </div>
                                </div>
                                <c:if test="${not empty promotion.updatedDateAsDate && promotion.updatedDateAsDate != promotion.createdDateAsDate}">
                                    <div class="col-12">
                                        <div class="d-flex align-items-center text-muted">
                                            <i class="bi bi-pencil-square me-2 fs-5"></i>
                                            <div>
                                                <div class="small fw-semibold">Cập nhật lần cuối</div>
                                                <div><fmt:formatDate value="${promotion.updatedDateAsDate}" pattern="dd/MM/yyyy HH:mm" /></div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        
                        <%-- Description --%>
                        <c:if test="${not empty promotion.description}">
                            <div class="mb-4">
                                <p class="lead text-primary mb-0" style="font-size: 1.1rem; font-weight: 500;">
                                    ${promotion.description}
                                </p>
                            </div>
                        </c:if>
                        
                        <%-- Back Button --%>
                        <div class="mt-auto pt-4">
                            <a href="${pageContext.request.contextPath}/promotions" 
                               class="btn btn-outline-primary btn-lg w-100">
                                <i class="bi bi-arrow-left me-2"></i>Quay lại danh sách khuyến mãi
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </article>

        <%-- Detailed Content Section --%>
        <c:if test="${not empty promotion.content}">
            <div class="card shadow-sm border-0 mb-5">
                <div class="card-body p-4 p-lg-5">
                    <h2 class="h4 mb-4 fw-bold">
                        <i class="bi bi-info-circle me-2 text-primary"></i>Chi tiết chương trình
                    </h2>
                    <div class="promotion-content">
                        ${promotion.content}
                    </div>
                </div>
            </div>
        </c:if>

        <%-- Related Promotions (Optional - if there are other promotions) --%>
        <c:if test="${not empty relatedPromotions && fn:length(relatedPromotions) > 0}">
            <div class="mb-5">
                <h3 class="h4 mb-4 fw-bold">
                    <i class="bi bi-tag me-2 text-primary"></i>Các khuyến mãi khác
                </h3>
                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    <c:forEach items="${relatedPromotions}" var="relatedPromo">
                        <div class="col">
                            <div class="card promotion-card-related shadow-sm h-100" 
                                 onclick="window.location.href='${pageContext.request.contextPath}/promotions?id=${relatedPromo.id}'">
                                <div class="card-img-container" style="height: 200px; overflow: hidden;">
                                    <c:set var="relatedPromoImgSrc" value="${relatedPromo.imageUrl}"/>
                                    <c:if test="${not empty relatedPromoImgSrc}">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(relatedPromoImgSrc, 'http')}">
                                                <c:set var="relatedPromoImgSrc" value="${relatedPromo.imageUrl}"/>
                                            </c:when>
                                            <c:when test="${fn:startsWith(relatedPromoImgSrc, 'uploads/')}">
                                                <c:set var="relatedPromoImgSrc" value="${pageContext.request.contextPath}/${relatedPromo.imageUrl}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="relatedPromoImgSrc" value="${pageContext.request.contextPath}/uploads/${relatedPromo.imageUrl}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <img src="${empty relatedPromoImgSrc ? 'https://via.placeholder.com/300?text=No+Image' : relatedPromoImgSrc}" 
                                         class="card-img-top" 
                                         alt="${relatedPromo.title}"
                                         style="width: 100%; height: 100%; object-fit: cover;"
                                         onerror="this.src='https://via.placeholder.com/300?text=No+Image'">
                                </div>
                                <div class="card-body d-flex flex-column">
                                    <h5 class="card-title">${relatedPromo.title}</h5>
                                    <c:if test="${not empty relatedPromo.description}">
                                        <p class="card-text text-muted small flex-grow-1">${fn:substring(relatedPromo.description, 0, 100)}${fn:length(relatedPromo.description) > 100 ? '...' : ''}</p>
                                    </c:if>
                                    <div class="mt-auto">
                                        <small class="text-muted d-block mb-2">
                                            <fmt:formatDate value="${relatedPromo.createdDateAsDate}" pattern="dd/MM/yyyy" />
                                        </small>
                                        <span class="btn btn-sm btn-outline-primary w-100">
                                            Xem chi tiết <i class="bi bi-arrow-right ms-1"></i>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </c:if>
</div>
