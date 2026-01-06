# TÀI LIỆU ÔN THI - 15 CÂU HỎI VỀ LOGIC VÀ NGHIỆP VỤ
## Dự án: AloTra - Hệ thống bán trà sữa online

---

## Câu 1: Luồng xử lý đăng nhập và quản lý session như thế nào?

### Câu hỏi:
Khi người dùng đăng nhập, hệ thống xử lý như thế nào? Session được tạo và lưu trữ thông tin gì? Làm sao để kiểm tra user đã đăng nhập ở các trang khác?

### Trả lời:

**Luồng xử lý:**

1. **Controller xử lý đăng nhập:**
   - File: `src/main/java/stnw/controller/auth/LoginController.java`
   - Method `doPost()` nhận `username` và `password` từ form
   - Gọi `UserService.login(username, password)` để xác thực

2. **Service xử lý logic:**
   - File: `src/main/java/stnw/service/impl/UserServiceImpl.java`
   - Method `login()`:
     - Tìm user theo username từ DB qua `UserDao.findByUsername()`
     - Kiểm tra user có tồn tại và `isActive = true`
     - Verify password bằng `PasswordUtils.verifyPassword(plain, hashed)`
     - Nếu đúng → trả về User object

3. **Lưu vào Session:**
   - Controller lưu User vào session: `session.setAttribute("currentUser", user)`
   - Session được quản lý bởi Tomcat, tự động tạo session ID

4. **Kiểm tra đăng nhập ở các trang khác:**
   - Sử dụng Filter: `src/main/java/stnw/filter/UserAuthenticationFilter.java`
   - Hoặc trong Controller: `User currentUser = (User) session.getAttribute("currentUser")`
   - Nếu `currentUser == null` → redirect về `/login`

**Files tham khảo:**
- `src/main/java/stnw/controller/auth/LoginController.java`
- `src/main/java/stnw/service/impl/UserServiceImpl.java` (method `login()`)
- `src/main/java/stnw/utils/PasswordUtils.java` (method `verifyPassword()`)

---

## Câu 2: Phân quyền Admin/User được xử lý như thế nào? Filter chặn URL như thế nào?

### Câu hỏi:
Làm sao hệ thống phân biệt Admin và User? Làm thế nào để chặn User không cho vào trang admin? Filter hoạt động như thế nào?

### Trả lời:

**Cơ chế phân quyền:**

1. **Role trong Database:**
   - User có field `roleid`: 1 = ADMIN, 2 = STAFF, 3 = CUSTOMER
   - Constants: `src/main/java/stnw/utils/Roles.java`

2. **Filter chặn Admin URLs:**
   - File: `src/main/java/stnw/filter/AdminAuthorizationFilter.java`
   - Annotation: `@WebFilter(urlPatterns = {"/admin/*", "/api/admin/*"})`
   - Logic trong `doFilter()`:
     ```java
     // Kiểm tra đăng nhập
     if (user == null) → redirect /login
     
     // Kiểm tra role
     if (user.getRoleid() == Roles.ADMIN) → cho phép
     if (user.getRoleid() == Roles.CUSTOMER || user.getRoleid() == Roles.STAFF) 
         → redirect /home?alert=access_denied
     ```

3. **Kiểm tra trong Controller:**
   - `AdminDashboardController.java` kiểm tra `currentUser.getRoleid() == 1`
   - Nếu không phải ADMIN → redirect về home

**Files tham khảo:**
- `src/main/java/stnw/filter/AdminAuthorizationFilter.java`
- `src/main/java/stnw/utils/Roles.java`
- `src/main/java/stnw/controller/admin/AdminDashboardController.java`

---

## Câu 3: Luồng thêm sản phẩm vào giỏ hàng (Cart) hoạt động như thế nào?

### Câu hỏi:
Khi user click "Thêm vào giỏ hàng", hệ thống xử lý như thế nào? Làm sao lưu trữ giỏ hàng? Xử lý size, topping, sugar, ice như thế nào?

### Trả lời:

**Luồng xử lý:**

1. **Frontend gửi request:**
   - AJAX POST đến `/cart/add`
   - Parameters: `productId`, `quantity`, `size`, `sweetness`, `ice`, `topping`

