<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h1 class="h4 mb-0">
    <i class="bi bi-box-seam text-primary"></i> Quản lý sản phẩm
  </h1>
  <a href="${pageContext.request.contextPath}/admin/products/create" class="btn btn-primary">
    <i class="bi bi-plus-circle"></i> Thêm sản phẩm
  </a>
</div>

<c:if test="${not empty sessionScope.success}">
  <div class="alert alert-success alert-dismissible fade show">
    <i class="bi bi-check-circle"></i> ${sessionScope.success}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="success" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.error}">
  <div class="alert alert-danger alert-dismissible fade show">
    <i class="bi bi-exclamation-triangle"></i> ${sessionScope.error}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="error" scope="session"/>
</c:if>

<div class="card">
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle">
   
     <thead class="table-light">
          <tr>
            <th style="width: 60px;">ID</th>
            <th style="width: 80px;">Ảnh</th>
            <th>Tên sản phẩm</th>
            <th>Danh mục</th>
            <th class="text-end">Giá</th>
            <th class="text-center">Kho</th>
   
         <th class="text-center">Trạng thái</th>
            <th class="text-center" style="width: 150px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty list}">
              <tr>
         
       <td colspan="8" class="text-center text-muted py-4">
                  <i class="bi bi-inbox fs-3"></i>
                  <p class="mb-0 mt-2">Chưa có sản phẩm nào</p>
                </td>
              </tr>
            </c:when>
  
          <c:otherwise>
              <c:forEach var="p" items="${list}">
                <tr>
                  <td><strong>#${p.product_id}</strong></td>
                  <td>
                    <c:choose>
  
                    <c:when test="${not empty p.thumbnail}">
                        <img src="${p.thumbnail}" 
                             class="rounded" 
                     
        style="width: 60px; height: 60px;
object-fit: cover;"
                             alt="${p.product_name}"/>
                      </c:when>
                      <c:otherwise>
                        <div class="bg-secondary 
text-white rounded d-flex align-items-center justify-content-center"
                             style="width: 60px;
height: 60px;">
                          <i class="bi bi-image"></i>
                        </div>
                      </c:otherwise>
                    </c:choose>
     
             </td>
                  <td>
                    <div class="fw-semibold">${p.product_name}</div>
                    <c:if test="${p.isFeatured}">
                      <span class="badge bg-warning text-dark">
  
                      <i class="bi bi-star-fill"></i> Nổi bật
                      </span>
                    </c:if>
                  </td>
              
    <td>
                    <c:choose>
                      <c:when test="${not empty p.category}">
                        <span class="badge bg-info">${p.category.name}</span>
                      </c:when>
   
                   <c:otherwise>
                        <span class="text-muted">-</span>
                      </c:otherwise>
                    </c:choose>
              
    </td>
                  <td class="text-end">
                    <strong class="text-primary">
                      <fmt:formatNumber value="${p.price}" pattern="#,##0₫"/>
                    </strong>
            
        <c:if test="${p.discount != null && p.discount > 0}">
                      <div>
                        <span class="badge bg-danger">-${p.discount}%</span>
                      </div>
               
     </c:if>
                  </td>
                  <td class="text-center">
                    <c:choose>
                      <c:when test="${p.stock == null ||
p.stock == 0}">
                        <span class="badge bg-danger">Hết hàng</span>
                      </c:when>
                      <c:when test="${p.stock < 10}">
                        
<span class="badge bg-warning text-dark">${p.stock}</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge bg-success">${p.stock}</span>
                      </c:otherwise>
     
               </c:choose>
                  </td>
                  <td class="text-center">
                    <c:choose>
                      <c:when test="${p.isActive}">
     
                   <span class="badge bg-success">
                          <i class="bi bi-eye"></i> Hiển thị
                        </span>
                      </c:when>
   
                   <c:otherwise>
                        <span class="badge bg-secondary">
                          <i class="bi bi-eye-slash"></i> Ẩn
                        </span>
  
                    </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-center">
                    <div class="btn-group btn-group-sm" 
role="group">
                      <a href="${pageContext.request.contextPath}/p?id=${p.product_id}" 
                         class="btn btn-outline-info" 
                         title="Xem chi tiết"
                      
   target="_blank">
                        <i class="bi bi-eye"></i>
                      </a>
                      <a href="${pageContext.request.contextPath}/admin/products/edit?id=${p.product_id}" 
                         
class="btn btn-outline-primary" 
                         title="Chỉnh sửa">
                        <i class="bi bi-pencil"></i>
                      </a>
                      
                      <form action="${pageContext.request.contextPath}/admin/products/delete" method="post" 
                            style="display: inline;" 
                            onsubmit="return confirm('Xác nhận xóa sản phẩm &quot;${p.product_name}&quot;?')">
                        <input type="hidden" name="id" value="${p.product_id}">
                        <button type="submit" class="btn btn-outline-danger" title="Xóa">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                      
                    </div>
                  </td>
                </tr>
              </c:forEach>
 
           </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
  
  <c:if test="${not empty list}">
    <div class="card-footer bg-light">
      <div class="text-muted small">
        <i class="bi bi-info-circle"></i> 
        Tổng cộng: <strong>${list.size()}</strong> sản phẩm
      </div>
    </div>
  </c:if>
</div>

<style>
  .btn-group-sm 
.btn {
    padding: 0.25rem 0.5rem;
  }
  
  /* Đảm bảo form bên trong btn-group không làm hỏng layout */
  .btn-group-sm form {
    margin: 0;
  }
  .btn-group-sm form .btn {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
  }
</style>