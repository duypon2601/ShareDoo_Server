# TỔNG HỢP CHỨC NĂNG DỰ ÁN SHAREDOO SERVER

## 📋 Tổng quan dự án
**ShareDoo** là một ứng dụng cho thuê đồ dùng dành cho sinh viên, được xây dựng bằng Spring Boot với kiến trúc RESTful API. Dự án sử dụng JWT authentication, Spring Security, và tích hợp AI để đề xuất sản phẩm.

## 🏗️ Kiến trúc hệ thống
- **Framework**: Spring Boot 3.2.3
- **Java Version**: 21
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Token)
- **Security**: Spring Security với OAuth2
- **Documentation**: Swagger/OpenAPI 3
- **AI Integration**: Hugging Face API
- **Build Tool**: Maven

## 🔐 Hệ thống xác thực và phân quyền

### 1. Authentication (AuthController)
- **Đăng nhập**: `POST /api/login`
  - Xác thực người dùng và trả về JWT token
  - Hỗ trợ validation cho thông tin đăng nhập
  - Trả về thông tin user và token

### 2. Phân quyền người dùng
- **Roles**: 
  - `ADMIN`: Quản trị viên hệ thống
  - `USER`: Người dùng thông thường
- **Security Configuration**:
  - JWT-based authentication
  - Public endpoints không cần xác thực
  - Method-level security với `@PreAuthorize`

## 👥 Quản lý người dùng (UserController)

### 1. Đăng ký người dùng
- **Endpoint**: `POST /api/users/register`
- **Chức năng**: Tạo tài khoản mới cho người dùng
- **Validation**: Kiểm tra thông tin đầu vào

### 2. Quản lý thông tin người dùng
- **Lấy thông tin user**: `GET /api/users/{id}`
- **Cập nhật thông tin**: `PUT /api/users/{id}`
- **Xem tất cả users**: `GET /api/users/get/all` (ADMIN/MANAGER only)
- **Xem users đã xóa**: `GET /api/users/deleted` (với pagination)
- **Khôi phục user**: `PUT /api/users/{id}/restore`
- **Xóa user**: `DELETE /api/users/{id}` (soft delete)

### 3. Thông tin người dùng
- **Fields**: userId, name, email, address, imageUrl, location, username, password
- **Status**: isActive, isVerified, isDeleted
- **Timestamps**: createdAt, updatedAt, lastLoginAt

## 🛍️ Quản lý sản phẩm (ProductController)

### 1. CRUD Operations
- **Tạo sản phẩm**: `POST /api/products`
- **Lấy tất cả sản phẩm**: `GET /api/products` (với pagination và sorting)
- **Lấy sản phẩm theo ID**: `GET /api/products/{id}`
- **Cập nhật sản phẩm**: `PUT /api/products/{id}` (owner hoặc ADMIN)
- **Xóa sản phẩm**: `DELETE /api/products/{id}` (soft delete)

### 2. Tìm kiếm và lọc sản phẩm
- **Endpoint**: `GET /api/products/search`
- **Filters**:
  - Từ khóa tìm kiếm
  - Danh mục sản phẩm
  - Khoảng giá (minPrice, maxPrice)
  - Phân trang và sắp xếp

### 3. Thông tin sản phẩm
- **Fields**: productId, userId, name, description, imageUrl, location
- **Category**: CAMPING, HIKING, FISHING, BICYCLING, CITY, BEACH, MOUNTAINS, FOREST, SKIING, SNOWBOARDING, OTHER
- **Status**: availabilityStatus (available/unavailable)
- **Pricing**: pricePerDay
- **Timestamps**: createdAt, updatedAt, deletedAt

## 🏠 Quản lý cho thuê (RentalController)

### 1. Tạo đơn thuê
- **Endpoint**: `POST /api/rentals`
- **Chức năng**: Tạo đơn thuê sản phẩm
- **Validation**: Kiểm tra tính khả dụng của sản phẩm
- **Logic**: Chỉ cho phép thuê nếu sản phẩm còn trống

### 2. Thông tin đơn thuê
- **Fields**: id, user, product, startDate, endDate, totalPrice, status
- **Timestamps**: createdAt, updatedAt, deletedAt

## 🤖 AI Product Recommendation (ProductRecommendationController)

