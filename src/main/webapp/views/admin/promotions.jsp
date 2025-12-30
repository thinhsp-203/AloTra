<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container-fluid">
    <h1 class="h3 mb-2 text-gray-800">Quản lý Khuyến mãi</h1>

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
                    <c:when test="${not empty promotion}">Chỉnh sửa Khuyến mãi</c:when>
                    <c:otherwise>Thêm Khuyến mãi Mới</c:otherwise>
                </c:choose>
            </h6>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/promotions" method="POST" enctype="multipart/form-data">
                <c:if test="${not empty promotion}">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${promotion.id}">
                </c:if>
                <c:if test="${empty promotion}">
                    <input type="hidden" name="action" value="add">
                </c:if>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="title" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="title" name="title" 
                               value="${promotion.title}" required>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="description" class="form-label">Mô tả ngắn</label>
                        <textarea class="form-control" id="description" name="description" rows="2">${promotion.description}</textarea>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="content" class="form-label">Nội dung chi tiết</label>
                        <textarea class="form-control" id="content" name="content" rows="5">${promotion.content}</textarea>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="promotionFile" class="form-label">Chọn ảnh (Ưu tiên)</label>
                        <input type="file" class="form-control" id="promotionFile" name="promotionFile" accept="image/*">
                        <div class="form-text">Chọn file từ máy tính.</div>
                        
                        <%-- Preview ảnh khi chọn file --%>
                        <div class="mt-3" id="imagePreviewContainer" style="display: none;">
                            <small class="text-muted d-block mb-2">Xem trước ảnh mới:</small>
                            <img id="imagePreview" src="" alt="Preview" 
                                 class="border rounded" 
                                 style="max-width: 200px; max-height: 200px; object-fit: cover;">
                        </div>
                        
                        <%-- Hiển thị ảnh hiện tại (khi edit) --%>
                        <c:if test="${not empty promotion and not empty promotion.imageUrl}">
                            <div class="mt-3" id="currentImageContainer">
                                <small class="text-muted d-block mb-2">Ảnh hiện tại:</small>
                                <c:choose>
                                    <c:when test="${fn:startsWith(promotion.imageUrl, 'http')}">
                                        <img src="${promotion.imageUrl}" alt="Current" id="currentImage"
                                             class="border rounded" 
                                             style="max-width: 200px; max-height: 200px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/uploads/${promotion.imageUrl}" 
                                             alt="Current" id="currentImage"
                                             class="border rounded" 
                                             style="max-width: 200px; max-height: 200px; object-fit: cover;">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="imageUrl" class="form-label">Hoặc dán URL ảnh</label>
                        <input type="text" class="form-control" id="imageUrl" name="imageUrl" 
                               value="${promotion.imageUrl}" placeholder="https://...">
                        <div class="form-text">Nếu không upload file, hệ thống sẽ lấy URL này.</div>
                    </div>
                </div>
                
                <div class="row"> 
                    <div class="col-md-2 mb-3 d-flex align-items-center">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                                   ${promotion.isActive() ? 'checked' : ''}>
                            <label class="form-check-label" for="isActive">
                                Hiển thị?
                            </label>
                        </div>
                    </div>
                    <div class="col-md-2 mb-3">
                        <button type="submit" class="btn btn-primary">
                            <c:choose>
                                <c:when test="${not empty promotion}">Cập nhật</c:when>
                                <c:otherwise>Thêm</c:otherwise>
                            </c:choose>
                        </button>
                        <c:if test="${not empty promotion}">
                            <a href="${pageContext.request.contextPath}/admin/promotions" class="btn btn-secondary">Hủy</a>
                        </c:if>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">Danh sách Khuyến mãi</h6>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>Ảnh</th>
                            <th>Tiêu đề</th>
                            <th>Mô tả</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${promotions}" var="p">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${fn:startsWith(p.imageUrl, 'http')}">
                                            <img src="${p.imageUrl}" alt="Promotion" height="50">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/uploads/${p.imageUrl}" alt="Promotion" height="50">
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${p.title}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${fn:length(p.description) > 50}">
                                            ${fn:substring(p.description, 0, 50)}...
                                        </c:when>
                                        <c:otherwise>
                                            ${p.description}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${p.isActive() ? 'Hiển thị' : 'Ẩn'}</td>
                                <td>
                                    <fmt:formatDate value="${p.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm" />
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/promotions?id=${p.id}" 
                                       class="btn btn-sm btn-warning">Sửa</a>
                                    <form action="${pageContext.request.contextPath}/admin/promotions" method="POST" class="d-inline">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${p.id}">
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

<script>
// Preview ảnh khi chọn file
document.getElementById('promotionFile').addEventListener('change', function(e) {
    const file = e.target.files[0];
    const previewContainer = document.getElementById('imagePreviewContainer');
    const previewImg = document.getElementById('imagePreview');
    const currentImageContainer = document.getElementById('currentImageContainer');
    
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            previewContainer.style.display = 'block';
            // Ẩn ảnh hiện tại khi có preview mới
            if (currentImageContainer) {
                currentImageContainer.style.display = 'none';
            }
        };
        reader.readAsDataURL(file);
    } else {
        previewContainer.style.display = 'none';
        // Hiện lại ảnh hiện tại nếu không chọn file
        if (currentImageContainer) {
            currentImageContainer.style.display = 'block';
        }
    }
});
</script>

