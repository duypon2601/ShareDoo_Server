# Changes Summary: Added imageUrl and location fields to User

## Overview
Added two new fields `imageUrl` and `location` to the User entity and updated all related classes and logic.

## Files Modified

### 1. **Entity**
- **`src/main/java/com/server/ShareDoo/entity/User.java`**
  - Added `imageUrl` field (String) with column name `image_url`
  - Added `location` field (String) with column name `location`

### 2. **DTOs**
- **`src/main/java/com/server/ShareDoo/dto/request/userRequest/UserDTO.java`**
  - Added `imageUrl` field (String, optional)
  - Added `location` field (String, optional)

- **`src/main/java/com/server/ShareDoo/dto/response/ResUserDTO.java`**
  - Added `imageUrl` field (String)
  - Added `location` field (String)

- **`src/main/java/com/server/ShareDoo/dto/response/ResCreateUserDTO.java`**
  - Added `imageUrl` field (String)
  - Added `location` field (String)

- **`src/main/java/com/server/ShareDoo/dto/request/userRequest/CreateUserDTO.java`**
  - Added `imageUrl` field (String, optional)
  - Added `location` field (String, optional)

### 3. **Mapper**
- **`src/main/java/com/server/ShareDoo/mapper/UserMapper.java`**
  - Added mappings for `imageUrl` and `location` in all three mapping methods:
    - `mapToUserDTO`
    - `mapToUser`
    - `mapToResCreateUserDTO`

### 4. **Service**
- **`src/main/java/com/server/ShareDoo/service/userService/UserServiceImpl.java`**
  - Updated `createUser` method to handle new fields
  - Updated `updateUser` method to handle new fields
  - Updated `convertToResUserDTO` method to include new fields
  - Updated `searchUsers` method to include location in search criteria

### 5. **Tests**
- **`src/test/java/com/server/ShareDoo/controller/AuthControllerTest.java`**
  - Updated test to include new required fields (name, address, imageUrl, location)

### 6. **Database Migration**
- **`user_database_migration.sql`** (new file)
  - SQL script to add new columns to existing database
  - Adds `image_url` (VARCHAR(500)) and `location` (VARCHAR(255)) columns

## Database Changes Required

Run the following SQL script on your database:

```sql
ALTER TABLE user ADD COLUMN image_url VARCHAR(500);
ALTER TABLE user ADD COLUMN location VARCHAR(255);
```

## API Changes

The API endpoints remain the same, but now accept and return the new fields:

### Request Body (UserDTO)
```json
{
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "address": "123 Main Street",
  "imageUrl": "https://example.com/avatar.jpg",
  "location": "Ho Chi Minh City",
  "username": "johndoe",
  "password": "password123",
  "role": "USER",
  "isActive": true,
  "isVerified": false,
  "isDeleted": false
}
```

### Response Body (ResUserDTO)
```json
{
  "userId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "address": "123 Main Street",
  "imageUrl": "https://example.com/avatar.jpg",
  "location": "Ho Chi Minh City",
  "username": "johndoe",
  "role": "USER",
  "isActive": true,
  "isVerified": false,
  "isDeleted": false,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "lastLoginAt": "2024-01-01T00:00:00"
}
```

### Create User Request (CreateUserDTO)
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "address": "123 Main Street",
  "imageUrl": "https://example.com/avatar.jpg",
  "location": "Ho Chi Minh City",
  "username": "johndoe",
  "password": "password123",
  "role": "USER"
}
```

## Search Functionality

The search functionality now includes the `location` field in searches, allowing users to search for other users by location.

## Testing

All existing tests have been updated to include the new required fields. The application should work seamlessly with the new fields.

## Notes

- The new fields are optional, so existing functionality will continue to work without modification
- The `imageUrl` field is designed to store URLs to user profile images
- The `location` field is designed to store the user's current location or base location
- Both fields are included in search functionality to improve user discovery 