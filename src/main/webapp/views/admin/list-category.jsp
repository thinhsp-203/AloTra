<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<h1 class="h3 mb-2 text-gray-800">Quản lý danh mục</h1>

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
    <h6 class="m-0 font-weight-bold text-primary">Danh sách danh mục</h6>
     <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-primary btn-sm">
      <i class="fas fa-plus fa-sm"></i> Thêm mới
    </a>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th style="width: 60px;">#</th>
            <th style="width: 100px;">Icon</th>
            <th>Tên danh mục</th>
            <th class="text-center" style="width: 150px;">Thao 
tác</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty cateList}">
              <tr>
                <td colspan="4" class="text-center text-muted py-4">
                
  <i class="bi bi-inbox fs-3"></i>
                  <p class="mb-0 mt-2">Chưa có danh mục nào</p>
                </td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach items="${cateList}" var="cate" 
varStatus="st">
                <tr>
                  <td><strong>${st.index + 1}</strong></td>
                  <td>
                    <c:choose>
                      <c:when test="${not empty cate.icon}">
                       <img src="${pageContext.request.contextPath}/uploads/${cate.icon}" 
                             class="rounded" 
                             style="width: 60px;
height: 60px; object-fit: cover;"
                             alt="${cate.name}"/>
                      </c:when>
                      <c:otherwise>
                        
<div class="bg-secondary text-white rounded d-flex align-items-center justify-content-center"
                             style="width: 60px;
height: 60px;">
                          <i class="bi bi-image fs-4"></i>
                        </div>
                      </c:otherwise>
                    </c:choose>
    
              </td>
                  <td>
                    <strong class="d-block">${cate.name}</strong>
                    <small class="text-muted">ID: ${cate.id}</small>
                  </td>
       
           <td class="text-center">
                    <div class="btn-group btn-group-sm" role="group">
                      <a href="${pageContext.request.contextPath}/admin/category/edit?id=${cate.id}" 
                         class="btn btn-outline-primary" 
              
           title="Chỉnh sửa">
                        <i class="fas fa-pencil-alt"></i>
                      </a>
                      
                      <form action="${pageContext.request.contextPath}/admin/category/delete" method="post" 
                            style="display: inline;" 
                            onsubmit="return confirm('Xác nhận xóa danh mục &quot;${cate.name}&quot;?')">
                        <input type="hidden" name="id" value="${cate.id}">
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
  
  <c:if test="${not empty cateList}">
    <div class="card-footer small text-muted">
        Tổng cộng: <strong>${fn:length(cateList)}</strong> danh mục
    </div>
  </c:if>
</div>