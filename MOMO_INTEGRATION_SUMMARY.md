# Tích hợp Thanh toán MoMo QR - Tóm tắt

## Tổng quan
Đã tích hợp thanh toán MoMo QR vào dự án AloTrà theo luồng:
1. Checkout → Chọn MoMo → Server tạo payment request → Hiển thị QR
2. User quét QR bằng MoMo Test app
3. MoMo gọi IPN về server → Cập nhật trạng thái đơn hàng (PAID)
4. Trang đơn hàng hiển thị "Đã thanh toán"

## Files đã tạo mới

### 1. Model/Entity
- **`src/main/java/stnw/model/Orders.java`** (sửa)
  - Thêm 6 fields: `momo_request_id`, `momo_order_id`, `momo_trans_id`, `momo_pay_url`, `momo_qr_code_url`, `momo_signature`

### 2. Utils
- **`src/main/java/stnw/utils/MomoSignatureUtil.java`**
  - Tạo và verify signature HMAC-SHA256 theo chuẩn MoMo
  - Methods: `createSignature()`, `createRawData()`, `verifySignature()`

- **`src/main/java/stnw/utils/MomoHttpClient.java`**
  - HTTP client sử dụng `java.net.http` (Java 17)
  - Method: `postRequest()` để gọi MoMo API

### 3. Service
- **`src/main/java/stnw/service/MomoPaymentService.java`** (interface)
  - `createPaymentRequest()`: Tạo payment request với MoMo
  - `handleIpnCallback()`: Xử lý IPN callback
  - `getMomoConfig()`: Lấy config từ PaymentConfig hoặc web.xml

- **`src/main/java/stnw/service/impl/MomoPaymentServiceImpl.java`** (implementation)
  - Logic tạo payment request, verify signature, update order
  - Xử lý idempotent: không update nếu đã xử lý

### 4. Controller
- **`src/main/java/stnw/controller/payment/MomoCreatePaymentController.java`**
  - Endpoint: `POST /payment/momo/create?orderId=xxx`
  - Tạo payment request, trả về JSON `{ok, payUrl, qrCodeUrl, requestId}`

- **`src/main/java/stnw/controller/payment/MomoIpnController.java`**
  - Endpoint: `POST /payment/momo/ipn`
  - Nhận IPN từ MoMo, verify signature, update order status

- **`src/main/java/stnw/controller/payment/MomoReturnController.java`**
  - Endpoint: `GET /payment/momo/return?orderId=xxx&resultCode=0`
  - Hiển thị kết quả thanh toán (chỉ để hiển thị, IPN mới là nguồn chính xác)

### 5. View
- **`src/main/webapp/views/order/checkout.jsp`** (sửa)
  - Thêm MoMo QR Modal
  - JavaScript xử lý: detect `momoOrderId` trong URL, gọi API, hiển thị QR

### 6. Config
- **`src/main/webapp/WEB-INF/web.xml`** (sửa)
  - Thêm context-param cho MoMo config (fallback nếu không có trong PaymentConfig)

### 7. Service sửa đổi
- **`src/main/java/stnw/controller/order/CheckoutController.java`** (sửa)
  - Nếu payment = MOMO, redirect với `?momoOrderId=xxx` thay vì `/user/orders`

- **`src/main/java/stnw/service/impl/OrderServiceImpl.java`** (sửa)
  - Nếu paymentMethod = MOMO, set `paymentStatus = "Chưa thanh toán"` (chờ IPN)

## Database Migration

Cần thêm các cột sau vào bảng `Orders`:

```sql
ALTER TABLE Orders ADD momo_request_id NVARCHAR(100) NULL;
ALTER TABLE Orders ADD momo_order_id NVARCHAR(100) NULL;
ALTER TABLE Orders ADD momo_trans_id NVARCHAR(100) NULL;
ALTER TABLE Orders ADD momo_pay_url NVARCHAR(MAX) NULL;
ALTER TABLE Orders ADD momo_qr_code_url NVARCHAR(MAX) NULL;
ALTER TABLE Orders ADD momo_signature NVARCHAR(500) NULL;

-- Tạo index cho momo_order_id để query nhanh
CREATE INDEX IX_Orders_MomoOrderId ON Orders(momo_order_id);
```

