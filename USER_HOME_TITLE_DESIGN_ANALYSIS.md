# 📚 PHÂN TÍCH: CÁCH HIỂN THỊ TITLE Ở TRANG HOME CỦA USER

## 🎯 TỔNG QUAN

Trang home của user (front-end) sử dụng **CSS Grid Layout** để căn giữa title, khác với admin header (dùng position absolute).

---

## 📐 CẤU TRÚC HTML

### **File:** `src/main/webapp/views/_partials/navbar.jsp` (Line 147-193)

```jsp
<header class="sticky-top bg-white shadow-sm">
    <div class="container py-2">
        <div class="header-top-wrapper">
            <!-- LEFT: Logo + Search -->
            <div class="header-left">
                <a class="navbar-brand" href="...">...</a>
                <div class="search-autocomplete-container">...</div>
            </div>
            
            <!-- CENTER: Title -->
            <div class="header-center">
                <h1 class="site-title mb-0">AloTra</h1>
            </div>
            
            <!-- RIGHT: Cart + Notifications + User -->
            <div class="header-right">
                <div class="d-flex align-items-center gap-3">
                    <!-- Cart icon, Notifications, User dropdown -->
                </div>
            </div>
        </div>
    </div>
</header>
```

**Key Structure:**
- `.header-top-wrapper`: Container dùng CSS Grid
- `.header-left`: Cột trái (Logo + Search)
- `.header-center`: Cột giữa (Title)
- `.header-right`: Cột phải (Cart + Notifications + User)

---

## 🎨 CSS STYLES

### **File:** `src/main/webapp/assets/css/custom.css`

#### **1. Grid Layout Container (Line 74-84)**

```css
.header-top-wrapper {
    display: grid;
    grid-template-columns: 1fr auto 1fr;  /* 3 cột: trái | giữa | phải */
    align-items: center;
    gap: 1.5rem;
    background: #ffffff;
    padding: 0.5rem 1rem;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 102, 51, 0.08);
    margin: 0.15rem 0;
}
```

**Đặc điểm:**
- `grid-template-columns: 1fr auto 1fr`:
  - Cột 1 (`1fr`): Chiếm không gian còn lại (Left)
  - Cột 2 (`auto`): Vừa với nội dung (Center - Title)
  - Cột 3 (`1fr`): Chiếm không gian còn lại (Right)
- Title tự động căn giữa nhờ grid layout
- Không cần `position: absolute`

#### **2. Center Column (Line 103-107)**

```css
.header-center {
    flex-shrink: 0;
    text-align: center;
    justify-self: center;  /* Căn giữa trong grid cell */
}
```

#### **3. Site Title Styles (Line 109-148)**

```css
.site-title {
    font-size: 2.25rem;              /* Large font */
    font-weight: 800;                /* Extra bold */
    
    /* GRADIENT TEXT EFFECT */
    background: linear-gradient(135deg, var(--bs-primary) 0%, #004d26 50%, var(--bs-primary) 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    
    letter-spacing: 3px;             /* Spacing giữa các chữ */
    text-transform: uppercase;       /* UPPERCASE */
    font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
    margin: 0;
    white-space: nowrap;             /* Không xuống dòng */
    text-shadow: 0 2px 10px rgba(0, 102, 51, 0.2);
    position: relative;
    padding: 0.25rem 0.5rem;
    transition: transform 0.3s ease, letter-spacing 0.3s ease;
}

/* HOVER EFFECTS */
.site-title:hover {
    transform: scale(1.05);          /* Phóng to 5% */
    letter-spacing: 4px;             /* Tăng spacing */
}

/* HOVER BACKGROUND (Pseudo-element) */
.site-title::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(0, 102, 51, 0.1) 0%, rgba(0, 77, 38, 0.1) 100%);
    border-radius: 8px;
    z-index: -1;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.site-title:hover::before {
    opacity: 1;                      /* Hiện background khi hover */
}
```

**Đặc điểm nổi bật:**

1. **Gradient Text Effect:**
   - Dùng `background-clip: text` với `-webkit-text-fill-color: transparent`
   - Tạo hiệu ứng gradient cho chữ (không phải background)

2. **Hover Effects:**
   - Scale: `transform: scale(1.05)`
   - Letter-spacing tăng: `3px → 4px`
   - Background gradient hiện ra (pseudo-element `::before`)

3. **Typography:**
   - Font size lớn: `2.25rem`
   - Font weight: `800` (extra bold)
   - Uppercase với letter-spacing rộng

---

## 🔄 SO SÁNH: USER HOME vs ADMIN HEADER

| Đặc điểm | User Home | Admin Header |
|----------|-----------|--------------|
| **Layout Method** | CSS Grid (`grid-template-columns: 1fr auto 1fr`) | Position Absolute (`translate-middle`) |
| **Title Style** | Gradient text (background-clip) | Pill với gradient background |
| **Text Effect** | Text gradient (transparent fill) | Solid white text trên gradient bg |
| **Hover Effect** | Scale + letter-spacing + bg overlay | Transform + shadow + icon rotation |
| **Responsive** | Hide center trên mobile (`display: none`) | Switch to static position |
| **Positioning** | Grid tự động căn giữa | Manual absolute positioning |

**Ưu điểm Grid Layout:**
- ✅ Tự động căn giữa, không cần tính toán
- ✅ Không bị overlap issues
- ✅ Responsive dễ dàng (chỉ cần hide cột giữa)

**Ưu điểm Position Absolute (Admin):**
- ✅ Title luôn căn giữa tuyệt đối, không phụ thuộc vào nội dung 2 bên
- ✅ Có thể overlap với elements khác (với z-index)

---

## 📱 RESPONSIVE

### **Mobile (≤992px)**

```css
@media (max-width: 992px) {
    .header-center {
        display: none;  /* Ẩn title trên mobile */
    }
    
    .header-left {
        flex: 1 1 auto;  /* Left column chiếm toàn bộ */
    }
}
```

**Logic:** Trên mobile, title bị ẩn, chỉ hiển thị logo + search bên trái và cart/user bên phải.

---

## 💡 ÁP DỤNG VÀO ADMIN HEADER (NẾU MUỐN)

Nếu muốn áp dụng cách làm Grid Layout vào admin header, có thể:

1. **Thay position absolute bằng Grid:**
```css
.topbar {
    display: grid;
    grid-template-columns: auto 1fr auto;
    align-items: center;
}

.topbar .admin-title-center {
    position: static;  /* Không dùng absolute nữa */
    justify-self: center;
}
```

2. **Hoặc giữ nguyên position absolute** (như hiện tại) vì:
   - Admin header đơn giản hơn (chỉ có title + avatar)
   - Position absolute cho phép title luôn căn giữa tuyệt đối
   - Không cần responsive phức tạp như user home

---

## 📝 TÓM TẮT

### **Cách User Home làm:**

1. ✅ **CSS Grid Layout**: `grid-template-columns: 1fr auto 1fr`
2. ✅ **Gradient Text**: `background-clip: text` với transparent fill
3. ✅ **Hover Effects**: Scale + letter-spacing + pseudo-element background
4. ✅ **Responsive**: Hide center column trên mobile

### **Key Learning Points:**

- Grid layout tự động căn giữa, không cần absolute positioning
- Gradient text effect tạo visual đẹp mà không cần background pill
- Pseudo-element (`::before`) cho hover effects phức tạp
- Responsive đơn giản: chỉ cần hide/show columns

---

**Files tham khảo:**
- `src/main/webapp/views/_partials/navbar.jsp` (Line 147-193)
- `src/main/webapp/assets/css/custom.css` (Line 74-165)

