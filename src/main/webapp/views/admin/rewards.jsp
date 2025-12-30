<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="container-fluid">
    <h1 class="h3 mb-2 text-gray-800">Quản lý Quà Tặng Hội Viên</h1>

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
                    <c:when test="${not empty reward}">Chỉnh sửa Quà Tặng</c:when>
                    <c:otherwise>Thêm Quà Tặng Mới</c:otherwise>
                </c:choose>
            </h6>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/admin/rewards" method="POST">
                <c:if test="${not empty reward}">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${reward.reward_id}">
                </c:if>
                <c:if test="${empty reward}">
                    <input type="hidden" name="action" value="add">
                </c:if>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="name" class="form-label">Tên quà tặng <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" 
                               value="${reward.name}" required>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="description" class="form-label">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" rows="3">${reward.description}</textarea>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="points_required" class="form-label">Điểm cần thiết <span class="text-danger">*</span></label>
                        <input type="number" class="form-control" id="points_required" name="points_required" 
                               value="${reward.points_required}" min="1" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="stock" class="form-label">Số lượng tồn kho</label>
                        <input type="number" class="form-control" id="stock" name="stock" 
                               value="${reward.stock}" min="0" placeholder="Để trống = không giới hạn">
                        <div class="form-text">Để trống nếu không giới hạn số lượng</div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <label for="image_url" class="form-label">URL hình ảnh</label>
                        <input type="url" class="form-control" id="image_url" name="image_url" 
                               value="${reward.image_url}" placeholder="https://...">
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12 mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                                   ${reward.isActive ? 'checked' : ''} ${empty reward ? 'checked' : ''}>
                            <label class="form-check-label" for="isActive">
                                Kích hoạt
                            </label>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-12">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> ${not empty reward ? 'Cập nhật' : 'Thêm mới'}
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/rewards" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Hủy
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">Danh sách Quà Tặng</h6>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Tên quà</th>
                            <th>Mô tả</th>
                            <th class="text-end">Điểm cần</th>
                            <th class="text-center">Tồn kho</th>
                            <th class="text-center">Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty rewards}">
                                <tr>
                                    <td colspan="7" class="text-center text-muted py-4">Chưa có quà tặng nào.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="r" items="${rewards}">
                                    <tr>
                                        <td>${r.reward_id}</td>
                                        <td><strong>${r.name}</strong></td>
                                        <td>${r.description}</td>
                                        <td class="text-end">
                                            <span class="badge bg-warning text-dark">
                                                <fmt:formatNumber value="${r.points_required}" pattern="#,##0"/>
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${r.stock == null}">
                                                    <span class="text-muted">∞</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${r.stock}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <span class="badge text-bg-${r.isActive ? 'success' : 'secondary'}">
                                                ${r.isActive ? 'Hoạt động' : 'Ngừng'}
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/admin/rewards?id=${r.reward_id}" 
                                                   class="btn btn-outline-primary" title="Chỉnh sửa">
                                                    <i class="fas fa-pencil-alt"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/admin/rewards" method="POST" 
                                                      style="display: inline;" 
                                                      onsubmit="return confirm('Xác nhận xóa quà tặng &quot;${r.name}&quot;?')">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${r.reward_id}">
                                                    <button type="submit" class="btn btn-outline-danger" title="Xóa">
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
</div>

