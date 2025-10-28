<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:if test="${not empty searchKeyword}">
    <h1 class="h5">Kết quả tìm kiếm cho: "<strong>${searchKeyword}</strong>"</h1>
</c:if>
<c:if test="${empty searchKeyword}">
    <h1 class="h5">Danh sách sản phẩm</h1>
</c:if>

<div id="grid" class="row row-cols-2 row-cols-md-4 g-3 mb-4">
    <%-- Dữ liệu sản phẩm sẽ được chèn vào đây bởi JavaScript --%>
</div>

<div class="text-center">
    <button id="btnLoadMore" class="btn btn-outline-primary" onclick="loadMore()" style="display:none;">
        Xem thêm <i class="bi bi-arrow-down"></i>
    </button>
    <div id="loading" class="spinner-border text-primary" role="status" style="display:none;">
        <span class="visually-hidden">Loading...</span>
    </div>
</div>

<script>
var page = 0;
var isLoading = false;
var hasMore = true;
var searchKeyword = "${searchKeyword}";

document.addEventListener("DOMContentLoaded", function() {
    if (typeof loadMore === 'function') {
        loadMore();
    } else {
        console.error("Hàm loadMore() không tồn tại.");
    }
});
</script>