2. **Controller xử lý:**
   - File: `src/main/java/stnw/controller/user/cart/CartController.java`
   - Method `handleAdd()`:
     - Kiểm tra đăng nhập: nếu chưa đăng nhập → trả JSON `{"ok":false,"redirect":"/login"}`
     - Lấy cart từ session: `List<CartItem> cart = getCart(session)`
     - Gọi `CartService.addToCart()`

3. **Service xử lý logic:**
   - File: `src/main/java/stnw/service/impl/CartServiceImpl.java`
   - Method `addToCart()`:
     - Load Product từ DB qua `ProductDao.findById()`
     - Load ProductSize từ DB qua `ProductSizeDao.findByProductIdAndSizeName()`
     - Load Toppings từ DB qua `ToppingDao.findByIds()` (nếu có)
     - Tính giá: `unitPrice = productSize.getPrice()`
     - Tính giá topping: `toppingTotal = sum(topping.getPrice())`
     - Tính `lineTotal = (unitPrice + toppingTotal) * quantity`
     - Tạo `CartItem` object và add vào list
     - Lưu lại vào session: `session.setAttribute("CART", cart)`

4. **Lưu trữ:**
   - Cart được lưu trong `HttpSession` với key `"CART"`
   - Type: `List<CartItem>` (CartItem là POJO, không phải Entity)
   - Session tự động persist khi user đóng trình duyệt (timeout)

**Files tham khảo:**
- `src/main/java/stnw/controller/user/cart/CartController.java` (method `handleAdd()`)
- `src/main/java/stnw/service/impl/CartServiceImpl.java` (method `addToCart()`)
- `src/main/java/stnw/model/CartItem.java`

---

## Câu 4: Luồng checkout và tạo đơn hàng (Order) hoạt động như thế nào? Transaction được xử lý ra sao?

### Câu hỏi:
Khi user nhấn "Đặt hàng", hệ thống tạo đơn hàng như thế nào? Vì sao cần transaction? Các bước xử lý voucher, shipping fee, payment status?

### Trả lời:

**Luồng xử lý:**

1. **Controller nhận request:**
   - File: `src/main/java/stnw/controller/user/order/CheckoutController.java`
   - Method `doPost()` nhận: `fullname`, `phone`, `address`, `note`, `voucher`, `payment`, `shippingType`

2. **Service xử lý:**
   - File: `src/main/java/stnw/service/impl/OrderServiceImpl.java`
   - Method `placeOrder()`:

   **Bước 1: Xử lý Voucher**
   - Nếu có `voucherCode`:
     - Tìm voucher từ DB: `voucherDao.findActiveByCode(code)`
     - Kiểm tra `min_order_value` và `total` của cart
     - Tính discount:
       - Nếu `PERCENT`: `discount = total * (discount_value / 100)`
       - Nếu `AMOUNT`: `discount = discount_value`
     - Áp dụng `max_discount` nếu có
     - Tăng `used_count` và update voucher: `voucherDao.update(v)`

   **Bước 2: Tính tổng tiền**
   - `grandTotal = total - discount + shippingFee`
   - Nếu `grandTotal < 0` → set = 0

   **Bước 3: Xác định Payment Status**
   - Nếu `paymentMethod == "COD"` → `paymentStatus = "Chưa thanh toán"`
   - Nếu khác → `paymentStatus = "Đã thanh toán"`

   **Bước 4: Tạo Order (có Transaction)**
   - Gọi `orderDao.createOrder()` - DAO tự quản lý transaction
   - File: `src/main/java/stnw/dao/impl/OrderDaoImpl.java`
   - Method `createOrder()`:
     ```java
     EntityManager em = JpaUtils.em();
     EntityTransaction trans = em.getTransaction();
     try {
         trans.begin();
         // Tạo Orders entity
         Orders order = new Orders();
         // Set các field...
         em.persist(order);
         
         // Tạo OrderDetail cho mỗi CartItem
         for (CartItem item : items) {
             OrderDetail detail = new OrderDetail();
             // Set order, product, quantity, price...
             em.persist(detail);
         }
         trans.commit();
     } catch (Exception e) {
         trans.rollback();
         throw e;
     } finally {
         em.close();
     }
     ```

   **Bước 5: Tạo Notification**
   - Gọi `notificationService.createNotification()` để thông báo cho user

