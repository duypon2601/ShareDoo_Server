-- Sample data for ShareDoo database

-- USERS
INSERT INTO user (user_id, username, password, email, role, image_url, location)
VALUES
  (1, 'user1', 'password1', 'user1@example.com', 'USER', 'https://randomuser.me/api/portraits/men/1.jpg', 'Hanoi'),
  (2, 'user2', 'password2', 'user2@example.com', 'USER', 'https://randomuser.me/api/portraits/women/2.jpg', 'HCM'),
  (3, 'admin', 'admin', 'admin@example.com', 'ADMIN', 'https://randomuser.me/api/portraits/men/3.jpg', 'Danang');

-- PRODUCTS
INSERT INTO product (product_id, name, description, price_per_day, category, image_url, location, availability_status)
VALUES
  (1, 'Lều cắm trại', 'Lều 4 người, chống nước', 100000, 'CAMPING', 'https://example.com/leu.jpg', 'Hanoi', 'AVAILABLE'),
  (2, 'Bếp gas mini', 'Bếp gas du lịch', 50000, 'CAMPING', 'https://example.com/bep.jpg', 'HCM', 'AVAILABLE'),
  (3, 'Đèn pin', 'Đèn pin siêu sáng', 20000, 'CAMPING', 'https://example.com/denpin.jpg', 'Hanoi', 'AVAILABLE'),
  (4, 'Vợt cầu lông', 'Vợt Yonex chính hãng', 30000, 'SPORT', 'https://example.com/vot.jpg', 'HCM', 'AVAILABLE');

-- ORDERS
INSERT INTO orders (id, order_code, user_id, total_amount, status, description)
VALUES
  (1, 'ORDER_TEST001', 1, 250000.00, 'INIT', 'Test order for payment integration'),
  (2, 'ORDER_TEST002', 2, 100000.00, 'PAID', 'Order paid'),
  (3, 'ORDER_TEST003', 1, 60000.00, 'CANCELLED', 'Order cancelled');

-- ORDER_ITEMS
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, total_price, notes)
VALUES
  (1, 1, 1, 2, 100000.00, 200000.00, 'Thuê 2 ngày'),
  (2, 1, 3, 1, 20000.00, 20000.00, 'Đèn pin đi kèm'),
  (3, 2, 2, 2, 50000.00, 100000.00, 'Bếp gas mini'),
  (4, 3, 4, 2, 30000.00, 60000.00, 'Vợt cầu lông cho 2 người'); 