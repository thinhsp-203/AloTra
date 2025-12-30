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
                    
                    <%-- Tạo thẻ <img> với logic src chính xác --%>
                    <c:set var="imgTag">
                        <c:choose>
                            <%-- Nếu là URL (bắt đầu bằng http) --%>
                            <c:when test="${fn:startsWith(b.imageUrl, 'http')}">
                                <img src="${b.imageUrl}" alt="Banner ${status.count}">
                            </c:when>
                            <c:otherwise>
							    <img src="${pageContext.request.contextPath}/uploads/${b.imageUrl}" alt="Banner ${status.count}">
							</c:otherwise>
                        </c:choose>
                    </c:set>

                    <c:choose>
                        <c:when test="${not empty b.linkUrl}">
                            <a href="${b.linkUrl}" target="_blank">
                                ${imgTag}
                            </a>
                        </c:when>
                        <c:otherwise>
                            ${imgTag}
                        </c:otherwise>
                    </c:choose>
                    
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
        <h2 class="h4 mb-4">BEST SELLERS - TRÀ THƠM CHẤT LƯỢNG</h2>
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
        <h2 class="h4 mb-4">SẢN PHẨM MỚI</h2>
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
    <div class="text-center mb-4">
        <h2 class="h4">Tin tức & Khuyến mãi</h2>
        <p class="text-muted">Tin tức & Khuyến mãi của Phúc Long</p>
    </div>
    <div class="row row-cols-1 row-cols-md-4 g-4">
        <c:forEach items="${promotions}" var="promo" varStatus="status" begin="0" end="3">
            <div class="col">
                <div class="card news-card" style="cursor: pointer; transition: transform 0.3s ease;" 
                     onclick="window.location.href='${pageContext.request.contextPath}/promotions?id=${promo.id}'"
                     onmouseover="this.style.transform='translateY(-5px)'"
                     onmouseout="this.style.transform='translateY(0)'">
                    <div style="height: 200px; overflow: hidden;">
                        <c:choose>
                            <c:when test="${fn:startsWith(promo.imageUrl, 'http')}">
                                <img src="${promo.imageUrl}" class="card-img-top" alt="${promo.title}" 
                                     style="width: 100%; height: 100%; object-fit: cover;">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/uploads/${promo.imageUrl}" 
                                     class="card-img-top" alt="${promo.title}"
                                     style="width: 100%; height: 100%; object-fit: cover;">
                            </c:otherwise>
                        </c:choose>
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

<div class="container-fluid bg-light py-5">
    <div class="container text-center">
        <h2 class="h4">Hệ thống cửa hàng</h2>
        <p class="text-muted mb-4">Tìm cửa hàng AloTra gần bạn nhất</p>
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="input-group input-group-lg mb-4">
                    <input type="text" 
                           class="form-control" 
                           id="storeSearchInput" 
                           placeholder="Nhập địa chỉ, quận/huyện, thành phố...">
                    <button class="btn btn-primary" type="button" id="storeSearchBtn">
                        <i class="bi bi-search"></i> Tìm kiếm
                    </button>
                </div>
                
                <div id="storeSearchResults" class="mt-4" style="display: none;">
                    <div id="storeResultsContainer"></div>
                </div>
                
                <div id="storeSearchLoading" class="text-center mt-4" style="display: none;">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Đang tìm kiếm...</span>
                    </div>
                </div>
            </div>
        </div>
        
    </div>
</div>

<script>
(function() {
    const searchInput = document.getElementById('storeSearchInput');
    const searchBtn = document.getElementById('storeSearchBtn');
    const resultsContainer = document.getElementById('storeResultsContainer');
    const resultsDiv = document.getElementById('storeSearchResults');
    const loadingDiv = document.getElementById('storeSearchLoading');
    const contextPath = '${pageContext.request.contextPath}';
    
    function searchStores() {
        const keyword = searchInput.value.trim();
        
        if (keyword === '') {
            alert('Vui lòng nhập địa chỉ để tìm kiếm!');
            return;
        }
        
        loadingDiv.style.display = 'block';
        resultsDiv.style.display = 'none';
        
        fetch(contextPath + '/api/stores/search?keyword=' + encodeURIComponent(keyword))
            .then(response => response.json())
            .then(data => {
                loadingDiv.style.display = 'none';
                
                if (data.success) {
                    displayResults(data.stores, keyword);
                } else {
                    resultsContainer.innerHTML = '<div class="alert alert-danger">' + 
                        (data.message || 'Có lỗi xảy ra khi tìm kiếm') + '</div>';
                    resultsDiv.style.display = 'block';
                }
            })
            .catch(error => {
                loadingDiv.style.display = 'none';
                resultsContainer.innerHTML = '<div class="alert alert-danger">Có lỗi xảy ra khi tìm kiếm cửa hàng</div>';
                resultsDiv.style.display = 'block';
                console.error('Error:', error);
            });
    }
    
    function displayResults(stores, keyword) {
        if (stores.length === 0) {
            resultsContainer.innerHTML = '<div class="alert alert-info">' +
                'Không tìm thấy cửa hàng nào với từ khóa: <strong>' + escapeHtml(keyword) + '</strong></div>';
            resultsDiv.style.display = 'block';
            return;
        }
        
        let html = '<h5 class="mb-3">Tìm thấy ' + stores.length + ' cửa hàng:</h5>';
        html += '<div class="row g-3">';
        
        stores.forEach(store => {
            html += '<div class="col-md-6 col-lg-4">';
            html += '<div class="card h-100">';
            html += '<div class="card-body">';
            html += '<h6 class="card-title text-primary"><i class="bi bi-shop"></i> ' + escapeHtml(store.store_name) + '</h6>';
            
            if (store.address) {
                html += '<p class="card-text mb-1"><i class="bi bi-geo-alt"></i> <small>' + escapeHtml(store.address) + '</small></p>';
            }
            
            if (store.district || store.city) {
                let location = [];
                if (store.district) location.push(store.district);
                if (store.city) location.push(store.city);
                if (location.length > 0) {
                    html += '<p class="card-text mb-1"><small class="text-muted">' + escapeHtml(location.join(', ')) + '</small></p>';
                }
            }
            
            if (store.phone) {
                html += '<p class="card-text mb-1"><i class="bi bi-telephone"></i> <small>' + escapeHtml(store.phone) + '</small></p>';
            }
            
            if (store.opening_hours) {
                html += '<p class="card-text mb-0"><i class="bi bi-clock"></i> <small class="text-muted">' + escapeHtml(store.opening_hours) + '</small></p>';
            }
            
            html += '</div>';
            html += '</div>';
            html += '</div>';
        });
        
        html += '</div>';
        resultsContainer.innerHTML = html;
        resultsDiv.style.display = 'block';
        
        // Scroll to results
        resultsDiv.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
    
    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.toString().replace(/[&<>"']/g, m => map[m]);
    }
    
    // Event listeners
    searchBtn.addEventListener('click', searchStores);
    
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            searchStores();
        }
    });
})();
</script>