3. **Vì sao cần Transaction?**
   - Đảm bảo tính **ACID**: Nếu tạo Order thành công nhưng tạo OrderDetail lỗi → rollback toàn bộ
   - Tránh dữ liệu không nhất quán (Order không có OrderDetail)
   - Đảm bảo voucher `used_count` chỉ tăng khi đơn hàng được tạo thành công

**Files tham khảo:**
- `src/main/java/stnw/controller/user/order/CheckoutController.java`
- `src/main/java/stnw/service/impl/OrderServiceImpl.java` (method `placeOrder()`)
- `src/main/java/stnw/dao/impl/OrderDaoImpl.java` (method `createOrder()`)

---

## Câu 5: Logic tính toán và áp dụng Voucher/Giảm giá như thế nào?

### Câu hỏi:
Khi user nhập mã giảm giá, hệ thống kiểm tra và tính discount như thế nào? Các điều kiện áp dụng voucher là gì?

### Trả lời:

**Luồng xử lý:**

1. **API endpoint:**
   - File: `src/main/java/stnw/controller/user/ajax/VoucherAjaxController.java`
   - URL: `/api/voucher` (POST)
   - Nhận `code` và `cart` từ session

2. **Service xử lý:**
   - File: `src/main/java/stnw/service/impl/VoucherServiceImpl.java`
   - Method `applyVoucher(code, cartItems)`:

   **Bước 1: Validate input**
   - Kiểm tra `code` không null/empty
   - Tính `total` từ cart items

   **Bước 2: Tìm voucher từ DB**
   - Gọi `voucherDao.findActiveByCode(code)`
   - DAO kiểm tra:
     - `isActive = true`
     - `start_date <= now <= end_date`
     - `used_count < usage_limit` (nếu có limit)

   **Bước 3: Kiểm tra điều kiện**
   - Nếu `voucher.min_order_value != null`:
     - So sánh: `cartTotal >= min_order_value`
     - Nếu không đủ → trả về `ApplyResult(false, "Đơn hàng chưa đủ điều kiện...")`

   **Bước 4: Tính discount**
   ```java
   if (voucher.discount_type == "PERCENT") {
       discountAmount = total * (discount_value / 100)
   } else {
       discountAmount = discount_value
   }
   
   // Áp dụng max_discount nếu có
   if (voucher.max_discount != null && discountAmount > max_discount) {
       discountAmount = max_discount
   }
   ```

   **Bước 5: Tính tổng mới**
   - `newTotal = total - discountAmount`
   - Nếu `newTotal < 0` → set = 0

3. **Khi checkout:**
   - Voucher được áp dụng lại trong `OrderServiceImpl.placeOrder()`
   - Tăng `used_count` và update vào DB

**Files tham khảo:**
- `src/main/java/stnw/controller/user/ajax/VoucherAjaxController.java`
- `src/main/java/stnw/service/impl/VoucherServiceImpl.java` (method `applyVoucher()`)
- `src/main/java/stnw/dao/impl/VoucherDaoImpl.java` (method `findActiveByCode()`)

---

## Câu 6: Hệ thống điểm tích lũy (Loyalty Points) hoạt động như thế nào?

### Câu hỏi:
Khi user đặt hàng, điểm tích lũy được tính như thế nào? Làm sao để đổi quà bằng điểm? Logic kiểm tra đủ điểm và trừ điểm?

### Trả lời:

**1. Tích điểm từ đơn hàng:**

- File: `src/main/java/stnw/service/impl/LoyaltyServiceImpl.java`
- Method `earnPointsFromOrder(user, orderTotal, orderId)`:

  **Công thức:**
  - `pointsEarned = (orderTotal / 1000) * POINTS_PER_1000_VND`
  - Mặc định: 1 điểm = 1000 VND

  **Luồng:**
  1. Tính điểm từ `orderTotal`
  2. Load user từ DB: `userDao.findById(userId)`
  3. Cập nhật điểm: `user.loyalty_points += pointsEarned`
  4. Update user: `userDao.update(user)`
  5. Tạo transaction record:
     - `PointTransaction` với `type = "EARN"`
     - Lưu `points`, `description`, `balance_after`
     - Link với `order` nếu có

**2. Đổi quà bằng điểm:**

