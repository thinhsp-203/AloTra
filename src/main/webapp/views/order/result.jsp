<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h5 mb-3">Kết quả thanh toán</h1>
<c:choose>
  <c:when test="${payStatus eq 'success'}">
    <div class="alert alert-success">Thanh toán thành công (mô phỏng). Đơn hàng đã được ghi nhận.</div>
  </c:when>
  <c:otherwise>
    <div class="alert alert-warning">Thanh toán thất bại hoặc bị hủy.</div>
  </c:otherwise>
</c:choose>
<a class="btn btn-primary" href="${pageContext.request.contextPath}/home">Về trang chủ</a>
