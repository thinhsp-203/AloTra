-- Script: Tự động thêm sizes mặc định (S/M/L) cho các sản phẩm thức uống chưa có size
-- Giá size mặc định:
--   - Size S: +0 VND
--   - Size M: +5,000 VND
--   - Size L: +10,000 VND

-- Lưu ý: Script này chỉ insert cho các sản phẩm thức uống (category.isDrink = 1) chưa có size nào

BEGIN TRANSACTION;

-- Insert Size S (giá +0 VND) cho tất cả thức uống chưa có size S
INSERT INTO [dbo].[ProductSize] ([product_id], [size_name], [price_adjustment])
SELECT 
    p.product_id,
    'S' AS size_name,
    0 AS price_adjustment
FROM [dbo].[Product] p
INNER JOIN [dbo].[Category] c ON p.cate_id = c.cate_id
WHERE c.isDrink = 1
  AND p.product_id NOT IN (
      SELECT DISTINCT ps.product_id 
      FROM [dbo].[ProductSize] ps 
      WHERE ps.size_name = 'S'
  );

-- Insert Size M (giá +5,000 VND) cho tất cả thức uống chưa có size M
INSERT INTO [dbo].[ProductSize] ([product_id], [size_name], [price_adjustment])
SELECT 
    p.product_id,
    'M' AS size_name,
    5000 AS price_adjustment
FROM [dbo].[Product] p
INNER JOIN [dbo].[Category] c ON p.cate_id = c.cate_id
WHERE c.isDrink = 1
  AND p.product_id NOT IN (
      SELECT DISTINCT ps.product_id 
      FROM [dbo].[ProductSize] ps 
      WHERE ps.size_name = 'M'
  );

-- Insert Size L (giá +10,000 VND) cho tất cả thức uống chưa có size L
INSERT INTO [dbo].[ProductSize] ([product_id], [size_name], [price_adjustment])
SELECT 
    p.product_id,
    'L' AS size_name,
    10000 AS price_adjustment
FROM [dbo].[Product] p
INNER JOIN [dbo].[Category] c ON p.cate_id = c.cate_id
WHERE c.isDrink = 1
  AND p.product_id NOT IN (
      SELECT DISTINCT ps.product_id 
      FROM [dbo].[ProductSize] ps 
      WHERE ps.size_name = 'L'
  );

-- Kiểm tra kết quả
SELECT 
    p.product_id,
    p.product_name,
    c.cate_name,
    ps.size_name,
    ps.price_adjustment
FROM [dbo].[Product] p
INNER JOIN [dbo].[Category] c ON p.cate_id = c.cate_id
LEFT JOIN [dbo].[ProductSize] ps ON p.product_id = ps.product_id
WHERE c.isDrink = 1
ORDER BY p.product_id, ps.size_name;

COMMIT TRANSACTION;
GO