- Method `redeemReward(user, rewardId)`:

  **Luồng:**
  1. Load reward từ DB: `rewardDao.findById(rewardId)`
  2. Kiểm tra reward `isActive = true`
  3. Load user và kiểm tra điểm:
     - `currentPoints >= reward.points_required`
  4. Kiểm tra tồn kho:
     - `reward.stock > 0` (nếu có stock)
  5. Trừ điểm:
     - `user.loyalty_points -= points_required`
     - `userDao.update(user)`
  6. Giảm stock:
     - `reward.stock -= 1`
     - `rewardDao.update(reward)`
  7. Tạo transaction:
     - `PointTransaction` với `type = "REDEEM"`, `points = -points_required`

**Files tham khảo:**
- `src/main/java/stnw/service/impl/LoyaltyServiceImpl.java`
- `src/main/java/stnw/model/PointTransaction.java`
- `src/main/java/stnw/model/Reward.java`

---

## Câu 7: Luồng cập nhật số lượng sản phẩm trong giỏ hàng như thế nào?

### Câu hỏi:
Khi user tăng/giảm số lượng trong giỏ hàng, hệ thống cập nhật như thế nào? Làm sao tính lại tổng tiền?

### Trả lời:

**Luồng xử lý:**

1. **Frontend gửi request:**
   - AJAX POST đến `/cart/update`
   - Parameters: `productId`, `size`, `sugarLevel`, `iceLevel`, `toppings`, `quantity`

2. **Controller:**
   - File: `src/main/java/stnw/controller/user/cart/CartController.java`
   - Method `handleUpdate()`:
     - Lấy cart từ session
     - Gọi `cartService.updateQuantity(cart, productId, size, sugarLevel, iceLevel, toppings, newQuantity)`

3. **Service:**
   - File: `src/main/java/stnw/service/impl/CartServiceImpl.java`
   - Method `updateQuantity()`:
     - Tìm CartItem trong list khớp với `productId`, `size`, `sugarLevel`, `iceLevel`, `toppings`
     - Cập nhật `quantity` của item đó
     - Tính lại `lineTotal = (unitPrice + toppingTotal) * newQuantity`
     - Set lại `lineTotal` cho item
     - Lưu lại vào session

4. **Frontend cập nhật UI:**
   - Nhận response JSON `{"ok":true}`
   - Tính lại subtotal từ tất cả `lineTotal` của các items
   - Cập nhật hiển thị

**Files tham khảo:**
- `src/main/java/stnw/controller/user/cart/CartController.java` (method `handleUpdate()`)
- `src/main/java/stnw/service/impl/CartServiceImpl.java` (method `updateQuantity()`)

---

## Câu 8: Logic xóa sản phẩm (hard delete) và xử lý các bản ghi liên quan?

### Câu hỏi:
Khi admin xóa sản phẩm, hệ thống xử lý các bản ghi liên quan (OrderDetail, Review, WishlistItem, ProductSize) như thế nào? Vì sao phải xóa theo thứ tự?

### Trả lời:

**Luồng xử lý:**

1. **Controller:**
   - File: `src/main/java/stnw/controller/admin/product/ProductDeleteController.java`
   - Nhận `productId` từ request

2. **Service:**
   - File: `src/main/java/stnw/service/impl/AdminProductServiceImpl.java`
   - Method `deleteProduct(productId)`:
     - Gọi `productDao.deleteProduct(productId)`

3. **DAO xử lý cascading delete:**
   - File: `src/main/java/stnw/dao/impl/ProductDaoImpl.java`
   - Method `deleteProduct(productId)`:

   **Thứ tự xóa (quan trọng):**
   ```java
   EntityManager em = JpaUtils.em();
   EntityTransaction trans = em.getTransaction();
   try {
       trans.begin();
       
       // 1. Xóa OrderDetail (foreign key đến Product)
       em.createNativeQuery("DELETE FROM OrderDetail WHERE product_id = ?")
         .setParameter(1, productId).executeUpdate();
       
       // 2. Xóa Review (foreign key đến Product)
       em.createNativeQuery("DELETE FROM Review WHERE product_id = ?")
         .setParameter(1, productId).executeUpdate();
       
       // 3. Xóa WishlistItem (foreign key đến Product)
       em.createNativeQuery("DELETE FROM WishlistItem WHERE product_id = ?")
         .setParameter(1, productId).executeUpdate();
       
       // 4. Xóa ProductSize (foreign key đến Product)
       em.createNativeQuery("DELETE FROM ProductSize WHERE product_id = ?")
         .setParameter(1, productId).executeUpdate();
       
       // 5. Xóa ViewHistory (nếu có)
       em.createNativeQuery("DELETE FROM ViewHistory WHERE product_id = ?")
         .setParameter(1, productId).executeUpdate();
       
       // 6. Cuối cùng mới xóa Product
       em.createNativeQuery("DELETE FROM Product WHERE product_id = ?")
         .setParameter(1, productId).executeUpdate();
       
       trans.commit();
   } catch (Exception e) {
       trans.rollback();
       throw e;
   } finally {
       em.close();
   }
   ```

