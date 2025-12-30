<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container-fluid">
    <h1 class="h3 mb-2 text-gray-800">Quản lý Cửa hàng</h1>

    <%-- Thông báo (nếu có) --%>
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
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">
                <c:choose>
                    <c:when test="${not empty store}">Chỉnh sửa Cửa hàng</c:when>
                    <c:otherwise>Thêm Cửa hàng Mới</c:otherwise>
                </c:choose>
            </h6>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/stores" method="POST">
                <c:if test="${not empty store}">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${store.store_id}">
                </c:if>
                <c:if test="${empty store}">
                    <input type="hidden" name="action" value="add">
                </c:if>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="store_name" class="form-label">Tên cửa hàng <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="store_name" name="store_name" 
                               value="${store.store_name}" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="phone" class="form-label">Số điện thoại</label>
                        <input type="text" class="form-control" id="phone" name="phone" 
                               value="${store.phone}">
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="address" class="form-label">Địa chỉ</label>
                        <input type="text" class="form-control" id="address" name="address" 
                               value="${store.address}">
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="ward" class="form-label">Xã/Phường</label>
                        <input type="text" class="form-control" id="ward" name="ward" 
                               value="${store.ward}">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="province" class="form-label">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="province" name="province" 
                               value="${store.province}" required>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="mapIframe" class="form-label">Mã nhúng bản đồ Google Maps (iframe)</label>
                        <textarea class="form-control" id="mapIframe" name="mapIframe" rows="4" 
                                  placeholder="Dán mã iframe từ Google Maps...">${store.mapIframe}</textarea>
                        <div class="form-text">Lấy mã iframe từ Google Maps: Chọn địa điểm → Chia sẻ → Nhúng bản đồ → Sao chép HTML</div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" 
                               value="${store.email}">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="opening_hours" class="form-label">Giờ mở cửa</label>
                        <input type="text" class="form-control" id="opening_hours" name="opening_hours" 
                               value="${store.opening_hours}" placeholder="VD: 8:00 - 22:00">
                    </div>
                </div>
                
                <div class="row"> 
                    <div class="col-md-2 mb-3 d-flex align-items-center">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                                   ${store.isActive ? 'checked' : ''}>
                            <label class="form-check-label" for="isActive">
                                Kích hoạt?
                            </label>
                        </div>
                    </div>
                    <div class="col-md-2 mb-3">
                        <button type="submit" class="btn btn-primary">
                            <c:choose>
                                <c:when test="${not empty store}">Cập nhật</c:when>
                                <c:otherwise>Thêm</c:otherwise>
                            </c:choose>
                        </button>
                        <c:if test="${not empty store}">
                            <a href="${pageContext.request.contextPath}/admin/stores" class="btn btn-secondary">Hủy</a>
                        </c:if>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">Danh sách Cửa hàng</h6>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên cửa hàng</th>
                            <th>Địa chỉ</th>
                            <th>Số điện thoại</th>
                            <th>Tỉnh/Thành phố</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${stores}" var="s">
                            <tr>
                                <td>${s.store_id}</td>
                                <td>${s.store_name}</td>
                                <td>${s.address}</td>
                                <td>${s.phone}</td>
                                <td>${s.province}</td>
                                <td>${s.isActive ? 'Kích hoạt' : 'Vô hiệu'}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/stores?id=${s.store_id}" 
                                       class="btn btn-sm btn-warning">Sửa</a>
                                    <form action="${pageContext.request.contextPath}/admin/stores" method="POST" class="d-inline">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${s.store_id}">
                                        <button type="submit" class="btn btn-danger btn-sm" 
                                                onclick="return confirm('Bạn chắc chắn muốn xóa?')">Xóa</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

