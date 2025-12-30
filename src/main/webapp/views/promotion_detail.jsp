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

    <c:if test="${not empty promotion}">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/promotions" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-2"></i>Quay lại danh sách
            </a>
        </div>

        <article class="card shadow-sm">
            <div class="row g-0">
                <%-- Banner bên trái - chiếm 50% --%>
                <div class="col-md-6">
                    <div style="height: 100%; min-height: 400px; overflow: hidden;">
                        <c:choose>
                            <c:when test="${fn:startsWith(promotion.imageUrl, 'http')}">
                                <img src="${promotion.imageUrl}" class="img-fluid" alt="${promotion.title}" 
                                     style="width: 100%; height: 100%; object-fit: cover;">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/uploads/${promotion.imageUrl}" 
                                     class="img-fluid" alt="${promotion.title}"
                                     style="width: 100%; height: 100%; object-fit: cover;">
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <%-- Text bên phải - chiếm 50% --%>
                <div class="col-md-6">
                    <div class="card-body p-4 h-100 d-flex flex-column">
                        <h1 class="card-title mb-3">${promotion.title}</h1>
                        <div class="text-muted mb-3">
                            <i class="bi bi-calendar3 me-2"></i>
                            <fmt:formatDate value="${promotion.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm" />
                        </div>
                        <c:if test="${not empty promotion.description}">
                            <p class="lead text-muted">${promotion.description}</p>
                        </c:if>
                        <c:if test="${not empty promotion.content}">
                            <div class="mt-4 promotion-content flex-grow-1" style="line-height: 1.8; white-space: pre-wrap;">
                                ${promotion.content}
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </article>
    </c:if>
</div>