4. **Vì sao phải xóa theo thứ tự?**
   - Foreign key constraint: Nếu xóa Product trước → lỗi vì OrderDetail/Review vẫn reference đến Product
   - Phải xóa các bảng con (child) trước, sau đó mới xóa bảng cha (parent)
   - Sử dụng native SQL để đảm bảo xóa hoàn toàn, tránh JPA cache issues

**Files tham khảo:**
- `src/main/java/stnw/controller/admin/product/ProductDeleteController.java`
- `src/main/java/stnw/dao/impl/ProductDaoImpl.java` (method `deleteProduct()`)

---

## Câu 9: Luồng đăng ký tài khoản và xác thực email (OTP) như thế nào?

### Câu hỏi:
Khi user đăng ký, hệ thống xử lý như thế nào? Làm sao gửi OTP qua email? Xác thực OTP như thế nào?

### Trả lời:

**Luồng đăng ký:**

1. **Controller:**
   - File: `src/main/java/stnw/controller/auth/RegisterController.java`
   - Method `doPost()` nhận: `username`, `email`, `password`, `fullname`, `phone`

2. **Service xử lý:**
   - File: `src/main/java/stnw/service/impl/UserServiceImpl.java`
   - Method `register()`:

   **Bước 1: Validation**
   - Kiểm tra email/username đã tồn tại: `userDao.existsByEmail()`, `userDao.existsByUsername()`
   - Kiểm tra phone đã tồn tại (nếu có)

   **Bước 2: Tạo user mới**
   - Hash password: `PasswordUtils.hashPassword(rawPassword)`
   - Tạo code OTP: `TokenUtils.generateUrlToken()` hoặc random 6 số
   - Set `isActive = false` (chưa kích hoạt)
   - Set `roleid = 3` (CUSTOMER)
   - Lưu vào DB: `userDao.save(user)`

   **Bước 3: Gửi email OTP**
   - File: `src/main/java/stnw/utils/EmailUtils.java`
   - Method `sendVerificationEmail(email, code)`
   - Sử dụng JavaMail API
   - Email chứa link: `/verify?email=xxx&code=xxx`

3. **Xác thực OTP:**
   - File: `src/main/java/stnw/controller/auth/VerifyController.java`
   - Nhận `email` và `code` từ URL
   - Tìm user: `userDao.findByEmail(email)`
   - So sánh `user.code == code` từ URL
   - Nếu đúng: `user.setIsActive(true)`, `userDao.update(user)`

**Files tham khảo:**
- `src/main/java/stnw/controller/auth/RegisterController.java`
- `src/main/java/stnw/service/impl/UserServiceImpl.java` (method `register()`)
- `src/main/java/stnw/utils/EmailUtils.java`
- `src/main/java/stnw/controller/auth/VerifyController.java`

---

## Câu 10: Logic tính giá sản phẩm với size và topping như thế nào?

### Câu hỏi:
Khi user chọn size (S/M/L) và topping, giá sản phẩm được tính như thế nào? Làm sao load danh sách size và topping từ DB?

### Trả lời:

**1. Load size và topping:**

- File: `src/main/java/stnw/service/impl/ProductQueryServiceImpl.java`
- Method `getSizes(productId)`:
  - Gọi `productSizeDao.findByProductId(productId)`
  - Trả về `List<ProductSize>` với các size: S, M, L, XL...

- Method `getAvailableToppingsForCategory(categoryName)`:
  - Load Category: `categoryDao.findByName(categoryName)`
  - Kiểm tra `category.isDrink == true` (chỉ hiển thị topping cho đồ uống)
  - Nếu đúng: `toppingDao.findByCategoryName(categoryName)`
  - Trả về `List<Topping>` với `isAvailable = true`

