<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<style>
.star-rating { display: flex; flex-direction: row-reverse; justify-content: flex-end; }
.star-rating input[type="radio"] { display: none; }
.star-rating label { font-size: 1.5rem; color: #ddd; cursor: pointer; padding: 0 0.1rem;}
.star-rating input[type="radio"]:checked ~ label { color: #ffc107; }
.star-rating label:hover, .star-rating label:hover ~ label { color: #ffc107; }
.avg-rating .star { color: #ddd; font-size: 1.1rem; }
.avg-rating .star.filled { color: #ffc107; }
</style>

<div class="container my-5">
    <div class="row g-4">
        <div class="col-lg-5">
            <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
            <c:if test="${not empty thumbnailSrc}">
                <c:choose>
                    <c:when test="${fn:startsWith(thumbnailSrc, 'http')}">
                        <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                    </c:when>
                    <c:when test="${fn:startsWith(thumbnailSrc, 'uploads/')}">
                        <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/${p.thumbnail}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/products/${p.thumbnail}"/>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <c:if test="${empty thumbnailSrc}">
               <c:set var="thumbnailSrc" value="https://via.placeholder.com/400"/>
            </c:if>
            <div style="width: 100%; height: 500px; overflow: hidden; border-radius: 0.5rem; box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075); background-color: #f8f9fa; display: flex; align-items: center; justify-content: center;">
                <img src="${thumbnailSrc}" 
                     class="rounded" 
                     alt="${p.product_name}"
                     style="width: 100%; height: 100%; object-fit: cover;">
            </div>
        </div>
        
        <div class="col-lg-7">
            <h2>${p.product_name}</h2>
            
            <div class="d-flex align-items-center mb-2 avg-rating">
                <c:if test="${not empty totalReviews}">
                    <strong class="text-primary fs-5 me-2"><fmt:formatNumber value="${avgRating}" maxFractionDigits="1"/>/5</strong>
                    <div class="d-inline-block">
                        <c:forEach begin="1" end="5" var="i">
                           <span class="star ${i <= (avgRating + 0.25) ? 'filled' : ''}"><i class="bi bi-star-fill"></i></span>
                        </c:forEach>
                    </div>
                    <span class="text-muted ms-2">(${totalReviews} đánh giá)</span>
                </c:if>
                <c:if test="${empty totalReviews}">
                    <span class="text-muted">Chưa có đánh giá</span>
                </c:if>
            </div>
            <div>
                <c:choose>
                    <c:when test="${p.hasDiscount()}">
                        <div class="mb-2">
                            <span class="badge bg-danger text-white fs-6 px-3 py-2">
                                <i class="bi bi-tag-fill"></i> Giảm <fmt:formatNumber value="${p.discount}" pattern="#,##0"/>%
                            </span>
                        </div>
                        <p class="lead text-danger fw-bold fs-2 mb-1">
                            <fmt:formatNumber value="${p.finalPrice}" pattern="#,##0₫"/>
                        </p>
                        <p class="text-muted mb-0" style="text-decoration: line-through; font-size: 1.1rem;">
                            <fmt:formatNumber value="${p.price}" pattern="#,##0₫"/>
                        </p>
                    </c:when>
                    <c:otherwise>
                        <p class="lead text-primary fw-bold fs-3">
                            <fmt:formatNumber value="${p.price}" pattern="#,##0₫"/>
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <p class="text-muted">
                <span>
                    <i class="bi bi-tag"></i> Danh mục: 
                    <a href="${pageContext.request.contextPath}/products?cate=${p.category.id}">
                        ${p.category.name}
                    </a>
                </span>
            </p>
            
            <p>${p.description}</p>
            
            <hr>
            
            <form>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Số lượng:</label>
                    <input type="number" class="form-control" value="1" min="1" style="width: 100px;">
                </div>
                
                <div class="d-flex gap-2">
                    <button class="btn btn-primary btn-lg" 
                            data-bs-toggle="modal" 
                            data-bs-target="#productModal" 
                            data-product-id="${p.product_id}"
                            type="button">
                        <i class="bi bi-cart-plus"></i> Thêm vào giỏ
                    </button>
                    
                    <button class="btn btn-outline-danger btn-lg btn-wishlist" 
                            data-product-id="${p.product_id}"
                            title="Thêm vào yêu thích"
                            type="button">
                        <i class="bi bi-heart"></i>
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <div class="row mt-5">
        <div class="col-lg-8">
            <h4 class="mb-3">Đánh giá sản phẩm</h4>
            
            <c:if test="${canReview}">
                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h5 class="card-title">Viết đánh giá của bạn</h5>
                        <form action="${pageContext.request.contextPath}/submit-review" method="POST">
                            <input type="hidden" name="productId" value="${p.product_id}">
                            <div class="mb-3">
                                <label class="form-label">Bạn xếp hạng mấy sao? <span class="text-danger">*</span></label>
                                <div class="star-rating">
                                    <input type="radio" id="5-stars" name="rating" value="5" /><label for="5-stars"><i class="bi bi-star-fill"></i></label>
                                    <input type="radio" id="4-stars" name="rating" value="4" /><label for="4-stars"><i class="bi bi-star-fill"></i></label>
                                    <input type="radio" id="3-stars" name="rating" value="3" /><label for="3-stars"><i class="bi bi-star-fill"></i></label>
                                    <input type="radio" id="2-stars" name="rating" value="2" /><label for="2-stars"><i class="bi bi-star-fill"></i></label>
                                    <input type="radio" id="1-star" name="rating" value="1" required /><label for="1-star"><i class="bi bi-star-fill"></i></label>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="comment" class="form-label">Bình luận của bạn</label>
                                <textarea class="form-control" id="comment" name="comment" rows="3" placeholder="Sản phẩm này rất tuyệt..."></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
                        </form>
                    </div>
                </div>
            </c:if>
             <c:if test="${not canReview && not empty sessionScope.currentUser && empty reviews}">
                <div class="alert alert-info">
                   Bạn cần mua sản phẩm này và hoàn thành đơn hàng trước khi có thể đánh giá.
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty reviews}">
                    <div class="alert alert-secondary">Chưa có đánh giá nào cho sản phẩm này.</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="r" items="${reviews}">
                        <div class="d-flex mb-3">
                            <div class="flex-shrink-0">
                                <c:set var="avatarSrc">
                                    <c:choose>
                                        <c:when test="${not empty r.user.avatar}">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(r.user.avatar, 'http')}">
                                                    ${r.user.avatar}
                                                </c:when>
                                                <c:when test="${fn:startsWith(r.user.avatar, 'uploads/')}">
                                                    ${pageContext.request.contextPath}/${r.user.avatar}
                                                </c:when>
                                                <c:otherwise>
                                                    ${pageContext.request.contextPath}/uploads/${r.user.avatar}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            https://via.placeholder.com/60/006633/FFFFFF?text=${fn:substring(r.user.username, 0, 1)}
                                        </c:otherwise>
                                    </c:choose>
                                </c:set>
                                <img src="${avatarSrc}" 
                                     alt="${r.user.username}" 
                                     class="rounded-circle" 
                                     style="width: 60px; height: 60px; object-fit: cover; border: 2px solid #dee2e6;"
                                     onerror="this.onerror=null; this.src='data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'60\' height=\'60\' viewBox=\'0 0 60 60\'%3E%3Ccircle cx=\'30\' cy=\'30\' r=\'30\' fill=\'%23006633\'/%3E%3Ctext x=\'50%25\' y=\'50%25\' dominant-baseline=\'central\' text-anchor=\'middle\' fill=\'white\' font-size=\'24\' font-weight=\'bold\'%3E${fn:substring(r.user.username, 0, 1)}%3C/text%3E%3C/svg%3E';">
                            </div>
                            <div class="ms-3 flex-grow-1">
                                <h6 class="fw-bold mb-0">${r.user.fullname != null ? r.user.fullname : r.user.username}</h6>
                                <div class="avg-rating mb-1">
                                    <c:forEach begin="1" end="5" var="i">
                                       <span class="star ${i <= r.rating ? 'filled' : ''}"><i class="bi bi-star-fill"></i></span>
                                    </c:forEach>
                                </div>
                                <p class="mb-1">${r.comment}</p>
                                <small class="text-muted">
                                    <fmt:formatDate value="${r.createdDateAsDate}" pattern="dd/MM/yyyy 'lúc' HH:mm"/>
                                </small>
                            </div>
                        </div>
                        <hr class="my-3">
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            
        </div>
    </div>
    <div class="mt-5">
        <h4 class="mb-3">Sản phẩm liên quan</h4>
        <div class="row row-cols-1 row-cols-md-4 g-4">
            
            <c:forEach var="relatedProd" items="${relatedProducts}">
                <c:set var="p" value="${relatedProd}" scope="request"/>
                <div class="col">
                    <jsp:include page="/views/_partials/product_card.jsp" />
                </div>
            </c:forEach>
            
        </div>
    </div>
</div>