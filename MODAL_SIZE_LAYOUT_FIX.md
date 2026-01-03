# ✅ FIX: Modal Size Buttons Layout - S/M/L Không Thẳng Hàng

## 📋 ROOT CAUSE ANALYSIS

### **Vấn đề:**
Trong modal "Tùy chỉnh sản phẩm", 3 nút size (S, M, L) bị lệch vị trí: nút S nằm khác hàng/khác cụm so với M/L (không thẳng hàng, không đều khoảng cách).

### **Root Cause:**
**File:** `src/main/webapp/assets/js/app.js` (dòng 96-143)

**Vấn đề chính:**
- Code đang tách sizes thành **2 nhóm riêng biệt**:
  1. `otherSizesHtml`: Các size không phải M và L (như S) → render trong `row g-2` với `col-auto`
  2. `mlSizesHtml`: M và L → render trong `col-12 d-flex justify-content-center` riêng, nằm **ngoài** row đó
- Khi ghép lại: `sizesHtml = otherSizesHtml + mlSizesHtml`
  - S nằm trong: `<div class="row g-2">...S...</div>`
  - M và L nằm trong: `<div class="col-12 d-flex...">...M...L...</div>` (nằm ngoài row)
- Kết quả: S nằm một hàng, M và L nằm hàng khác → **KHÔNG THẲNG HÀNG**

### **Code cũ (SAI):**
```javascript
// Tách sizes thành 2 nhóm
const otherSizes = data.sizes.filter(s => name !== 'M' && name !== 'L');
const otherSizesHtml = otherSizes.map(...).join(''); // S trong row g-2

const mlSizesHtml = `
    <div class="col-12 d-flex justify-content-center..."> // M và L trong div riêng
        ...
    </div>
`;
sizesHtml = otherSizesHtml + mlSizesHtml; // GHÉP LẠI → LỆCH
```

---

## ✅ GIẢI PHÁP

### **File đã sửa:**
- `src/main/webapp/assets/js/app.js` (dòng 96-143)

### **Thay đổi:**
- **BỎ:** Logic tách sizes thành 2 nhóm riêng
- **THAY BẰNG:** Render **tất cả sizes trong cùng một container** `row g-2` với `col-auto`

### **Code mới (ĐÚNG):**
```javascript
// Render tất cả sizes trong cùng một container để thẳng hàng đều
const defaultSizeIndex = data.sizes.findIndex(s => s.priceAdjustment == 0);
const sizesHtml = data.sizes.map((s, index) => `
    <div class="col-auto">
        <input type="radio" class="btn-check" name="size" id="modal-size-${index}" ...>
        <label class="btn btn-outline-primary btn-size" for="modal-size-${index}">
            ${escapeHtml(s.name)}
            <div class="small fw-normal">...</div>
        </label>
    </div>
`).join('');
```

**Kết quả HTML:**
```html
<div class="row g-2 align-items-center">
    <div class="col-auto">...S...</div>
    <div class="col-auto">...M...</div>
    <div class="col-auto">...L...</div>
</div>
```

---

## 📄 HTML STRUCTURE SAU KHI SỬA

### **Container structure:**
```html
<div class="option-group">
    <h6>Chọn kích cỡ</h6>
    <div class="row g-2 align-items-center">
        <!-- TẤT CẢ sizes nằm trong CÙNG một row -->
        <div class="col-auto">
            <input type="radio" ... id="modal-size-0">
            <label class="btn btn-outline-primary btn-size">S (+0 đ)</label>
        </div>
        <div class="col-auto">
            <input type="radio" ... id="modal-size-1">
            <label class="btn btn-outline-primary btn-size">M (+5.000 đ)</label>
        </div>
        <div class="col-auto">
            <input type="radio" ... id="modal-size-2">
            <label class="btn btn-outline-primary btn-size">L (+10.000 đ)</label>
        </div>
    </div>
</div>
```

---

## 🎨 CSS ĐÃ CÓ (Không cần sửa)

CSS hiện tại đã hỗ trợ tốt:
- `.row g-2`: Bootstrap grid với gap 0.5rem
- `.col-auto`: Tự động điều chỉnh width theo content
- `.align-items-center`: Căn giữa theo chiều dọc
- `.btn-size`: Đã có `min-width: 75px`, `text-align: center`

**File CSS:** `src/main/webapp/assets/css/custom.css`
- Dòng 728-736: `.btn-size` styling
- Dòng 204: Container đã dùng `row g-2 align-items-center`

---

## 🧪 HƯỚNG DẪN TEST

### **Test Case 1: Desktop (> 768px)**
1. Mở trang web trên desktop
2. Click vào sản phẩm thức uống → Mở modal "Tùy chỉnh sản phẩm"
3. **Kiểm tra:**
   - ✅ 3 nút size (S, M, L) nằm **cùng một hàng**
   - ✅ Khoảng cách đều nhau (`g-2` = 0.5rem)
   - ✅ Căn thẳng hàng theo chiều dọc (cùng baseline)
   - ✅ Nút size có cùng height và min-width

### **Test Case 2: Tablet (576px - 768px)**
1. Resize browser về 768px
2. Mở modal "Tùy chỉnh sản phẩm"
3. **Kiểm tra:**
   - ✅ 3 nút size vẫn nằm cùng một hàng (nếu đủ chỗ)
   - ✅ Hoặc tự động wrap xuống hàng nếu không đủ chỗ (responsive)

### **Test Case 3: Mobile (< 576px)**
1. Resize browser về < 576px (hoặc mở trên mobile)
2. Mở modal "Tùy chỉnh sản phẩm"
3. **Kiểm tra:**
   - ✅ 3 nút size có thể wrap xuống hàng (2-1 hoặc 1-1-1)
   - ✅ Khi wrap, vẫn đều khoảng cách và căn chỉnh
   - ✅ Không bị overflow hoặc lệch

### **Test Case 4: Layout khác không bị ảnh hưởng**
1. Mở modal "Tùy chỉnh sản phẩm"
2. **Kiểm tra:**
   - ✅ Phần "Độ ngọt" và "Mức đá" vẫn hiển thị đúng
   - ✅ Phần "Topping" vẫn hiển thị đúng
   - ✅ Tổng thể layout không bị ảnh hưởng

---

## ✅ KẾT QUẢ

1. ✅ **3 nút size (S/M/L) thẳng hàng** trong cùng một container
2. ✅ **Khoảng cách đều nhau** (Bootstrap `g-2`)
3. ✅ **Responsive:** Tự động wrap khi màn hình nhỏ
4. ✅ **Không ảnh hưởng layout khác:** Độ ngọt, Mức đá, Topping vẫn đúng
5. ✅ **Không dùng position absolute:** Chỉ dùng Bootstrap grid/flex chuẩn

---

## 📝 NOTES

- **Bootstrap Grid:** Sử dụng `row g-2` + `col-auto` để đảm bảo responsive và đều khoảng cách
- **Flexbox fallback:** Nếu cần, có thể thêm `d-flex flex-wrap gap-2` nhưng hiện tại `row g-2` đã đủ
- **Default selection:** Logic check size mặc định (price = 0) vẫn hoạt động đúng

---

**Status:** ✅ Hoàn thành  
**Date:** 2026-01-04  
**Files changed:** 1 file (`src/main/webapp/assets/js/app.js`)


