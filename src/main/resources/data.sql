-- Insert sample products
INSERT INTO product (user_id, name, description, category, availability_status, price_per_day, created_at, updated_at, deleted_at)
VALUES 
(1, 'Bàn tiệc tròn 1.6m', 'Bàn tiệc tròn đường kính 1.6m, màu trắng, chất liệu gỗ công nghiệp cao cấp', 'FURNITURE', 'AVAILABLE', 150000, NOW(), NOW(), NULL),
(1, 'Ghế nhựa tiệc', 'Ghế nhựa cao cấp, màu trắng, chịu lực tốt, phù hợp cho tiệc ngoài trời', 'FURNITURE', 'AVAILABLE', 20000, NOW(), NOW(), NULL),
(1, 'Lều bạt 3x6m', 'Lều bạt chống nắng mưa, kích thước 3x6m, màu xanh dương', 'PARTY', 'AVAILABLE', 300000, NOW(), NOW(), NULL),
(1, 'Bộ âm thanh cơ bản', 'Bộ âm thanh 2 loa, 1 mixer, 2 micro không dây, phù hợp cho sự kiện nhỏ', 'PARTY', 'AVAILABLE', 400000, NOW(), NOW(), NULL),
(1, 'Bàn cocktail', 'Bàn cocktail cao 1.2m, kích thước 60x60cm, màu đen', 'FURNITURE', 'AVAILABLE', 100000, NOW(), NOW(), NULL),
(1, 'Ghế bar', 'Ghế bar cao 75cm, màu đen, chất liệu nhựa cao cấp', 'FURNITURE', 'AVAILABLE', 30000, NOW(), NOW(), NULL),
(1, 'Bàn dài 1.8m', 'Bàn dài 1.8m, rộng 0.8m, cao 0.75m, màu trắng', 'FURNITURE', 'AVAILABLE', 200000, NOW(), NOW(), NULL),
(1, 'Bộ trang trí tiệc', 'Bộ trang trí gồm bóng bay, ruy băng, backdrop', 'DECORATION', 'AVAILABLE', 150000, NOW(), NOW(), NULL),
(1, 'Máy chiếu mini', 'Máy chiếu mini độ phân giải HD, phù hợp cho thuyết trình', 'PARTY', 'AVAILABLE', 250000, NOW(), NOW(), NULL),
(1, 'Bàn tròn 1.2m', 'Bàn tròn đường kính 1.2m, màu trắng, chất liệu gỗ công nghiệp', 'FURNITURE', 'AVAILABLE', 120000, NOW(), NOW(), NULL); 