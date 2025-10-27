<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>

<style>
  .user-filter-form {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 16px;
    align-items: center;
  }
  .user-filter-form input[type="text"],
  .user-filter-form select {
    padding: 6px 10px;
    border: 1px solid #ced4da;
    border-radius: 4px;
    min-width: 180px;
  }
  .user-filter-form button {
    padding: 7px 16px;
    border: none;
    border-radius: 4px;
    background-color: #0d6efd;
    color: #fff;
    cursor: pointer;
  }
  .user-filter-form .btn-reset {
    background-color: #6c757d;
    color: #fff;
    text-decoration: none;
    display: inline-block;
    padding: 7px 16px;
    border-radius: 4px;
  }
  table.user-table {
    border-collapse: collapse;
    width: 100%;
  }
  table.user-table th, table.user-table td {
    border: 1px solid #dee2e6;
    padding: 8px 10px;
    text-align: left;
  }
  table.user-table th {
    background-color: #f8f9fa;
  }
  .pagination {
    display: flex;
    list-style: none;
    gap: 6px;
    padding: 0;
  }
  .pagination li a {
    display: block;
    padding: 6px 12px;
    border: 1px solid #dee2e6;
    border-radius: 4px;
    text-decoration: none;
    color: #0d6efd;
  }
  .pagination li.active a {
    background-color: #0d6efd;
    color: #fff;
  }
  .pagination li.disabled a {
    color: #adb5bd;
    pointer-events: none;
  }
  .alert {
    padding: 12px 16px;
    border-radius: 4px;
    border: 1px solid #b6d4fe;
    background-color: #e7f1ff;
    color: #084298;
  }
  .alert.alert-info {
    border-color: #b6effb;
    background-color: #e0f5ff;
    color: #055160;
  }
</style>

<h2>Quản lý người dùng</h2>

<form method="get" action="${pageContext.request.contextPath}/admin/users" class="user-filter-form">
  <input type="text" name="keyword" placeholder="Tìm tên, email, SĐT" value="${fn:escapeXml(keyword)}"/>
  <select name="roleId">
    <option value="">Tất cả vai trò</option>
    <c:forEach var="entry" items="${roles}">
      <option value="${entry.key}" <c:if test="${selectedRoleId != null && selectedRoleId eq entry.key}">selected</c:if>>${entry.value}</option>
    </c:forEach>
  </select>
  <select name="size">
    <c:forEach var="option" items="${pageSizes}">
      <option value="${option}" <c:if test="${option == pageSize}">selected</c:if>>${option} / trang</option>
    </c:forEach>
  </select>
  <button type="submit">Lọc</button>
  <a class="btn-reset" href="${pageContext.request.contextPath}/admin/users">Bỏ lọc</a>
</form>

<p><strong>${totalUsers}</strong> người dùng được tìm thấy.</p>

<c:choose>
  <c:when test="${empty users}">
    <div class="alert alert-info">Không có dữ liệu phù hợp.</div>
  </c:when>
  <c:otherwise>
    <table class="user-table">
      <thead>
      <tr>
        <th>#</th>
        <th>Tên đăng nhập</th>
        <th>Họ tên</th>
        <th>Email</th>
        <th>Số điện thoại</th>
        <th>Vai trò</th>
        <th>Ngày tạo</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="user" items="${users}" varStatus="st">
        <tr>
          <td>${fromRecord  st.index}</td>
          <td>${user.userName}</td>
          <td>${empty user.fullName ? '-' : user.fullName}</td>
          <td>${user.email}</td>
          <td>${empty user.phone ? '-' : user.phone}</td>
          <td>${user.roleName}</td>
          <td>
            <c:choose>
              <c:when test="${not empty user.createdDate}">
                <fmt:formatDate value="${user.createdDate}" pattern="dd/MM/yyyy"/>
              </c:when>
              <c:otherwise>-</c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <p>Hiển thị ${fromRecord} - ${toRecord} / ${totalUsers}</p>

    <c:if test="${totalPages > 1}">
      <ul class="pagination">
        <c:set var="prevPage" value="${page > 1 ? page - 1 : 1}"/>
        <c:url var="prevUrl" value="/admin/users">
          <c:param name="page" value="${prevPage}"/>
          <c:param name="size" value="${pageSize}"/>
          <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
          <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
        </c:url>
        <li class="${page <= 1 ? 'disabled' : ''}"><a href="${pageContext.request.contextPath}${prevUrl}">«</a></li>

        <c:forEach begin="1" end="${totalPages}" var="p">
          <c:if test="${p == 1 || p == totalPages || (p >= page - 2 && p <= page  2)}">
            <c:url var="pageUrl" value="/admin/users">
              <c:param name="page" value="${p}"/>
              <c:param name="size" value="${pageSize}"/>
              <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
              <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
            </c:url>
            <li class="${p == page ? 'active' : ''}"><a href="${pageContext.request.contextPath}${pageUrl}">${p}</a></li>
          </c:if>
        </c:forEach>

        <c:set var="nextPage" value="${page < totalPages ? page  1 : totalPages}"/>
        <c:url var="nextUrl" value="/admin/users">
          <c:param name="page" value="${nextPage}"/>
          <c:param name="size" value="${pageSize}"/>
          <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
          <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
        </c:url>
        <li class="${page >= totalPages ? 'disabled' : ''}"><a href="${pageContext.request.contextPath}${nextUrl}">»</a></li>
      </ul>
    </c:if>
  </c:otherwise>
</c:choose>
