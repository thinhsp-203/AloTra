<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- XÓA TẤT CẢ PHẦN STYLE INLINE CŨ --%>
<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-lg">
                <div class="card-body p-4">
                    <h2 class="card-title text-center mb-4">Đăng ký tài khoản</h2>
                    
                    <c:if test="${not empty alert}">
                        <p class="alert alert-danger text-center">${alert}</p>
                    </c:if>
                    
                    <c:if test="${not empty success}">
                        <p class="alert alert-success text-center">${success}</p>
                    </c:if>
                    
                    <form action="register" method="post">
                        <div class="mb-3">
                            <label class="form-label">Tên đăng nhập</label>
                            <input type="text" class="form-control" name="username" placeholder="Nhập tên đăng nhập" required autofocus>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" name="email" placeholder="Nhập email của bạn" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Họ tên</label>
                            <input type="text" class="form-control" name="fullname" placeholder="Nhập họ và tên của bạn" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Số điện thoại (Tùy chọn)</label>
                            <input type="tel" class="form-control" name="phone" placeholder="Nhập số điện thoại (tùy chọn)">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <input type="password" class="form-control" name="password" placeholder="Nhập mật khẩu" required minlength="6">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Xác nhận mật khẩu</label>
                            <input type="password" class="form-control" name="confirmPassword" placeholder="Nhập lại mật khẩu" required minlength="6">
                        </div>
                        <button type="submit" class="btn btn-primary w-100 mb-2">Đăng ký</button>
                    </form>
                    
                    <p class="text-center mt-3 text-muted">Đã có tài khoản? <a href="login">Đăng nhập</a></p>
                </div>
            </div>
        </div>
    </div>
</div>