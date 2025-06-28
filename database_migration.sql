-- Migration script to add imageUrl and location columns to product table
-- Run this script on your database to add the new fields

ALTER TABLE product ADD COLUMN image_url VARCHAR(500);
ALTER TABLE product ADD COLUMN location VARCHAR(255);

-- Add comments to document the new columns
COMMENT ON COLUMN product.image_url IS 'URL of the product image';
COMMENT ON COLUMN product.location IS 'Location where the product is available'; 