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
    background-image: url('https://images.unsplash.com/photo-1572097613329-3c870233b8c3?q=80&w=1887&auto=format&fit=crop');
    background-size: cover;
    background-position: center;
    min-height: 300px;
  }
  
  @media (min-width: 992px) {
    .auth-card {
      border-radius: 0 0.5rem 0.5rem 0;
    }
    .auth-image-col {
      border-radius: 0.5rem 0 0 0.5rem;
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
        
        <div class="col-lg-6 d-none d-lg-block auth-image-col">
            </div>

        <div class="col-lg-6 col-md-9">
            <div class="card shadow-lg auth-card">
                <div class="card-body p-4 p-md-5">
                    <div class="text-center mb-4">
                        <img src="https://www.phuclong.com.vn/images/logo-phuclong.png" alt="AloTra Logo" style="height: 50px;" class="mb-3">
                        <h2 class="h4 mt-2 mb-0">Đăng nhập</h2>
                        <p class="text-muted small">Chào mừng bạn quay trở lại!</p>
                    </div>
            
        
                    <c:if test="${not empty sessionScope.success}">
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="bi bi-check-circle"></i> ${sessionScope.success}
                   <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="success" scope="session"/>
                    </c:if>
        
            
                    <c:if test="${not empty alert}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i> ${alert}
                       <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="login" method="post">
                                        <div class="mb-3">
                            <label class="form-label">Tên đăng nhập hoặc Email</label>
                            <div class="form-icon-group">
                                <i class="bi bi-person form-icon"></i>
                                <input type="text" 
                                       class="form-control" 
                         name="username" 
                                       placeholder="Nhập tên đăng nhập hoặc email" 
        value="${username}"
                                       required 
                                       autofocus>
               </div>
                        </div>
                        
                                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <div class="form-icon-group">
                                <i class="bi bi-lock form-icon"></i>
              <input type="password" 
                                       class="form-control" 
                                       name="password" 
                                   id="password"
                                       placeholder="Nhập mật khẩu" 
                required>
                                <button class="btn-toggle-pass" 
                                        type="button" 
                                id="togglePassword">
                                    <i class="bi bi-eye"></i>
  </button>
                            </div>
                        </div>
                        
                        <div class="d-flex justify-content-between align-items-center mb-3">
  <div class="form-check">
                                <input class="form-check-input" 
                                       type="checkbox" 
     name="rememberMe" 
                                       id="rememberMe">
                                <label class="form-check-label small" for="rememberMe">
    Ghi nhớ đăng nhập
                                </label>
                            </div>
                            <a href="${pageContext.request.contextPath}/auth/forgot" 
                               class="d-block small text-decoration-none">
             <i class="bi bi-question-circle"></i> Quên mật khẩu?
</a>
                        </div>
                 
       <button type="submit" class="btn btn-primary w-100 mb-3 btn-lg">
                            <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                        </button>
                    </form>
            
        
                    <div class="text-center">
                        <hr class="my-3">
                        <p class="text-muted small mb-2">Chưa có tài khoản?</p>
                        <a href="register" class="btn btn-outline-success w-100">
           <i class="bi bi-person-plus"></i> Đăng ký ngay
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
</script>