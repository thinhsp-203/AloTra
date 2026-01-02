<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container-fluid">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 mb-0 text-gray-800">Quản lý Bài viết Về chúng tôi</h1>
        <a href="${pageContext.request.contextPath}/admin/about/create" class="btn btn-primary">
            <i class="fas fa-plus"></i> Thêm bài viết mới
        </a>
    </div>

    <%-- Thông báo --%>
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
            <h6 class="m-0 font-weight-bold text-primary">Danh sách Bài viết</h6>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th width="80">Ảnh</th>
                            <th>Tiêu đề</th>
                            <th width="80">Thứ tự</th>
                            <th width="100">Trạng thái</th>
                            <th width="180">Ngày tạo</th>
                            <th width="150">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty aboutList}">
                                <tr>
                                    <td colspan="6" class="text-center text-muted py-4">
                                        Chưa có bài viết nào. <a href="${pageContext.request.contextPath}/admin/about/create">Thêm bài viết mới</a>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${aboutList}" var="about">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty about.image and fn:startsWith(about.image, 'http')}">
                                                    <img src="${about.image}" alt="${about.title}" 
                                                         class="img-thumbnail" style="width: 60px; height: 60px; object-fit: cover;">
                                                </c:when>
                                                <c:when test="${not empty about.image}">
                                                    <img src="${pageContext.request.contextPath}/uploads/${about.image}" 
                                                         alt="${about.title}" 
                                                         class="img-thumbnail" style="width: 60px; height: 60px; object-fit: cover;"
                                                         onerror="this.src='${pageContext.request.contextPath}/assets/img/placeholder.jpg'">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="bg-light d-flex align-items-center justify-content-center" 
                                                         style="width: 60px; height: 60px;">
                                                        <i class="fas fa-image text-muted"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <strong>${about.title}</strong>
                                            <c:if test="${not empty about.content}">
                                                <br><small class="text-muted">${fn:substring(fn:replace(about.content, '<[^>]*>', ''), 0, 100)}...</small>
                                            </c:if>
                                        </td>
                                        <td class="text-center">${about.sortOrder}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${about.isActive}">
                                                    <span class="badge bg-success">Hiển thị</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Ẩn</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <small>${about.createdDate}</small>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/about/edit?id=${about.id}" 
                                               class="btn btn-sm btn-primary">
                                                <i class="fas fa-edit"></i> Sửa
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/about/delete" method="POST" 
                                                  class="d-inline" onsubmit="return confirm('Bạn chắc chắn muốn xóa bài viết này?');">
                                                <input type="hidden" name="id" value="${about.id}">
                                                <button type="submit" class="btn btn-sm btn-danger">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </button>
                                            </form>
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
</div>

