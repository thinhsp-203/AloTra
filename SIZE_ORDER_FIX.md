# ✅ FIX: Thứ Tự Hiển Thị Size - S -> M -> L

## 📋 ROOT CAUSE ANALYSIS

### **Vấn đề:**
Trong modal "Tùy chỉnh sản phẩm", các nút size đang hiển thị sai thứ tự: **L -> M -> S** (sai).
Mục tiêu: luôn hiển thị theo thứ tự chuẩn: **S -> M -> L** (đúng).

### **Root Cause:**
**File 1:** `src/main/java/service/impl/ProductQueryServiceImpl.java` (dòng 54)
**File 2:** `src/main/java/dao/impl/ProductSizeDaoImpl.java` (dòng 40)

**Vấn đề chính:**
- Query JPQL đang dùng `ORDER BY ps.size_name` → Sort theo **alphabet**
- Alphabet: **L < M < S** → Kết quả: **L, M, S** (SAI)
- Cần sort theo thứ tự logic: **S -> M -> L** (ĐÚNG)

### **Code cũ (SAI):**
```java
// ProductQueryServiceImpl.java - Line 54
TypedQuery<ProductSize> query = em.createQuery(
    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid ORDER BY ps.size_name",
    ProductSize.class);
```
```java
// ProductSizeDaoImpl.java - Line 40
TypedQuery<ProductSize> query = em.createQuery(
    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid ORDER BY ps.size_name",
    ProductSize.class);
```

**Kết quả:** 
- DB trả về: `[L, M, S]` (theo alphabet)
- Frontend render: **L -> M -> S** ❌

---

## ✅ GIẢI PHÁP

### **Files đã sửa:**
1. `src/main/java/service/impl/ProductQueryServiceImpl.java` (dòng 53-60)
2. `src/main/java/dao/impl/ProductSizeDaoImpl.java` (dòng 39-47)

### **Thay đổi:**
- **BỎ:** `ORDER BY ps.size_name` (sort theo alphabet)
- **THAY BẰNG:** `ORDER BY CASE ps.size_name ...` (sort theo thứ tự logic)

### **Code mới (ĐÚNG):**
```java
// ProductQueryServiceImpl.java - Line 53-60
TypedQuery<ProductSize> query = em.createQuery(
    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid " +
    "ORDER BY CASE ps.size_name " +
    "    WHEN 'S' THEN 1 " +
    "    WHEN 'M' THEN 2 " +
    "    WHEN 'L' THEN 3 " +
    "    ELSE 99 " +
    "END",
    ProductSize.class);
```

```java
// ProductSizeDaoImpl.java - Line 39-47
TypedQuery<ProductSize> query = em.createQuery(
    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid " +
    "ORDER BY CASE ps.size_name " +
    "    WHEN 'S' THEN 1 " +
    "    WHEN 'M' THEN 2 " +
    "    WHEN 'L' THEN 3 " +
    "    ELSE 99 " +
    "END",
    ProductSize.class);
```

**Kết quả:** 
- DB trả về: `[S, M, L]` (theo logic)
- Frontend render: **S -> M -> L** ✅

---

## 🔍 JPQL QUERY SAU KHI SỬA

### **Query JPQL:**
```sql
SELECT ps 
FROM ProductSize ps 
WHERE ps.product.product_id = :pid 
ORDER BY CASE ps.size_name 
    WHEN 'S' THEN 1 
    WHEN 'M' THEN 2 
    WHEN 'L' THEN 3 
    ELSE 99 
END
```

### **Giải thích:**
- `CASE ps.size_name WHEN 'S' THEN 1`: Size S → order value = 1 (đầu tiên)
- `WHEN 'M' THEN 2`: Size M → order value = 2 (giữa)
- `WHEN 'L' THEN 3`: Size L → order value = 3 (cuối)
- `ELSE 99`: Các size khác (XL, XXL...) → order value = 99 (sau cùng)

**Thứ tự kết quả:**
1. S (1)
2. M (2)
3. L (3)
4. Các size khác (99)

---

## 📄 DATA FLOW

### **1. Backend → API:**
```
ProductModalApiController.doGet()
  → productQueryService.getSizes(productId)
    → ProductQueryServiceImpl.getSizes()
      → JPQL Query với CASE WHEN
        → DB trả về: [S, M, L] ✅
```

