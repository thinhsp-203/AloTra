<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="row g-4">
    <div class="col-md-3">
        <%-- Include sidebar --%>
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">Thông tin cá nhân</h2>

        <c:if test="${not empty success}">
          <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>

        <c:if test="${not empty error}">
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>
        
        <div class="row g-4">
            <div class="col-md-6">
    <div class="card">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Cập nhật thông tin</h5>
      </div>
      <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/user/profile">
          <input type="hidden" name="action" value="updateProfile">
          
          <div class="mb-3">
            <label class="form-label">Email <span class="text-muted small">(không thể thay đổi)</span></label>
            <input type="email" class="form-control" value="${user.email}" readonly>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Username <span class="text-muted small">(không thể thay đổi)</span></label>
            <input type="text" class="form-control" value="${user.username}" readonly>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Họ tên</label>
            <input type="text" class="form-control" name="fullname" value="${user.fullname}" required>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Số điện thoại</label>
            <input type="tel" class="form-control" name="phone" value="${user.phone}" pattern="[0-9]{9,11}">
          </div>
          
          <div class="mb-3">
            <label class="form-label">Địa chỉ</label>
            <textarea class="form-control" name="address" rows="3">${user.address}</textarea>
          </div>
          
          <button type="submit" class="btn btn-primary">Cập nhật thông tin</button>
        </form>
      </div>
    </div>
  </div>
  
  <!-- Đổi mật khẩu -->
  <div class="col-md-6">
    <div class="card">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Đổi mật khẩu</h5>
      </div>
      <div class="card-body">
        <form method="post" action="${pageContext.request.contextPath}/user/profile" id="changePasswordForm">
          <input type="hidden" name="action" value="changePassword">
          
          <div class="mb-3">
            <label class="form-label">Mật khẩu hiện tại</label>
            <input type="password" class="form-control" name="oldPassword" required>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Mật khẩu mới</label>
            <input type="password" class="form-control" name="newPassword" id="newPassword" minlength="6" required>
            <small class="text-muted">Tối thiểu 6 ký tự</small>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Xác nhận mật khẩu mới</label>
            <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" minlength="6" required>
          </div>
          
          <button type="submit" class="btn btn-warning">Đổi mật khẩu</button>
        </form>
      </div>
    </div>
    
    <!-- Thống kê đơn hàng -->
    <div class="card mt-4">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Thống kê</h5>
      </div>
      <div class="card-body">
        <div class="d-flex justify-content-between mb-2">
          <span>Tài khoản từ:</span>
          <strong>
            <c:choose>
              <c:when test="${not empty user.createdDate}">
                <fmt:formatDate value="${user.createdDate}" pattern="dd/MM/yyyy"/>
              </c:when>
              <c:otherwise>N/A</c:otherwise>
            </c:choose>
          </strong>
        </div>
        <div class="d-flex justify-content-between">
          <span>Vai trò:</span>
          <strong>${user.roleName}</strong>
        </div>
        <hr>
        <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-outline-primary btn-sm w-100">
          Xem đơn hàng của tôi
        </a>
      </div>
    </div>
  </div>
</div>

<script>
document.getElementById('changePasswordForm').addEventListener('submit', function(e) {
  var newPass = document.getElementById('newPassword').value;
  var confirmPass = document.getElementById('confirmPassword').value;
  
  if (newPass !== confirmPass) {
    e.preventDefault();
    alert('Mật khẩu xác nhận không khớp!');
  }
});
</script>

<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>