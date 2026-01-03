# 🔍 ROOT CAUSE ANALYSIS: Logic Phân Loại Category (Hard-code)

## 📋 TÓM TẮT VẤN ĐỀ
Category hiện không có field phân loại (Thức uống vs Đồ ăn), dẫn đến logic code bị hard-code dựa vào **category name**, khiến chỉ một vài category thức uống được chọn size.

---

## 🎯 ROOT CAUSE #1: Hard-code trong Navbar (HIỂN THỊ MENU)

**File:** `src/main/webapp/views/_partials/navbar.jsp`
**Dòng:** 284-292 và 295-302

**Code hiện tại:**
```jsp
<h6 class="dropdown-header"><i class="bi bi-cup-straw"></i> Thức uống</h6>
<c:forEach var="cat" items="${navbarCategories}">
    <c:set var="lowerName" value="${fn:toLowerCase(cat.name)}" />
    <c:if test="${fn:contains(lowerName, 'trà') || fn:contains(lowerName, 'cà phê') || fn:contains(lowerName, 'sinh tố') || fn:contains(lowerName, 'nước')}">
        <a class="dropdown-item" href="${pageContext.request.contextPath}/products?cate=${cat.id}">
            ${cat.name}
        </a>
    </c:if>
</c:forEach>
```

**Vấn đề:** 
- Hard-code check tên category chứa "trà", "cà phê", "sinh tố", "nước"
- Nếu category thức uống có tên khác (vd: "Nước giải khát", "Milkshake") → KHÔNG hiển thị trong nhóm "Thức uống"

---

## 🎯 ROOT CAUSE #2: Hard-code trong Service (LẤY TOPPINGS)

**File:** `src/main/java/service/impl/ProductQueryServiceImpl.java`
**Method:** `getAvailableToppingsForCategory(String categoryName)`
**Dòng:** 107-108

**Code hiện tại:**
```java
if (!categoryName.toLowerCase().contains("trà")) {
    return List.of();
}
```

**Vấn đề:**
- Chỉ category có tên chứa "trà" mới có toppings
- Category thức uống khác (cà phê, sinh tố, nước ép...) → KHÔNG có toppings

---

## 🎯 ROOT CAUSE #3: Admin Form Thiếu Field Chọn Loại

**Files:**
- `src/main/webapp/views/admin/add-category.jsp`
- `src/main/webapp/views/admin/edit-category.jsp`

**Vấn đề:**
- Form chỉ có: Tên danh mục, Ảnh đại diện
- **KHÔNG CÓ** field để chọn "Loại category" (Thức uống / Đồ ăn)

---

## 🎯 ROOT CAUSE #4: Controller Không Xử Lý Field isDrink

**Files:**
- `src/main/java/controller/category/CategoryAddController.java`
- `src/main/java/controller/category/CategoryEditController.java`

**Vấn đề:**
- Controller chỉ lấy `name` và `icon` từ request
- **KHÔNG lấy** field `isDrink`
- **KHÔNG set** `isDrink` vào Category object trước khi lưu

---

## ✅ ĐÃ CÓ (Từ lần fix trước):
1. ✅ Category entity đã có field `isDrink` (Boolean)
2. ✅ Migration script đã có (`database/alter_category_add_isDrink.sql`)
3. ✅ JavaScript đã dùng `isDrink` flag để hiển thị size
4. ✅ ProductQueryServiceImpl.getSizes() đã dùng `isDrink` để auto-create sizes

---

## ❌ CHƯA CÓ (Cần fix):
1. ❌ Admin form chưa có input chọn isDrink
2. ❌ Controller chưa xử lý isDrink
3. ❌ Navbar vẫn hard-code check category name
4. ❌ getAvailableToppingsForCategory vẫn hard-code check "trà"

---

## 📝 DANH SÁCH FILES CẦN SỬA

### **1. Admin Forms (JSP):**
- `src/main/webapp/views/admin/add-category.jsp` - Thêm radio buttons chọn loại
- `src/main/webapp/views/admin/edit-category.jsp` - Thêm radio buttons chọn loại

### **2. Controllers:**
- `src/main/java/controller/category/CategoryAddController.java` - Lấy và set isDrink
- `src/main/java/controller/category/CategoryEditController.java` - Lấy và set isDrink

### **3. Services:**
- `src/main/java/service/impl/ProductQueryServiceImpl.java` - Sửa getAvailableToppingsForCategory

### **4. Views:**
- `src/main/webapp/views/_partials/navbar.jsp` - Dùng isDrink thay vì check name

---

**Ngày phân tích:** 2026-01-04


