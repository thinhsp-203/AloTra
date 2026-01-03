# ✅ COMPLETE PATCH - Loại Bỏ Hard-code Category Logic

## 📋 TÓM TẮT

Đã hoàn thành việc loại bỏ **TẤT CẢ** hard-code logic phân loại category dựa vào tên, thay bằng field `isDrink` rõ ràng trong database.

---

## 🎯 ROOT CAUSE ĐÃ XÁC ĐỊNH

### **1. navbar.jsp (dòng 287-298)**
- **Vấn đề:** Hard-code check category name contains "trà", "cà phê", "sinh tố", "nước", "bánh", "ăn vặt", "snack"
- **File:** `src/main/webapp/views/_partials/navbar.jsp`

### **2. ProductQueryServiceImpl.getAvailableToppingsForCategory (dòng 108)**
- **Vấn đề:** Hard-code check `categoryName.toLowerCase().contains("trà")`
- **File:** `src/main/java/service/impl/ProductQueryServiceImpl.java`

### **3. Admin Forms**
- **Vấn đề:** Không có input field để chọn loại category
- **Files:** `add-category.jsp`, `edit-category.jsp`

### **4. Controllers**
- **Vấn đề:** Không xử lý field `isDrink` khi create/update
- **Files:** `CategoryAddController.java`, `CategoryEditController.java`

---

## ✅ FILES ĐÃ SỬA (6 files)

### **1. src/main/webapp/views/admin/add-category.jsp**
**Thay đổi:** Thêm radio buttons chọn loại category (Thức uống / Đồ ăn)

### **2. src/main/webapp/views/admin/edit-category.jsp**
**Thay đổi:** Thêm radio buttons với giá trị từ DB (`${category.isDrink}`)

### **3. src/main/java/controller/category/CategoryAddController.java**
**Thay đổi:**
- Lấy parameter `isDrink` từ request
- Set vào Category object: `c.setIsDrink(isDrink)`

### **4. src/main/java/controller/category/CategoryEditController.java**
**Thay đổi:**
- Lấy parameter `isDrink` từ request
- Set vào Category object: `c.setIsDrink(isDrink)`

### **5. src/main/webapp/views/_partials/navbar.jsp**
**Thay đổi:**
- **BỎ:** Hard-code check `fn:contains(lowerName, 'trà') || ...`
- **THAY BẰNG:** `cat.isDrink` và `not cat.isDrink`

### **6. src/main/java/service/impl/ProductQueryServiceImpl.java**
**Thay đổi:**
- **BỎ:** Hard-code check `categoryName.toLowerCase().contains("trà")`
- **THAY BẰNG:** Query Category từ DB, check `category.getIsDrink()`

---

## 📄 CHI TIẾT CODE THAY ĐỔI

### **File 1: add-category.jsp**

**Vị trí:** Sau dòng 62 (sau field "Tên danh mục"), trước field "Ảnh đại diện"

**Code thêm vào:**
```jsp
<div class="mb-4">
    <label class="form-label fw-semibold mb-2">
        <i class="fas fa-list text-primary" style="margin-right: 10px;"></i>Loại danh mục <span class="text-danger">*</span>
    </label>
    <div class="form-check mb-2">
        <input class="form-check-input" 
               type="radio" 
               name="isDrink" 
               id="isDrink_true" 
               value="true" 
               checked
               required>
        <label class="form-check-label" for="isDrink_true">
            <i class="bi bi-cup-straw text-primary"></i> Thức uống
        </label>
        <div class="form-text ms-4">Sản phẩm thuộc danh mục này sẽ có size (S/M/L)</div>
    </div>
    <div class="form-check">
        <input class="form-check-input" 
               type="radio" 
               name="isDrink" 
               id="isDrink_false" 
               value="false"
               required>
        <label class="form-check-label" for="isDrink_false">
            <i class="bi bi-cake2 text-warning"></i> Bánh & Đồ ăn vặt
        </label>
        <div class="form-text ms-4">Sản phẩm thuộc danh mục này không có size</div>
    </div>
</div>
```

---

### **File 2: edit-category.jsp**

**Vị trí:** Sau dòng 63 (sau field "Tên danh mục"), trước field "Ảnh đại diện"

