<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<fmt:setLocale value="vi_VN"/>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-users text-primary" style="margin-right: 10px;"></i>Quản lý người dùng
        </h1>
        <p class="text-muted mb-0">Quản lý tài khoản người dùng trong hệ thống</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/users/create" class="btn btn-primary">
        <i class="fas fa-plus" style="margin-right: 10px;"></i>Thêm người dùng
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

<%-- Filter Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-primary text-white py-3">
        <h6 class="m-0 font-weight-bold">
            <i class="fas fa-filter" style="margin-right: 10px;"></i>Tìm kiếm & Lọc người dùng
        </h6>
    </div>
    <div class="card-body p-4">
        <form method="get" action="${pageContext.request.contextPath}/admin/users">
            <div class="row g-3 align-items-center">
                <div class="col-md-5">
                    <input type="text" class="form-control" name="keyword" 
                           placeholder="Tìm tên, email, SĐT..." 
                           value="${fn:escapeXml(keyword)}"
                           onkeyup="if(event.key === 'Enter') this.form.submit();"/>
                </div>
                <div class="col-md-3">
                    <select class="form-select" name="roleId">
                        <option value="">-- Tất cả vai trò --</option>
                        <c:forEach var="entry" items="${roles}">
                            <option value="${entry.key}" <c:if test="${selectedRoleId != null && selectedRoleId eq entry.key}">selected</c:if>>${entry.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary" title="Lọc">
                        <i class="fas fa-filter" style="margin-right: 10px;"></i>Lọc
                    </button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users" title="Bỏ lọc">
                        <i class="fas fa-sync-alt"></i>
                    </a>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Users Table Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách người dùng
            </h6>
            <span class="badge bg-primary text-white fs-6 px-3 py-2">
                <i class="fas fa-users" style="margin-right: 5px;"></i>Tổng: ${totalUsers} tài khoản
            </span>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="px-4 py-3 bg-light border-bottom">
            <p class="text-muted small mb-0">
                <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Hiển thị <strong>${fromRecord}</strong> - <strong>${toRecord}</strong> trên tổng số <strong>${totalUsers}</strong> người dùng
            </p>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 80px;" class="ps-4">#</th>
                        <th>Tên đăng nhập</th>
                        <th>Họ tên</th>
                        <th>Email</th>
                        <th style="width: 120px;">Vai trò</th>
                        <th style="width: 150px;" class="text-center">Trạng thái</th>
                        <th style="width: 200px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty users}">
                            <tr>
                                <td colspan="7" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có người dùng nào</h5>
                                        <p class="text-muted small mb-0">Không tìm thấy dữ liệu phù hợp với bộ lọc</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="user" items="${users}" varStatus="st">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <strong class="text-primary">${fromRecord + st.index}</strong>
                                    </td>
                                    <td>
                                        <div class="fw-semibold">${user.username}</div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${empty user.fullname}">
                                                <span class="text-muted">-</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${user.fullname}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${user.email}</td>
                                    <td>
                                        <span class="badge bg-info text-white">${user.roleName}</span>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${user.isActive ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${user.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${user.isActive ? 'Kích hoạt' : 'Vô hiệu hóa'}
                                        </span>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/users/edit?id=${user.id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <%-- Chỉ hiển thị nút inactive nếu không phải admin và không phải chính mình --%>
                                            <c:if test="${user.roleid != 1 && sessionScope.currentUser.id != user.id}">
                                                <form action="${pageContext.request.contextPath}/admin/users/toggle-status" 
                                                      method="post" 
                                                      style="display: inline; margin: 0 7.5px;">
                                                    <input type="hidden" name="id" value="${user.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-secondary" title="Đổi trạng thái">
                                                        <i class="fas fa-toggle-on"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                            <%-- Chỉ hiển thị nút xóa vĩnh viễn nếu không phải admin và không phải chính mình --%>
                                            <c:if test="${user.roleid != 1 && sessionScope.currentUser.id != user.id}">
                                                <form action="${pageContext.request.contextPath}/admin/users/delete-permanent" 
                                                      method="post" 
                                                      style="display: inline; margin: 0 7.5px;" 
                                                      onsubmit="return confirm('CẢNH BÁO: Bạn có chắc chắn muốn XÓA VĨNH VIỄN người dùng \'${fn:escapeXml(user.username)}\' không?\n\nHành động này không thể hoàn tác!')">
                                                    <input type="hidden" name="id" value="${user.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa vĩnh viễn">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </form>
                                            </c:if>
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
    <div class="card-footer bg-white border-top py-3">
        <div class="row align-items-center">
            <div class="col-md-3">
                <label class="form-label small mb-0" style="margin-right: 10px;">
                    <i class="fas fa-list-ol" style="margin-right: 10px;"></i>Số lượng / trang:
                </label>
                <form method="get" action="${pageContext.request.contextPath}/admin/users" style="display: inline;">
                    <c:if test="${not empty keyword}">
                        <input type="hidden" name="keyword" value="${keyword}"/>
                    </c:if>
                    <c:if test="${not empty roleIdParam}">
                        <input type="hidden" name="roleId" value="${roleIdParam}"/>
                    </c:if>
                    <select class="form-select form-select-sm d-inline-block" name="size" style="width: auto;" onchange="this.form.submit();">
                        <c:forEach var="option" items="${pageSizes}">
                            <option value="${option}" <c:if test="${option == pageSize}">selected</c:if>>${option}</option>
                        </c:forEach>
                    </select>
                </form>
            </div>
            <div class="col-md-9">
                <c:if test="${totalPages > 1}">
                    <nav>
                        <ul class="pagination justify-content-end mb-0">
                            <c:set var="prevPage" value="${page > 1 ? page - 1 : 1}"/>
                            <c:url var="prevUrl" value="/admin/users">
                                <c:param name="page" value="${prevPage}"/>
                                <c:param name="size" value="${pageSize}"/>
                                <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                                <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
                            </c:url>
                            <li class="page-item ${page <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${prevUrl}">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </li>
                            
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <c:if test="${p == 1 || p == totalPages || (p >= page - 2 && p <= page + 2)}">
                                    <c:url var="pageUrl" value="/admin/users">
                                        <c:param name="page" value="${p}"/>
                                        <c:param name="size" value="${pageSize}"/>
                                        <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                                        <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
                                    </c:url>
                                    <li class="page-item ${p == page ? 'active' : ''}">
                                        <a class="page-link" href="${pageUrl}">${p}</a>
                                    </li>
                                </c:if>
                            </c:forEach>
                            
                            <c:set var="nextPage" value="${page < totalPages ? page + 1 : totalPages}"/>
                            <c:url var="nextUrl" value="/admin/users">
                                <c:param name="page" value="${nextPage}"/>
                                <c:param name="size" value="${pageSize}"/>
                                <c:if test="${not empty keyword}"><c:param name="keyword" value="${keyword}"/></c:if>
                                <c:if test="${not empty roleIdParam}"><c:param name="roleId" value="${roleIdParam}"/></c:if>
                            </c:url>
                            <li class="page-item ${page >= totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${nextUrl}">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>
</div>