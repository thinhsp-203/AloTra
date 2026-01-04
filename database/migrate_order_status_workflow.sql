-- ============================================================
-- Migration Script: Chuẩn hóa Order Status Workflow
-- Mục đích: Map các trạng thái cũ sang trạng thái mới
-- Ngày tạo: $(date)
-- ============================================================

-- BƯỚC 1: Cập nhật các đơn hàng có status = "Hủy Đơn" sang "Hủy bởi khách" hoặc "Hủy bởi shop"
-- Lưu ý: Vì không thể phân biệt được ai hủy (user hay shop) từ status cũ,
-- nên mặc định set là "Hủy bởi khách" (vì thường user hủy trước khi admin xác nhận)

-- Cập nhật "Hủy Đơn" -> "Hủy bởi khách" (mặc định)
UPDATE Orders
SET order_status = 'Hủy bởi khách'
WHERE order_status = 'Hủy Đơn';

-- Nếu có logic đặc biệt: đơn đã từng là "Đang chuẩn bị" hoặc sau đó thì là "Hủy bởi shop"
-- (Cần kiểm tra lại dữ liệu thực tế trước khi chạy)
-- UPDATE Orders
-- SET order_status = 'Hủy bởi shop'
-- WHERE order_status = 'Hủy Đơn' 
--   AND order_id IN (
--       SELECT DISTINCT order_id FROM OrderDetail WHERE ... -- logic xác định đơn đã được xác nhận
--   );

-- BƯỚC 2: Đảm bảo các trạng thái khác đúng chuẩn (nếu có lệch)
-- Không cần thiết vì các status khác đã đúng:
-- "Chờ xác nhận" -> "Chờ xác nhận"
-- "Đang chuẩn bị" -> "Đang chuẩn bị"
-- "Đang giao" -> "Đang giao"
-- "Hoàn thành" -> "Hoàn thành"

-- BƯỚC 3: Thêm trạng thái mới "Từ chối" nếu chưa có trong DB
-- (Không cần thiết vì chỉ là string, không cần ALTER TABLE)

-- BƯỚC 4: Kiểm tra Payment Status
-- Đảm bảo payment_status đúng format:
-- "Chưa thanh toán", "Chờ thanh toán", "Đã thanh toán", "Đã hoàn tiền", "Thất bại"

-- Xác nhận kết quả:
-- SELECT DISTINCT order_status FROM Orders ORDER BY order_status;
-- SELECT DISTINCT payment_status FROM Orders ORDER BY payment_status;

-- ============================================================
-- ROLLBACK SCRIPT (Nếu cần rollback)
-- ============================================================
-- UPDATE Orders
-- SET order_status = 'Hủy Đơn'
-- WHERE order_status IN ('Hủy bởi khách', 'Hủy bởi shop');

