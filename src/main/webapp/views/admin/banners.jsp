<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container-fluid">
    <h1 class="h3 mb-2 text-gray-800">Quản lý Banner</h1>

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
            <h6 class="m-0 font-weight-bold text-primary">Thêm Banner Mới</h6>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/banners" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="action" value="add">
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="bannerFile" class="form-label">Chọn ảnh (Ưu tiên)</label>
                        <input type="file" class="form-control" id="bannerFile" name="bannerFile" accept="image/*">
                        <div class="form-text">Chọn file từ máy tính.</div>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="imageUrl" class="form-label">Hoặc dán URL ảnh</label>
                        <input type="text" class="form-control" id="imageUrl" name="imageUrl" placeholder="https://...">
                        <div class="form-text">Nếu không upload file, hệ thống sẽ lấy URL này.</div>
                    </div>
                </div>
                <div class="row"> 
                    <div class="col-md-2 mb-3">
                        <label for="sortOrder" class="form-label">Thứ tự:</label>
                        <input type="number" class="form-control" id="sortOrder" name="sortOrder" value="0">
                    </div>
                    <div class="col-md-2 mb-3 d-flex align-items-center pt-4">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isActive" name="isActive" value="true" checked>
                            <label class="form-check-label" for="isActive">
                                Hiển thị?
                            </label>
                        </div>
                    </div>
                    <div class="col-md-2 mb-3">
                        <button type="submit" class="btn btn-primary w-100 mt-4">Thêm</button>
                    </div>
                </div>
        </div>
            </form>
        </div>
    </div>

    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">Danh sách Banner</h6>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Ảnh</th>
                            <th>Đường dẫn Ảnh/URL</th>
                            <th>Thứ tự</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${banners}" var="b">
                            <tr>
                             <td>
								    <c:choose>
								        <%-- Nếu là URL (bắt đầu bằng http) --%>
								        <c:when test="${fn:startsWith(b.imageUrl, 'http')}">
								            <img src="${b.imageUrl}" alt="Banner" height="50">
								        </c:when>
								        <%-- Nếu là file upload (thêm /uploads/) --%>
								        <c:otherwise>
								            <img src="${pageContext.request.contextPath}/uploads/${b.imageUrl}" alt="Banner" height="50">
								        </c:otherwise>
								    </c:choose>
								</td>
								<td>${b.imageUrl}</td>
								<td>${b.linkUrl}</td>
								<td>${b.sortOrder}</td>
								<td>${b.isActive() ? 'Hiển thị' : 'Ẩn'}</td>
								<td>
								    <form action="${pageContext.request.contextPath}/admin/banners" method="POST" class="d-inline">
								        <input type="hidden" name="action" value="delete">
								        <input type="hidden" name="id" value="${b.id}">
								        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Bạn chắc chắn muốn xóa?')">Xóa</button>
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