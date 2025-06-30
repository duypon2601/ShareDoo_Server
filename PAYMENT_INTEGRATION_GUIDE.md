# Hướng dẫn tích hợp thanh toán PayOS + QR Code

## Tổng quan
Dự án ShareDoo đã được tích hợp thanh toán PayOS với hỗ trợ QR Code, thẻ ATM, và thẻ quốc tế.

## Cấu hình

### 1. Cập nhật application.properties
```properties
# PayOS Payment Configuration
payos.client-id=YOUR_CLIENT_ID
payos.api-key=YOUR_API_KEY
payos.checksum-key=YOUR_CHECKSUM_KEY
payos.return-url=http://localhost:3000/checkout
payos.cancel-url=http://localhost:3000/cancel
payos.webhook-url=http://localhost:8080/api/payment/webhook
```

### 2. Chạy migration database
```sql
-- Chạy file payment_database_migration.sql
```

## API Endpoints

### 1. Tạo đơn hàng
```http
POST /api/payment/orders
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "notes": "Thuê trong 2 ngày"
    }
  ],
  "description": "Đơn hàng thuê sản phẩm"
}
```

### 2. Tạo payment link + QR Code
```http
POST /api/payment/create-payment-link/{orderId}
Authorization: Bearer <JWT_TOKEN>
```

Response:
```json
{
  "statusCode": 200,
  "message": "Payment link created successfully",
  "data": {
    "checkoutUrl": "https://payos.vn/checkout/...",
    "orderCode": "ORDER_ABC12345",
    "orderId": 1,
    "amount": 500000,
    "status": "WAIT_FOR_PAYMENT"
  }
}
```

### 3. Xác nhận thanh toán
```http
POST /api/payment/confirm?orderCode=ORDER_ABC12345
```

### 4. Lấy thông tin đơn hàng
```http
GET /api/payment/orders/{orderId}
Authorization: Bearer <JWT_TOKEN>
```

### 5. Lấy đơn hàng theo mã
```http
GET /api/payment/orders/code/{orderCode}
```

### 6. Lấy đơn hàng của user
```http
GET /api/payment/orders/user
Authorization: Bearer <JWT_TOKEN>
```

### 7. Hủy đơn hàng
```http
POST /api/payment/orders/{orderId}/cancel
Authorization: Bearer <JWT_TOKEN>
```

## Quy trình thanh toán

1. **Tạo đơn hàng**: User tạo đơn hàng với các sản phẩm muốn thuê
2. **Tạo payment link**: Hệ thống tạo link thanh toán từ PayOS
3. **Hiển thị QR Code**: Frontend hiển thị QR từ `checkoutUrl`
4. **Quét QR**: Khách hàng quét mã QR để thanh toán
5. **Thanh toán**: Chuyển đến PayOS để xử lý thanh toán
6. **Callback**: PayOS gọi webhook về hệ thống
7. **Xác nhận**: Hệ thống cập nhật trạng thái đơn hàng

## Trạng thái đơn hàng

- `INIT`: Đơn hàng mới tạo
- `WAIT_FOR_PAYMENT`: Chờ thanh toán
- `PAID`: Đã thanh toán
- `DELIVERING`: Đang giao hàng
- `RECEIVED`: Đã nhận hàng
- `CANCELLED`: Đã hủy

## Frontend Integration

### 1. Tạo đơn hàng
```javascript
const createOrder = async (orderData) => {
  const response = await fetch('/api/payment/orders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(orderData)
  });
  return response.json();
};
```

### 2. Tạo payment link
```javascript
const createPaymentLink = async (orderId) => {
  const response = await fetch(`/api/payment/create-payment-link/${orderId}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};
```

### 3. Hiển thị QR Code
```javascript
const displayQRCode = (checkoutUrl) => {
  // Sử dụng thư viện QR code để hiển thị
  // Hoặc redirect đến checkoutUrl để PayOS hiển thị QR
  window.open(checkoutUrl, '_blank');
};
```

## Bảo mật

- Tất cả API đều yêu cầu JWT authentication
- PayOS sử dụng checksum để verify request
- Webhook endpoint xác thực signature từ PayOS

## Testing

### 1. Test với PayOS Sandbox
- Sử dụng test credentials từ PayOS
- Test các trường hợp thanh toán thành công/thất bại

### 2. Test Webhook
- Sử dụng ngrok để expose local webhook
- Test callback từ PayOS

## Troubleshooting

### Lỗi thường gặp:
1. **Invalid credentials**: Kiểm tra PayOS credentials
2. **Order not found**: Kiểm tra orderId/orderCode
3. **Payment failed**: Kiểm tra log PayOS
4. **Webhook not working**: Kiểm tra URL và signature

### Log monitoring:
```bash
# Xem log payment
tail -f logs/application.log | grep "payment"
```

## Production Deployment

1. **Cập nhật URLs**: Thay đổi return/cancel URLs cho production
2. **SSL**: Đảm bảo HTTPS cho webhook
3. **Monitoring**: Setup monitoring cho payment flow
4. **Backup**: Backup database thường xuyên

## Support

- PayOS Documentation: https://docs.payos.vn/
- ShareDoo Team: support@sharedoo.com 