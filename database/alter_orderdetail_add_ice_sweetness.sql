-- Thêm cột ice_level và sweetness_level vào bảng OrderDetail
ALTER TABLE [dbo].[OrderDetail] 
ADD [ice_level] NVARCHAR(20) NULL,
    [sweetness_level] NVARCHAR(20) NULL;

GO


