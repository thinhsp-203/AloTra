<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-box text-primary" style="margin-right: 10px;"></i>Quản lý sản phẩm
        </h1>
        <p class="text-muted mb-0">Quản lý toàn bộ sản phẩm trong cửa hàng</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/products/create" class="btn btn-primary">
        <i class="fas fa-plus" style="margin-right: 10px;"></i>Thêm sản phẩm
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

<%-- Products Table Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách sản phẩm
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 80px;" class="ps-4">ID</th>
                        <th style="width: 100px;">Ảnh</th>
                        <th style="width: 200px;">Tên sản phẩm</th>
                        <th style="width: 150px;">Danh mục</th>
                        <th style="width: 130px;" class="text-end">Giá</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 180px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty list}">
                            <tr>
                                <td colspan="7" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có sản phẩm nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm sản phẩm mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="p" items="${list}">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <strong class="text-primary">#${p.product_id}</strong>
                                    </td>
                                    <td>
                                        <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                                        <c:if test="${not empty thumbnailSrc}">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(thumbnailSrc, 'http')}">
                                                    <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                                                </c:when>
                                                <c:when test="${fn:startsWith(thumbnailSrc, 'uploads/')}">
                                                    <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/${p.thumbnail}"/>
                                                </c:when>
                                                <c:when test="${fn:startsWith(thumbnailSrc, 'products/')}">
                                                    <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/${p.thumbnail}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/products/${p.thumbnail}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                        <c:if test="${empty thumbnailSrc}">
                                            <c:set var="thumbnailSrc" value="https://via.placeholder.com/80"/>
                                        </c:if>
                                        
                                        <img src="${thumbnailSrc}" 
                                             class="rounded shadow-sm" 
                                             style="width: 80px; height: 80px; object-fit: cover;"
                                             alt="${p.product_name}"
                                             onerror="this.src='https://via.placeholder.com/80'"/>
                                    </td>
                                    <td style="width: 200px; max-width: 200px;">
                                        <div class="fw-semibold mb-1 text-truncate" title="${p.product_name}">${p.product_name}</div>
                                        <c:if test="${p.isFeatured}">
                                            <span class="badge bg-warning text-dark">
                                                <i class="fas fa-star" style="margin-right: 5px;"></i>Nổi bật
                                            </span>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty p.category}">
                                                <span class="badge bg-info text-white">${p.category.name}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <strong class="text-success fs-5">
                                            <fmt:formatNumber value="${p.price}" pattern="#,##0₫"/>
                                        </strong>
                                        <c:if test="${p.discount != null && p.discount > 0}">
                                            <div class="mt-1">
                                                <span class="badge bg-danger text-white">-${p.discount}%</span>
                                            </div>
                                        </c:if>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${p.isActive}">
                                                <span class="badge bg-success text-white px-3 py-2">
                                                    <i class="fas fa-check-circle" style="margin-right: 5px;"></i>Hiển thị
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary text-white px-3 py-2">
                                                    <i class="fas fa-times-circle" style="margin-right: 5px;"></i>Ẩn
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center gap-2">
                                            <a href="${pageContext.request.contextPath}/p?id=${p.product_id}" 
                                               class="btn btn-sm btn-outline-info" 
                                               title="Xem chi tiết"
                                               target="_blank">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/products/edit?id=${p.product_id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <c:choose>
                                                <c:when test="${p.isActive}">
                                                    <form action="${pageContext.request.contextPath}/admin/products/disable" method="post" 
                                                          style="display: inline;" 
                                                          onsubmit="return confirm('Xác nhận ngừng bán sản phẩm &quot;${p.product_name}&quot;?')">
                                                        <input type="hidden" name="id" value="${p.product_id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-warning" title="Ngừng bán">
                                                            <i class="fas fa-ban"></i>
                                                        </button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/admin/products/enable" method="post" 
                                                          style="display: inline;" 
                                                          onsubmit="return confirm('Xác nhận kích hoạt sản phẩm &quot;${p.product_name}&quot;?')">
                                                        <input type="hidden" name="id" value="${p.product_id}">
                                                        <button type="submit" class="btn btn-sm btn-outline-success" title="Kích hoạt">
                                                            <i class="fas fa-check-circle"></i>
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                            <form action="${pageContext.request.contextPath}/admin/products/delete" method="post" 
                                                  style="display: inline;" 
                                                  onsubmit="return confirm('CẢNH BÁO: Xóa sản phẩm &quot;${p.product_name}&quot; vĩnh viễn?\n\nLưu ý: Nếu sản phẩm đã có đơn hàng, hệ thống sẽ từ chối và đề xuất dùng &quot;Ngừng bán&quot;.')">
                                                <input type="hidden" name="id" value="${p.product_id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa vĩnh viễn">
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