### **2. API → Frontend:**
```javascript
// ProductModalApiController.java - Line 39, 47-49
List<ProductSize> sizes = productQueryService.getSizes(productId);
String sizesJson = sizes.stream()
    .map(s -> String.format("{\"name\":\"%s\", \"priceAdjustment\":%s}", ...))
    .collect(Collectors.joining(","));
// JSON: [{"name":"S","priceAdjustment":0}, {"name":"M","priceAdjustment":5000}, {"name":"L","priceAdjustment":10000}]
```

### **3. Frontend → UI:**
```javascript
// app.js - Line 98-107
const sizesHtml = data.sizes.map((s, index) => `
    <div class="col-auto">
        <input type="radio" ... value="${escapeHtml(s.name)}">
        <label>${escapeHtml(s.name)}</label>
    </div>
`).join('');
// Render: S -> M -> L ✅
```

---

## 🧪 HƯỚNG DẪN TEST

### **Test Case 1: Modal "Tùy chỉnh sản phẩm"**
1. Mở trang web
2. Click vào sản phẩm thức uống (ví dụ: Trà sữa)
3. Modal "Tùy chỉnh sản phẩm" hiện ra
4. **Kiểm tra:**
   - ✅ Nút size đầu tiên: **S** (+0 đ)
   - ✅ Nút size thứ hai: **M** (+5.000 đ)
   - ✅ Nút size thứ ba: **L** (+10.000 đ)
   - ✅ **Thứ tự hiển thị: S -> M -> L** (KHÔNG phải L -> M -> S)

### **Test Case 2: Product Detail Page**
1. Mở trang chi tiết sản phẩm thức uống
2. Scroll xuống phần "Chọn kích cỡ"
3. **Kiểm tra:**
   - ✅ Thứ tự hiển thị: **S -> M -> L**
   - ✅ Nút S được checked mặc định (nếu price = 0)

### **Test Case 3: Nhiều sản phẩm khác nhau**
1. Mở modal cho sản phẩm thức uống A
2. Verify: **S -> M -> L** ✅
3. Đóng modal, mở modal cho sản phẩm thức uống B
4. Verify: **S -> M -> L** ✅
5. Lặp lại với 3-4 sản phẩm khác nhau
6. **Kiểm tra:**
   - ✅ Mọi sản phẩm thức uống đều hiển thị **S -> M -> L**
   - ✅ Thứ tự ổn định, không thay đổi

### **Test Case 4: Sản phẩm có size tùy chỉnh (không phải S/M/L)**
1. Tạo/mở sản phẩm có size: S, M, L, XL
2. **Kiểm tra:**
   - ✅ Thứ tự: **S -> M -> L -> XL** (XL sau L vì ELSE 99)
   - ✅ Vẫn đúng thứ tự logic

---

## ✅ KẾT QUẢ

1. ✅ **Thứ tự hiển thị đúng:** S -> M -> L (KHÔNG phải L -> M -> S)
2. ✅ **Fix đúng root cause:** Sửa query DB/JPA, không hard-code ở JSP/JS
3. ✅ **Ổn định:** Mọi sản phẩm thức uống đều hiển thị đúng thứ tự
4. ✅ **Không ảnh hưởng:** Các phần khác (Độ ngọt, Mức đá, Topping) không bị ảnh hưởng

---

## 📝 NOTES

- **JPQL CASE WHEN:** Syntax `CASE column WHEN value THEN order ELSE default END` được hỗ trợ bởi Hibernate/JPA
- **SQLServer compatibility:** Query này tương thích với SQLServer (CASE WHEN cũng có trong SQL Server)
- **Future sizes:** Nếu thêm size mới (XL, XXL), chúng sẽ hiển thị sau L (vì ELSE 99)
- **Performance:** CASE WHEN trong ORDER BY không ảnh hưởng đáng kể đến performance (size list thường < 5 items)

---

**Status:** ✅ Hoàn thành  
**Date:** 2026-01-04  
**Files changed:** 
- `src/main/java/service/impl/ProductQueryServiceImpl.java`
- `src/main/java/dao/impl/ProductSizeDaoImpl.java`

