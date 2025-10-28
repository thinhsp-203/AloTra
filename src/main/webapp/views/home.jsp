<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<style>
    .category-section {
        background-color: #fff;
        padding: 1.5rem;
        border-radius: 0.25rem;
        box-shadow: 0 1px 2px 0 rgba(0,0,0,0.1);
    }
    .category-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
        gap: 1rem;
    }
    .category-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        text-decoration: none;
        color: #333;
        transition: transform 0.2s;
    }
    .category-item:hover {
        transform: translateY(-5px);
        color: #0d6efd;
    }
    .category-icon {
        width: 70px;
        height: 70px;
        border-radius: 50%;
        object-fit: cover;
        margin-bottom: 0.5rem;
        background-color: #f0f0f0;
        border: 1px solid #eee;
    }
    .category-name {
        font-size: 0.85rem;
    }
</style>

<div class="row g-4 mb-5">
    <div class="col-md-7 pt-5">
        <h1 class="display-4 fw-bold mb-3">AloTra – Thưởng thức trà sữa chuẩn vị mỗi ngày</h1>
        <p class="lead text-muted">
            Từ những ly trà sữa truyền thống đến các công thức sáng tạo, AloTra luôn sẵn sàng phục vụ bạn.
        </p>
        <p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-lg btn-primary mt-3">
                <i class="bi bi-search"></i> Khám phá menu
            </a>
        </p>
    </div>
    <div class="col-md-5 text-center">
        <img src="${pageContext.request.contextPath}/assets/images/hero-milk-tea.png" 
             alt="Milk Tea" class="img-fluid d-none d-md-block" style="max-height: 350px;">
    </div>
</div>

<c:if test="${not empty categories}">
    <div class="category-section mb-5">
        <h2 class="h5 mb-3 text-uppercase text-muted">Danh Mục</h2>
        <div class="category-grid">
            <c:forEach var="cat" items="${categories}">
                <a href="${pageContext.request.contextPath}/products?cate=${cat.id}" class="category-item">
                    <img src="${pageContext.request.contextPath}/${cat.icon}" class="category-icon" alt="${cat.name}">
                    <span class="category-name">${cat.name}</span>
                </a>
            </c:forEach>
        </div>
    </div>
</c:if>
<h2 class="h4 mb-3"><i class="bi bi-fire text-danger me-2"></i>Sản phẩm nổi bật</h2>

<div class="row row-cols-2 row-cols-md-4 g-3 mb-5">
  <c:forEach var="product" items="${featured}">
    <%-- Đặt biến 'p' vào request scope để product_card.jsp sử dụng --%>
    <c:set var="p" value="${product}" scope="request" />
    <jsp:include page="/views/_partials/product_card.jsp" />
  </c:forEach>
</div>


<h2 class="h4 mb-3"><i class="bi bi-calendar-check me-2"></i>Sản phẩm mới</h2>
<div class="row row-cols-2 row-cols-md-4 g-3">
  <c:forEach var="product" items="${newest}">
    <c:set var="p" value="${product}" scope="request" />
    <jsp:include page="/views/_partials/product_card.jsp" />
  </c:forEach>
</div>