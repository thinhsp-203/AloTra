<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<h1 class="h5">Danh sách hàng hóa</h1>
<div id="grid" class="row row-cols-2 row-cols-md-4 g-3">
    <%-- Dữ liệu sản phẩm sẽ được chèn vào đây bởi JavaScript --%>
</div>

<%-- SỬA Ở ĐÂY: Chạy script sau khi DOM sẵn sàng --%>
<script>
document.addEventListener("DOMContentLoaded", function() {
    // Gọi lần đầu để tải trang 1
    if (typeof loadMore === 'function') {
        loadMore();
    } else {
        console.error("Hàm loadMore() không tồn tại.");
    }
});
</script>