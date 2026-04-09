# 🎓 Trung Tâm Tiếng Anh - Hệ thống Quản lý Học tập (LMS) Cao Cấp

Hệ thống quản lý trung tâm tiếng Anh hiện đại, được tối ưu hóa với công nghệ Trí tuệ nhân tạo (AI) và trải nghiệm người dùng (UX) chuẩn Premium 2024.

---

## 🌟 ĐIỂM NHẤN CÔNG NGHỆ (PREMIUM FEATURES)

*   **Hệ thống AI Gemini 1.5 Flash [SPECIAL]**: Phân tích đề thi từ hình ảnh/văn bản với độ chính xác cao, hỗ trợ JSON Mode và cơ chế tự động thử lại (Retry) vượt qua giới hạn rate-limit của API.
*   **Trực quan hóa dữ liệu (Chart.js)**: Dashboard Admin và Học viên được trang bị biểu đồ trực quan, theo dõi xu hướng đăng ký, GPA và chuyên môn giáo viên.
*   **Virtual Classroom v2**: Phòng học ảo chuyên nghiệp với hiệu ứng Live/Recording, Video routing thông minh và thanh điều khiển Glassmorphism hiện đại.
*   **Thiết kế Modern UI**: Sử dụng font chữ Inter/Outfit, gradient cao cấp và các hiệu ứng micro-animation mượt mà trên toàn bộ hệ thống.

---

## 🛠️ CÔNG NGHỆ SỬ DỤNG (TECH STACK)

Hệ thống được xây dựng trên nền tảng công nghệ mạnh mẽ, đảm bảo tính ổn định và khả năng mở rộng cao.

### 🔹 Backend (Máy chủ & Xử lý)
*   **Ngôn ngữ**: Java 21 (LTS).
*   **Framework**: Spring Boot 3.2.4.
*   **Data Access**: Spring Data JPA (Hibernate).
*   **Database**: MySQL Database.
*   **Security**: Spring Security (Role-based access control).
*   **Tiện ích**: 
    *   Lombok (Reduce boilerplate code).
    *   Apache POI (Xuất báo cáo Excel chuyên nghiệp).
    *   Spring Mail (Xử lý xác thực OTP qua Gmail).

### 🔹 Frontend (Giao diện người dùng)
*   **Engine**: Thymeleaf (Server-side rendering).
*   **UI Framework**: Bootstrap 3.3.6 (Tối ưu hóa layout).
*   **Scripting**: jQuery & JavaScript (ES6+).
*   **Data Visualization**: Chart.js (Biểu đồ tương tác).
*   **Icons & Fonts**: 
    *   Font Awesome (Hệ thống icon phong phú).
    *   Google Fonts (Inter, Roboto, Outfit).
*   **Hiệu ứng**: Owl Carousel, Smoothscroll, Micro-animations.

---

## 🚀 Danh sách Chức năng Hệ thống (30+ Tính năng)

### 1. Quản lý Tài khoản & Bảo mật
1. **Đăng ký học viên**: Đăng ký tài khoản công khai với tính năng xác thực OTP qua Gmail.
2. **Đăng nhập đa vai trò**: Cung cấp giao diện riêng biệt cho Admin, Giáo viên và Học sinh.
3. **Quên mật khẩu thông minh**: Quy trình khôi phục mật khẩu bảo mật qua mã OTP Gmail.
4. **Cơ chế Override Admin (SPECIAL)**: Nút khôi phục mật khẩu chuyên dụng cho Admin giúp bỏ qua các bước xác thực thủ công trong trường hợp khẩn cấp.
5. **Bộ lọc bảo mật (AuthFilter)**: Kiểm soát truy cập dựa trên vai trò, đảm bảo an toàn dữ liệu 🛡️.

