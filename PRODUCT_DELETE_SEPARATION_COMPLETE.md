# ✅ PRODUCT DELETE SEPARATION COMPLETE

## 📋 YÊU CẦU

Tách rõ 2 hành động:
1. ✅ "Ngừng bán/Ẩn" (soft delete): set `isActive = false`
2. ✅ "Xóa" (hard delete): xóa thật record Product

**Ràng buộc:**
- ✅ Nếu Product đã có OrderDetail → KHÔNG cho phép xóa thật
- ✅ Nếu Product chưa có OrderDetail → Cho phép xóa thật + xóa các bảng liên quan

---

## ✅ ROOT CAUSE

**File:** `src/main/java/service/impl/AdminProductServiceImpl.java`
**Method:** `deleteProduct()` (line 119-132)

**Code hiện tại:**
```java
@Override
public void deleteProduct(int id, jakarta.servlet.ServletContext servletContext) {
    Product p = productDao.findById(id);
    if (p == null) {
        throw new IllegalArgumentException("Sản phẩm không tồn tại!");
    }
    
    // Soft delete
    p.setIsActive(false);
    p.setUpdatedDate(LocalDateTime.now());
    productDao.update(p);
    
    // (Tùy chọn) Xóa file ảnh
    UploadUtil.deleteOldImage(p.getThumbnail(), servletContext);
}
```

**Vấn đề:** Method `deleteProduct()` chỉ thực hiện soft delete (set `isActive = false`), không xóa thật.

---

## ✅ PHÂN TÍCH SCHEMA & FK CONSTRAINTS

### **Các bảng liên quan đến Product:**

| Bảng | FK Column | CASCADE? | Xử lý khi hard delete |
|------|-----------|----------|----------------------|
| **OrderDetail** | `product_id` | ❌ NO | **KHÔNG xóa** - Check trước, nếu có order → throw exception |
| **ProductSize** | `product_id` | ✅ YES | Xóa thủ công (có CASCADE nhưng xóa trước để chắc chắn) |
| **Review** | `product_id` | ❌ NO | Xóa thủ công |
| **ViewHistory** | `product_id` | ❌ NO | Xóa thủ công (nếu bảng tồn tại) |
| **WishlistItem** | `product_id` | ❌ NO | Xóa thủ công |
| **Cart** | `product_id` | ❌ NO | Không cần xóa (session-based, sẽ tự cleanup) |

**Quan trọng:** `OrderDetail` KHÔNG có CASCADE và phải giữ lại để bảo toàn lịch sử đơn hàng.

---

## ✅ GIẢI PHÁP

### **1. Service Interface**

**File:** `src/main/java/service/AdminProductService.java`

**Thay đổi:**
- Rename comment: `deleteProduct()` → "Xóa sản phẩm vĩnh viễn"
- Thêm method mới: `disableProduct()` - "Ngừng bán sản phẩm (Soft delete)"

### **2. Service Implementation**

**File:** `src/main/java/service/impl/AdminProductServiceImpl.java`

**Methods:**

1. **`disableProduct()`** - Soft delete (rename từ `deleteProduct` cũ):
   ```java
   - Set isActive = false
   - Update updatedDate
   - KHÔNG xóa file ảnh (giữ lại để có thể khôi phục)
   ```

2. **`deleteProduct()`** - Hard delete (mới):
   ```java
   - Check product exists
   - Check OrderDetail count (nếu > 0 → throw exception)
   - Nếu OK:
     * Delete Review
     * Delete ViewHistory (nếu có)
     * Delete WishlistItem
     * Delete ProductSize (có CASCADE nhưng xóa thủ công)
     * Delete image file
     * Delete Product
   - Dùng Transaction để đảm bảo atomicity
   ```

### **3. Controller**

**File:** `src/main/java/controller/admin/AdminProductController.java`

**Endpoints:**
- POST `/admin/products/disable` → `disableProduct()` (soft delete)
- POST `/admin/products/delete` → `deleteProduct()` (hard delete)

### **4. JSP UI**

**File:** `src/main/webapp/views/admin/products.jsp`

**Thay đổi:**
- **Nút "Ngừng bán"** (icon `fa-ban`, màu `btn-outline-warning`):
  - Chỉ hiển thị khi `isActive = true`
  - Endpoint: `/admin/products/disable`
  - Confirm: "Xác nhận ngừng bán sản phẩm?"
  
- **Nút "Xóa"** (icon `fa-trash`, màu `btn-outline-danger`):
  - Hiển thị cho tất cả sản phẩm
  - Endpoint: `/admin/products/delete`
  - Confirm: "CẢNH BÁO: Xóa sản phẩm vĩnh viễn? Lưu ý: Nếu sản phẩm đã có đơn hàng, hệ thống sẽ từ chối..."

- **Badge "Đã ngừng bán"**:
  - Hiển thị khi `isActive = false`
  - Thay thế nút "Ngừng bán"

---

## 📄 FILES CHANGED

### **Modified Files:**
1. `src/main/java/service/AdminProductService.java` - Thêm method `disableProduct()`
2. `src/main/java/service/impl/AdminProductServiceImpl.java` - Implement `disableProduct()` và refactor `deleteProduct()`
3. `src/main/java/controller/admin/AdminProductController.java` - Thêm endpoint `/admin/products/disable` và method `disableProduct()`
4. `src/main/webapp/views/admin/products.jsp` - Tách 2 nút "Ngừng bán" và "Xóa"

