<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container py-5">
    <div class="text-center mb-5">
        <h1 class="display-4 fw-bold mb-3">Về Chúng Tôi</h1>
        <p class="lead text-muted">Khám phá câu chuyện và giá trị của AloTra</p>
    </div>

    <c:choose>
        <c:when test="${empty aboutList}">
            <div class="alert alert-info text-center">
                <i class="bi bi-info-circle me-2"></i>
                Nội dung đang được cập nhật. Vui lòng quay lại sau!
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach items="${aboutList}" var="about" varStatus="status">
                    <div class="col-12">
                        <div class="card shadow-sm border-0 mb-4">
                            <div class="row g-0">
                                <c:if test="${not empty about.image}">
                                    <div class="col-md-4">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(about.image, 'http')}">
                                                <img src="${about.image}" 
                                                     class="img-fluid rounded-start h-100" 
                                                     style="object-fit: cover; min-height: 250px;"
                                                     alt="${about.title}">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/uploads/${about.image}" 
                                                     class="img-fluid rounded-start h-100" 
                                                     style="object-fit: cover; min-height: 250px;"
                                                     alt="${about.title}"
                                                     onerror="this.src='${pageContext.request.contextPath}/assets/img/placeholder.jpg'">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                                <div class="${not empty about.image ? 'col-md-8' : 'col-md-12'}">
                                    <div class="card-body p-4">
                                        <h2 class="card-title h3 fw-bold mb-3">${about.title}</h2>
                                        <div class="card-text">
                                            <c:choose>
                                                <c:when test="${fn:contains(about.content, '<')}">
                                                    ${about.content}
                                                </c:when>
                                                <c:otherwise>
                                                    <p style="white-space: pre-wrap; line-height: 1.8; color: #555;">
                                                        ${about.content}
                                                    </p>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
.card {
    transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.1) !important;
}

.card-title {
    color: var(--bs-primary);
}

.card-text {
    color: #555;
    font-size: 1.05rem;
    line-height: 1.8;
}
</style>

