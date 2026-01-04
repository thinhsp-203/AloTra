<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:if test="${not empty banners}">
    <div id="heroCarousel" class="carousel slide" data-bs-ride="carousel">
        
        <div class="carousel-indicators">
            <c:forEach items="${banners}" var="b" varStatus="status">
                <button type="button" 
                        data-bs-target="#heroCarousel" 
                        data-bs-slide-to="${status.index}" 
                        class="${status.index == 0 ? 'active' : ''}" 
                        aria-label="Slide ${status.count}">
                </button>
            </c:forEach>
        </div>

        <div class="carousel-inner">
            <c:forEach items="${banners}" var="b" varStatus="status">
                <div class="carousel-item ${status.index == 0 ? 'active' : ''}">
                    <%-- Overlay mờ để tăng contrast --%>
                    <div class="carousel-overlay"></div>
                    
                    <%-- Tạo thẻ <img> với logic src chính xác --%>
                    <c:set var="bannerImgSrc">
                        <c:choose>
                            <%-- Nếu là URL (bắt đầu bằng http) --%>
                            <c:when test="${fn:startsWith(b.imageUrl, 'http')}">
                                ${b.imageUrl}
                            </c:when>
                            <%-- Nếu đã có prefix uploads/ --%>
                            <c:when test="${fn:startsWith(b.imageUrl, 'uploads/')}">
                                ${pageContext.request.contextPath}/${b.imageUrl}
                            </c:when>
                            <%-- Không có prefix --%>
                            <c:otherwise>
                                ${pageContext.request.contextPath}/uploads/${b.imageUrl}
                            </c:otherwise>
                        </c:choose>
                    </c:set>
                    <img src="${bannerImgSrc}" alt="Banner ${status.count}">
                    
                    <%-- Caption với text và CTA --%>
                    <div class="carousel-caption d-flex flex-column justify-content-center align-items-center">
                        <h2 class="carousel-title mb-3">Khám phá hương vị tuyệt vời</h2>
                        <p class="carousel-subtitle mb-4">Sản phẩm chất lượng, giá cả hợp lý</p>
                        <c:choose>
                            <c:when test="${not empty b.linkUrl}">
                                <a href="${b.linkUrl}" class="carousel-cta-btn">
                                    <i class="bi bi-arrow-right me-2"></i>
                                    Xem ngay
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/products" class="carousel-cta-btn">
                                    <i class="bi bi-arrow-right me-2"></i>
                                    Xem sản phẩm
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                </div>
            </c:forEach>
        </div>

        <button class="carousel-control-prev" type="button" data-bs-target="#heroCarousel" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#heroCarousel" data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
        </button>
    </div>
</c:if>


<%-- Best Sellers --%>
<c:if test="${not empty featured}">
    <div class="container text-center mb-5">
        <div class="section-title-wrapper">
            <h2 class="section-title">
                <i class="bi bi-star-fill"></i>
                BEST SELLERS - TRÀ THƠM CHẤT LƯỢNG
            </h2>
        </div>
   <div class="product-grid-container">
            <div class="row row-cols-2 row-cols-md-5 g-3">
              <c:forEach var="product" items="${featured}" varStatus="status" begin="0" end="4">
                <div class="col">
                    <c:set var="p" value="${product}" scope="request" />
<jsp:include page="/views/_partials/product_card.jsp" />
                </div>
              </c:forEach>
              
              <c:forEach var="product" items="${featured}" begin="5">
                <div class="col hidden-featured-item" style="display: none;">
   <c:set var="p" value="${product}" scope="request" />
                    <jsp:include page="/views/_partials/product_card.jsp" />
                </div>
              </c:forEach>
            </div>
        </div>
        
        <c:if test="${fn:length(featured) > 5}">
          <button class="btn btn-outline-primary mt-4" onclick="showMoreHomepage('featured', this)">
                Xem thêm BEST SELLERS
            </button>
        </c:if>
    </div>
</c:if>

