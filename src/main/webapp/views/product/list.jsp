<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<style>
  .filter-sidebar {
    background: #f8f9fa;
    border-radius: 0.5rem;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
  }
  .filter-section {
    margin-bottom: 1.5rem;
  }
  .filter-section h6 {
    font-size: 0.9rem;
    font-weight: 600;
    text-transform: uppercase;
    color: #6c757d;
    margin-bottom: 1rem;
  }
  .category-filter-item {
    display: block;
    padding: 0.5rem 0.75rem;
    margin-bottom: 0.25rem;
    border-radius: 0.25rem;
    text-decoration: none;
    color: #495057;
    transition: all 0.2s;
  }
  .category-filter-item:hover {
    background: #e9ecef;
    color: #0d6efd;
  }
  .category-filter-item.active {
    background: #0d6efd;
    color: #fff;
    font-weight: 500;
  }
  .page-header {
    margin-bottom: 1.5rem;
  }
</style>

<div class="row g-4">
  <!-- Sidebar Filter -->
  <div class="col-md-3">
    <div class="filter-sidebar">
      <div class="filter-section">
        <h6><i class="bi bi-funnel"></i> Lọc sản phẩm</h6>
        
        <!-- Category Filter -->
        <div class="mb-3">
          <label class="form-label small fw-semibold">Danh mục</label>
          <div>
            <a href="${contextPath}/products" 
               class="category-filter-item ${empty selectedCate ? 'active' : ''}">
              <i class="bi bi-grid"></i> Tất cả sản phẩm
            </a>
            <c:forEach var="cat" items="${categories}">
              <a href="${contextPath}/products?cate=${cat.id}" 
                 class="category-filter-item ${selectedCate eq cat.id ? 'active' : ''}">
                ${cat.name}
              </a>
            </c:forEach>
          </div>
        </div>
      </div>
      
      <!-- Clear Filter Button -->
      <c:if test="${not empty selectedCate or not empty searchKeyword}">
        <a href="${contextPath}/products" class="btn btn-outline-secondary btn-sm w-100">
          <i class="bi bi-x-circle"></i> Xóa bộ lọc
        </a>
      </c:if>
    </div>
  </div>
  
  <!-- Products Grid -->
  <div class="col-md-9">
    <div class="page-header">
      <c:choose>
        <c:when test="${not empty currentCategory}">
          <h1 class="h4 mb-1">${currentCategory.name}</h1>
          <p class="text-muted small mb-0">Khám phá các sản phẩm ${currentCategory.name} chất lượng</p>
        </c:when>
        <c:when test="${not empty searchKeyword}">
          <h1 class="h5 mb-1">Kết quả tìm kiếm: "<strong>${searchKeyword}</strong>"</h1>
        </c:when>
        <c:otherwise>
          <h1 class="h5 mb-1">Tất cả sản phẩm</h1>
          <p class="text-muted small mb-0">Khám phá menu đa dạng của chúng tôi</p>
        </c:otherwise>
      </c:choose>
    </div>

    <div id="grid" class="row row-cols-2 row-cols-md-3 g-3 mb-4">
        <%-- Dữ liệu sản phẩm sẽ được chèn vào đây bởi JavaScript --%>
    </div>

    <div class="text-center">
        <button id="btnLoadMore" class="btn btn-outline-primary" onclick="loadMore()" style="display:none;">
            <i class="bi bi-arrow-down-circle"></i> Xem thêm
        </button>
        <div id="loading" class="spinner-border text-primary" role="status" style="display:none;">
            <span class="visually-hidden">Đang tải...</span>
        </div>
    </div>
  </div>
</div>

<script>
var page = 0;
var isLoading = false;
var hasMore = true;
var searchKeyword = "${searchKeyword}";
var selectedCate = "${selectedCate}";
var selectedSupplier = "${selectedSupplier}";

document.addEventListener("DOMContentLoaded", function() {
    if (typeof loadMore === 'function') {
        loadMore();
    } else {
        console.error("Hàm loadMore() không tồn tại.");
    }
});
</script>