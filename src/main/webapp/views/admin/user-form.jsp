<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-user-edit text-primary" style="margin-right: 10px;"></i>${empty user ? 'Tạo mới' : 'Chỉnh sửa'} người dùng
        </h1>
        <p class="text-muted mb-0">${empty user ? 'Thêm tài khoản người dùng mới vào hệ thống' : 'Chỉnh sửa thông tin tài khoản người dùng'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left" style="margin-right: 10px;"></i>Quay lại
    </a>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>

<div class="row">
    <div class="col-lg-8">
        <%-- Avatar Section --%>
        <c:if test="${not empty user}">
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-image" style="margin-right: 10px;"></i>Ảnh đại diện
                    </h6>
                </div>
                <div class="card-body text-center p-4">
                    <c:set var="avatarSrc" value="${user.avatar}"/>
                    <c:if test="${not empty avatarSrc and not fn:startsWith(avatarSrc, 'http')}">
                        <c:set var="avatarSrc" value="${pageContext.request.contextPath}/${user.avatar}"/>
                    </c:if>
                    <c:if test="${empty avatarSrc}">
                        <c:set var="avatarSrc" value="https://via.placeholder.com/150?text=No+Avatar"/>
                    </c:if>
                    <img src="${avatarSrc}" 
                         class="rounded-circle shadow-sm mb-3" 
                         style="width: 150px; height: 150px; object-fit: cover;"
                         alt="Avatar"
                         onerror="this.src='https://via.placeholder.com/150?text=No+Avatar'"/>
                    <div>
                        <c:if test="${not empty user.avatar}">
                            <span class="badge bg-success text-white">
                                <i class="fas fa-check-circle" style="margin-right: 5px;"></i>Đã có ảnh đại diện
                            </span>
                        </c:if>
                        <c:if test="${empty user.avatar}">
                            <span class="badge bg-secondary text-white">
                                <i class="fas fa-times-circle" style="margin-right: 5px;"></i>Chưa có ảnh đại diện
                            </span>
                        </c:if>
                    </div>
                </div>
            </div>
        </c:if>
        
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header bg-white border-bottom py-3">
            <h6 class="m-0 font-weight-bold text-primary">
              <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin tài khoản
            </h6>
          </div>
            <div class="card-body">
  
              <c:set var="isAdmin" value="${not empty user and user.roleid != null and user.roleid == 1}"/>
              <c:if test="${isAdmin}">
                  <div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
                      <i class="fas fa-exclamation-triangle" style="margin-right: 10px;"></i>
                      <strong>Cảnh báo!</strong> Không thể chỉnh sửa thông tin quản trị viên. 
                      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                  </div>
              </c:if>
              
              <form method="post" action="${pageContext.request.contextPath}/admin/users/save" ${isAdmin ? 'onsubmit="return false;"' : ''}>
                    <input type="hidden" name="id" value="${user.id}">
                    
                    <div class="row g-3" ${isAdmin ? 'style="opacity: 0.6; pointer-events: none;"' : ''}>
                   
     <div class="col-md-6">
                            <label class="form-label fw-semibold mb-2">
                              <i class="fas fa-user text-primary" style="margin-right: 10px;"></i>Username <span class="text-danger">*</span>
                            </label>
                            <input type="text" 
                                   class="form-control" 
                                   name="username" 
                                   value="${user.username}" 
                                   ${not empty user ? 'readonly' : 'required'}
                                   style="font-size: 1rem; padding: 0.75rem;"/>
                            <c:if test="${not empty user}">
                                <div class="form-text mt-2">
                                  <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Username không thể thay đổi
                                </div>
                            </c:if>
                      </div>
                        
                        <div class="col-md-6">
                            <label class="form-label fw-semibold mb-2">
                              <i class="fas fa-envelope text-primary" style="margin-right: 10px;"></i>Email <span class="text-danger">*</span>
                            </label>
                            <input type="email" 
                                   class="form-control" 
                                   name="email" 
                                   value="${user.email}" 
                                   required
                                   style="font-size: 1rem; padding: 0.75rem;"/>
                        </div>
      
                  
                        <div class="col-md-6">
                            <label class="form-label fw-semibold mb-2">
                              <i class="fas fa-id-card text-primary" style="margin-right: 10px;"></i>Họ tên
                            </label>
                            <input type="text" 
                                   class="form-control" 
                                   name="fullname" 
                                   value="${user.fullname}"
                                   placeholder="Nhập họ và tên"
                                   style="font-size: 1rem; padding: 0.75rem;"/>
                        </div>
                        
            <div class="col-md-6">
                            <label class="form-label fw-semibold mb-2">
                              <i class="fas fa-phone text-success" style="margin-right: 10px;"></i>Số điện thoại <span class="text-danger">*</span>
                            </label>
                            <input type="tel" 
                                   class="form-control" 
                                   name="phone" 
                                   value="${user.phone}" 
                                   pattern="[0-9]{9,11}" 
                                   required
                                   placeholder="Nhập số điện thoại"
                                   style="font-size: 1rem; padding: 0.75rem;"/>
                        </div>
                        
                        <div class="col-12">
                            <label class="form-label fw-semibold mb-2">
                              <i class="fas fa-map-marker-alt text-warning" style="margin-right: 10px;"></i>Địa chỉ
                            </label>
                            <textarea class="form-control" 
                                      name="address" 
                                      rows="3"
                                      placeholder="Nhập địa chỉ"
                                      style="font-size: 1rem; padding: 0.75rem;">${user.address}</textarea>
                        </div>
                        
      
                  <div class="col-md-6">
                            <label class="form-label fw-semibold mb-2">
                              <i class="fas fa-lock text-danger" style="margin-right: 10px;"></i>Mật khẩu <c:if test="${empty user}"><span class="text-danger">*</span></c:if>
                            </label>
                            <input type="password" 
                                   class="form-control" 
                                   name="password" 
                                   minlength="6" 
                                   ${empty user ? 'required' : ''}
                                   placeholder="${empty user ? 'Nhập mật khẩu' : 'Để trống nếu không đổi'}"
                                   style="font-size: 1rem; padding: 0.75rem;"/>
                            <c:if test="${not empty user}">
                                <div class="form-text mt-2">
                                  <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Để trống nếu không muốn đổi mật khẩu
                                </div>
                            </c:if>
                        </div>
                        
                        <%-- Role: Chỉ cho phép sửa nếu user có role là người dùng (CUSTOMER = 3) --%>
                        <c:if test="${not empty user}">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold mb-2" style="font-size: 1.1rem;">
                                    <i class="fas fa-user-tag text-primary" style="margin-right: 10px;"></i>Vai trò
                                </label>
                                <c:choose>
                                    <c:when test="${user.roleid == 1}">
                                        <%-- Admin: Không thể sửa --%>
                                        <div class="form-control form-control-lg" style="font-size: 1.1rem; padding: 0.75rem 1rem; height: auto; background-color: #e9ecef; cursor: not-allowed;">
                                            Quản trị viên
                                            <small class="text-muted d-block mt-1">
                                                <i class="fas fa-info-circle"></i> Vai trò không thể thay đổi
                                            </small>
                                        </div>
                                    </c:when>
                                    <c:when test="${user.roleid == 2}">
                                        <%-- Staff: Có thể đổi lên Admin hoặc hạ xuống Khách hàng --%>
                                        <select class="form-select form-select-lg" name="roleId" style="font-size: 1.1rem;">
                                            <option value="3" ${user.roleid == 3 ? 'selected' : ''}>Khách hàng</option>
                                            <option value="2" ${user.roleid == 2 ? 'selected' : ''}>Nhân viên</option>
                                            <option value="1" ${user.roleid == 1 ? 'selected' : ''}>Quản trị viên</option>
                                        </select>
                                        <small class="text-muted d-block mt-1">
                                            <i class="fas fa-info-circle"></i> Có thể nâng cấp lên Quản trị viên hoặc hạ xuống Khách hàng
                                        </small>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- Customer: Có thể đổi lên Staff hoặc Admin --%>
                                        <select class="form-select form-select-lg" name="roleId" style="font-size: 1.1rem;">
                                            <option value="3" ${user.roleid == 3 ? 'selected' : ''}>Khách hàng</option>
                                            <option value="2" ${user.roleid == 2 ? 'selected' : ''}>Nhân viên</option>
                                            <option value="1" ${user.roleid == 1 ? 'selected' : ''}>Quản trị viên</option>
                                        </select>
                                        <small class="text-muted d-block mt-1">
                                            <i class="fas fa-info-circle"></i> Có thể nâng cấp lên Nhân viên hoặc Quản trị viên
                                        </small>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                        
                  
      <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="isActive" 
                                       id="isActive" 
                                       ${user.isActive || empty user ? 'checked' : ''}>
                                <label class="form-check-label fw-semibold" for="isActive">
                                    Kích hoạt tài khoản
                                </label>
                                <div class="form-text mt-1">
                                  <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Bỏ chọn để vô hiệu hóa tài khoản này
                                </div>
                            </div>
                        </div>
                        
                 
       <div class="col-12">
                            <hr class="my-4">
                            <div class="d-flex gap-3">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu thông tin
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/users" 
                                   class="btn btn-outline-secondary">
                                    <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy bỏ
                                </a>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <c:if test="${not empty user}">
        <div class="col-lg-4">
            <%-- User Information Card --%>
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin chi tiết
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-4">
                        <small class="text-muted d-block mb-2">
                            <i class="fas fa-hashtag" style="margin-right: 10px;"></i>ID người dùng
                        </small>
                        <div class="fw-bold fs-5 text-primary">#${user.id}</div>
                    </div>
                    
                    <div class="mb-4">
                        <small class="text-muted d-block mb-2">
                            <i class="fas fa-calendar-alt" style="margin-right: 10px;"></i>Ngày tạo tài khoản
                        </small>
                        <div class="fw-semibold">
                            <c:choose>
                                <c:when test="${not empty user.createdDateAsDate}">
                                    <fmt:formatDate value="${user.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <div class="mb-4">
                        <small class="text-muted d-block mb-2">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Trạng thái
                        </small>
                        <div>
                            <span class="badge ${user.isActive ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                <i class="fas ${user.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                ${user.isActive ? 'Kích hoạt' : 'Vô hiệu hóa'}
                            </span>
                        </div>
                    </div>
                    
                    <c:if test="${not empty user.code}">
                        <div class="mb-4">
                            <small class="text-muted d-block mb-2">
                                <i class="fas fa-key" style="margin-right: 10px;"></i>Mã người dùng
                            </small>
                            <div class="fw-semibold">${user.code}</div>
                        </div>
                    </c:if>
                    
                    <c:if test="${user.loyalty_points != null && user.loyalty_points > 0}">
                        <div class="mb-3">
                            <small class="text-muted d-block mb-2">
                                <i class="fas fa-star text-warning" style="margin-right: 10px;"></i>Điểm tích lũy
                            </small>
                            <div class="fw-bold fs-4 text-warning">
                                <i class="fas fa-coins" style="margin-right: 5px;"></i>
                                <fmt:formatNumber value="${user.loyalty_points}" pattern="#,##0"/> điểm
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </c:if>
</div>