**Code thêm vào:**
```jsp
<div class="mb-4">
    <label class="form-label fw-semibold mb-2">
        <i class="fas fa-list text-primary" style="margin-right: 10px;"></i>Loại danh mục <span class="text-danger">*</span>
    </label>
    <div class="form-check mb-2">
        <input class="form-check-input" 
               type="radio" 
               name="isDrink" 
               id="isDrink_true" 
               value="true" 
               ${category.isDrink ? 'checked' : ''}
               required>
        <label class="form-check-label" for="isDrink_true">
            <i class="bi bi-cup-straw text-primary"></i> Thức uống
        </label>
        <div class="form-text ms-4">Sản phẩm thuộc danh mục này sẽ có size (S/M/L)</div>
    </div>
    <div class="form-check">
        <input class="form-check-input" 
               type="radio" 
               name="isDrink" 
               id="isDrink_false" 
               value="false"
               ${not category.isDrink ? 'checked' : ''}
               required>
        <label class="form-check-label" for="isDrink_false">
            <i class="bi bi-cake2 text-warning"></i> Bánh & Đồ ăn vặt
        </label>
        <div class="form-text ms-4">Sản phẩm thuộc danh mục này không có size</div>
    </div>
</div>
```

---

### **File 3: CategoryAddController.java**

**Thay đổi 1:** Thêm dòng lấy parameter (sau dòng 30):
```java
String isDrinkParam = req.getParameter("isDrink");
Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);
```

**Thay đổi 2:** Thêm dòng set vào Category (sau dòng 60, trước `service.insert(c)`):
```java
c.setIsDrink(isDrink);
```

---

### **File 4: CategoryEditController.java**

**Thay đổi 1:** Thêm dòng lấy parameter (sau dòng 41):
```java
String isDrinkParam = req.getParameter("isDrink");
Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);
```

**Thay đổi 2:** Thêm dòng set vào Category (sau dòng 84, trước `service.edit(c)`):
```java
c.setIsDrink(isDrink);
```

---

### **File 5: navbar.jsp**

**Vị trí:** Dòng 283-304

**Thay đổi HOÀN TOÀN:**

**TRƯỚC:**
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

**SAU:**
```jsp
<h6 class="dropdown-header"><i class="bi bi-cup-straw"></i> Thức uống</h6>
<c:forEach var="cat" items="${navbarCategories}">
    <c:if test="${cat.isDrink}">
        <a class="dropdown-item" href="${pageContext.request.contextPath}/products?cate=${cat.id}">
            ${cat.name}
        </a>
    </c:if>
</c:forEach>
```

**Tương tự cho phần "Bánh & Đồ ăn vặt":**

**TRƯỚC:**
```jsp
<c:if test="${fn:contains(lowerName, 'bánh') || fn:contains(lowerName, 'ăn vặt') || fn:contains(lowerName, 'snack')}">
```

**SAU:**
```jsp
<c:if test="${not cat.isDrink}">
```

---

### **File 6: ProductQueryServiceImpl.java**

**Method:** `getAvailableToppingsForCategory(String categoryName)`

**Thay đổi HOÀN TOÀN:**

**TRƯỚC:**
```java
if (!categoryName.toLowerCase().contains("trà")) {
    return List.of();
}
EntityManager em = JpaUtil.em();
try {
    return em.createQuery("SELECT t FROM Topping t WHERE t.isAvailable = true ORDER BY t.topping_name", Topping.class)
             .getResultList();
} finally {
    em.close();
}
```

**SAU:**
```java
EntityManager em = JpaUtil.em();
try {
    // Lấy category từ name để check isDrink (thay vì hard-code check "trà")
    TypedQuery<Category> catQuery = em.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name", 
            Category.class);
    catQuery.setParameter("name", categoryName);
    List<Category> categories = catQuery.getResultList();
    
    // Chỉ trả về toppings nếu là thức uống (isDrink = true)
    if (categories.isEmpty() || !Boolean.TRUE.equals(categories.get(0).getIsDrink())) {
        return List.of();
    }
    
    return em.createQuery("SELECT t FROM Topping t WHERE t.isAvailable = true ORDER BY t.topping_name", Topping.class)
             .getResultList();
} finally {
    em.close();
}
```

**Thêm import:**
```java
import model.Category;
```

---

## 🗄️ DATABASE MIGRATION

**File:** `database/alter_category_add_isDrink.sql` (đã có từ lần fix trước)

