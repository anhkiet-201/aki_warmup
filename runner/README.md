# Hướng dẫn sử dụng Scripts Runner

Thư mục này chứa các script PowerShell dùng để điều khiển và chạy test tự động hóa song song trên nhiều thiết bị Android (hoặc giả lập).

## 🛠 Tech Stack
- **PowerShell**: Ngôn ngữ kịch bản chính để điều phối.
- **ADB (Android Debug Bridge)**: Dùng để giao tiếp và ra lệnh cho các thiết bị.

---

## 📂 Danh sách các Script

### 1. `run_parallel.ps1`
Đây là script lõi (core) chịu trách nhiệm điều phối việc chạy test song song.
- **Tính năng**:
  - Tự động build APK (Debug và AndroidTest).
  - Nhận diện danh sách thiết bị đang kết nối (hoặc đọc từ file).
  - Cài đặt APK lên tất cả thiết bị đồng thời.
  - Kích hoạt chạy test song song thông qua PowerShell Background Jobs.
  - Thu thập kết quả trả về từ App (qua `Instrumentation.finish`) và hiển thị lên Terminal.
  - Lưu log chi tiết nếu thiết bị nào bị lỗi.

- **Tham số**:
  - `-method`: Tên hàm test cụ thể muốn chạy (ví dụ: `autoPost`). Nếu không truyền sẽ chạy toàn bộ class test.
  - `-captions`: Mảng các chuỗi nội dung (caption) để truyền vào test.
  - `-deviceFile`: Đường dẫn đến file `.txt` chứa danh sách thiết bị (mỗi dòng 1 IP/ID thiết bị). Nếu không truyền, script tự quét bằng `adb devices`.

- **Ví dụ sử dụng**:
  ```powershell
  # Chạy test mặc định trên các thiết bị tự động quét được
  ./runner/run_parallel.ps1
  
  # Chạy một hàm test cụ thể và đọc danh sách thiết bị từ file
  ./runner/run_parallel.ps1 -method autoPost -deviceFile "C:\path\to\devices.txt"
  ```

### 2. `run_autoPost.ps1`
Script chuyên biệt được tối ưu riêng để chạy tính năng Auto Post.
- **Tính năng**:
  - Tự động tìm và đọc file `content.txt` ở gốc dự án để lấy danh sách caption (mỗi dòng 1 caption).
  - Tự động truyền danh sách caption này vào `run_parallel.ps1` và kích hoạt hàm test `autoPost`.
- **Tham số**:
  - `-contentPath`: Đường dẫn tùy chỉnh đến file chứa nội dung bài đăng. Nếu không truyền sẽ mặc định lấy file `content.txt` ở gốc dự án.
  - `-deviceFile`: Đường dẫn đến file chứa danh sách thiết bị (mỗi dòng 1 thiết bị).
- **Ví dụ sử dụng**:
  ```powershell
  # Chạy với file content.txt mặc định ở gốc dự án
  ./runner/run_autoPost.ps1
  
  # Chạy với file content tùy chỉnh ở đường dẫn khác
  ./runner/run_autoPost.ps1 -contentPath "D:\du_lieu\my_content.txt"
  
  # Chạy kết hợp cả file nội dung tùy chỉnh và file danh sách thiết bị
  ./runner/run_autoPost.ps1 -contentPath "D:\du_lieu\my_content.txt" -deviceFile "C:\path\to\devices.txt"
  ```

### 3. `utils.ps1`
Chứa các hàm tiện ích dùng chung cho các script khác (Không chạy trực tiếp file này).
- `Set-Utf8Encoding`: Cấu hình Console hiển thị đúng tiếng Việt.
- `Get-AdbDevices`: Đọc danh sách thiết bị (ưu tiên đọc từ file nếu truyền vào, hoặc tự quét thiết bị đang cắm).

### 4. `install_apk.ps1`
Dùng để cài đặt APK lên nhiều thiết bị đồng thời mà không cần chạy test.
- **Tính năng**:
  - Tự động build dự án trước khi cài đặt (có thể bỏ qua).
  - Cài đặt song song lên tất cả thiết bị đang kết nối hoặc danh sách từ file.
  - Hỗ trợ cài đặt cả App APK và Test APK.
- **Tham số**:
  - `-apkPath`: Đường dẫn APK tùy chỉnh.
  - `-noBuild`: Bỏ qua bước build Gradle (dùng khi đã có APK sẵn).
  - `-onlyMain`: Chỉ cài đặt App chính, không cài Test APK.
  - `-deviceFile`: Đường dẫn file danh sách thiết bị.
- **Ví dụ sử dụng**:
  ```powershell
  # Build và cài đặt mặc định lên tất cả thiết bị
  ./runner/install_apk.ps1
  
  # Chỉ cài đặt App chính và bỏ qua bước build
  ./runner/install_apk.ps1 -onlyMain -noBuild
  ```

### 5. `stop_tests.ps1`
Dùng để dọn dẹp và dừng khẩn cấp tất cả các test đang chạy ngầm nếu bạn lỡ tay tắt script chính giữa chừng.

---