---

## 🔒 SECURITY & BUSINESS LOGIC

### **Hard Delete Logic:**
```java
1. Check product exists
2. Check OrderDetail count:
   SELECT COUNT(od) FROM OrderDetail od 
   WHERE od.product.product_id = :productId
   
3. If count > 0:
   → Throw IllegalArgumentException("Sản phẩm đã có đơn hàng, không thể xóa vĩnh viễn. Vui lòng dùng chức năng 'Ngừng bán'.")
   
4. If count = 0:
   → Delete child tables (Review, ViewHistory, WishlistItem, ProductSize)
   → Delete image file
   → Delete Product
```

### **Transaction:**
- Tất cả operations trong 1 transaction
- Rollback nếu có lỗi
- Đảm bảo atomicity (all or nothing)

---

## 🎯 KẾT QUẢ

### **Functionality:**
1. ✅ **Ngừng bán (Soft):**
   - Set `isActive = false`
   - Sản phẩm vẫn còn trong DB
   - Có thể khôi phục bằng cách set `isActive = true`

2. ✅ **Xóa (Hard):**
   - Xóa thật record Product
   - Xóa các bảng liên quan (Review, WishlistItem, ProductSize, ViewHistory)
   - Xóa file ảnh
   - **Bảo vệ:** Không cho xóa nếu đã có OrderDetail

### **UI/UX:**
- ✅ Nút "Ngừng bán" (màu vàng) - chỉ hiển thị khi `isActive = true`
- ✅ Nút "Xóa" (màu đỏ) - hiển thị cho tất cả
- ✅ Badge "Đã ngừng bán" - hiển thị khi `isActive = false`
- ✅ Confirm dialog rõ ràng cho cả 2 hành động

---

## 🧪 HƯỚNG DẪN TEST

### **Test Case 1: Ngừng bán sản phẩm (Soft Delete)**
1. Vào Admin → Quản lý Sản phẩm
2. Tìm sản phẩm có `isActive = true`
3. Click nút "Ngừng bán" (icon ban, màu vàng)
4. **Kiểm tra:**
   - ✅ Hiển thị confirm: "Xác nhận ngừng bán sản phẩm?"
   - ✅ Click OK → Sản phẩm biến mất khỏi danh sách (hoặc hiển thị với badge "Đã ngừng bán")
   - ✅ DB: `isActive = false`
   - ✅ Record vẫn còn trong DB
   - ✅ Ảnh vẫn còn

### **Test Case 2: Xóa sản phẩm chưa có đơn hàng (Hard Delete)**
1. Tạo sản phẩm mới (chưa có đơn hàng)
2. Vào Admin → Quản lý Sản phẩm
3. Click nút "Xóa" (icon trash, màu đỏ)
4. **Kiểm tra:**
   - ✅ Hiển thị confirm: "CẢNH BÁO: Xóa sản phẩm vĩnh viễn?..."
   - ✅ Click OK → Sản phẩm biến mất khỏi danh sách
   - ✅ DB: Record Product đã bị xóa
   - ✅ DB: Review, WishlistItem, ProductSize của sản phẩm đã bị xóa
   - ✅ File ảnh đã bị xóa

### **Test Case 3: Xóa sản phẩm đã có đơn hàng (Bị chặn)**
1. Tạo đơn hàng với sản phẩm X
2. Vào Admin → Quản lý Sản phẩm
3. Tìm sản phẩm X
4. Click nút "Xóa"
5. **Kiểm tra:**
   - ✅ Hiển thị confirm
   - ✅ Click OK → Hiển thị error: "Sản phẩm đã có đơn hàng, không thể xóa vĩnh viễn. Vui lòng dùng chức năng 'Ngừng bán'."
   - ✅ Sản phẩm vẫn còn trong DB
   - ✅ OrderDetail vẫn còn (không bị ảnh hưởng)

### **Test Case 4: Ngừng bán sản phẩm đã có đơn hàng (OK)**
1. Sản phẩm X đã có đơn hàng
2. Click nút "Ngừng bán"
3. **Kiểm tra:**
   - ✅ Thành công (không bị chặn)
   - ✅ DB: `isActive = false`
   - ✅ OrderDetail vẫn còn (không bị ảnh hưởng)

### **Test Case 5: UI - Badge "Đã ngừng bán"**
1. Vào Admin → Quản lý Sản phẩm
2. Tìm sản phẩm có `isActive = false`
3. **Kiểm tra:**
   - ✅ Hiển thị badge "Đã ngừng bán" (màu xám) thay vì nút "Ngừng bán"
   - ✅ Vẫn có nút "Xóa" (màu đỏ)
   - ✅ Nếu sản phẩm đã có order, nút "Xóa" vẫn bị chặn

---

## 📝 QUERY CHECK ORDER

**Query kiểm tra sản phẩm đã có đơn hàng:**
```java
SELECT COUNT(od) FROM OrderDetail od 
WHERE od.product.product_id = :productId
```

**Nếu count > 0:** Không cho phép xóa
**Nếu count = 0:** Cho phép xóa

---

**Status:** ✅ Hoàn thành  
**Date:** 2026-01-04  
**Total files changed:** 4 files

