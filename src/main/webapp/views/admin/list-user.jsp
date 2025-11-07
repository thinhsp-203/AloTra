<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h2>Quản lý người dùng</h2>
  <a href="${pageContext.request.contextPath}/admin/users/create" class="btn btn-primary">
    <i class="bi bi-plus-circle"></i> Tạo người dùng mới
  </a>
</div>

<c:if test="${not empty sessionScope.success}">
  <div class="alert alert-success alert-dismissible fade show">
    ${sessionScope.success}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="success" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.error}">
  <div class="alert alert-danger alert-dismissible fade show">
    ${sessionScope.error}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="error" scope="session"/>
</c:if>

<div class="card">
  
<div class="card-body">
    <form method="get" action="${pageContext.request.contextPath}/admin/users" class="row g-3 mb-4">
      <div class="col-md-5">
        <input type="text" class="form-control" name="keyword" placeholder="Tìm tên, email, SĐT" value="${fn:escapeXml(keyword)}"/>
      </div>
      <div class="col-md-3">
        <select class="form-select" name="roleId">
          <option value="">Tất cả vai trò</option>
          <c:forEach var="entry" items="${roles}">
            <option value="${entry.key}" <c:if test="${selectedRoleId != null 
&& selectedRoleId eq entry.key}">selected</c:if>>${entry.value}</option>
          </c:forEach>
        </select>
      </div>
      <div class="col-md-2">
        <select class="form-select" name="size">
          <c:forEach var="option" items="${pageSizes}">
            <option value="${option}" <c:if test="${option == pageSize}">selected</c:if>>${option} / trang</option>
          </c:forEach>
        </select>
      </div>
 
     <div class="col-md-2 d-flex">
        <button type="submit" class="btn btn-primary flex-grow-1 me-2">Lọc</button>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users" title="Bỏ lọc"><i class="bi bi-arrow-repeat"></i></a>
      </div>
    </form>

    <p class="text-muted small">Hiển thị ${fromRecord} - ${toRecord} trên tổng số ${totalUsers} người dùng.</p>

    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
        <tr>
        
  <th>#</th>
          <th>Tên đăng nhập</th>
          <th>Họ tên</th>
          <th>Email</th>
          <th>Vai trò</th>
          <th class="text-center">Trạng thái</th>
          <th class="text-center">Thao tác</th>
        </tr>
        </thead>
        <tbody>
      
  <c:choose>
          <c:when test="${empty users}">
            <tr>
              <td colspan="7" class="text-center text-muted py-4">Không có dữ liệu phù hợp.</td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="user" items="${users}" varStatus="st">
    
          <tr>
                <td>${fromRecord + st.index}</td>
                <td><strong>${user.username}</strong></td>
                <td>${empty user.fullname ?
'-' : user.fullname}</td>
                <td>${user.email}</td>
                <td>${user.roleName}</td>
                <td class="text-center">
                  <span class="badge bg-${user.isActive ? 'success' : 'secondary'}">
                      ${user.isActive ?
'Kích hoạt' : 'Vô hiệu hóa'}
                  </span>
                </td>
                <td class="text-center">
                  <div class="btn-group btn-group-sm">
                    <a href="${pageContext.request.contextPath}/admin/users/edit?id=${user.id}" class="btn btn-outline-primary" 
title="Chỉnh sửa">
                      <i class="bi bi-pencil-square"></i>
                    </a>
                    
                    <form action="${pageContext.request.contextPath}/admin/users/toggle-status" method="post" style="display: inline;">
                      <input type="hidden" name="id" value="${user.id}">
                      <button type="submit" class="btn btn-outline-secondary" title="Đổi trạng thái">
                        <i class="bi bi-toggles"></i>
                      </button>
                    </form>
                    
                    <c:if test="${sessionScope.currentUser.id != user.id}">
                      <form action="${pageContext.request.contextPath}/admin/users/delete" method="post" 
                            style="display: inline;" 
                            onsubmit="return confirm('Bạn có chắc chắn muốn xóa người dùng \'${fn:escapeXml(user.username)}\' không?')">
                        <input type="hidden" name="id" value="${user.id}">
                        <button type="submit" class="btn btn-outline-danger" title="Xóa">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                    </c:if>
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

<c:if test="${totalPages > 1}">
  <nav class="mt-4">
    <ul class="pagination justify-content-center">
      <c:set var="prevPage" value="${page > 1 ?
page - 1 : 1}"/>
      <c:url var="prevUrl" value="/admin/users">
        <c:param name="page" value="${prevPage}"/>
        <c:param name="size" value="${pageSize}"/>
        <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
        <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
      </c:url>
      <li class="page-item ${page <= 1 ?
'disabled' : ''}"><a class="page-link" href="${prevUrl}">«</a></li>

      <c:forEach begin="1" end="${totalPages}" var="p">
        <c:if test="${p == 1 ||
p == totalPages || (p >= page - 2 && p <= page + 2)}">
          <c:url var="pageUrl" value="/admin/users">
            <c:param name="page" value="${p}"/>
            <c:param name="size" value="${pageSize}"/>
            <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
            <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
          </c:url>
  
        <li class="page-item ${p == page ?
'active' : ''}"><a class="page-link" href="${pageUrl}">${p}</a></li>
        </c:if>
      </c:forEach>

      <c:set var="nextPage" value="${page < totalPages ?
page + 1 : totalPages}"/>
      <c:url var="nextUrl" value="/admin/users">
        <c:param name="page" value="${nextPage}"/>
        <c:param name="size" value="${pageSize}"/>
        <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
        <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
      </c:url>
      <li class="page-item ${page >= totalPages ?
'disabled' : ''}"><a class="page-link" href="${nextUrl}">»</a></li>
    </ul>
  </nav>
</c:if>