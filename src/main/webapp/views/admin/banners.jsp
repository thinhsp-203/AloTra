<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-images text-primary" style="margin-right: 10px;"></i>Quản lý Banner & Logo
        </h1>
        <p class="text-muted mb-0">Quản lý banner và logo website</p>
    </div>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<%-- Logo Settings Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-image" style="margin-right: 10px;"></i>Cài đặt Logo Website
        </h6>
    </div>
    <div class="card-body p-4">
        <form method="POST" action="${pageContext.request.contextPath}/admin/banners/save" enctype="multipart/form-data">
            <input type="hidden" name="action" value="updateLogo">
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="logoFile" class="form-label fw-semibold">
                        <i class="fas fa-upload" style="margin-right: 5px;"></i>Upload Logo
                    </label>
                    <input type="file" class="form-control" id="logoFile" name="logoFile" 
                           accept="image/jpeg,image/jpg,image/png,image/webp"
                           onchange="handleLogoFileChange(event)">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Chọn file ảnh từ máy tính (JPG, PNG, WEBP, tối đa 10MB)
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="logoUrl" class="form-label fw-semibold">
                        <i class="fas fa-link" style="margin-right: 5px;"></i>Hoặc nhập Logo URL
                    </label>
                    <input type="text" class="form-control" id="logoUrl" name="LOGO_URL" 
                           value="${siteSettings.LOGO_URL}" 
                           placeholder="https://.../logo.png hoặc /uploads/logo.png"
                           oninput="handleLogoUrlChange(event)">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Dán link ảnh logo từ internet hoặc đường dẫn tương đối
                    </div>
                </div>
                <div class="col-md-12">
                    <label class="form-label fw-semibold">Xem trước Logo</label>
                    <div class="border rounded p-3 bg-light text-center" style="min-height: 100px;">
                        <div id="logoPreview">
                            <c:choose>
                                <c:when test="${not empty siteSettings.LOGO_URL}">
                                    <c:choose>
                                        <%-- Nếu là URL external (bắt đầu bằng http/https) --%>
                                        <c:when test="${fn:startsWith(siteSettings.LOGO_URL, 'http')}">
                                            <img src="${siteSettings.LOGO_URL}" 
                                                 alt="Logo" 
                                                 style="max-height: 100px; max-width: 100%; object-fit: contain;"
                                                 onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\\'text-danger py-3\\'>Không thể tải ảnh</div>';">
                                        </c:when>
                                        <%-- Nếu đã có prefix uploads/ (không có / ở đầu) --%>
                                        <c:when test="${fn:startsWith(siteSettings.LOGO_URL, 'uploads/')}">
                                            <img src="${pageContext.request.contextPath}/${siteSettings.LOGO_URL}" 
                                                 alt="Logo" 
                                                 style="max-height: 100px; max-width: 100%; object-fit: contain;"
                                                 onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\\'text-danger py-3\\'>Không thể tải ảnh</div>';">
                                        </c:when>
                                        <%-- Nếu bắt đầu bằng /uploads/ --%>
                                        <c:when test="${fn:startsWith(siteSettings.LOGO_URL, '/uploads/')}">
                                            <img src="${pageContext.request.contextPath}${siteSettings.LOGO_URL}" 
                                                 alt="Logo" 
                                                 style="max-height: 100px; max-width: 100%; object-fit: contain;"
                                                 onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\\'text-danger py-3\\'>Không thể tải ảnh</div>';">
                                        </c:when>
                                        <%-- Trường hợp khác (tương thích ngược) --%>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/uploads/${siteSettings.LOGO_URL}" 
                                                 alt="Logo" 
                                                 style="max-height: 100px; max-width: 100%; object-fit: contain;"
                                                 onerror="this.onerror=null; this.parentElement.innerHTML='<div class=\\'text-danger py-3\\'>Không thể tải ảnh</div>';">
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-muted py-3">Chưa có logo</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div class="col-md-12">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu Logo
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
// Preview logo khi chọn file
function handleLogoFileChange(event) {
    const file = event.target.files[0];
    const preview = document.getElementById('logoPreview');
    const urlInput = document.getElementById('logoUrl');
    
    if (!preview) {
        console.error('Không tìm thấy element logoPreview');
        return;
    }
    
    if (file) {
        // Clear URL input khi chọn file
        if (urlInput) {
            urlInput.value = '';
        }
        
        // Validate file type
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
        if (!allowedTypes.includes(file.type)) {
            alert('Chỉ chấp nhận file ảnh: JPG, PNG, WEBP');
            event.target.value = '';
            preview.innerHTML = '<div class="text-muted py-3">Chưa có logo</div>';
            return;
        }
        
        // Validate file size (10MB)
        if (file.size > 10 * 1024 * 1024) {
            alert('File quá lớn! Kích thước tối đa: 10MB');
            event.target.value = '';
            preview.innerHTML = '<div class="text-muted py-3">Chưa có logo</div>';
            return;
        }
        
        // Show preview
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.innerHTML = '<img src="' + e.target.result + '" alt="Logo" style="max-height: 100px; max-width: 100%; object-fit: contain;">';
        };
        reader.onerror = function() {
            preview.innerHTML = '<div class="text-danger py-3">Lỗi khi đọc file</div>';
        };
        reader.readAsDataURL(file);
    } else {
        // Nếu không có file, hiển thị lại logo hiện tại hoặc "Chưa có logo"
        const currentUrl = urlInput ? urlInput.value.trim() : '';
        if (currentUrl) {
            handleLogoUrlChange({ target: urlInput });
        } else {
            preview.innerHTML = '<div class="text-muted py-3">Chưa có logo</div>';
        }
    }
}

