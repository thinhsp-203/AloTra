<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Chi Tiết Quà Đã Đổi - Hội Viên" scope="request"/>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h4 mb-0">
                <i class="bi bi-gift-fill text-success"></i> Chi Tiết Quà Đã Đổi
            </h2>
            <div class="badge bg-warning text-dark fs-6">
                Điểm hiện tại: <fmt:formatNumber value="${points}" pattern="#,##0"/>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${empty transaction || empty reward}">
            <div class="alert alert-warning">
                Không tìm thấy thông tin quà đã đổi.
            </div>
            <a href="${pageContext.request.contextPath}/user/point-history" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Quay lại lịch sử
            </a>
        </c:if>

        <c:if test="${not empty transaction && not empty reward}">
            <%-- Thông báo thành công --%>
            <div class="alert alert-success border-0 shadow-sm mb-4">
                <div class="d-flex align-items-center">
                    <i class="bi bi-check-circle-fill fs-3 me-3"></i>
                    <div>
                        <h5 class="mb-1">Đổi quà thành công!</h5>
                        <p class="mb-0">Bạn đã đổi quà <strong>${reward.name}</strong> thành công. Vui lòng xem thông tin chi tiết bên dưới.</p>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <%-- Thông tin quà đã đổi --%>
                <div class="col-md-5">
                    <div class="card shadow-sm h-100">
                        <div class="card-header bg-white border-bottom">
                            <h5 class="mb-0">
                                <i class="bi bi-gift text-primary"></i> Thông Tin Quà
                            </h5>
                        </div>
                        <div class="card-body">
                            <c:if test="${not empty reward.image_url}">
                                <c:set var="rewardImgSrc" value="${reward.image_url}"/>
                                <c:choose>
                                    <c:when test="${fn:startsWith(rewardImgSrc, 'http')}">
                                        <c:set var="rewardImgSrc" value="${reward.image_url}"/>
                                    </c:when>
                                    <c:when test="${fn:startsWith(rewardImgSrc, 'uploads/')}">
                                        <c:set var="rewardImgSrc" value="${pageContext.request.contextPath}/${reward.image_url}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="rewardImgSrc" value="${pageContext.request.contextPath}/uploads/${reward.image_url}"/>
                                    </c:otherwise>
                                </c:choose>
                                <div class="text-center mb-3">
                                    <img src="${rewardImgSrc}" class="img-fluid rounded" alt="${reward.name}" 
                                         style="max-height: 300px; object-fit: cover;"
                                         onerror="this.src='https://via.placeholder.com/300'">
                                </div>
                            </c:if>
                            
                            <h4 class="mb-3">${reward.name}</h4>
                            
                            <c:if test="${not empty reward.description}">
                                <p class="text-muted">${reward.description}</p>
                            </c:if>
                            
                            <div class="mt-3">
                                <span class="badge bg-warning text-dark fs-6 px-3 py-2">
                                    <i class="bi bi-star-fill"></i> Đã trừ: <fmt:formatNumber value="${transaction.points * -1}" pattern="#,##0"/> điểm
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Hướng dẫn sử dụng và nhận quà --%>
                <div class="col-md-7">
                    <div class="card shadow-sm h-100">
                        <div class="card-header bg-white border-bottom">
                            <h5 class="mb-0">
                                <i class="bi bi-info-circle text-primary"></i> Hướng Dẫn
                            </h5>
                        </div>
                        <div class="card-body">
                            <%-- Thông tin giao dịch --%>
                            <div class="mb-4">
                                <h6 class="fw-bold mb-3">
                                    <i class="bi bi-receipt text-secondary"></i> Thông Tin Giao Dịch
                                </h6>
                                <div class="table-responsive">
                                    <table class="table table-sm table-borderless">
                                        <tr>
                                            <td class="text-muted" style="width: 40%;">Mã giao dịch:</td>
                                            <td><strong>#${transaction.transaction_id}</strong></td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted">Thời gian đổi:</td>
                                            <td>
                                                <fmt:formatDate value="${transaction.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted">Số điểm đã trừ:</td>
                                            <td>
                                                <span class="text-danger fw-bold">
                                                    <fmt:formatNumber value="${transaction.points * -1}" pattern="#,##0"/> điểm
                                                </span>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-muted">Số dư sau giao dịch:</td>
                                            <td>
                                                <span class="text-success fw-bold">
                                                    <fmt:formatNumber value="${transaction.balance_after}" pattern="#,##0"/> điểm
                                                </span>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>

                            <hr>

                            <%-- Hướng dẫn nhận quà --%>
                            <div class="mb-4">
                                <h6 class="fw-bold mb-3">
                                    <i class="bi bi-box-seam text-success"></i> Cách Nhận Quà
                                </h6>
                                <div class="alert alert-info border-0">
                                    <div class="d-flex align-items-start">
                                        <i class="bi bi-info-circle-fill me-2 mt-1"></i>
                                        <div>
                                            <p class="mb-2"><strong>Quà sẽ được gửi đến bạn theo một trong các cách sau:</strong></p>
                                            <ul class="mb-0">
                                                <li>Quà sẽ được gửi kèm trong đơn hàng tiếp theo của bạn</li>
                                                <li>Hoặc bạn có thể đến cửa hàng để nhận quà trực tiếp</li>
                                                <li>Vui lòng liên hệ hotline <strong>1900-xxxx</strong> để được hỗ trợ nhận quà</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <%-- Hướng dẫn sử dụng quà --%>
                            <div class="mb-4">
                                <h6 class="fw-bold mb-3">
                                    <i class="bi bi-book text-primary"></i> Hướng Dẫn Sử Dụng
                                </h6>
                                <div class="alert alert-light border">
                                    <ul class="mb-0">
                                        <li>Quà có thể sử dụng ngay sau khi nhận được</li>
                                        <li>Vui lòng kiểm tra kỹ thông tin quà trước khi sử dụng</li>
                                        <li>Nếu có thắc mắc, vui lòng liên hệ bộ phận chăm sóc khách hàng</li>
                                    </ul>
                                </div>
                            </div>

                            <%-- Lưu ý --%>
                            <div class="alert alert-warning border-0">
                                <h6 class="fw-bold mb-2">
                                    <i class="bi bi-exclamation-triangle"></i> Lưu Ý
                                </h6>
                                <ul class="mb-0 small">
                                    <li>Vui lòng lưu lại mã giao dịch <strong>#${transaction.transaction_id}</strong> để tra cứu khi cần</li>
                                    <li>Quà đã đổi không thể hoàn trả điểm</li>
                                    <li>Thời hạn sử dụng quà sẽ được thông báo khi bạn nhận quà</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Actions --%>
            <div class="mt-4 d-flex gap-3">
                <a href="${pageContext.request.contextPath}/user/point-history" class="btn btn-outline-secondary">
                    <i class="bi bi-clock-history"></i> Xem Lịch Sử Giao Dịch
                </a>
                <a href="${pageContext.request.contextPath}/user/rewards" class="btn btn-outline-primary">
                    <i class="bi bi-gift"></i> Đổi Quà Khác
                </a>
                <a href="${pageContext.request.contextPath}/user/loyalty" class="btn btn-primary">
                    <i class="bi bi-house"></i> Về Trang Hội Viên
                </a>
            </div>
        </c:if>
    </div>
</div>

