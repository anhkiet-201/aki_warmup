# AkiWarmup Framework (v1.0)

Framework tự động hóa Android chuyên sâu, tập trung vào mô phỏng hành vi con người (Human Behavior Simulation) cho các ứng dụng như TikTok, Facebook, v.v.

## 🚀 Tính năng nổi bật

- **DSL mạnh mẽ**: Định nghĩa kịch bản (Scene), màn hình (Screen) và hành động (Action) cực kỳ trực quan bằng Kotlin.
- **Mô phỏng con người**:
  - `humanSwipe`: Vuốt theo đường cong Bezier, không phải đường thẳng robotic.
  - `humanType`: Nhập liệu theo cụm (burst), có nhịp điệu và thỉnh thoảng gõ sai rồi xóa sửa.
  - `choose`: Lựa chọn hành động ngẫu nhiên dựa trên trọng số (Weights).
- **Nhận diện màn hình thông minh**: Tự động quét và khớp kịch bản dựa trên ID, Text hoặc cấu trúc UI.
- **Tối ưu hóa tốc độ**: Cấu hình bỏ qua trạng thái Idle của Android để chạy mượt mà trên các ứng dụng video.
- **Hệ thống Log chuẩn hóa**: Theo dõi chi tiết mọi thao tác qua tag `AkiFramework`.

## 🛠 Cấu trúc dự án

- `core/dsl`: Bộ não của framework, chứa định nghĩa kịch bản và các lệnh tương tác.
- `core/human`: Công cụ mô phỏng hành vi người dùng.
- `core/screen`: Bộ nhận diện màn hình tự động.
- `core/loop`: Trình điều khiển vòng lặp hành động và xử lý lỗi/phục hồi.
- `scenes/`: Nơi chứa các kịch bản cụ thể (ví dụ: `TikTokWarmupScene.kt`).

## 📖 Hướng dẫn viết kịch bản (Scene DSL)

```kotlin
val MyScene = scene("my_scene") {
    config {
        targetPackage = "com.example.app"
        recoveryTimeoutMs = 20000L
    }

    screen("Home") {
        detect { 
            // Nhận diện bằng ID hoặc tổ hợp ID + Text
            has(id("com.example.app:id/home_icon")) 
        }

        actions {
            action("Lướt tin", weight = 80) {
                loop {
                    swipeUp() // Vuốt kiểu người
                    wait(random(2000, 5000)) // Đợi ngẫu nhiên 2-5s
                    
                    // Chọn ngẫu nhiên hành vi con
                    choose(
                        0.1f to { doubleTap(center) }, // 10% thả tim
                        0.9f to { wait(1000) }         // 90% chỉ xem
                    )
                }
            }
        }
    }
}
```

## 🏃 Cách chạy

1. Kết nối thiết bị Android (đã bật USB Debugging).
2. Chạy test trong file `AkiFrameworkTest.kt`.
3. Theo dõi tiến trình qua Logcat với filter `AkiFramework`.

## ⚠️ Lưu ý quan trọng
- Luôn ưu tiên dùng `id(...)` thay vì `text(...)` để tốc độ nhận diện đạt mức < 1 giây.
- Sử dụng `endAction()` để thoát nhanh khỏi một Action Group khi cần thiết.
- Dùng `waitSeconds(n)` hoặc `wait(mili)` để điều khiển thời gian chờ.

---
*Phát triển bởi Aki Team.*
