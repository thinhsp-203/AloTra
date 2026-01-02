<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-info-circle text-primary" style="margin-right: 10px;"></i>Quản lý Bài viết Về chúng tôi
        </h1>
        <p class="text-muted mb-0">Quản lý các bài viết giới thiệu về cửa hàng</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/about/create" class="btn btn-primary">
        <i class="fas fa-plus" style="margin-right: 10px;"></i>Thêm bài viết mới
    </a>
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

<%-- About List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Bài viết
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 100px;" class="ps-4">Ảnh</th>
                        <th>Tiêu đề</th>
                        <th style="width: 80px;" class="text-center">Thứ tự</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 150px;">Ngày tạo</th>
                        <th style="width: 180px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty aboutList}">
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có bài viết nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm bài viết mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${aboutList}" var="about">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <c:choose>
                                            <c:when test="${not empty about.image and fn:startsWith(about.image, 'http')}">
                                                <img src="${about.image}" alt="${about.title}" 
                                                     class="rounded shadow-sm"
                                                     style="width: 80px; height: 80px; object-fit: cover;">
                                            </c:when>
                                            <c:when test="${not empty about.image}">
                                                <img src="${pageContext.request.contextPath}/uploads/${about.image}" 
                                                     alt="${about.title}" 
                                                     class="rounded shadow-sm"
                                                     style="width: 80px; height: 80px; object-fit: cover;"
                                                     onerror="this.src='${pageContext.request.contextPath}/assets/img/placeholder.jpg'">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="bg-light d-flex align-items-center justify-content-center rounded" 
                                                     style="width: 80px; height: 80px;">
                                                    <i class="fas fa-image text-muted"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="fw-semibold fs-5">${about.title}</div>
                                        <c:if test="${not empty about.content}">
                                            <small class="text-muted">${fn:substring(fn:replace(about.content, '<[^>]*>', ''), 0, 100)}...</small>
                                        </c:if>
                                    </td>
                                    <td class="text-center">${about.sortOrder}</td>
                                    <td class="text-center">
                                        <span class="badge ${about.isActive ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${about.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${about.isActive ? 'Hiển thị' : 'Ẩn'}
                                        </span>
                                    </td>
                                    <td>
                                        <small class="text-muted">${about.createdDate}</small>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/about/edit?id=${about.id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/about/delete" method="POST" 
                                                  style="display: inline; margin: 0 7.5px;" 
                                                  onsubmit="return confirm('Xác nhận xóa bài viết \'${about.title}\'?')">
                                                <input type="hidden" name="id" value="${about.id}">
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
