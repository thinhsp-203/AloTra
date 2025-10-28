<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<h1 class="h5 mb-3">Đặt lại mật khẩu</h1>

<c:if test="${invalid}">
  <div class="alert alert-danger">Liên kết không hợp lệ hoặc đã hết hạn.</div>
</c:if>
<c:if test="${not empty error}">
  <div class="alert alert-warning">${error}</div>
</c:if>

<c:if test="${!invalid}">
  <form method="post" action="${pageContext.request.contextPath}/auth/reset">
    <input type="hidden" name="token" value="${token}"/>
    <div class="mb-2">
      <label class="form-label">Mật khẩu mới</label>
      <input class="form-control" type="password" name="password" required minlength="6"/>
    </div>
    <div class="mb-3">
      <label class="form-label">Xác nhận mật khẩu</label>
      <input class="form-control" type="password" name="confirm" required minlength="6"/>
    </div>
    <button class="btn btn-primary">Cập nhật mật khẩu</button>
  </form>
</c:if>

<a class="btn btn-outline-secondary mt-3" href="${pageContext.request.contextPath}/login">Về đăng nhập</a>
