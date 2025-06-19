-- Sample product data for testing
INSERT INTO product (availability_status, category, created_at, description, name, price_per_day, updated_at, user_id) VALUES
('AVAILABLE', 'CAMPING', NOW(), 'High-quality 4-person tent with waterproof material and easy setup', 'Coleman 4-Person Tent', 25.00, NOW(), 1),
('AVAILABLE', 'HIKING', NOW(), 'Professional hiking boots with ankle support and waterproof membrane', 'Salomon Hiking Boots', 15.00, NOW(), 1),
('AVAILABLE', 'FISHING', NOW(), 'Complete fishing rod set with reel and basic accessories', 'Shimano Fishing Kit', 20.00, NOW(), 1),
('AVAILABLE', 'BICYCLING', NOW(), 'Mountain bike with 21-speed gear system and front suspension', 'Trek Mountain Bike', 30.00, NOW(), 1),
('AVAILABLE', 'CITY', NOW(), 'Foldable electric scooter perfect for urban commuting', 'Xiaomi Electric Scooter', 18.00, NOW(), 1),
('AVAILABLE', 'BEACH', NOW(), 'Professional surfboard for beginners and intermediate surfers', 'Wavestorm Surfboard', 35.00, NOW(), 1),
('AVAILABLE', 'MOUNTAINS', NOW(), 'Professional climbing gear set with harness and ropes', 'Black Diamond Climbing Set', 40.00, NOW(), 1),
('AVAILABLE', 'FOREST', NOW(), 'High-quality binoculars for wildlife observation', 'Nikon Binoculars', 12.00, NOW(), 1),
('AVAILABLE', 'SKIING', NOW(), 'Complete ski set including skis, boots, and poles', 'Atomic Ski Package', 45.00, NOW(), 1),
('AVAILABLE', 'SNOWBOARDING', NOW(), 'Professional snowboard with bindings', 'Burton Snowboard', 40.00, NOW(), 1),
('AVAILABLE', 'OTHER', NOW(), 'Portable camping stove with fuel canister', 'MSR Camping Stove', 10.00, NOW(), 1),
('UNAVAILABLE', 'CAMPING', NOW(), 'Luxury camping hammock with mosquito net', 'ENO DoubleNest Hammock', 8.00, NOW(), 1),
('AVAILABLE', 'HIKING', NOW(), 'Lightweight hiking backpack with hydration system', 'Osprey Hiking Backpack', 12.00, NOW(), 1),
('AVAILABLE', 'FISHING', NOW(), 'Professional fishing tackle box with various lures', 'Plano Tackle Box', 7.00, NOW(), 1),
('AVAILABLE', 'BICYCLING', NOW(), 'Bike repair toolkit with essential tools', 'Park Tool Bike Kit', 5.00, NOW(), 1); 