## Cấu hình

### Option 1: PaymentConfig Table (Ưu tiên)
Thêm record vào bảng `PaymentConfig`:
```sql
INSERT INTO PaymentConfig (payment_method, isActive, display_name, api_endpoint, merchant_id, secret_key, access_key, display_order, createdDate, updatedDate)
VALUES ('MOMO', 1, 'Ví MoMo', 'https://test-payment.momo.vn/v2/gateway/api/create', 
        'MOMOBKUN20180529', 'at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa', 'klm05TvNBzhg7h7j', 3, GETDATE(), GETDATE());
```

### Option 2: web.xml (Fallback)
Đã cấu hình sẵn trong `web.xml` với test keys:
- `MOMO_PARTNER_CODE`: MOMOBKUN20180529
- `MOMO_ACCESS_KEY`: klm05TvNBzhg7h7j
- `MOMO_SECRET_KEY`: at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa
- `MOMO_API_ENDPOINT`: https://test-payment.momo.vn/v2/gateway/api/create

**LƯU Ý**: Không commit secret key thật vào repo. Nếu repo public, hãy để placeholder và hướng dẫn set tại local.

## Hướng dẫn Test

### 1. Setup Local với ngrok/cloudflared

```bash
# Cài đặt ngrok (hoặc cloudflared)
# Chạy ngrok để expose localhost:8080
ngrok http 8080

# Lấy public URL (ví dụ: https://abc123.ngrok.io)
```

### 2. Cấu hình IPN URL và Return URL

Cập nhật trong code hoặc config:
- IPN URL: `https://your-ngrok-url.ngrok.io/AloTra/payment/momo/ipn`
- Return URL: `https://your-ngrok-url.ngrok.io/AloTra/payment/momo/return`

**Lưu ý**: MoMo chỉ gọi IPN về HTTPS URL. Nếu test local, bắt buộc phải dùng ngrok/cloudflared.

### 3. Test Flow

1. **Tạo đơn hàng với MoMo**:
   - Vào trang checkout
   - Chọn "Ví MOMO"
   - Điền thông tin và submit
   - Modal QR sẽ tự động hiển thị

2. **Thanh toán**:
   - Mở MoMo Test app trên điện thoại
   - Quét QR code hoặc nhấn "Mở MoMo để thanh toán"
   - Xác nhận thanh toán trong app

3. **Kiểm tra IPN**:
   - Xem log server để thấy IPN callback
   - Kiểm tra database: `Orders.payment_status` = "Đã thanh toán"
   - Kiểm tra `momo_trans_id` đã được lưu

4. **Kiểm tra kết quả**:
   - Vào `/user/orders`
   - Đơn hàng hiển thị "Đã thanh toán"

### 4. Checklist lỗi thường gặp

- [ ] **Sai signature**: Kiểm tra secret key, raw data format
- [ ] **IPN không gọi được**: Kiểm tra IPN URL phải là HTTPS public URL
- [ ] **HTTP vs HTTPS**: MoMo chỉ gọi IPN về HTTPS
- [ ] **Body đọc sai encoding**: Đảm bảo request encoding UTF-8
- [ ] **Amount mismatch**: Kiểm tra amount (VND, không có decimal)
- [ ] **Idempotent**: IPN gọi lại nhiều lần không làm update sai (đã xử lý)

## Security

- ✅ Verify signature HMAC-SHA256 cho cả request và IPN
- ✅ Xử lý idempotent: IPN gọi lại không update sai
- ✅ Kiểm tra amount khớp với order
- ✅ Chỉ update order khi status = "Chưa thanh toán"
- ✅ Log đầy đủ để debug

## Notes

- IPN là nguồn chính xác để update order status
- Return URL chỉ để hiển thị kết quả cho user
- Nếu IPN fail, có thể retry bằng cách gọi lại `/payment/momo/create` với cùng orderId
- QR code được tạo từ `qrCodeUrl` hoặc từ `payUrl` (fallback)

## Production Checklist

- [ ] Thay test keys bằng production keys
- [ ] Cấu hình IPN URL và Return URL production
- [ ] Test lại toàn bộ flow
- [ ] Monitor IPN logs
- [ ] Setup alert nếu IPN fail nhiều lần
- [ ] Backup và recovery plan

