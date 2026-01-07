<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-tags text-primary" style="margin-right: 10px;"></i>Quản lý danh mục
        </h1>
        <p class="text-muted mb-0">Quản lý các danh mục sản phẩm trong cửa hàng</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/category/add" class="btn btn-primary">
        <i class="fas fa-plus" style="margin-right: 10px;"></i>Thêm danh mục
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

<%-- Categories Table Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách danh mục
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0" style="table-layout: fixed; width: 100%;">
                <thead class="table-light">
                    <tr>
                        <th class="text-center col-id" style="width: 80px;">ID</th>
                        <th class="text-center col-icon" style="width: 110px;">Icon</th>
                        <th class="col-name">Tên danh mục</th>
                        <th class="text-center col-type" style="width: 180px;">Loại danh mục</th>
                        <th class="text-center col-action" style="width: 120px;">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty cateList}">
                            <tr>
                                <td colspan="5" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có danh mục nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm danh mục mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${cateList}" var="cate" varStatus="st">
                                <tr class="border-bottom">
                                    <td class="text-center">
                                        <strong class="text-primary">${cate.id}</strong>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${not empty cate.icon}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(cate.icon, 'http')}">
                                                        <img src="${cate.icon}" 
                                                             class="rounded shadow-sm" 
                                                             style="width: 60px; height: 60px; object-fit: cover;"
                                                             alt="${cate.name}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/${cate.icon}" 
                                                             class="rounded shadow-sm" 
                                                             style="width: 60px; height: 60px; object-fit: cover;"
                                                             alt="${cate.name}"
                                                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/uploads/categories/default.png';"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/uploads/categories/default.png" 
                                                     class="rounded shadow-sm" 
                                                     style="width: 60px; height: 60px; object-fit: cover;"
                                                     alt="${cate.name}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="fw-semibold mb-1 fs-5">${cate.name}</div>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${cate.isDrink}">
                                                <span class="badge bg-primary text-white">
                                                    <i class="fas fa-coffee" style="margin-right: 5px;"></i>Thức uống
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success text-white">
                                                    <i class="fas fa-cookie-bite" style="margin-right: 5px;"></i>Bánh & Đồ ăn vặt
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/category/edit?id=${cate.id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/category/delete" method="post" 
                                                  style="display: inline; margin: 0 7.5px;" 
                                                  onsubmit="return confirm('Xác nhận xóa danh mục &quot;${cate.name}&quot;?')">
                                                <input type="hidden" name="id" value="${cate.id}">
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
    <c:if test="${not empty cateList}">
        <div class="card-footer bg-white border-top py-3">
            <div class="text-muted small">
                <i class="fas fa-list" style="margin-right: 10px;"></i>Tổng cộng: <strong class="text-primary">${fn:length(cateList)}</strong> danh mục
            </div>
        </div>
    </c:if>
</div>