### 1. Đề xuất sản phẩm thông minh
- **Endpoint**: `POST /api/products/recommendations`
- **Chức năng**: Đề xuất sản phẩm phù hợp dựa trên mô tả sự kiện
- **AI Integration**: Sử dụng Hugging Face API
- **Input**: Mô tả sự kiện và ngân sách tối đa
- **Output**: Danh sách sản phẩm phù hợp

### 2. Quy trình AI
- **Text Processing**: Xử lý và chuẩn hóa input text
- **Category Classification**: Phân loại sự kiện vào các danh mục
- **Product Matching**: Tìm sản phẩm phù hợp với danh mục
- **Price Filtering**: Lọc theo ngân sách

## 🔧 Cấu hình và tiện ích

### 1. Security Configuration
- **JWT Configuration**: HS512 algorithm
- **Public Endpoints**: Login, register, Swagger docs
- **CORS**: Cho phép cross-origin requests
- **Session Management**: Stateless

### 2. Database Configuration
- **JPA/Hibernate**: ORM mapping
- **Soft Delete**: Sử dụng @Where clause
- **Auditing**: Tự động cập nhật timestamps

### 3. API Documentation
- **Swagger/OpenAPI**: Tự động tạo documentation
- **Security Schemes**: JWT Bearer token
- **Response Schemas**: Định nghĩa rõ ràng response format

## 📊 DTOs và Response Format

### 1. Request DTOs
- **UserDTO**: Thông tin người dùng
- **LoginDTO**: Thông tin đăng nhập
- **ProductDTO**: Thông tin sản phẩm
- **RentalRequestDTO**: Thông tin đơn thuê
- **ProductRecommendationRequest**: Yêu cầu đề xuất sản phẩm

### 2. Response DTOs
- **ResLoginDTO**: Response đăng nhập
- **ResUserDTO**: Response thông tin user
- **ResProductDTO**: Response thông tin sản phẩm
- **RentalResponseDTO**: Response đơn thuê
- **RestResponse<T>**: Generic response wrapper

### 3. Error Handling
- **GlobalException**: Xử lý lỗi toàn cục
- **Custom Exceptions**: 
  - AuthHandlerException
  - IdInvalidException
  - NotFoundException

## 🧪 Testing
- **Unit Tests**: Cho tất cả controllers
- **Security Tests**: Spring Security test support
- **Test Coverage**: AuthController, ProductController, UserController, RentalController, ProductRecommendationController

## 🚀 Deployment và Configuration
- **Environment**: application.properties
- **Database Migration**: SQL scripts cho schema
- **Maven Build**: Maven wrapper included
- **Dependencies**: Spring Boot Starters, MySQL, JWT, MapStruct, Lombok

## 📈 Tính năng nổi bật
1. **AI-Powered Recommendations**: Sử dụng AI để đề xuất sản phẩm phù hợp
2. **Flexible Search**: Tìm kiếm và lọc sản phẩm theo nhiều tiêu chí
3. **Role-based Access Control**: Phân quyền chi tiết cho từng chức năng
4. **Soft Delete**: Bảo toàn dữ liệu khi xóa
5. **Pagination**: Hỗ trợ phân trang cho danh sách lớn
6. **JWT Authentication**: Bảo mật cao với token-based auth
7. **Comprehensive API Documentation**: Swagger UI tích hợp
8. **Rental Management**: Quản lý đơn thuê với validation

## 🔗 API Endpoints Summary
```
Authentication:
├── POST /api/login

User Management:
├── POST /api/users/register
├── GET /api/users/{id}
├── GET /api/users/get/all
├── GET /api/users/deleted
├── PUT /api/users/{id}
├── PUT /api/users/{id}/restore
└── DELETE /api/users/{id}

Product Management:
├── POST /api/products
├── GET /api/products
├── GET /api/products/{id}
├── PUT /api/products/{id}
├── DELETE /api/products/{id}
└── GET /api/products/search

Rental Management:
└── POST /api/rentals

AI Recommendations:
└── POST /api/products/recommendations
```

Dự án ShareDoo Server cung cấp một nền tảng hoàn chỉnh cho việc cho thuê đồ dùng với các tính năng hiện đại như AI recommendation, quản lý người dùng, sản phẩm và đơn thuê một cách an toàn và hiệu quả. 