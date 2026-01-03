-- Alter Notification table to use NVARCHAR for message and link columns
-- This fixes font encoding issues with Vietnamese text

USE [AloTra]
GO

-- Alter message column to NVARCHAR
ALTER TABLE [dbo].[Notification]
ALTER COLUMN [message] NVARCHAR(500) NOT NULL
GO

-- Alter link column to NVARCHAR
ALTER TABLE [dbo].[Notification]
ALTER COLUMN [link] NVARCHAR(500) NULL
GO