// Preview logo khi nhập URL
function handleLogoUrlChange(event) {
    const url = event.target ? event.target.value.trim() : '';
    const preview = document.getElementById('logoPreview');
    const fileInput = document.getElementById('logoFile');
    const contextPath = '${pageContext.request.contextPath}';
    
    if (!preview) {
        console.error('Không tìm thấy element logoPreview');
        return;
    }
    
    if (url) {
        // Clear file input khi nhập URL
        if (fileInput) {
            fileInput.value = '';
        }
        
        let imgSrc = url;
        // Nếu là URL external (http/https) → dùng trực tiếp
        if (url.startsWith('http://') || url.startsWith('https://')) {
            imgSrc = url;
        }
        // Nếu bắt đầu bằng uploads/ → thêm contextPath
        else if (url.startsWith('uploads/')) {
            imgSrc = contextPath + '/' + url;
        }
        // Nếu bắt đầu bằng /uploads/ → thêm contextPath
        else if (url.startsWith('/uploads/')) {
            imgSrc = contextPath + url;
        }
        // Nếu bắt đầu bằng / → có thể là absolute path
        else if (url.startsWith('/')) {
            imgSrc = url;
        }
        // Trường hợp khác → không hợp lệ
        else {
            preview.innerHTML = '<div class="text-warning py-3">URL không hợp lệ (phải bắt đầu bằng http://, https://, /uploads/ hoặc uploads/)</div>';
            return;
        }
        
        // Tạo img element với error handling
        const img = document.createElement('img');
        img.src = imgSrc;
        img.alt = 'Logo';
        img.style.cssText = 'max-height: 100px; max-width: 100%; object-fit: contain;';
        img.onerror = function() {
            preview.innerHTML = '<div class="text-danger py-3">Không thể tải ảnh từ URL này</div>';
        };
        img.onload = function() {
            preview.innerHTML = '';
            preview.appendChild(img);
        };
        
        // Nếu ảnh đã được cache, onload có thể không fire, nên append luôn
        preview.innerHTML = '';
        preview.appendChild(img);
    } else {
        preview.innerHTML = '<div class="text-muted py-3">Chưa có logo</div>';
    }
}

// Khởi tạo preview khi trang load (nếu có logo hiện tại)
document.addEventListener('DOMContentLoaded', function() {
    const urlInput = document.getElementById('logoUrl');
    const preview = document.getElementById('logoPreview');
    
    // Nếu có logo hiện tại trong preview và có URL input, đảm bảo preview đúng
    if (urlInput && preview && urlInput.value) {
        // Preview đã được render từ server, không cần làm gì
        // Nhưng nếu user thay đổi, sẽ trigger handleLogoUrlChange
    }
});
</script>


<%-- Banners List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Banner
            </h6>
            <a href="${pageContext.request.contextPath}/admin/banners/create" class="btn btn-primary btn-sm">
                <i class="fas fa-plus" style="margin-right: 5px;"></i>Thêm Banner Mới
            </a>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 120px;" class="ps-4">Ảnh</th>
                        <th>Đường dẫn Ảnh/URL</th>
                        <th style="width: 100px;" class="text-center">Thứ tự</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 120px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty banners}">
                            <tr>
                                <td colspan="5" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có banner nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm banner mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${banners}" var="b">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <c:set var="bannerImgSrc" value="${b.imageUrl}"/>
                                        <c:if test="${not empty bannerImgSrc}">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(bannerImgSrc, 'http')}">
                                                    <c:set var="bannerImgSrc" value="${b.imageUrl}"/>
                                                </c:when>
                                                <c:when test="${fn:startsWith(bannerImgSrc, 'uploads/')}">
                                                    <c:set var="bannerImgSrc" value="${pageContext.request.contextPath}/${b.imageUrl}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="bannerImgSrc" value="${pageContext.request.contextPath}/uploads/${b.imageUrl}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                        <img src="${empty bannerImgSrc ? 'https://via.placeholder.com/80' : bannerImgSrc}" 
                                             alt="Banner" 
                                             class="rounded shadow-sm"
                                             style="width: 80px; height: 80px; object-fit: cover;"
                                             onerror="this.src='https://via.placeholder.com/80'">
                                    </td>
                                    <td>
                                        <small class="text-muted">${b.imageUrl}</small>
                                    </td>
                                    <td class="text-center">${b.sortOrder}</td>
                                    <td class="text-center">
                                        <span class="badge ${b.isActive() ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${b.isActive() ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${b.isActive() ? 'Hiển thị' : 'Ẩn'}
                                        </span>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center gap-3">
                                            <a href="${pageContext.request.contextPath}/admin/banners/edit?id=${b.id}" 
                                               class="btn btn-sm btn-outline-primary" title="Sửa">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/banners/delete" method="POST" 
                                                  style="display: inline;" 
                                                  onsubmit="return confirm('Xác nhận xóa banner này?')">
                                                <input type="hidden" name="id" value="${b.id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa">
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
</div>
