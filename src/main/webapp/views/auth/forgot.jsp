<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<h1 class="h5 mb-3">Quên mật khẩu</h1>

<c:if test="${not empty msg}">
  <div class="alert alert-info">${msg}</div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/auth/forgot">
  <div class="mb-2">
    <label class="form-label">Email</label>
    <input class="form-control" type="email" name="email" required />
  </div>
  <button class="btn btn-primary">Gửi hướng dẫn đặt lại</button>
  <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/auth/login">Về đăng nhập</a>
</form>
