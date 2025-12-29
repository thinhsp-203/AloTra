<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">
            <i class="bi bi-person-circle text-primary"></i> Thông tin cá nhân
        </h2>

        <c:if test="${not empty success}">
          <div 
class="alert alert-success alert-dismissible fade show">
            <i class="bi bi-check-circle"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>

        <c:if test="${not empty error}">
          <div class="alert alert-danger alert-dismissible fade show">
            <i class="bi bi-exclamation-triangle"></i> ${error}
       
     <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>
        
        <div class="row g-4">
        
            <div class="col-md-4">
                <div class="card">
                     <div class="card-header bg-light">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-image"></i> Ảnh đại diện
                        </h5>
                    </div>
                    <div class="card-body text-center">
                        <form method="post" action="${pageContext.request.contextPath}/user/profile" 
                              enctype="multipart/form-data" id="avatarForm">
                            <input type="hidden" name="action" value="changeAvatar">
                            
                            <c:set var="avatarSrc">
                                <c:choose>
                                    <c:when test="${not empty user.avatar}">
                                        ${pageContext.request.contextPath}/uploads/${user.avatar}
                                    </c:when>
                                    <c:otherwise>
                                        https://via.placeholder.com/200/006633/FFFFFF?text=${fn:substring(user.username, 0, 1)}
                                    </c:otherwise>
                                </c:choose>
                            </c:set>
                            <img src="${avatarSrc}" id="avatarPreview" 
                                 class="rounded-circle border mb-3" 
                                 style="width: 200px; height: 200px; object-fit: cover;"
                                 alt="Avatar">

                            <div class="mb-3">
                                <label for="avatarInput" class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-upload"></i> Chọn ảnh mới
                                </label>
                                <input type="file" name="avatar" id="avatarInput" 
                                       class="d-none" accept="image/png, image/jpeg, image/gif">
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100" id="btnSaveAvatar" style="display: none;">
                                <i class="bi bi-save"></i> Lưu ảnh
                            </button>
                        </form>
                    </div>
                </div>
            </div>
            
            <div class="col-md-8">
                <div class="card h-100">
         
           <div class="card-header bg-light">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-pencil-square"></i> Cập nhật thông tin
                        </h5>
   
                 </div>
                    <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/user/profile">
                            <input type="hidden" name="action" value="updateProfile">
     
                       
                            <div class="mb-3">
                                <label class="form-label">Email</label>
               
                 <input type="email" class="form-control" value="${user.email}" readonly>
                                <small class="text-muted">Email không thể thay đổi</small>
                            </div>
              
              
                            <div class="mb-3">
                                <label class="form-label">Username</label>
                        
        <input type="text" class="form-control" value="${user.username}" readonly>
                                <small class="text-muted">Username không thể thay đổi</small>
                            </div>
                       
     
                            <div class="mb-3">
                                <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                              
  <input type="text" class="form-control" name="fullname" 
                                       value="${user.fullname}" required maxlength="100">
                            </div>
                         
   
                            <div class="mb-3">
                                <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                <input 
type="tel" class="form-control" name="phone" 
                                       value="${user.phone}" pattern="[0-9]{9,11}" 
                                       placeholder="9-11 chữ số" required>
               
             </div>
                            
                            <div class="mb-3">
                              
  <label class="form-label">Địa chỉ</label>
                                <textarea class="form-control" name="address" rows="3" 
                                          maxlength="500">${user.address}</textarea>
                  
          </div>
                            
                            <button type="submit" class="btn btn-primary w-100">
                              
  <i class="bi bi-save"></i> Cập nhật thông tin
                            </button>
                        </form>
                    </div>
                </div>
        </div>
            
            <div class="col-md-12">
                <div class="card h-100">
                    <div class="card-header bg-light">
           
             <h5 class="card-title mb-0">
                            <i class="bi bi-shield-lock"></i> Đổi mật khẩu
                        </h5>
                    </div>
        
            <div class="card-body">
                        <form method="post" action="${pageContext.request.contextPath}/user/profile" 
                              id="changePasswordForm">
                            <input type="hidden" 
name="action" value="changePassword">
                            
                            <div class="mb-3">
                                <label class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
    
                            <div class="input-group">
                                    <input type="password" class="form-control" name="oldPassword" 
                               
            id="oldPassword" required>
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="togglePasswordVisibility('oldPassword')">
   
                                     <i class="bi bi-eye"></i>
                                    </button>
                         
       </div>
                            </div>
                            
                            <div class="mb-3">
        
                        <label class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <input type="password" 
