<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Hero Section --%>
<div class="hero-section mb-5">
    <div class="hero-overlay"></div>
    <div class="hero-content text-white">
        <h1 class="display-4 fw-bold">Đặt món giao tận nơi</h1>
        <p class="lead">Trải nghiệm hương vị trà và cà phê đậm vị đặc trưng.</p>
    </div>
</div>


<%-- Best Sellers --%>
<c:if test="${not empty featured}">
    <div class="container text-center mb-5">
        <h2 class="h4 mb-4">BEST SELLERS - TRÀ THƠM CHẤT LƯỢNG</h2>
        <div class="row row-cols-2 row-cols-md-5 g-3">
          <%-- Hiển thị 5 sản phẩm đầu tiên --%>
          <c:forEach var="product" items="${featured}" varStatus="status" begin="0" end="4">
            <c:set var="p" value="${product}" scope="request" />
            <jsp:include page="/views/_partials/product_card.jsp" />
          </c:forEach>
          <%-- Render các sản phẩm còn lại nhưng ẩn đi --%>
          <c:forEach var="product" items="${featured}" begin="5">
            <div class="col hidden-featured-item" style="display: none;">
                <c:set var="p" value="${product}" scope="request" />
                <jsp:include page="/views/_partials/product_card.jsp" />
            </div>
          </c:forEach>
        </div>
        <%-- Nút "Xem thêm" --%>
        <c:if test="${fn:length(featured) > 5}">
            <button class="btn btn-outline-primary mt-4" onclick="showMoreHomepage('featured', this)">Xem thêm BEST SELLERS</button>
        </c:if>
    </div>
</c:if>

<%-- Newest Products --%>
<c:if test="${not empty newest}">
     <div class="container text-center mb-5">
        <h2 class="h4 mb-4">SẢN PHẨM MỚI</h2>
        <div class="row row-cols-2 row-cols-md-5 g-3">
          <%-- Hiển thị 5 sản phẩm đầu tiên --%>
          <c:forEach var="product" items="${newest}" end="4">
            <c:set var="p" value="${product}" scope="request" />
            <jsp:include page="/views/_partials/product_card.jsp" />
          </c:forEach>
          <%-- Render các sản phẩm còn lại nhưng ẩn đi --%>
          <c:forEach var="product" items="${newest}" begin="5">
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

<div class="container mb-5">
    <div class="text-center mb-4">
        <h2 class="h4">Tin tức & Khuyến mãi</h2>
        <p class="text-muted">Tin tức & Khuyến mãi của Phúc Long</p>
    </div>
    <div class="row row-cols-1 row-cols-md-4 g-4">
        <div class="col">
            <div class="card news-card">
                <img src="https://static.phuclong.com.vn/storage/5/2024/5/3/663467c60361f_dua-game-len-top-100-trung-thuong.jpg" class="card-img-top" alt="News">
                <div class="card-body">
                    <p class="card-text fw-bold">ĐUA GAME LÊN TOP - 100% TRÚNG THƯỞNG</p>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card news-card">
                <img src="https://static.phuclong.com.vn/storage/5/2024/4/26/662b21c4e883f_dam-cuoi-ke-save-the-date-thumbnail.jpg" class="card-img-top" alt="News">
                <div class="card-body">
                    <p class="card-text fw-bold">ĐÁM CƯỚI KỂ - SAVE THE DATE</p>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card news-card">
                <img src="https://static.phuclong.com.vn/storage/5/2024/4/22/6625dff10a300_resize-photo-online.jpg" class="card-img-top" alt="News">
                <div class="card-body">
                    <p class="card-text fw-bold">ƯU ĐÃI HỘI VIÊN - GIẢM 50% TRÀ SỮA BEST SELLER</p>
                </div>
            </div>
        </div>
         <div class="col">
            <div class="card news-card">
                <img src="https://static.phuclong.com.vn/storage/5/2023/12/12/teishoku-matcha-thumbnail.jpg" class="card-img-top" alt="News">
                <div class="card-body">
                    <p class="card-text fw-bold">TEISHOKU MATCHA - BỘ ĐÔI MATCHA ĐẬM VỊ</p>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid bg-light py-5">
    <div class="container text-center">
        <h2 class="h4">Hệ thống cửa hàng</h2>
        <p class="text-muted mb-4">Tìm cửa hàng AloTra gần bạn nhất</p>
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="input-group">
                    <input type="text" class="form-control" placeholder="Nhập địa chỉ của bạn...">
                    <button class="btn btn-primary" type="button">Tìm kiếm</button>
                </div>
            </div>
        </div>
    </div>
</div>