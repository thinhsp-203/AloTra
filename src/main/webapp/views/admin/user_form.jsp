<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<h1 class="h3 mb-4 text-gray-800">${empty user ? 'Tạo mới' : 'Chỉnh sửa'} người dùng</h1>

<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
        ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="row">
    <div class="col-lg-8">
        <div class="card shadow mb-4">
          <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">Thông tin tài khoản</h6>
          </div>
            <div class="card-body">
  
              <form method="post" action="${pageContext.request.contextPath}/admin/users/save">
                    <input type="hidden" name="id" value="${user.id}">
                    
                    <div class="row g-3">
                   
     <div class="col-md-6">
                            <label class="form-label">Username <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="username" 
                               
    value="${user.username}" 
                                   ${not empty user ?
'readonly' : 'required'}>
                            <c:if test="${not empty user}">
                                <small class="text-muted">Username không thể thay đổi</small>
                            </c:if>
  
                      </div>
                        
                        <div class="col-md-6">
                            <label 
class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control" name="email" 
                                   value="${user.email}" required>
                        </div>
      
                  
                        <div class="col-md-6">
                            <label class="form-label">Họ tên</label>
                           
 <input type="text" class="form-control" name="fullname" 
                                   value="${user.fullname}">
                        </div>
                        
            <div class="col-md-6">
                            <label class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel" class="form-control" name="phone" 
           value="${user.phone}" pattern="[0-9]{9,11}" required>
                        </div>
                        
                        <div class="col-12">
               
             <label class="form-label">Địa chỉ</label>
                            <textarea class="form-control" name="address" rows="2">${user.address}</textarea>
                        </div>
                        
      
                  <div class="col-md-6">
                            <label class="form-label">
                                Mật khẩu 
                  
              <c:if test="${empty user}"><span class="text-danger">*</span></c:if>
                            </label>
                            <input type="password" class="form-control" name="password" 
                       
            minlength="6" ${empty user ?
'required' : ''}>
                            <c:if test="${not empty user}">
                                <small class="text-muted">Để trống nếu không muốn đổi mật khẩu</small>
                           
 </c:if>
                        </div>
                        
                        <div class="col-md-6">
                          
  <label class="form-label">Vai trò <span class="text-danger">*</span></label>
                            <select class="form-select" name="roleid" required>
                                <option value="1" ${user.roleid == 1 ?
'selected' : ''}>Quản trị viên</option>
                                <option value="2" ${user.roleid == 2 ?
'selected' : ''}>Nhân viên</option>
                                <option value="3" ${user.roleid == 3 ||
empty user ? 'selected' : ''}>Khách hàng</option>
                            </select>
                        </div>
                        
                  
      <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="isActive" 
                            
           id="isActive" ${user.isActive || empty user ?
'checked' : ''}>
                                <label class="form-check-label" for="isActive">
                                    Kích hoạt tài khoản
                         
       </label>
                            </div>
                        </div>
                        
                 
       <div class="col-12">
                            <hr>
                            <button type="submit" class="btn btn-primary">
                                <i 
class="fas fa-save fa-sm"></i> Lưu thông tin
                            </button>
                            <a href="${pageContext.request.contextPath}/admin/users" 
                               class="btn btn-outline-secondary">Hủy</a>
      
                  </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <c:if test="${not empty user}">
        <div class="col-lg-4">
  
          <div class="card shadow-sm mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Thông tin</h6>
                </div>
                <div class="card-body">
               
     <div class="mb-2">
                        <small class="text-muted">ID:</small>
                        <div>${user.id}</div>
                    </div>
                    <div class="mb-2">
    
                    <small class="text-muted">Ngày tạo:</small>
                        <div>
                            <c:choose>
                          
      <c:when test="${not empty user.createdDateAsDate}">
                                    <fmt:formatDate value="${user.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:when>
                    
            <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                
    <div class="mb-2">
                        <small class="text-muted">Trạng thái:</small>
                        <div>
                            <span class="badge text-bg-${user.isActive ?
'success' : 'secondary'}">
                                ${user.isActive ? 'Kích hoạt' : 'Vô hiệu hóa'}
                            </span>
                        </div>
       
             </div>
                </div>
            </div>
        </div>
    </c:if>
</div>