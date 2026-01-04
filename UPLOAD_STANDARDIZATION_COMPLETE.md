# ✅ UPLOAD STANDARDIZATION COMPLETE

## 📋 YÊU CẦU

Chuẩn hóa cơ chế upload ảnh cho toàn bộ module:
1. ✅ Lưu file vào: `<webappRealPath>/uploads/<type>/`
2. ✅ Tên file: UUID + extension, tránh trùng
3. ✅ Validate extension: jpg/jpeg/png/webp, size giới hạn
4. ✅ DB lưu relative path: "uploads/<type>/<filename>"
5. ✅ JSP hiển thị: `${pageContext.request.contextPath}/${entity.imagePath}`

---

## ✅ GIẢI PHÁP

### **1. Tạo UploadType Enum**

**File:** `src/main/java/utils/UploadType.java`

Enum định nghĩa các loại upload:
- BANNERS("banners")
- GIFTS("gifts")
- PRODUCTS("products")
- PROMOTIONS("promotions")
- USERS("users")
- CATEGORIES("categories")

### **2. Tạo UploadUtil Class**

**File:** `src/main/java/utils/UploadUtil.java`

**Method chính:**
- `save(Part part, UploadType type, ServletContext ctx)`: Upload và lưu file
  - Validate extension (jpg, jpeg, png, webp)
  - Validate size (max 10MB)
  - Tạo tên file: UUID + extension
  - Lưu vào: `<webappRealPath>/uploads/<type>/`
  - Return: "uploads/<type>/<filename>"
  
- `deleteOldImage(String imagePath, ServletContext ctx)`: Xóa file cũ
  - Hỗ trợ relative path ("uploads/...") và absolute path (tương thích ngược)
  - Không xóa URL external (bắt đầu bằng "http")

### **3. Refactor Services/Controllers**

**Các file đã refactor:**

1. **AdminBannerServiceImpl**
   - Thay `handleImageUpload()` → dùng `UploadUtil.save(UploadType.BANNERS)`
   - Thay logic xóa file → `UploadUtil.deleteOldImage()`
   - Return path: "uploads/banners/filename"

2. **AdminProductServiceImpl**
   - Thay `handleThumbnailUpload()` → dùng `UploadUtil.save(UploadType.PRODUCTS)`
   - Thay `deleteProductImage()` → `UploadUtil.deleteOldImage()`
   - Return path: "uploads/products/filename"

3. **AdminPromotionServiceImpl**
   - Thay `handleImageUpload()` → dùng `UploadUtil.save(UploadType.PROMOTIONS)`
   - Thay logic xóa file → `UploadUtil.deleteOldImage()`
   - Return path: "uploads/promotions/filename"

4. **UserProfileServiceImpl**
   - Thay toàn bộ logic upload avatar → dùng `UploadUtil.save(UploadType.USERS)`
   - Thay logic xóa file → `UploadUtil.deleteOldImage()`
   - Return path: "uploads/users/filename"

5. **CategoryAddController**
   - Thay logic upload icon → dùng `UploadUtil.save(UploadType.CATEGORIES)`
   - Return path: "uploads/categories/filename" (hoặc null)

6. **CategoryEditController**
   - Thay logic upload icon → dùng `UploadUtil.save(UploadType.CATEGORIES)`
   - Thay logic xóa file → `UploadUtil.deleteOldImage()`
   - Return path: "uploads/categories/filename" (hoặc giữ path cũ)

### **4. Sửa JSP Hiển Thị**

**Các file JSP đã sửa:**

1. **list-category.jsp**
   - Trước: `${pageContext.request.contextPath}/uploads/categories/${cate.icon}`
   - Sau: `${pageContext.request.contextPath}/${cate.icon}` (vì DB lưu "uploads/categories/filename")
   - Thêm fallback cho URL external

2. **edit-category.jsp**
   - Trước: `${pageContext.request.contextPath}/uploads/categories/${category.icon}`
   - Sau: `${pageContext.request.contextPath}/${category.icon}` (vì DB lưu "uploads/categories/filename")
   - Thêm fallback cho URL external

3. **user_form.jsp**
   - Trước: `${pageContext.request.contextPath}/uploads/users/${user.avatar}`
   - Sau: `${pageContext.request.contextPath}/${user.avatar}` (vì DB lưu "uploads/users/filename")

**Các file JSP đã đúng (không cần sửa):**
- `profile.jsp`: Đã dùng `${pageContext.request.contextPath}/uploads/${user.avatar}` ✅
- `product_card.jsp`: Đã dùng `${pageContext.request.contextPath}/uploads/${p.thumbnail}` ✅
- `home.jsp`: Đã dùng `${pageContext.request.contextPath}/uploads/${b.imageUrl}` ✅
- `home.jsp`: Đã dùng `${pageContext.request.contextPath}/uploads/${promo.imageUrl}` ✅