<%-- Newest Products --%>
<c:if test="${not empty newest}">
     <div class="container text-center mb-5">
        <div class="section-title-wrapper">
            <h2 class="section-title">
                <i class="bi bi-fire"></i>
                SẢN PHẨM MỚI
            </h2>
        </div>
        <div class="row row-cols-2 row-cols-md-5 g-3">
      <%-- Hiển thị 5 sản phẩm đầu tiên --%>
          <c:forEach var="product" items="${newest}" end="4">
            <c:set var="p" value="${product}" scope="request" />
            <jsp:include page="/views/_partials/product_card.jsp" />
          </c:forEach>
          <%-- Render các sản phẩm còn lại nhưng ẩn đi --%>
          <c:forEach var="product" items="${newest}" 
begin="5">
             <div class="col hidden-newest-item" style="display: none;">
                <c:set var="p" value="${product}" scope="request" />
                <jsp:include page="/views/_partials/product_card.jsp" />
            </div>
          </c:forEach>
        </div>
        <%-- Nút "Xem thêm" --%>
     <c:if test="${fn:length(newest) > 5}">
            <button class="btn btn-outline-primary mt-4" onclick="showMoreHomepage('newest', this)">Xem thêm sản phẩm mới</button>
        </c:if>
    </div>
</c:if>

<c:if test="${not empty promotions}">
<div class="container mb-5">
    <div class="section-title-wrapper">
        <h2 class="section-title">
            <i class="bi bi-megaphone-fill"></i>
            Tin tức & Khuyến mãi
        </h2>
        <p class="text-muted mt-3">Tin tức & Khuyến mãi của AloTra</p>
    </div>
    <div class="row row-cols-1 row-cols-md-4 g-4">
        <c:forEach items="${promotions}" var="promo" varStatus="status" begin="0" end="3">
            <div class="col">
                <div class="card news-card" style="cursor: pointer; transition: transform 0.3s ease;" 
                     onclick="window.location.href='${pageContext.request.contextPath}/promotions?id=${promo.id}'"
                     onmouseover="this.style.transform='translateY(-5px)'"
                     onmouseout="this.style.transform='translateY(0)'">
                    <div style="height: 200px; overflow: hidden;">
                        <c:set var="homePromoImgSrc" value="${promo.imageUrl}"/>
                        <c:if test="${not empty homePromoImgSrc}">
                            <c:choose>
                                <c:when test="${fn:startsWith(homePromoImgSrc, 'http')}">
                                    <c:set var="homePromoImgSrc" value="${promo.imageUrl}"/>
                                </c:when>
                                <c:when test="${fn:startsWith(homePromoImgSrc, 'uploads/')}">
                                    <c:set var="homePromoImgSrc" value="${pageContext.request.contextPath}/${promo.imageUrl}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="homePromoImgSrc" value="${pageContext.request.contextPath}/uploads/${promo.imageUrl}"/>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <img src="${empty homePromoImgSrc ? 'https://via.placeholder.com/300?text=No+Image' : homePromoImgSrc}" 
                             class="card-img-top" alt="${promo.title}"
                             style="width: 100%; height: 100%; object-fit: cover;"
                             onerror="this.src='https://via.placeholder.com/300?text=No+Image'">
                    </div>
                    <div class="card-body">
                        <p class="card-text fw-bold">${promo.title}</p>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
    <div class="text-center mt-4">
        <a href="${pageContext.request.contextPath}/promotions" class="btn btn-outline-primary">Xem tất cả khuyến mãi</a>
    </div>
</div>
</c:if>

<c:if test="${not empty stores}">
<div class="container mb-5">
    <div class="section-title-wrapper">
        <h2 class="section-title">
            <i class="bi bi-shop"></i>
            Hệ thống cửa hàng
        </h2>
        <p class="text-muted mt-3">Các cửa hàng AloTra</p>
    </div>
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
        <c:forEach items="${stores}" var="store" begin="0" end="3">
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
                            <p class="mb-1 small">${store.address}</p>
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
                                <a href="tel:${store.phone}" class="text-decoration-none small">${store.phone}</a>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty store.opening_hours}">
                            <div class="mb-3">
                                <small class="text-muted d-block">
                                    <i class="bi bi-clock me-1"></i>
                                    <strong>Giờ mở cửa:</strong>
                                </small>
                                <small class="text-muted">${store.opening_hours}</small>
                            </div>
                        </c:if>
                        
                        <div class="mt-auto">
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
    <div class="text-center mt-4">
        <a href="${pageContext.request.contextPath}/stores" class="btn btn-outline-primary">Xem tất cả cửa hàng</a>
    </div>
</div>
</c:if>


