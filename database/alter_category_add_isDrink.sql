-- Migration: Thêm cột isDrink vào bảng Category
-- Mục đích: Phân biệt category "Thức uống" (isDrink=1) và "Đồ ăn" (isDrink=0)

-- Thêm cột mới
ALTER TABLE [dbo].[Category]
ADD [isDrink] [bit] NULL;
GO

-- Set giá trị mặc định = 0 (Đồ ăn)
UPDATE [dbo].[Category]
SET [isDrink] = 0
WHERE [isDrink] IS NULL;
GO

-- Set NOT NULL constraint sau khi update data
ALTER TABLE [dbo].[Category]
ALTER COLUMN [isDrink] [bit] NOT NULL;
GO

-- Set default value = 0 cho các record mới
ALTER TABLE [dbo].[Category]
ADD CONSTRAINT [DF_Category_isDrink] DEFAULT (0) FOR [isDrink];
GO

-- Update các category thức uống (dựa trên tên category hiện tại)
-- LƯU Ý: Bạn cần kiểm tra và cập nhật danh sách category names phù hợp với database của bạn
UPDATE [dbo].[Category]
SET [isDrink] = 1
WHERE LOWER([cate_name]) LIKE '%trà%' 
   OR LOWER([cate_name]) LIKE '%cà phê%'
   OR LOWER([cate_name]) LIKE '%sinh tố%'
   OR LOWER([cate_name]) LIKE '%nước%'
   OR LOWER([cate_name]) LIKE '%đồ uống%'
   OR LOWER([cate_name]) LIKE '%thức uống%'
   OR LOWER([cate_name]) LIKE '%smoothie%'
   OR LOWER([cate_name]) LIKE '%juice%';
GO

-- Kiểm tra kết quả
SELECT [cate_id], [cate_name], [isDrink] 
FROM [dbo].[Category]
ORDER BY [isDrink] DESC, [cate_name];
GO