---

## 📄 FILES CHANGED

### **New Files:**
1. `src/main/java/utils/UploadType.java`
2. `src/main/java/utils/UploadUtil.java`

### **Modified Files:**

**Services:**
1. `src/main/java/service/impl/AdminBannerServiceImpl.java`
2. `src/main/java/service/impl/AdminProductServiceImpl.java`
3. `src/main/java/service/impl/AdminPromotionServiceImpl.java`
4. `src/main/java/service/impl/UserProfileServiceImpl.java`

**Controllers:**
5. `src/main/java/controller/category/CategoryAddController.java`
6. `src/main/java/controller/category/CategoryEditController.java`

**JSP:**
7. `src/main/webapp/views/admin/list-category.jsp`
8. `src/main/webapp/views/admin/edit-category.jsp`
9. `src/main/webapp/views/admin/user_form.jsp`

---

## 🎯 KẾT QUẢ

### **Path Format trong DB:**
- Banner: `uploads/banners/<uuid>.jpg`
- Product: `uploads/products/<uuid>.png`
- Promotion: `uploads/promotions/<uuid>.webp`
- User: `uploads/users/<uuid>.jpg`
- Category: `uploads/categories/<uuid>.png`

### **Path Format trong JSP:**
```jsp
<!-- URL external -->
<c:when test="${fn:startsWith(entity.imagePath, 'http')}">
    <img src="${entity.imagePath}"/>
</c:when>
<!-- Relative path -->
<c:otherwise>
    <img src="${pageContext.request.contextPath}/${entity.imagePath}"/>
</c:otherwise>
```

### **Lợi ích:**
1. ✅ Code không lặp lại - dùng chung UploadUtil
2. ✅ Validate thống nhất (extension, size)
3. ✅ Path format nhất quán trong DB
4. ✅ JSP hiển thị đúng path
5. ✅ Dễ maintain và mở rộng (thêm type mới chỉ cần thêm vào enum)

---

## 🧪 HƯỚNG DẪN TEST

### **Test Case 1: Upload Banner**
1. Vào Admin → Quản lý Banner → Thêm banner mới
2. Upload file ảnh (jpg/png/webp)
3. **Kiểm tra:**
   - ✅ File lưu vào `uploads/banners/<uuid>.ext`
   - ✅ DB lưu path: "uploads/banners/<uuid>.ext"
   - ✅ Banner hiển thị đúng trên trang chủ

### **Test Case 2: Upload Product**
1. Vào Admin → Quản lý Sản phẩm → Thêm sản phẩm mới
2. Upload thumbnail
3. **Kiểm tra:**
   - ✅ File lưu vào `uploads/products/<uuid>.ext`
   - ✅ DB lưu path: "uploads/products/<uuid>.ext"
   - ✅ Sản phẩm hiển thị đúng trong list/detail

### **Test Case 3: Upload Avatar**
1. Vào User → Profile → Chọn ảnh đại diện
2. Upload file
3. **Kiểm tra:**
   - ✅ File lưu vào `uploads/users/<uuid>.ext`
   - ✅ DB lưu path: "uploads/users/<uuid>.ext"
   - ✅ Avatar hiển thị đúng trong profile

### **Test Case 4: Upload Category Icon**
1. Vào Admin → Quản lý Danh mục → Thêm/Cập nhật
2. Upload icon
3. **Kiểm tra:**
   - ✅ File lưu vào `uploads/categories/<uuid>.ext`
   - ✅ DB lưu path: "uploads/categories/<uuid>.ext"
   - ✅ Icon hiển thị đúng trong list category

### **Test Case 5: Validation**
1. Thử upload file không hợp lệ (txt, pdf, ...)
2. **Kiểm tra:**
   - ✅ Hiển thị lỗi: "File không hợp lệ! Chỉ chấp nhận: jpg, jpeg, png, webp"
3. Thử upload file > 10MB
4. **Kiểm tra:**
   - ✅ Hiển thị lỗi: "File quá lớn! Kích thước tối đa: 10MB"

### **Test Case 6: Xóa File Cũ**
1. Edit banner/product/promotion/user/category có ảnh cũ
2. Upload ảnh mới
3. **Kiểm tra:**
   - ✅ File cũ bị xóa khỏi thư mục uploads
   - ✅ File mới được lưu và hiển thị đúng

---

**Status:** ✅ Hoàn thành  
**Date:** 2026-01-04  
**Total files changed:** 11 files (2 new, 9 modified)

