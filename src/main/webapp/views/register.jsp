<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
  .auth-wrapper {
    background-color: #f4f7f6;
  }
  .auth-card {
    border: none;
    border-radius: 0;
  }
  .auth-image-col {
    background-color: #f8f9fa;
    min-height: 300px;
  }
  
  @media (min-width: 992px) {
    .auth-card {
      border-radius: 0.5rem 0 0 0.5rem;
    }
    .auth-image-col {
      border-radius: 0 0.5rem 0.5rem 0;
      min-height: auto;
    }
    .auth-row {
       box-shadow: 0 1rem 3rem rgba(0,0,0,.175);
       border-radius: 0.5rem;
    }
  }
</style>

<div class="container my-5 auth-wrapper">
    <div class="row g-0 justify-content-center auth-row">
        
        <div class="col-lg-6 col-md-9">
            <div class="card shadow-lg auth-card">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <img src="https://github.com/TurtleBP/Android/blob/main/app/src/main/res/mipmap-xhdpi/ic_launcher.webp?raw=true" alt="AloTra" style="height: 50px;" class="mb-3">
                        <h2 class="h4 mt-2 mb-0">Đăng ký tài khoản</h2>
                        <p class="text-muted small">Tạo tài khoản mới để bắt đầu mua sắm</p>
                    </div>
       
             
                    <c:if test="${not empty alert}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i> ${alert}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    
           <form action="register" method="post" id="registerForm">
                        <div class="row g-3">
                            <div class="col-12">
                                <label class="form-label">Email <span class="text-danger">*</span></label>
                                <div class="form-icon-group">
                                    <i class="bi bi-envelope form-icon"></i>
                                    <input type="email" 
                               class="form-control" 
                                           name="email" 
           value="${email}"
                                           placeholder="example@email.com" 
                                           required 
                  autofocus>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                   <label class="form-label">Tên đăng nhập <span class="text-danger">*</span></label>
                                <div class="form-icon-group">
                                    <i class="bi bi-person form-icon"></i>
                                    <input type="text" 
                                           class="form-control" 
                         name="username" 
                                           value="${username}"
                                           placeholder="4-32 ký tự" 
                                   pattern="[A-Za-z0-9._-]{4,32}"
                                           title="4-32 ký tự, chỉ chữ/số/._-"
                                           required>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
     <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
                                <div class="form-icon-group">
                                    <i class="bi bi-person-badge form-icon"></i>
                                    <input type="text" 
                                           class="form-control" 
           name="fullname" 
                                           value="${fullname}"
                                           placeholder="Nguyễn Văn A" 
                    required>
                                </div>
                            </div>
                            
                            <div class="col-12">
                     <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                <div class="form-icon-group">
                                    <i class="bi bi-telephone form-icon"></i>
                                    <input type="tel" 
                                           class="form-control" 
                         name="phone" 
                                           value="${phone}"
                                           placeholder="0901234567" 
                                   pattern="[0-9]{9,11}"
                                           title="9-11 chữ số"
                                           required>
                                </div>
                            </div>
               
                            <div class="col-md-6">
                                <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                                <div class="form-icon-group">
                                    <i class="bi bi-lock form-icon"></i>
                                 <input type="password" 
                                           class="form-control" 
              name="password" 
                                           id="password"
                                           placeholder="Tối thiểu 6 ký tự" 
                                     minlength="6"
                                           required>
        <button class="btn-toggle-pass" 
                                            type="button" 
                                            id="togglePassword">
                             <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                  <div class="progress mt-2" style="height: 5px;">
                                    <div class="progress-bar" id="passwordStrength" style="width: 0%"></div>
                                </div>
              <small class="form-text text-muted" id="passwordHelp">Độ mạnh mật khẩu</small>
                            </div>
                            
                            <div class="col-md-6">
                     <label class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                                <div class="form-icon-group">
                                    <i class="bi bi-shield-check form-icon"></i>
                                    <input type="password" 
                                           class="form-control" 
                            name="confirmPassword" 
                                           id="confirmPassword"
                                           placeholder="Nhập lại mật khẩu" 
                                      required>
                                </div>
                                <small class="form-text text-danger d-none" id="passwordMismatch">
                                    Mật khẩu không khớp!
                                </small>
                            </div>
                        </div>
                        
                        <div class="form-check my-3">
        <input class="form-check-input" 
                                   type="checkbox" 
                                   id="agreeTerms" 
                 required>
                            <label class="form-check-label small" for="agreeTerms">
                                Tôi đồng ý với <a href="#" class="text-primary">Điều khoản sử dụng</a> 
                      và <a href="#" class="text-primary">Chính sách bảo mật</a>
                            </label>
                        </div>
                        

                        <button type="submit" class="btn btn-primary w-100 mb-3 btn-lg">
                            <i class="bi bi-person-check"></i> Đăng ký
                        </button>
         </form>
                    
                    <div class="text-center">
                        <hr class="my-3">
                        <p class="text-muted small mb-2">Đã có tài khoản?</p>
                        <a href="login" class="btn btn-outline-secondary w-100">
                            <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                        </a>
         </div>
                </div>
            </div>
        </div>

        <div class="col-lg-6 d-none d-lg-block auth-image-col d-flex align-items-center justify-content-center p-5">
            <img src="${pageContext.request.contextPath}/uploads/products/logo_app.png" 
                 alt="AloTra Logo" 
                 class="img-fluid"
                 style="max-width: 100%; max-height: 100%; object-fit: contain;"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/placeholder.png'; console.error('Logo không tải được:', this.src);">
        </div>

    </div>
