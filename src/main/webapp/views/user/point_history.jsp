<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Lịch Sử Điểm - Hội Viên" scope="request"/>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h4 mb-0">
                <i class="bi bi-clock-history text-primary"></i> Lịch Sử Giao Dịch Điểm
            </h2>
            <div class="badge bg-warning text-dark fs-6">
                Điểm hiện tại: <fmt:formatNumber value="${points}" pattern="#,##0"/>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty transactions}">
                <div class="alert alert-info text-center">
                    Bạn chưa có giao dịch điểm nào.
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Thời gian</th>
                                <th>Loại</th>
                                <th>Mô tả</th>
                                <th class="text-end">Điểm</th>
                                <th class="text-end">Số dư sau</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="txn" items="${transactions}">
                                <tr>
                                    <td>
                                        <fmt:formatDate value="${txn.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${txn.type == 'EARN'}">
                                                <span class="badge bg-success">Tích điểm</span>
                                            </c:when>
                                            <c:when test="${txn.type == 'REDEEM'}">
                                                <span class="badge bg-danger">Đổi quà</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${txn.type}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${txn.description}</td>
                                    <td class="text-end">
                                        <c:choose>
                                            <c:when test="${txn.points > 0}">
                                                <span class="text-success">+<fmt:formatNumber value="${txn.points}" pattern="#,##0"/></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-danger"><fmt:formatNumber value="${txn.points}" pattern="#,##0"/></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-end">
                                        <strong><fmt:formatNumber value="${txn.balance_after}" pattern="#,##0"/></strong>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

