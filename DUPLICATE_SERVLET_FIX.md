# FIX: Duplicate Servlet URL Pattern Conflict

## 🔴 LỖI

```
java.lang.IllegalArgumentException: The servlets named 
[stnw.controller.admin.AdminUserListController] and 
[stnw.controller.admin.UserListController] are both mapped to 
the url-pattern [/admin/users] which is not permitted
```

## ✅ GIẢI PHÁP

### Vấn đề
Có 2 servlets cùng map đến URL pattern `/admin/users`:
1. `AdminUserListController.java` - ✅ GIỮ LẠI
2. `UserListController.java` - ❌ ĐÃ XÓA

### Lý do chọn giữ `AdminUserListController`
- ✅ Tên rõ ràng hơn (có prefix "Admin")
- ✅ Phù hợp với naming convention của các controller khác trong package admin:
  - `AdminProductController`
  - `AdminOrderController`
  - `AdminCategoryController`
  - etc.
- ✅ Code giống hệt nhau (100% duplicate)

### File đã xóa
- `src/main/java/stnw/controller/admin/UserListController.java`

### File giữ lại
- `src/main/java/stnw/controller/admin/AdminUserListController.java`
  - URL Pattern: `/admin/users`
  - Forward: `/views/admin/user-list.jsp`

---

## ✅ XÁC NHẬN

- ✅ Không có reference đến `UserListController` trong codebase
- ✅ `AdminUserListController` hoạt động độc lập
- ✅ Không ảnh hưởng đến functionality
- ✅ Server sẽ start được sau khi xóa duplicate

---

---

## 🔴 LỖI THỨ 2

```
The servlets named [stnw.controller.product.ProductListApiController] and 
[stnw.controller.product.ProductPageController] are both mapped to 
the url-pattern [/api/products] which is not permitted
```

### Vấn đề
Có 2 servlets cùng map đến URL pattern `/api/products`:
1. `ProductListApiController.java` - ✅ GIỮ LẠI
2. `ProductPageController.java` - ❌ ĐÃ XÓA

### Lý do chọn giữ `ProductListApiController`
- ✅ Tên rõ ràng hơn (có "Api" trong tên, phù hợp với URL `/api/products`)
- ✅ Phù hợp với naming convention của API controllers
- ✅ Code giống hệt nhau (100% duplicate)

### File đã xóa
- `src/main/java/stnw/controller/product/ProductPageController.java`

### File giữ lại
- `src/main/java/stnw/controller/product/ProductListApiController.java`
  - URL Pattern: `/api/products`
  - Response: JSON API endpoint

---

## 🔴 LỖI THỨ 3

```
The servlets named [stnw.controller.product.ProductsListController] and 
[stnw.controller.product.ProductListController] are both mapped to 
the url-pattern [/products] which is not permitted
```

### Vấn đề
Có 2 servlets cùng map đến URL pattern `/products`:
1. `ProductListController.java` - ✅ GIỮ LẠI
2. `ProductsListController.java` - ❌ ĐÃ XÓA

### Lý do chọn giữ `ProductListController`
- ✅ Tên ngắn gọn hơn (không có "s" thừa)
- ✅ Phù hợp với naming convention (ProductListController)
- ✅ Code giống hệt nhau (100% duplicate)

### File đã xóa
- `src/main/java/stnw/controller/product/ProductsListController.java`

### File giữ lại
- `src/main/java/stnw/controller/product/ProductListController.java`
  - URL Pattern: `/products`
  - Forward: `/views/product/list.jsp`

---

## 🔴 LỖI THỨ 4

```
The servlets named [stnw.controller.web.ImageDownloadController] and 
[stnw.controller.web.DownloadImageController] are both mapped to 
the url-pattern [/uploads/*] which is not permitted
```

### Vấn đề
Có 2 servlets cùng map đến URL pattern `/uploads/*`:
1. `ImageDownloadController.java` - ✅ GIỮ LẠI
2. `DownloadImageController.java` - ❌ ĐÃ XÓA

### Lý do chọn giữ `ImageDownloadController`
- ✅ Tên rõ ràng hơn (có "Image" trong tên)
- ✅ Phù hợp với chức năng (download image)
- ✅ Code giống hệt nhau (100% duplicate)

### File đã xóa
- `src/main/java/stnw/controller/web/DownloadImageController.java`

### File giữ lại
- `src/main/java/stnw/controller/web/ImageDownloadController.java`
  - URL Pattern: `/uploads/*`
  - Function: Download image files

---

## 📊 TỔNG KẾT

### Các lỗi đã sửa:
1. ✅ `/admin/users` - Xóa `UserListController.java`
2. ✅ `/api/products` - Xóa `ProductPageController.java`
3. ✅ `/products` - Xóa `ProductsListController.java`
4. ✅ `/uploads/*` - Xóa `DownloadImageController.java`

### Files đã xóa: 4 files

---

## ✅ KIỂM TRA CUỐI CÙNG

Đã chạy script kiểm tra toàn bộ URL patterns:
- ✅ **Không còn duplicate URL pattern nào**
- ✅ **Tất cả 80+ URL patterns đều unique**
- ✅ **Không có lỗi linter**
- ✅ **Server sẽ start được sau khi rebuild**

### Kết quả kiểm tra:
```
=== DUPLICATE URL PATTERNS ===
✅ No duplicate URL patterns found!
```

---

**Ngày fix:** 2025-01-27