</div>

<script>
// Toggle password visibility
document.getElementById('togglePassword')?.addEventListener('click', function() {
    const password = document.getElementById('password');
const icon = this.querySelector('i');
    if (password.type === 'password') {
        password.type = 'text';
        icon.classList.remove('bi-eye');
icon.classList.add('bi-eye-slash');
    } else {
        password.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
});
// Password strength indicator
document.getElementById('password')?.addEventListener('input', function() {
    const password = this.value;
    const strengthBar = document.getElementById('passwordStrength');
    const helpText = document.getElementById('passwordHelp');
    let strength = 0;
    let strengthText = '';
    let strengthClass = '';
    
    if (password.length >= 6) strength += 20;
    if (password.length >= 10) strength += 20;
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/[0-9]/.test(password)) strength += 10;
    if 
(/[^A-Za-z0-9]/.test(password)) strength += 10;
    
    if (strength < 40) {
        strengthText = 'Yếu';
        strengthClass = 'bg-danger';
    } else if (strength < 70) {
        strengthText = 'Trung bình';
        strengthClass = 'bg-warning';
    } else {
        strengthText = 'Mạnh';
        strengthClass = 'bg-success';
    }
    

    strengthBar.style.width = strength + '%';
    strengthBar.className = 'progress-bar ' + strengthClass;
helpText.textContent = 'Độ mạnh: ' + strengthText;
});

// Password match validation
document.getElementById('confirmPassword')?.addEventListener('input', function() {
    const password = document.getElementById('password').value;
    const confirmPassword = this.value;
    const mismatchMsg = document.getElementById('passwordMismatch');
    
    if (confirmPassword && password !== confirmPassword) {
        this.classList.add('is-invalid');
        mismatchMsg.classList.remove('d-none');
    } else {
        this.classList.remove('is-invalid');
        mismatchMsg.classList.add('d-none');
    }
});
// Form validation
document.getElementById('registerForm')?.addEventListener('submit', function(e) {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (password !== confirmPassword) {
        e.preventDefault();
        alert('Mật khẩu xác nhận không khớp!');
        return false;
    }
    
    if (password.length < 6) {
        e.preventDefault();
        alert('Mật khẩu phải có ít nhất 6 ký tự!');
        return false;
    }
    
    return true;
});
</script>