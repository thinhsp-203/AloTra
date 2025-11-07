<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <div class="card shadow-lg border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-plus-fill display-4 text-primary"></i>
                        <h2 class="h4 mt-3 mb-0">Đăng ký tài khoản</h2>
                        <p class="text-muted small">Tạo tài khoản mới để bắt đầu mua sắm</p>
                    </div>
                    
                    <c:if test="${not empty alert}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i> ${alert}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    
                    <form action="register" method="post" id="registerForm">
                        <div class="mb-3">
                            <label class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" 
                                   class="form-control" 
                                   name="email" 
                                   value="${email}"
                                   placeholder="example@email.com" 
                                   required 
                                   autofocus>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Tên đăng nhập <span class="text-danger">*</span></label>
                            <input type="text" 
                                   class="form-control" 
                                   name="username" 
                                   value="${username}"
                                   placeholder="4-32 ký tự (chữ/số/._-)" 
                                   pattern="[A-Za-z0-9._-]{4,32}"
                                   required>
                            <small class="form-text text-muted">4-32 ký tự, chỉ chữ/số/._-</small>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Họ và tên <span class="text-danger">*</span></label>
                            <input type="text" 
                                   class="form-control" 
                                   name="fullname" 
                                   value="${fullname}"
                                   placeholder="Nguyễn Văn A" 
                                   required>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Số điện thoại</label>
                            <input type="tel" 
                                   class="form-control" 
                                   name="phone" 
                                   value="${phone}"
                                   placeholder="0901234567 (không bắt buộc)" 
                                   pattern="[0-9]{9,11}">
                            <small class="form-text text-muted">9-11 chữ số</small>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <input type="password" 
                                       class="form-control" 
                                       name="password" 
                                       id="password"
                                       placeholder="Tối thiểu 6 ký tự" 
                                       minlength="6"
                                       required>
                                <button class="btn btn-outline-secondary" 
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
                        
                        <div class="mb-3">
                            <label class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                            <input type="password" 
                                   class="form-control" 
                                   name="confirmPassword" 
                                   id="confirmPassword"
                                   placeholder="Nhập lại mật khẩu" 
                                   required>
                            <small class="form-text text-danger d-none" id="passwordMismatch">
                                Mật khẩu không khớp!
                            </small>
                        </div>
                        
                        <div class="form-check mb-3">
                            <input class="form-check-input" 
                                   type="checkbox" 
                                   id="agreeTerms" 
                                   required>
                            <label class="form-check-label small" for="agreeTerms">
                                Tôi đồng ý với <a href="#" class="text-primary">Điều khoản sử dụng</a> 
                                và <a href="#" class="text-primary">Chính sách bảo mật</a>
                            </label>
                        </div>
                        
                        <button type="submit" class="btn btn-primary w-100 mb-3">
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
    if (/[^A-Za-z0-9]/.test(password)) strength += 10;
    
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