### 2. Hệ thống Phòng học ảo (Virtual Classroom) (SPECIAL)
6. **Presence thời gian thực**: Cơ chế nhận diện trạng thái Online/Offline kiểu Google Meet thông qua thuật toán Ping/Active.
7. **Lưới học sinh năng động**: Tự động hiển thị ô trạng thái (Xanh/Đỏ) dựa trên sĩ số thực tế và trạng thái kết nối của học viên.
8. **Đồng bộ Camera & Micro**: Tích hợp bật/tắt thiết bị với biểu tượng trạng thái hiển thị trực tiếp lên lưới ô lớp học.
9. **Chia sẻ màn hình**: Cho phép giáo viên chia sẻ tài liệu trực tiếp trong phòng học.
10. **Đồng hồ đếm ngược**: Hiển thị thời gian còn lại của tiết học một cách chính xác ⏳.
11. **Điều hướng thông minh**: Tự động chuyển hướng giữa màn hình "Chờ lớp", "Vào học" và "Kết thúc" dựa trên thời gian thực tế.

### 3. Kiểm tra & Tích hợp Trí tuệ nhân tạo (SPECIAL)
12. **AI Quiz Generation**: Tự động tạo câu hỏi trắc nghiệm từ văn bản, file Word hoặc **Hình ảnh chụp đề thi** thông qua máy chủ Gemini AI Vision.
13. **Cơ chế AI Retry**: Tự động thử lại (Exponential Backoff) khi gặp tình trạng quá tải API (lỗi 429), đảm bảo tính liên tục.
14. **Công nghệ Anti-cheating**: Phát hiện học sinh chuyển tab hoặc rời khỏi cửa sổ làm bài để cảnh báo gian lận.
15. **Tự động nộp bài (Auto-submit)**: Hệ thống tự ghi nhận điểm và khóa bài làm khi hết thời gian quy định.
16. **Phân tích kết quả & Xuất tài liệu**: Học sinh có thể xem điểm số và **Tải đề thi về máy (.doc)** để ôn tập offline.

### 4. Quản trị hệ thống (Admin Panel)
17. **Quản lý Lớp học & Sĩ số**: Kiểm soát danh sách lớp, giới hạn số lượng học viên và quản lý hình ảnh lớp học.
18. **Quản lý Giáo viên**: Hồ sơ chi tiết giáo viên kèm ảnh đại diện và chuyên môn.
19. **Quản lý Thời khóa biểu**: Thiết kế lịch học hàng tuần theo hệ thống "Tiết học" thông minh.
20. **Phê duyệt Đăng ký**: Kiểm soát luồng học viên đăng ký mới vào các lớp học.
21. **Theo dõi Học phí**: Giám sát trạng thái thanh toán (Đã đóng / Chờ duyệt) của từng học viên.
22. **Kiểm duyệt Đánh giá**: Tiếp nhận và quản lý phản hồi của học viên về chất lượng giảng dạy.
23. **Bảo trì dữ liệu**: Công cụ dọn dẹp (DbCleanup) giúp hệ thống luôn tinh gọn và ổn định.

### 5. Trải nghiệm Học viên (Student Experience)
24. **Bảng điều khiển học tập**: Tổng quan các lớp đang học và thông báo mới nhất.
25. **Thời khóa biểu tương tác**: Xem lịch học theo tuần và join lớp chỉ với một cú click.
26. **Mô phỏng Thanh toán Đa nền tảng (SPECIAL)**: Hỗ trợ thanh toán QR và **Giả lập Mobile Checkout** cực kỳ chuyên nghiệp.
27. **Hồ sơ năng lực (Portfolio)**: Thống kê chi tiết điểm trung bình (GPA), số lớp đã hoàn thành và tổng học phí.
28. **Hệ thống Thông báo**: Nhận thông báo tức thời về tình trạng đăng ký lớp và các cập nhật từ trung tâm.
29. **Quản lý Điểm số Tập trung**: Xem bảng điểm cá nhân và so sánh với điểm trung bình của giáo viên phụ trách.
30. **Đánh giá & Phản hồi**: Học viên có quyền chấm điểm rating và gửi nhận xét trực tiếp cho giáo viên sau mỗi khóa học.

---
*README được tự động cập nhật bởi Antigravity AI Code Assistant.*
