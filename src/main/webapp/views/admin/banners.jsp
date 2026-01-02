<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container-fluid">
    <h1 class="h3 mb-2 text-gray-800">Quản lý Banner & Logo</h1>

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

    <%-- Cài đặt Logo --%>
    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-image"></i> Cài đặt Logo Website
            </h6>
        </div>
        <div class="card-body">
            <form method="POST" action="${pageContext.request.contextPath}/admin/banners">
                <input type="hidden" name="action" value="updateLogo">
                <div class="row">
                    <div class="col-md-8">
                        <div class="mb-3">
                            <label for="logoUrl" class="form-label">Logo URL</label>
                            <input type="text" class="form-control" id="logoUrl" name="LOGO_URL" 
                                   value="${siteSettings.LOGO_URL}" 
                                   placeholder="https://.../logo.png hoặc /uploads/logo.png">
                            <div class="form-text">Dán link ảnh logo. Sẽ hiển thị ở góc trên bên trái (navbar).</div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Xem trước Logo</label>
                        <div class="border rounded p-3 bg-light text-center">
                            <c:choose>
                                <c:when test="${not empty siteSettings.LOGO_URL}">
                                    <img src="${siteSettings.LOGO_URL}" 
                                         alt="Logo" 
                                         id="logoPreview"
                                         style="max-height: 80px; max-width: 100%; object-fit: contain;">
                                </c:when>
                                <c:otherwise>
                                    <div class="text-muted py-3" id="logoPreview">Chưa có logo</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save fa-sm"></i> Lưu Logo
                </button>
            </form>
        </div>
    </div>

    <script>
    // Preview logo khi nhập URL
    document.getElementById('logoUrl')?.addEventListener('input', function(e) {
        const url = this.value.trim();
        const preview = document.getElementById('logoPreview');
        if (url && preview) {
            if (url.startsWith('http') || url.startsWith('/')) {
                preview.innerHTML = `<img src="${url}" alt="Logo" style="max-height: 80px; max-width: 100%; object-fit: contain;">`;
            }
        } else {
            preview.innerHTML = '<div class="text-muted py-3">Chưa có logo</div>';
        }
    });
    </script>

    <hr class="my-4">

    <%-- Quản lý Banner --%>
    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-images"></i> Thêm Banner Mới
            </h6>
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
            </form>
        </div>
    </div>

    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list"></i> Danh sách Banner
            </h6>
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