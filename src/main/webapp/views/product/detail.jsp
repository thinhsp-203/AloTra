<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css"/>
<style>
    .suggestion-swiper {
        padding-bottom: 40px; /* Thêm không gian cho pagination */
    }
</style>
<div class="container">
    <div id="product-detail-container" data-product-id="${p.product_id}">
        <div class="row g-4">
            <div class="col-md-5 text-center">
                <img class="img-fluid rounded shadow-sm" src="${p.thumbnail}" alt="${p.product_name}" id="product-image"/>
            </div>

            <div class="col-md-7">
                <h1 class="h3 fw-bold" id="product-name">${p.product_name}</h1>
                <p class="h4 text-primary fw-bold mb-4" id="product-base-price" data-price="${p.price}">
                    <fmt:formatNumber value="${p.price}" pattern="#,##0"/> đ
                </p>
                <div id="product-options">
                    <div class="text-center my-5"><div class="spinner-border text-primary"></div></div>
                </div>
                <hr>
                <div class="product-description mb-4">
                    <c:out value="${p.description}" escapeXml="false"/>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="sticky-bottom bg-white p-3 shadow-top">
    <div class="container">
        <div class="row justify-content-end">
            <div class="col-md-7">
                <button type="button" class="btn btn-primary btn-lg w-100" id="detailAddToCartBtn" disabled>
                    Thêm vào giỏ hàng
                </button>
            </div>
        </div>
    </div>
</div>


<div class="container mt-5">
    <c:if test="${not empty suggestedProducts}">
        <div class="suggestion-section">
            <h2 class="h5 mb-3">Sản phẩm gợi ý</h2>
            <div class="swiper suggestion-swiper">
                <div class="swiper-wrapper">
                    <c:forEach var="x" items="${suggestedProducts}">
                        <div class="swiper-slide">
                            <c:set var="p" value="${x}" scope="request" />
                            <jsp:include page="/views/_partials/product_card.jsp" />
                        </div>
                    </c:forEach>
                </div>
                <div class="swiper-button-next"></div>
                <div class="swiper-button-prev"></div>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>