<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
    .hero-section {
        position: relative;
        padding: 5rem 1rem;
        background-image: url('https://static.phuclong.com.vn/storage/5/2022/10/20/richtea-damvi-2_1666236962.jpg'); /* Thay bằng ảnh banner của bạn */
        background-size: cover;
        background-position: center;
        border-radius: 0.5rem;
        text-align: center;
        overflow: hidden;
    }
    .hero-overlay {
        position: absolute;
        top: 0; left: 0; right: 0; bottom: 0;
        background-color: rgba(0, 0, 0, 0.4);
    }
    .hero-content {
        position: relative;
        z-index: 2;
    }
    .hero-search-form {
        max-width: 700px;
        margin: 1.5rem auto 0;
        background: white;
        border-radius: 99px;
        padding: 0.5rem;
    }
    .hero-search-form .form-control {
        border: none;
        box-shadow: none;
        font-size: 1.1rem;
    }
    .hero-search-form .btn {
        border-radius: 99px;
    }
</style>

<div class="hero-section mb-5">
    <div class="hero-overlay"></div>
    <div class="hero-content text-white">
        <h1 class="display-4 fw-bold">Đặt món giao tận nơi</h1>
        <p class="lead">Trải nghiệm hương vị trà và cà phê đậm vị đặc trưng.</p>
        <form class="hero-search-form d-flex" action="${pageContext.request.contextPath}/products" method="get">
            <input class="form-control me-2" type="search" name="q" placeholder="Tìm món, trà sữa, cà phê...">
            <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i></button>
        </form>
    </div>
</div>

<c:if test="${not empty featured}">
    <h2 class="h4 mb-3">BEST SELLERS - TRÀ THƠM CHẤT LƯỢNG</h2>
    <div class="row row-cols-2 row-cols-md-5 g-3 mb-5">
      <c:forEach var="product" items="${featured}">
        <c:set var="p" value="${product}" scope="request" />
        <jsp:include page="/views/_partials/product_card.jsp" />
      </c:forEach>
    </div>
</c:if>

<c:if test="${not empty newest}">
    <h2 class="h4 mb-3">SẢN PHẨM MỚI</h2>
    <div class="row row-cols-2 row-cols-md-5 g-3">
      <c:forEach var="product" items="${newest}">
        <c:set var="p" value="${product}" scope="request" />
        <jsp:include page="/views/_partials/product_card.jsp" />
      </c:forEach>
    </div>
</c:if>