<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<h1 class="h3 mb-2 text-gray-800">Quản lý sản phẩm</h1>
<p class="mb-4">Danh sách toàn bộ sản phẩm trong cửa hàng.</p>


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

<div class="card shadow mb-4">
  <div class="card-header py-3 d-flex justify-content-between align-items-center">
    <h6 class="m-0 font-weight-bold text-primary">Danh sách sản phẩm</h6>
    <a href="${pageContext.request.contextPath}/admin/products/create" class="btn btn-primary btn-sm">
      <i class="fas fa-plus fa-sm"></i> Thêm sản phẩm
    </a>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle" id="dataTable" width="100%" cellspacing="0">
   
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
                    <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                    <c:if test="${not empty thumbnailSrc and not fn:startsWith(thumbnailSrc, 'http')}">
                       <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/${p.thumbnail}"/>
                    </c:if>
                    <c:if test="${empty thumbnailSrc}">
                       <c:set var="thumbnailSrc" value="https://via.placeholder.com/60"/>
                    </c:if>
                    
                    <img src="${thumbnailSrc}" 
                         class="rounded" 
                         style="width: 60px; height: 60px; object-fit: cover;"
                         alt="${p.product_name}"/>
                  </td>
                  <td>
                    <div class="fw-semibold">${p.product_name}</div>
                    <c:if test="${p.isFeatured}">
                      <span class="badge text-bg-warning">
  
                      <i class="bi bi-star-fill"></i> Nổi bật
                      </span>
                    </c:if>
                  </td>
              
    <td>
                    <c:choose>
                      <c:when test="${not empty p.category}">
                        <span class="badge text-bg-info">${p.category.name}</span>
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
                        <span class="badge text-bg-danger">-${p.discount}%</span>
                      </div>
               
     </c:if>
                  </td>
                  <td class="text-center">
                    <c:choose>
                      <c:when test="${p.stock == null ||
p.stock == 0}">
                        <span class="badge text-bg-danger">Hết hàng</span>
                      </c:when>
                      <c:when test="${p.stock < 10}">
                        
<span class="badge text-bg-warning">${p.stock}</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge text-bg-success">${p.stock}</span>
                      </c:otherwise>
     
               </c:choose>
                  </td>
                  <td class="text-center">
                    <c:choose>
                      <c:when test="${p.isActive}">
     
                   <span class="badge text-bg-success">
                          <i class="bi bi-eye"></i> Hiển thị
                        </span>
                      </c:when>
   
                   <c:otherwise>
                        <span class="badge text-bg-secondary">
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
                        <i class="fas fa-eye"></i>
                      </a>
                      <a href="${pageContext.request.contextPath}/admin/products/edit?id=${p.product_id}" 
                         
class="btn btn-outline-primary" 
                         title="Chỉnh sửa">
                        <i class="fas fa-pencil-alt"></i>
                      </a>
                      
                      <form action="${pageContext.request.contextPath}/admin/products/delete" method="post" 
                            style="display: inline;" 
                            onsubmit="return confirm('Xác nhận xóa sản phẩm &quot;${p.product_name}&quot;?')">
                        <input type="hidden" name="id" value="${p.product_id}">
                        <button type="submit" class="btn btn-outline-danger" title="Xóa">
                          <i class="fas fa-trash"></i>
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
</div>