# 📦 PATCH FILES SUMMARY - Fix Hard-code Category Logic

## 🎯 MỤC TIÊU
Loại bỏ hoàn toàn hard-code logic phân loại category dựa vào tên, thay bằng field `isDrink` rõ ràng.

---

## 📝 ROOT CAUSE TÓM TẮT

### **Hard-code Logic Tìm Thấy:**

1. **navbar.jsp (dòng 287-298):** 
   - Check category name contains "trà", "cà phê", "sinh tố", "nước"
   - Check category name contains "bánh", "ăn vặt", "snack"

2. **ProductQueryServiceImpl.getAvailableToppingsForCategory (dòng 107):**
   - Check `categoryName.toLowerCase().contains("trà")`

3. **Admin Forms:**
   - Không có input field để chọn loại category (Thức uống / Đồ ăn)

4. **Controllers:**
   - Không xử lý field `isDrink` khi create/update category

---

## ✅ FILES ĐÃ SỬA

### **1. Admin Forms (JSP)**
- ✅ `src/main/webapp/views/admin/add-category.jsp`
- ✅ `src/main/webapp/views/admin/edit-category.jsp`

### **2. Controllers**
- ✅ `src/main/java/controller/category/CategoryAddController.java`
- ✅ `src/main/java/controller/category/CategoryEditController.java`

### **3. Services**
- ✅ `src/main/java/service/impl/ProductQueryServiceImpl.java`

### **4. Views**
- ✅ `src/main/webapp/views/_partials/navbar.jsp`

---

## 📄 CHI TIẾT THAY ĐỔI

### **File 1: add-category.jsp**
**Vị trí:** Sau field "Tên danh mục", trước field "Ảnh đại diện"

**Thay đổi:** Thêm radio buttons chọn loại category:
```jsp
<div class="mb-4">
    <label class="form-label fw-semibold mb-2">
        <i class="fas fa-list text-primary"></i>Loại danh mục <span class="text-danger">*</span>
    </label>
    <div class="form-check mb-2">
        <input class="form-check-input" type="radio" name="isDrink" id="isDrink_true" value="true" checked required>
        <label class="form-check-label" for="isDrink_true">
            <i class="bi bi-cup-straw text-primary"></i> Thức uống
        </label>
    </div>
    <div class="form-check">
        <input class="form-check-input" type="radio" name="isDrink" id="isDrink_false" value="false" required>
        <label class="form-check-label" for="isDrink_false">
            <i class="bi bi-cake2 text-warning"></i> Bánh & Đồ ăn vặt
        </label>
    </div>
</div>
```

---

### **File 2: edit-category.jsp**
**Vị trí:** Sau field "Tên danh mục", trước field "Ảnh đại diện"

**Thay đổi:** Thêm radio buttons với giá trị từ DB:
```jsp
<div class="mb-4">
    <label class="form-label fw-semibold mb-2">
        <i class="fas fa-list text-primary"></i>Loại danh mục <span class="text-danger">*</span>
    </label>
    <div class="form-check mb-2">
        <input class="form-check-input" type="radio" name="isDrink" id="isDrink_true" 
               value="true" ${category.isDrink ? 'checked' : ''} required>
        <label class="form-check-label" for="isDrink_true">
            <i class="bi bi-cup-straw text-primary"></i> Thức uống
        </label>
    </div>
    <div class="form-check">
        <input class="form-check-input" type="radio" name="isDrink" id="isDrink_false" 
               value="false" ${not category.isDrink ? 'checked' : ''} required>
        <label class="form-check-label" for="isDrink_false">
            <i class="bi bi-cake2 text-warning"></i> Bánh & Đồ ăn vặt
        </label>
    </div>
</div>
```

---

### **File 3: CategoryAddController.java**
**Thay đổi 1:** Lấy parameter `isDrink` từ request:
```java
String isDrinkParam = req.getParameter("isDrink");
Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);
```

**Thay đổi 2:** Set vào Category object:
```java
c.setIsDrink(isDrink);
```

---

### **File 4: CategoryEditController.java**
**Thay đổi 1:** Lấy parameter `isDrink` từ request:
```java
String isDrinkParam = req.getParameter("isDrink");
Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);
```

**Thay đổi 2:** Set vào Category object:
```java
c.setIsDrink(isDrink);
```

---

### **File 5: navbar.jsp**
**Vị trí:** Dòng 283-304

**Thay đổi:** Bỏ hard-code check name, dùng `isDrink`:
```jsp
<!-- TRƯỚC (Hard-code): -->
<c:set var="lowerName" value="${fn:toLowerCase(cat.name)}" />
<c:if test="${fn:contains(lowerName, 'trà') || fn:contains(lowerName, 'cà phê') || ...}">

<!-- SAU (Dùng isDrink): -->
<c:if test="${cat.isDrink}">
    <!-- Thức uống -->
</c:if>
<c:if test="${not cat.isDrink}">
    <!-- Đồ ăn -->
</c:if>
```

---

### **File 6: ProductQueryServiceImpl.java**
**Method:** `getAvailableToppingsForCategory(String categoryName)`

**Thay đổi:** 
- **TRƯỚC:** Hard-code check `categoryName.toLowerCase().contains("trà")`
- **SAU:** Query Category từ DB, check `category.getIsDrink()`

**Code mới:**
```java
// Lấy category từ name để check isDrink
TypedQuery<Category> catQuery = em.createQuery(
        "SELECT c FROM Category c WHERE c.name = :name", 
        Category.class);
catQuery.setParameter("name", categoryName);
List<Category> categories = catQuery.getResultList();

// Nếu không tìm thấy category hoặc không phải thức uống, không trả về toppings
if (categories.isEmpty() || !Boolean.TRUE.equals(categories.get(0).getIsDrink())) {
    return List.of();
}
```

---

## 🔧 DATABASE MIGRATION

**File:** `database/alter_category_add_isDrink.sql` (đã có từ lần fix trước)

**Nội dung:**
- Thêm cột `isDrink BIT NOT NULL DEFAULT 0`
- Update các category thức uống = 1 (dựa trên tên - chỉ 1 lần migration)
- Set default = 0 cho các record mới

---

## ✅ KẾT QUẢ

1. ✅ **Không còn hard-code** check category name
2. ✅ **Admin có thể chọn** loại category khi thêm/sửa
3. ✅ **Navbar hiển thị đúng** dựa vào `isDrink`
4. ✅ **Toppings chỉ áp dụng** cho thức uống (dựa vào `isDrink`)
5. ✅ **Backward compatible:** Migration script update data cũ

---

**Status:** ✅ Hoàn thành
**Date:** 2026-01-04


