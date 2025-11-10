<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">
            <i class="bi bi-heart-fill text-primary"></i> Sản phẩm yêu thích
        </h2>
        
        <div class="row row-cols-1 row-cols-md-3 g-4" id="wishlist-container">
            <c:choose>
                <c:when test="${empty wishlistItems}">
                    <div class="col-12">
                        <div class="alert alert-info text-center">
                            Bạn chưa có sản phẩm yêu thích nào.
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="item" items="${wishlistItems}">
                        <c:set var="p" value="${item.product}" scope="request"/>
                        <div class="col">
                            <jsp:include page="/views/_partials/product_card.jsp" />
                        </div>
                    </c:forEach>
                    </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>