class="form-control" name="newPassword" 
                                           id="newPassword" minlength="6" maxlength="100" required>
                                    <button class="btn btn-outline-secondary" type="button" 
            
                                onclick="togglePasswordVisibility('newPassword')">
                                        <i class="bi bi-eye"></i>
                          
          </button>
                                </div>
                                <small class="text-muted">Tối thiểu 6 ký tự</small>
                     
           <div class="progress mt-2" style="height: 5px;">
                                    <div class="progress-bar" id="newPasswordStrength" style="width: 0%"></div>
                                </div>
             
               </div>
                            
                            <div class="mb-3">
                            
    <label class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" name="confirmPassword" 
                                       id="confirmPassword" minlength="6" required>
            
                    <small class="form-text text-danger d-none" id="passwordMismatchMsg">
                                    Mật khẩu không khớp!
</small>
                            </div>
                            
                            <button type="submit" class="btn btn-warning w-100">
            
                    <i class="bi bi-key"></i> Đổi mật khẩu
                            </button>
                        </form>
                    </div>
   
             </div>
            </div>
            
             <div class="col-md-12">
                <div class="card mt-4">
                    <div class="card-header bg-light">
           
             <h5 class="card-title mb-0">
                            <i class="bi bi-info-circle"></i> Thông tin tài khoản
                        </h5>
                    </div>
       
             <div class="card-body">
                        <div class="d-flex justify-content-between mb-2">
                            <span><i class="bi bi-calendar-event"></i> Ngày tạo:</span>
                           
 <strong>
                                <c:choose>
                                    <c:when test="${not empty user.createdDateAsDate}">
                            
            <fmt:formatDate value="${user.createdDateAsDate}" pattern="dd/MM/yyyy"/>
                                    </c:when>
                                    <c:otherwise>N/A</c:otherwise>
              
                  </c:choose>
                            </strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
   
                         <span><i class="bi bi-person-badge"></i> Vai trò:</span>
                            <strong>
                                <span class="badge bg-${user.roleid == 1 ?
'danger' : user.roleid == 2 ? 'warning' : 'primary'}">
                                    ${user.roleName}
                                </span>
                        
    </strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span><i class="bi bi-shield-check"></i> Trạng thái:</span>
              
              <strong>
                                <span class="badge bg-${user.isActive ?
'success' : 'secondary'}">
                                    ${user.isActive ? 'Hoạt động' : 'Vô hiệu hóa'}
                                </span>
                       
     </strong>
                        </div>
                        <hr>
                        <a href="${pageContext.request.contextPath}/user/orders" 
                     
      class="btn btn-outline-primary btn-sm w-100">
                            <i class="bi bi-receipt"></i> Xem đơn hàng của tôi
                        </a>
                    </div>
            
    </div>
            </div>
        </div>
    </div>
</div>

<script>
// (Giữ nguyên các script cho password và avatar)
function togglePasswordVisibility(fieldId) {
    const field = document.getElementById(fieldId);
    const btn = field.nextElementSibling; 
    if (!btn) return;
    const icon = btn.querySelector('i');
    if (!icon) return;
    if (field.type === 'password') {
        field.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
      field.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
}
}

document.getElementById('newPassword')?.addEventListener('input', function() {
    const password = this.value;
    const strengthBar = document.getElementById('newPasswordStrength');
    let strength = 0;
    let strengthClass = '';
    if (password.length >= 6) strength += 20;
    if (password.length >= 10) strength += 20;
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/[0-9]/.test(password)) strength += 10;
    if (/[^A-Za-z0-9]/.test(password)) strength += 10;
 if (strength < 40) {
        strengthClass = 'bg-danger';
    } else if (strength < 70) {
        strengthClass = 'bg-warning';
    } else {
        strengthClass = 'bg-success';
    }
    strengthBar.style.width = strength + '%';
    strengthBar.className = 'progress-bar ' + strengthClass;
});

document.getElementById('confirmPassword')?.addEventListener('input', function() {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = this.value;
    const mismatchMsg = document.getElementById('passwordMismatchMsg');
    if (confirmPassword && newPassword !== confirmPassword) {
        this.classList.add('is-invalid');
        mismatchMsg.classList.remove('d-none');
    } else {
        this.classList.remove('is-invalid');
        mismatchMsg.classList.add('d-none');
    }
});

document.getElementById('changePasswordForm')?.addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    if (newPassword !== confirmPassword) {
        e.preventDefault();
        alert('Mật khẩu xác nhận không khớp!');
        document.getElementById('confirmPassword').focus();
        return false;
    }
    if (newPassword.length < 6) {
        e.preventDefault();
        alert('Mật khẩu mới phải có ít nhất 6 ký tự!');
        document.getElementById('newPassword').focus();
        return false;
    }
    return true;
});

document.getElementById('avatarInput').addEventListener('change', function(e) {
  const file = e.target.files[0];
  if (file) {
    if (file.size > 10 * 1024 * 1024) { // 10MB
        alert('Lỗi: Kích thước file quá lớn (Tối đa 10MB)');
        e.target.value = null;
        return;
    }
    const reader = new FileReader();
    reader.onload = function(e) {
      document.getElementById('avatarPreview').src = e.target.result;
      document.getElementById('btnSaveAvatar').style.display = 'block';
    }
    reader.readAsDataURL(file);
  }
});
</script>