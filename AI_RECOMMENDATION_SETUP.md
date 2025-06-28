# HƯỚNG DẪN CẤU HÌNH VÀ SỬ DỤNG AI RECOMMENDATION

## 🔧 Cấu hình cần thiết

### 1. **Hugging Face API Key**
1. Đăng ký tài khoản tại [Hugging Face](https://huggingface.co/)
2. Tạo API token tại [Settings > Access Tokens](https://huggingface.co/settings/tokens)
3. Cập nhật trong `application.properties`:
```properties
huggingface.api.key=YOUR_ACTUAL_API_KEY_HERE
```

### 2. **Cấu hình AI Recommendation**
```properties
# AI Recommendation Config
ai.recommendation.threshold=0.3
ai.recommendation.max-recommendations=10
ai.recommendation.fallback-enabled=true
```

## 🧪 Kiểm tra và test

### 1. **Test endpoint**
```bash
POST /api/products/recommendations/test
```

### 2. **Test với dữ liệu thực**
```bash
POST /api/products/recommendations
Content-Type: application/json

{
  "eventDescription": "I want to go camping in the mountains this weekend",
  "maxPricePerDay": 100
}
```

## 🔍 Debug và Troubleshooting

### 1. **Kiểm tra logs**
- Xem logs để kiểm tra API calls
- Kiểm tra response từ Hugging Face
- Xem categories được chọn

### 2. **Các vấn đề thường gặp**

#### API Key không đúng
```
Error: Hugging Face API key not configured properly
Solution: Cập nhật API key trong application.properties
```

#### Model không phản hồi
```
Error: Hugging Face API call failed
Solution: Kiểm tra internet connection và API key
```

#### Không có sản phẩm phù hợp
```
Info: No categories found, returning all available products
Solution: Thêm sản phẩm vào database hoặc điều chỉnh threshold
```

## 📊 Cải thiện độ chính xác

### 1. **Điều chỉnh threshold**
- Giảm threshold (0.1-0.3) để có nhiều categories hơn
- Tăng threshold (0.5-0.7) để có ít categories nhưng chính xác hơn

### 2. **Thêm keywords**
Cập nhật `KEYWORD_CATEGORY_MAPPING` trong `ProductRecommendationService.java`:
```java
mapping.put("new_keyword", Arrays.asList(Category.RELEVANT_CATEGORY));
```

### 3. **Sử dụng model tốt hơn**
Thay đổi model trong `application.properties`:
```properties
# Model tốt hơn cho text classification
huggingface.api.url=https://api-inference.huggingface.co/models/MoritzLaurer/DeBERTa-v3-large-mnli-fever-anli-ling-wanli
```

## 🚀 Monitoring và Analytics

### 1. **Metrics cần theo dõi**
- Số lượng API calls thành công/thất bại
- Thời gian response của AI service
- Số lượng recommendations trả về
- Categories được chọn phổ biến

### 2. **Log analysis**
```bash
# Tìm logs liên quan đến AI
grep "AI Classification result" logs/application.log
grep "Hugging Face API" logs/application.log
```

## 📈 Tối ưu hóa Performance

### 1. **Caching**
- Cache kết quả AI cho các event description tương tự
- Sử dụng Redis hoặc in-memory cache

### 2. **Batch processing**
- Xử lý nhiều requests cùng lúc
- Giảm số lượng API calls

### 3. **Fallback strategy**
- Keyword-based fallback khi AI fail
- Popular products fallback
- Category-based fallback

## 🔒 Security Considerations

### 1. **API Key Security**
- Không commit API key vào git
- Sử dụng environment variables
- Rotate API keys định kỳ

### 2. **Input Validation**
- Validate event description length
- Sanitize input text
- Rate limiting cho API calls

## 📝 Best Practices

### 1. **Event Description**
- Mô tả chi tiết và cụ thể
- Bao gồm địa điểm, hoạt động, thời gian
- Ví dụ: "Camping trip in mountain forest for 3 days"

### 2. **Product Categories**
- Đảm bảo sản phẩm được phân loại đúng
- Cập nhật categories khi cần thiết
- Kiểm tra availability status

### 3. **Testing**
- Test với nhiều loại event khác nhau
- Test fallback scenarios
- Performance testing với load cao 