**2. Tính giá khi thêm vào cart:**

- File: `src/main/java/stnw/service/impl/CartServiceImpl.java`
- Method `addToCart()`:

  ```java
  // Lấy giá size
  ProductSize size = productSizeDao.findByProductIdAndSizeName(productId, sizeName);
  BigDecimal unitPrice = size.getPrice(); // Ví dụ: M = 35000, L = 40000
  
  // Tính tổng giá topping
  BigDecimal toppingTotal = BigDecimal.ZERO;
  if (toppingIds != null) {
      List<Topping> toppings = toppingDao.findByIds(toppingIds);
      for (Topping t : toppings) {
          toppingTotal = toppingTotal.add(t.getPrice());
      }
  }
  
  // Tính lineTotal
  BigDecimal lineTotal = (unitPrice + toppingTotal) * quantity;
  ```

**Files tham khảo:**
- `src/main/java/stnw/service/impl/ProductQueryServiceImpl.java`
- `src/main/java/stnw/service/impl/CartServiceImpl.java` (method `addToCart()`)
- `src/main/java/stnw/dao/impl/ProductSizeDaoImpl.java`
- `src/main/java/stnw/dao/impl/ToppingDaoImpl.java`

---

## Câu 11: Luồng quản lý trạng thái đơn hàng (Order Status) như thế nào?

### Câu hỏi:
Admin cập nhật trạng thái đơn hàng như thế nào? Các trạng thái nào được phép? Ai được phép đổi trạng thái?

### Trả lời:

**1. Các trạng thái đơn hàng:**

- File: `src/main/java/stnw/utils/OrderStatus.java`
- Các trạng thái:
  - `CHO_XAC_NHAN` ("Chờ xác nhận") - Mặc định khi tạo đơn
  - `DA_XAC_NHAN` ("Đã xác nhận")
  - `DANG_GIAO` ("Đang giao")
  - `HOAN_THANH` ("Hoàn thành")
  - `DA_HUY` ("Đã hủy")

**2. Controller xử lý:**

- File: `src/main/java/stnw/controller/admin/order/AdminOrderController.java`
- Method xử lý update status:
  - Chỉ ADMIN mới được truy cập (qua `AdminAuthorizationFilter`)
  - Nhận `orderId` và `newStatus` từ request

**3. Service xử lý:**

- File: `src/main/java/stnw/service/impl/AdminOrderServiceImpl.java`
- Method `updateOrderStatus(orderId, newStatus)`:
  - Load order: `orderDao.findById(orderId)`
  - Validate status hợp lệ
  - Update: `order.setOrder_status(newStatus)`
  - Lưu: `orderDao.update(order)`
  - Tạo notification cho user (nếu cần)

**4. User hủy đơn:**

- File: `src/main/java/stnw/service/impl/UserProfileServiceImpl.java`
- Method `cancelOrder(orderId, userId)`:
  - Kiểm tra order thuộc về user: `order.getUser().getId() == userId`
  - Chỉ cho phép hủy nếu `orderStatus == "Chờ xác nhận"`
  - Update: `order.setOrder_status("Đã hủy")`

**Files tham khảo:**
- `src/main/java/stnw/utils/OrderStatus.java`
- `src/main/java/stnw/controller/admin/order/AdminOrderController.java`
- `src/main/java/stnw/service/impl/AdminOrderServiceImpl.java`
- `src/main/java/stnw/service/impl/UserProfileServiceImpl.java` (method `cancelOrder()`)

---

## Câu 12: Logic tìm kiếm và lọc sản phẩm như thế nào?

### Câu hỏi:
Khi user tìm kiếm hoặc lọc sản phẩm theo category, hệ thống xử lý như thế nào? Phân trang được thực hiện ra sao?

### Trả lời:

**1. Controller:**

- File: `src/main/java/stnw/controller/user/product/ProductListController.java`
- Method `doGet()`:
  - Nhận parameters: `keyword`, `categoryId`, `page` (mặc định = 1)
  - Gọi `productQueryService.search(keyword, categoryId, page, pageSize)`

**2. Service xử lý:**

