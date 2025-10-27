<%@ taglib prefix="c" uri="jakarta.tags.core" %>
>
<h1 class="h5 mb-3">Quản lý sản phẩm</h1>
<a class="btn btn-primary btn-sm mb-3" href="${pageContext.request.contextPath}/admin/products/create">Thêm sản phẩm</a>
<table class="table table-sm align-middle">
  <thead><tr><th>ID</th><th>Tên</th><th>Giá</th><th>Kho</th><th>Hiển thị</th><th>Hot</th><th></th></tr></thead>
  <tbody>
  <c:forEach var="p" items="${list}">
    <tr>
      <td>${p.product_id}</td>
      <td>${p.product_name}</td>
      <td>${p.price}</td>
      <td>${p.stock}</td>
      <td><span class="badge bg-${p.isActive? 'success' : 'secondary'}">${p.isActive? 'ON':'OFF'}</span></td>
      <td><span class="badge bg-${p.isFeatured? 'warning text-dark' : 'secondary'}">${p.isFeatured? 'YES':'NO'}</span></td>
      <td>
        <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/products/edit?id=${p.product_id}">Sửa</a>
        <a class="btn btn-sm btn-outline-danger" href="${pageContext.request.contextPath}/admin/products/delete?id=${p.product_id}" onclick="return confirm('Xóa?')">Xóa</a>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
