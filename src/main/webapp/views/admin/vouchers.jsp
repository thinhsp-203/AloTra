<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<h1 class="h3 mb-2 text-gray-800">Quản lý Voucher</h1>
<p class="mb-4">Tạo và quản lý các mã giảm giá cho cửa hàng.</p>

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
    <h6 class="m-0 font-weight-bold text-primary">Danh sách mã giảm giá</h6>
    <a href="${pageContext.request.contextPath}/admin/vouchers/create" class="btn btn-primary btn-sm">
      <i class="fas fa-plus fa-sm"></i> Tạo voucher mới
    </a>
  </div>
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th>Mã (Code)</th>
            <th>Loại</th>
            <th>Giá trị</th>
            <th>Đơn tối thiểu</th>
            <th>Giảm tối đa</th>
            <th>Hiệu lực</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="v" items="${vouchers}">
            <tr>
              <td><strong class="text-primary">${v.code}</strong></td>
              <td>
                <c:if test="${v.discount_type eq 'PERCENT'}">Giảm theo %</c:if>
                <c:if test="${v.discount_type eq 'AMOUNT'}">Giảm số tiền</c:if>
              </td>
              <td>
                <c:if test="${v.discount_type eq 'PERCENT'}">${v.discount_value}%</c:if>
                <c:if test="${v.discount_type eq 'AMOUNT'}">
                  <fmt:formatNumber value="${v.discount_value}" pattern="#,##0₫"/>
                </c:if>
              </td>
              <td>
                <c:if test="${empty v.min_order_value}">-</c:if>
                <c:if test="${not empty v.min_order_value}">
                  <fmt:formatNumber value="${v.min_order_value}" pattern="#,##0₫"/>
                </c:if>
              </td>
              <td>
                 <c:if test="${empty v.max_discount}">-</c:if>
                 <c:if test="${not empty v.max_discount}">
                   <fmt:formatNumber value="${v.max_discount}" pattern="#,##0₫"/>
                 </c:if>
              </td>
              <td>
                <fmt:formatDate value="${v.start_dateAsDate}" pattern="dd/MM HH:mm"/>
                <i class="fas fa-arrow-right fa-sm"></i>
                <fmt:formatDate value="${v.end_dateAsDate}" pattern="dd/MM HH:mm"/>
              </td>
              <td class="text-center">
                <span class="badge text-bg-${v.isActive ? 'success' : 'secondary'}">
                  ${v.isActive ? 'Kích hoạt' : 'Vô hiệu hóa'}
                </span>
              </td>
              <td class="text-center">
                <div class="btn-group btn-group-sm">
                  <a href="${pageContext.request.contextPath}/admin/vouchers/edit?id=${v.voucher_id}" class="btn btn-outline-primary" title="Chỉnh sửa">
                    <i class="fas fa-pencil-alt"></i>
                  </a>
                  <form action="${pageContext.request.contextPath}/admin/vouchers/delete" method="post" 
                        style="display: inline;" 
                        onsubmit="return confirm('Xác nhận xóa voucher \'${v.code}\'?')">
                    <input type="hidden" name="id" value="${v.voucher_id}">
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