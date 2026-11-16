# User Service - Test Suite

Bộ test được thiết kế theo cấu trúc hiện tại của repository `phongpcng-jpg/user-service`
(Spring Boot 4.1.2, Java 21, Gradle).

## 1. Nội dung

| Test class | Phạm vi | Kiểu kiểm thử |
|---|---|---|
| `UserMapperTest` | Mapping DTO/entity, trim, lowercase email, nullable fields | Unit |
| `UserServiceImplTest` | CRUD, duplicate username/email, not found, pagination | Unit |
| `UserRepositoryTest` | Derived queries, ignore-case, pagination | JPA integration |
| `UserControllerTest` | CRUD endpoints, status code, validation, malformed JSON | MVC slice |
| `UserServiceApplicationTests` | Application context | Spring integration |

## 2. Dependency cần bổ sung

Thêm dòng sau vào `dependencies` trong `build.gradle`:

```gradle
testRuntimeOnly 'com.h2database:h2'
```

Các Spring Boot test starters hiện có trong repository cung cấp JUnit, Mockito,
AssertJ, MockMvc và các test slices. Không cần thêm các dependency trùng lặp.

## 3. Cách cài đặt

Giải nén ZIP tại thư mục gốc của repository, giữ nguyên cấu trúc `src/test`.

Sau đó thêm H2 vào `build.gradle` và chạy:

```bash
./gradlew clean test
```

Trên Windows:

```powershell
.\gradlew.bat clean test
```

## 4. Lưu ý kỹ thuật

- `UserRepositoryTest` dùng H2 thông qua `@DataJpaTest`.
- `UserControllerTest` dùng `@WebMvcTest` và `@MockitoBean` (Spring Framework/Spring Boot
  phiên bản mới); không khởi động PostgreSQL.
- `UserServiceApplicationTests` override datasource sang H2 để tránh phụ thuộc biến môi trường
  `DB_USERNAME`, `DB_PASSWORD` và PostgreSQL khi chạy test context.
- Bộ test kiểm tra logic đang có; không tự ý thay đổi contract API hoặc nghiệp vụ production.
- Đây là bộ test được tạo từ source code đọc được trên nhánh `main`. Chưa được chạy bằng
  Gradle trong môi trường này; cần chạy lệnh trên repository để xác nhận tương thích thực tế.

## 5. Phạm vi chưa bao phủ

Bộ test hiện tập trung vào Mapper, Service, Repository, Controller và context startup.
Có thể bổ sung riêng `GlobalExceptionHandlerTest` để kiểm thử trực tiếp từng handler,
cũng như JaCoCo để đo line/branch coverage. Không nên tuyên bố đạt coverage 80% trước khi
chạy báo cáo coverage thực tế.