**Script đầy đủ:**
```sql
-- Thêm cột isDrink
ALTER TABLE [dbo].[Category]
ADD [isDrink] [bit] NULL;
GO

-- Set giá trị mặc định = 0 (Đồ ăn)
UPDATE [dbo].[Category]
SET [isDrink] = 0
WHERE [isDrink] IS NULL;
GO

-- Set NOT NULL constraint
ALTER TABLE [dbo].[Category]
ALTER COLUMN [isDrink] [bit] NOT NULL;
GO

-- Set default value = 0 cho các record mới
ALTER TABLE [dbo].[Category]
ADD CONSTRAINT [DF_Category_isDrink] DEFAULT (0) FOR [isDrink];
GO

-- Update các category thức uống (chỉ 1 lần migration, sau đó bỏ hard-code này)
UPDATE [dbo].[Category]
SET [isDrink] = 1
WHERE LOWER([cate_name]) LIKE '%trà%' 
   OR LOWER([cate_name]) LIKE '%cà phê%'
   OR LOWER([cate_name]) LIKE '%sinh tố%'
   OR LOWER([cate_name]) LIKE '%nước%'
   OR LOWER([cate_name]) LIKE '%đồ uống%'
   OR LOWER([cate_name]) LIKE '%thức uống%'
   OR LOWER([cate_name]) LIKE '%smoothie%'
   OR LOWER([cate_name]) LIKE '%juice%';
GO
```

---

## 🧪 HƯỚNG DẪN TEST

### **Bước 1: Chạy Migration**
```sql
-- Chạy file: database/alter_category_add_isDrink.sql
```

### **Bước 2: Test Admin Form - Thêm Category Mới**

1. Đăng nhập admin
2. Vào **Danh mục → Thêm danh mục mới**
3. **Kiểm tra:**
   - ✅ Có 2 radio buttons: "Thức uống" và "Bánh & Đồ ăn vặt"
   - ✅ Mặc định chọn "Thức uống"
   - ✅ Có thể chọn và submit
4. Tạo category mới: Tên = "Test Drink", Loại = "Thức uống"
5. Tạo category mới: Tên = "Test Food", Loại = "Bánh & Đồ ăn vặt"

### **Bước 3: Test Admin Form - Sửa Category**

1. Vào **Danh mục → Danh sách**
2. Click "Sửa" một category thức uống
3. **Kiểm tra:**
   - ✅ Radio button "Thức uống" được checked
4. Click "Sửa" một category đồ ăn
5. **Kiểm tra:**
   - ✅ Radio button "Bánh & Đồ ăn vặt" được checked
6. Thử đổi loại category và save

### **Bước 4: Test Navbar Menu**

1. Vào trang chủ
2. Click menu "MENU" (dropdown)
3. **Kiểm tra:**
   - ✅ Category "Test Drink" hiển thị trong nhóm "Thức uống"
   - ✅ Category "Test Food" hiển thị trong nhóm "Bánh & Đồ ăn vặt"
   - ✅ Tất cả category thức uống (isDrink=1) đều ở nhóm "Thức uống"
   - ✅ Tất cả category đồ ăn (isDrink=0) đều ở nhóm "Bánh & Đồ ăn vặt"

### **Bước 5: Test Product Size & Toppings**

1. Tạo sản phẩm mới thuộc category "Test Drink" (isDrink=1)
2. Mở modal/product detail
3. **Kiểm tra:**
   - ✅ Hiển thị size options (S/M/L)
   - ✅ Hiển thị toppings (nếu có)
4. Tạo sản phẩm mới thuộc category "Test Food" (isDrink=0)
5. Mở modal/product detail
6. **Kiểm tra:**
   - ✅ KHÔNG hiển thị size options
   - ✅ KHÔNG hiển thị toppings

---

## ✅ KẾT QUẢ

1. ✅ **Không còn hard-code** check category name ở bất kỳ đâu
2. ✅ **Admin có thể chọn** loại category khi thêm/sửa
3. ✅ **Navbar hiển thị đúng** dựa vào `isDrink`
4. ✅ **Toppings chỉ áp dụng** cho thức uống (dựa vào `isDrink`)
5. ✅ **Sizes chỉ hiển thị** cho thức uống (đã fix từ trước)
6. ✅ **Backward compatible:** Migration script update data cũ

---

**Status:** ✅ Hoàn thành 100%  
**Date:** 2026-01-04  
**Files changed:** 6 files  
**Migration script:** 1 file