- File: `src/main/java/stnw/service/impl/ProductQueryServiceImpl.java`
- Method `search()`:

  ```java
  // Build query động
  String jpql = "SELECT p FROM Product p WHERE p.isActive = true";
  
  if (keyword != null && !keyword.isEmpty()) {
      jpql += " AND p.product_name LIKE :keyword";
  }
  
  if (categoryId != null && categoryId > 0) {
      jpql += " AND p.category.id = :categoryId";
  }
  
  jpql += " ORDER BY p.product_id DESC";
  
  // Phân trang
  int offset = (page - 1) * pageSize;
  query.setFirstResult(offset);
  query.setMaxResults(pageSize);
  
  // Đếm tổng số records
  String countJpql = "SELECT COUNT(p) FROM Product p WHERE ...";
  long total = countQuery.getSingleResult();
  
  return Map.of("products", products, "total", total, "page", page);
  ```

**3. Frontend hiển thị:**

- JSP: `src/main/webapp/views/product/list.jsp`
- Hiển thị danh sách products
- Pagination links: `/products?page=1&categoryId=2&keyword=trà`

**Files tham khảo:**
- `src/main/java/stnw/controller/user/product/ProductListController.java`
- `src/main/java/stnw/service/impl/ProductQueryServiceImpl.java` (method `search()`)
- `src/main/java/stnw/dao/impl/ProductQueryDaoImpl.java`

---

## Câu 13: Luồng xử lý Wishlist (Danh sách yêu thích) như thế nào?

### Câu hỏi:
Khi user click "Yêu thích", hệ thống lưu trữ như thế nào? Làm sao toggle (thêm/xóa) item trong wishlist?

### Trả lời:

**1. AJAX Controller:**

- File: `src/main/java/stnw/controller/user/ajax/WishlistAjaxController.java`
- URL: `/api/wishlist/toggle` (POST)
- Nhận `productId` từ request

**2. Service xử lý:**

- File: `src/main/java/stnw/service/impl/WishlistServiceImpl.java`
- Method `toggleItem(userId, productId)`:

  ```java
  // Kiểm tra item đã tồn tại chưa
  WishlistItem existing = wishlistDao.findByUserAndProduct(userId, productId);
  
  if (existing != null) {
      // Đã có → Xóa
      wishlistDao.delete(existing);
      return false; // Đã bỏ yêu thích
  } else {
      // Chưa có → Thêm mới
      WishlistItem item = new WishlistItem();
      item.setUser(user);
      item.setProduct(product);
      wishlistDao.save(item);
      return true; // Đã thêm yêu thích
  }
  ```

**3. Lưu trữ:**

- Entity: `src/main/java/stnw/model/WishlistItem.java`
- Bảng `WishlistItem` với `user_id` và `product_id`
- Composite key hoặc primary key tự tăng

**4. Hiển thị danh sách:**

- File: `src/main/java/stnw/controller/user/wishlist/WishlistController.java`
- Load: `wishlistService.listItems(userId)`
- Trả về `List<WishlistItem>` với product đã load

**Files tham khảo:**
- `src/main/java/stnw/controller/user/ajax/WishlistAjaxController.java`
- `src/main/java/stnw/service/impl/WishlistServiceImpl.java` (method `toggleItem()`)
- `src/main/java/stnw/dao/impl/WishlistDaoImpl.java`

---

## Câu 14: Logic upload và lưu trữ ảnh sản phẩm như thế nào?

### Câu hỏi:
Khi admin upload ảnh sản phẩm, file được lưu ở đâu? Làm sao validate file? Đường dẫn ảnh được lưu trong DB như thế nào?

### Trả lời:

**1. Controller xử lý upload:**

- File: `src/main/java/stnw/controller/admin/product/ProductSaveController.java`
- Method xử lý `multipart/form-data`:

  ```java
  Part imagePart = req.getPart("image");
  if (imagePart != null && imagePart.getSize() > 0) {
      String fileName = imagePart.getSubmittedFileName();
      
      // Validate file extension
      if (!fileName.toLowerCase().endsWith(".jpg") && 
          !fileName.toLowerCase().endsWith(".png") &&
          !fileName.toLowerCase().endsWith(".jpeg")) {
          // Lỗi: chỉ chấp nhận ảnh
      }
      
      // Validate file size (ví dụ: max 5MB)
      if (imagePart.getSize() > 5 * 1024 * 1024) {
          // Lỗi: file quá lớn
      }
      
      // Tạo tên file unique
      String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
      
      // Lưu file
      String uploadPath = getServletContext().getRealPath("/uploads/products");
      File uploadDir = new File(uploadPath);
      if (!uploadDir.exists()) {
          uploadDir.mkdirs();
      }
      
      String filePath = uploadPath + File.separator + uniqueFileName;
      imagePart.write(filePath);
      
      // Lưu đường dẫn vào DB (relative path)
      String relativePath = "/uploads/products/" + uniqueFileName;
      product.setThumbnail(relativePath);
  }
  ```

