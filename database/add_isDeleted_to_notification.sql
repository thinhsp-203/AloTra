-- Migration script: Add isDeleted column to Notification table
-- Execute this script on SQL Server database BEFORE starting the application

USE AloTra;
GO

-- Step 1: Add isDeleted column as NULLABLE first (allows adding to non-empty table)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Notification]') AND name = 'isDeleted')
BEGIN
    -- Add column as nullable first
    ALTER TABLE [dbo].[Notification]
    ADD [isDeleted] [bit] NULL;
    
    PRINT 'Step 1: Column isDeleted added as NULLABLE.';
    
    -- Step 2: Update existing records to set isDeleted = 0 (false)
    UPDATE [dbo].[Notification]
    SET [isDeleted] = 0
    WHERE [isDeleted] IS NULL;
    
    PRINT 'Step 2: Existing records updated with isDeleted = 0.';
    
    -- Step 3: Add DEFAULT constraint
    ALTER TABLE [dbo].[Notification]
    ADD CONSTRAINT [DF_Notification_isDeleted] DEFAULT 0 FOR [isDeleted];
    
    PRINT 'Step 3: DEFAULT constraint added.';
    
    -- Step 4: Make column NOT NULL (now safe because all rows have value and default exists)
    ALTER TABLE [dbo].[Notification]
    ALTER COLUMN [isDeleted] [bit] NOT NULL;
    
    PRINT 'Step 4: Column isDeleted set to NOT NULL.';
    PRINT 'Migration completed successfully!';
END
ELSE
BEGIN
    PRINT 'Column isDeleted already exists in Notification table.';
END
GO

