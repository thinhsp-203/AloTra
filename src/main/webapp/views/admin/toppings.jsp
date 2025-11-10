<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<h1 class="h3 mb-2 text-gray-800">Quản lý Topping</h1>
<p class="mb-4">Quản lý các loại topping (trân châu, pudding, v.v.).</p>

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

<div class="card shadow mb-4">
  <div class="card-header py-3 d-flex justify-content-between align-items-center">
    <h6 class="m-0 font-weight-bold text-primary">Danh sách Topping</h6>
    <a href="${pageContext.request.contextPath}/admin/toppings/create" class="btn btn-primary btn-sm">
      <i class="fas fa-plus fa-sm"></i> Thêm Topping
    </a>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>Tên Topping</th>
            <th class="text-end">Giá</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="item" items="${items}">
            <tr>
              <td><strong>${item.topping_name}</strong></td>
              <td class="text-end"><fmt:formatNumber value="${item.price}" pattern="#,##0₫"/></td>
              <td class="text-center">
                <span class="badge text-bg-${item.isAvailable ? 'success' : 'secondary'}">
                  ${item.isAvailable ? 'Đang bán' : 'Ngừng bán'}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <a href="${pageContext.request.contextPath}/admin/toppings/edit?id=${item.topping_id}" class="btn btn-outline-primary" title="Chỉnh sửa">
                    <i class="fas fa-pencil-alt"></i>
                  </a>
                  <form action="${pageContext.request.contextPath}/admin/toppings/delete" method="post" 
                        style="display: inline;" 
                        onsubmit="return confirm('Xác nhận xóa topping \'${item.topping_name}\'?')">
                    <input type="hidden" name="id" value="${item.topping_id}">
                    <button type="submit" class="btn btn-outline-danger" title="Xóa">
                      <i class="fas fa-trash"></i>
                    </button>
                  </form>
                </div>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>