**2. Lưu trữ:**

- File được lưu trong: `src/main/webapp/uploads/products/`
- DB lưu relative path: `/uploads/products/1234567890_image.jpg`
- Khi hiển thị: `<img src="${pageContext.request.contextPath}${product.thumbnail}">`

**3. Xóa ảnh cũ khi update:**

- Khi update sản phẩm, xóa file ảnh cũ:
  ```java
  if (oldThumbnail != null) {
      String oldFilePath = getServletContext().getRealPath(oldThumbnail);
      File oldFile = new File(oldFilePath);
      if (oldFile.exists()) {
          oldFile.delete();
      }
  }
  ```

**Files tham khảo:**
- `src/main/java/stnw/controller/admin/product/ProductSaveController.java`
- `src/main/webapp/uploads/products/` (thư mục lưu ảnh)

---

## Câu 15: Xử lý lỗi và bảo mật trong hệ thống như thế nào?

### Câu hỏi:
Hệ thống xử lý các lỗi phổ biến như thế nào? Các biện pháp bảo mật đã được áp dụng?

### Trả lời:

**1. Xử lý lỗi:**

- **Transaction rollback:**
  - Tất cả DAO đều có try-catch-finally
  - Nếu lỗi → `trans.rollback()`, đóng `EntityManager`

- **Validation input:**
  - Controller validate parameters trước khi gọi service
  - Service validate business rules (ví dụ: email đã tồn tại)

- **Error pages:**
  - `web.xml` hoặc `@WebServlet` có thể config error page
  - Redirect về trang lỗi với message

**2. Bảo mật:**

- **Password hashing:**
  - File: `src/main/java/stnw/utils/PasswordUtils.java`
  - Sử dụng BCrypt với 10 rounds
  - Method: `hashPassword()` và `verifyPassword()`
  - Không lưu plain password

- **XSS Prevention:**
  - JSP sử dụng JSTL `<c:out>` để escape HTML
  - JSON response escape special characters

- **SQL Injection:**
  - Sử dụng JPA với parameterized queries
  - Không dùng string concatenation trong JPQL

- **CSRF:**
  - Session-based authentication
  - Có thể thêm CSRF token (nếu cần)

- **Authorization:**
  - Filter chặn URL: `AdminAuthorizationFilter`
  - Kiểm tra role trong Controller

- **Session Security:**
  - Session timeout (mặc định Tomcat)
  - Kiểm tra `currentUser` trong session

**3. Encoding UTF-8:**

- File: `src/main/java/stnw/filter/CharacterEncodingFilter.java`
- Set `request.setCharacterEncoding("UTF-8")` và `response.setCharacterEncoding("UTF-8")`
- Đảm bảo tiếng Việt hiển thị đúng

**Files tham khảo:**
- `src/main/java/stnw/utils/PasswordUtils.java`
- `src/main/java/stnw/filter/CharacterEncodingFilter.java`
- `src/main/java/stnw/filter/AdminAuthorizationFilter.java`

---

## KẾT LUẬN

Tài liệu này bao gồm 15 câu hỏi về logic và nghiệp vụ các chức năng chính của dự án AloTra. Mỗi câu hỏi đều có:
- **Luồng xử lý chi tiết** (step-by-step)
- **Files/Classes tham khảo** cụ thể
- **Giải thích logic nghiệp vụ**

**Lưu ý khi thi:**
- Nhớ rõ luồng: Controller → Service → DAO → DB
- Hiểu rõ transaction và vì sao cần transaction
- Biết cách xử lý session và phân quyền
- Nắm rõ các business rules (voucher, loyalty points, order status)

**Chúc bạn thi tốt!**

