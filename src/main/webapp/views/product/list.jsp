<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %> <%-- Đảm bảo có dòng này --%>

<div class="container my-4">
    <div class="row">
        <div class="col-lg-3">
            <h5 class="mb-3"><i class="bi bi-filter"></i> Bộ lọc</h5>
            
            <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/products">
                <input type="hidden" name="q" value="${fn:escapeXml(keyword)}">
                <input type="hidden" name="cate" value="${fn:escapeXml(selectedCateId)}">
                
                <div class="mb-3">
                    <label class="form-label fw-semibold">Danh mục</label>
                    <div class="list-group">
                        <a href="${pageContext.request.contextPath}/products?q=${fn:escapeXml(keyword)}&sortBy=${fn:escapeXml(selectedSortBy)}${not empty selectedPrice ? '&price=' : ''}${fn:escapeXml(selectedPrice)}" 
                           class="list-group-item list-group-item-action ${empty selectedCateId ? 'active' : ''}">
                           <i class="bi bi-grid-3x3-gap me-2"></i>Tất cả danh mục
                        </a>
                        <c:forEach var="cat" items="${categories}">
                            <a href="${pageContext.request.contextPath}/products?cate=${cat.id}&q=${fn:escapeXml(keyword)}&sortBy=${fn:escapeXml(selectedSortBy)}${not empty selectedPrice ? '&price=' : ''}${fn:escapeXml(selectedPrice)}" 
                               class="list-group-item list-group-item-action ${selectedCateId eq cat.id ? 'active' : ''}">
                                <c:if test="${not empty cat.icon}">
                                    <c:choose>
                                        <c:when test="${fn:startsWith(cat.icon, 'http')}">
                                            <img src="${cat.icon}" alt="${cat.name}" style="width: 20px; height: 20px; object-fit: contain; margin-right: 8px;">
                                        </c:when>
                                        <c:when test="${fn:startsWith(cat.icon, 'uploads/')}">
                                            <img src="${pageContext.request.contextPath}/${cat.icon}" alt="${cat.name}" style="width: 20px; height: 20px; object-fit: contain; margin-right: 8px;">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/uploads/${cat.icon}" alt="${cat.name}" style="width: 20px; height: 20px; object-fit: contain; margin-right: 8px;">
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                                ${cat.name}
                            </a>
                        </c:forEach>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="sortBy" class="form-label fw-semibold">Sắp xếp theo</label>
                    <select class="form-select" name="sortBy" id="sortBy" onchange="this.form.submit()">
                        <option value="default" ${empty selectedSortBy ? 'selected' : ''}>Mặc định</option>
                        <option value="newest" ${selectedSortBy eq 'newest' ? 'selected' : ''}>Mới nhất</option>
                        <option value="price-asc" ${selectedSortBy eq 'price-asc' ? 'selected' : ''}>Giá: Tăng dần</option>
                        <option value="price-desc" ${selectedSortBy eq 'price-desc' ? 'selected' : ''}>Giá: Giảm dần</option>
                    </select>
                </div>
                
                <div class="mb-3">
                    <label class="form-label fw-semibold">Lọc theo giá</label>
                    <div class="list-group">
                        <a href="${pageContext.request.contextPath}/products?cate=${fn:escapeXml(selectedCateId)}&q=${fn:escapeXml(keyword)}&sortBy=${fn:escapeXml(selectedSortBy)}" 
                           class="list-group-item list-group-item-action ${empty selectedPrice ? 'active' : ''}">
                           Tất cả giá
                        </a>
                        <a href="${pageContext.request.contextPath}/products?cate=${fn:escapeXml(selectedCateId)}&q=${fn:escapeXml(keyword)}&sortBy=${fn:escapeXml(selectedSortBy)}&price=0-50000" 
                           class="list-group-item list-group-item-action ${selectedPrice eq '0-50000' ? 'active' : ''}">
                           Dưới 50.000₫
                        </a>
                        <a href="${pageContext.request.contextPath}/products?cate=${fn:escapeXml(selectedCateId)}&q=${fn:escapeXml(keyword)}&sortBy=${fn:escapeXml(selectedSortBy)}&price=50000-100000" 
                           class="list-group-item list-group-item-action ${selectedPrice eq '50000-100000' ? 'active' : ''}">
                           50.000₫ - 100.000₫
                        </a>
                        <a href="${pageContext.request.contextPath}/products?cate=${fn:escapeXml(selectedCateId)}&q=${fn:escapeXml(keyword)}&sortBy=${fn:escapeXml(selectedSortBy)}&price=100000%2B" 
                           class="list-group-item list-group-item-action ${selectedPrice eq '100000+' ? 'active' : ''}">
                           Trên 100.000₫
                        </a>
                    </div>
                </div>
            </form>
        </div>
        
        <div class="col-lg-9">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h4 class="mb-0">
                    <c:choose>
                        <c:when test="${not empty category}">${category.name}</c:when>
                        <c:when test="${not empty keyword}">Kết quả tìm kiếm</c:when>
                        <c:otherwise>Tất cả sản phẩm</c:otherwise>
                    </c:choose>
                </h4>
                <span class="text-muted small">${fn:length(products)} sản phẩm</span>
            </div>

            <div class="row row-cols-1 row-cols-md-3 g-4" id="productsContainer">
                <c:choose>
                    <c:when test="${empty products}">
                        <div class="col-12">
                            <div class="alert alert-warning text-center">
                                <i class="bi bi-emoji-frown fs-3"></i>
                                <p class="mb-0 mt-2">Không tìm thấy sản phẩm nào phù hợp.</p>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <%-- Nếu là menu click (không keyword, không phân trang thủ công) thì hiển thị tất cả, ngược lại hiển thị 6 sản phẩm đầu --%>
                        <c:choose>
                            <c:when test="${isMenuClick}">
                                <%-- Click vào menu: Hiển thị TẤT CẢ sản phẩm ngay lập tức --%>
                                <c:forEach var="product" items="${products}">
                                    <div class="col">
                                        <c:set var="p" value="${product}" scope="request"/>
                                        <jsp:include page="/views/_partials/product_card.jsp" />
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <%-- Các trường hợp khác: Hiển thị 6 sản phẩm đầu tiên --%>
                                <c:forEach var="product" items="${products}" varStatus="status" begin="0" end="5">
                                    <div class="col">
                                        <c:set var="p" value="${product}" scope="request"/>
                                        <jsp:include page="/views/_partials/product_card.jsp" />
                                    </div>
                                </c:forEach>
                                <%-- Render các sản phẩm còn lại nhưng ẩn đi --%>
                                <c:forEach var="product" items="${products}" begin="6">
                                    <div class="col hidden-product-item" style="display: none;">
                                        <c:set var="p" value="${product}" scope="request"/>
                                        <jsp:include page="/views/_partials/product_card.jsp" />
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
<%-- Chỉ hiển thị nút "Xem thêm" khi không phải menu click và có nhiều hơn 6 sản phẩm --%>
<c:if test="${not isMenuClick && fn:length(products) > 6}">
<div class="text-center mt-4" id="loadMoreContainer">
    <button class="btn btn-outline-primary" onclick="showMoreProducts(this)">
        Xem thêm sản phẩm <i class="bi bi-chevron-down"></i>
    </button>
</div>
</c:if>