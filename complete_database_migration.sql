-- Complete Database Migration Script
-- Run this script to add imageUrl and location fields to both Product and User tables

-- =====================================================
-- PRODUCT TABLE MIGRATION
-- =====================================================
ALTER TABLE product ADD COLUMN image_url VARCHAR(500);
ALTER TABLE product ADD COLUMN location VARCHAR(255);

-- Add comments for Product table
COMMENT ON COLUMN product.image_url IS 'URL of the product image';
COMMENT ON COLUMN product.location IS 'Location where the product is available';

-- =====================================================
-- USER TABLE MIGRATION  
-- =====================================================
ALTER TABLE user ADD COLUMN image_url VARCHAR(500);
ALTER TABLE user ADD COLUMN location VARCHAR(255);

-- Add comments for User table
COMMENT ON COLUMN user.image_url IS 'URL of the user profile image';
COMMENT ON COLUMN user.location IS 'Location where the user is based';

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================
-- Verify the changes
DESCRIBE product;
DESCRIBE user;

-- Show sample data structure
SELECT 
    'product' as table_name,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'product' 
AND COLUMN_NAME IN ('image_url', 'location')
UNION ALL
SELECT 
    'user' as table_name,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'user' 
AND COLUMN_NAME IN ('image_url', 'location'); 