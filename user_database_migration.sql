-- Migration script to add imageUrl and location columns to user table
-- Run this script on your database to add the new fields

ALTER TABLE user ADD COLUMN image_url VARCHAR(500);
ALTER TABLE user ADD COLUMN location VARCHAR(255);

-- Add comments to document the new columns
COMMENT ON COLUMN user.image_url IS 'URL of the user profile image';
COMMENT ON COLUMN user.location IS 'Location where the user is based'; 