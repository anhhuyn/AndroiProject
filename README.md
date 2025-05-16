# Ứng Dụng Bán Nông Sản – Nối Gần Người Mua và Người Bán

## Nhóm sinh viên thực hiện
- **21110476 - Nguyễn Thị Ánh Huyền**  
- **22110414 - La Nguyễn Phúc Thành**

---

## Giới thiệu

Trong bối cảnh nhu cầu sử dụng công nghệ để kết nối sản phẩm nông nghiệp với người tiêu dùng ngày càng tăng cao, việc xây dựng một **ứng dụng bán nông sản** không chỉ mang ý nghĩa học thuật mà còn là một giải pháp thực tiễn hướng đến phát triển nông nghiệp số.

Thay vì lựa chọn các đề tài đã quen thuộc, nhóm chúng em quyết định tập trung vào **lĩnh vực nông sản** – một lĩnh vực tưởng chừng truyền thống nhưng đang khát khao đổi mới trong thời đại chuyển đổi số.

> Nhiều nông dân và hợp tác xã vẫn gặp khó khăn trong việc tiếp cận khách hàng, quảng bá sản phẩm và tìm đầu ra ổn định. Trong khi đó, người tiêu dùng lại có nhu cầu tìm mua nông sản sạch, rõ nguồn gốc, nhưng lại thiếu thông tin và kênh tiếp cận đáng tin cậy.

Khoảng trống này chính là động lực để nhóm xây dựng **một ứng dụng di động giúp kết nối trực tiếp người mua và người bán**, giảm thiểu trung gian, tăng hiệu quả tiêu thụ và minh bạch hóa thị trường.

Đây cũng là cơ hội để nhóm áp dụng kiến thức đã học về:
- Lập trình di động
- Quản lý cơ sở dữ liệu
- Thiết kế trải nghiệm người dùng
- Tích hợp các công nghệ hiện đại

Ứng dụng không chỉ là sản phẩm công nghệ, mà còn là một **nỗ lực đóng góp nhỏ vào việc nâng tầm giá trị nông sản Việt Nam** trong bối cảnh hội nhập và chuyển mình số.

---

## Nội dung chính

### 1. Mô tả chung

Ứng dụng bán nông sản là một hệ thống **thương mại điện tử đơn giản**, được thiết kế nhằm kết nối **người nông dân hoặc nhà cung cấp nông sản** với **người tiêu dùng**.

Tính năng chính:
- Tìm kiếm sản phẩm theo tên, danh mục hoặc nhà cung cấp
- Xem chi tiết sản phẩm (hình ảnh, giá, đơn vị tính, nguồn gốc)
- Thêm sản phẩm vào giỏ hàng và đặt mua trực tuyến
- Quản lý đơn hàng và theo dõi tình trạng giao hàng
- Lưu địa chỉ giao nhận và đánh giá sản phẩm
- Gợi ý sản phẩm theo mức độ phổ biến hoặc khuyến mãi

---

### 2. Công nghệ sử dụng

#### **Frontend (Mobile App)**
- **Nền tảng**: Android Studio
- **Ngôn ngữ**: Java
- **Thư viện**:
  - `Retrofit` – xử lý HTTP request/response
  - `SharedPreferences` – lưu trữ cục bộ token và thông tin người dùng

#### **Backend (API Server)**
- **Ngôn ngữ**: Java
- **Framework**: Spring Boot
- **Thiết kế API RESTful** để giao tiếp với mobile app

#### **Cơ sở dữ liệu**
- **Hệ quản trị**: Microsoft SQL Server
- **Kiểu dữ liệu**: Quan hệ (Relational Database)
- **Đặc điểm**: Cấu trúc rõ ràng, tối ưu truy vấn, dễ mở rộng

### 3. Hướng dẫn cài đặt Frontend cho EcoVeggieApp (Android)

Hướng dẫn này sẽ giúp thiết lập và chạy ứng dụng frontend EcoVeggieApp trên môi trường Android Studio.

## Yêu cầu tiên quyết

Trước khi bắt đầu, hãy đảm bảo đã cài đặt các phần mềm sau:

* **Android Studio:** Môi trường phát triển tích hợp (IDE) chính thức cho phát triển ứng dụng Android. Có thể tải xuống từ [https://developer.android.com/studio](https://developer.android.com/studio).
* **Android SDK (Software Development Kit):** Bộ công cụ phát triển cần thiết để xây dựng ứng dụng Android. Android Studio sẽ giúp tải xuống các SDK cần thiết trong quá trình cài đặt hoặc cấu hình dự án.
* **Thiết bị Android thực tế hoặc Android Emulator:** Để chạy và kiểm thử ứng dụng. Android Studio cung cấp trình giả lập (emulator) mà có thể cấu hình.

## Các bước cài đặt

1.  **Clone hoặc tải xuống dự án:**
    * **Sử dụng Git:** Mở terminal hoặc command prompt, điều hướng đến thư mục muốn lưu trữ dự án và chạy lệnh sau:
        ```bash
        git clone https://github.com/anhhuyn/AndroiProject.git

2.  **Mở dự án trong Android Studio:**
    * Khởi động Android Studio.
    * Trên màn hình Welcome, chọn **Open an existing project**.
    * Điều hướng đến thư mục chứa dự án đã clone của EcoVeggieApp và chọn thư mục gốc của dự án (thường là thư mục chứa file `build.gradle` cấp dự án). Nhấn **OK**.
    * Android Studio sẽ bắt đầu build dự án và tải xuống các dependencies cần thiết. Quá trình này có thể mất một chút thời gian tùy thuộc vào tốc độ internet và kích thước dự án.

3.  **Kiểm tra và cấu hình `StringHelper.java` (nếu cần):**
    * Trong cửa sổ **Project** của Android Studio (thường ở bên trái), điều hướng đến đường dẫn sau: `app` -> `java` -> (các package) -> `vn.iotstar.ecoveggieapp.helpers`.
    * Mở file `StringHelper.java`.
    * Kiểm tra biến `SERVER_IP`:

        ```java
        package vn.iotstar.ecoveggieapp.helpers;

        public class StringHelper {
            public static final String SERVER_IP = "192.168.1.104";
              ...
        }
        ```

    * Đảm bảo giá trị của `SERVER_IP` trỏ đến địa chỉ IP chính xác của server backend EcoVeggieApp.

4.  **Build và chạy ứng dụng:**
    * **Chọn thiết bị chạy:** Trên thanh công cụ của Android Studio, thấy một dropdown menu hiển thị thiết bị đang được chọn. Nếu chưa có thiết bị nào được cấu hình hoặc kết nối,cần tạo một Android Virtual Device (AVD) bằng cách nhấp vào **Device Manager** hoặc kết nối thiết bị Android thực tế qua USB.
    * **Chạy ứng dụng:** Nhấp vào nút **Run** (biểu tượng mũi tên màu xanh lá cây) trên thanh công cụ.
    * Android Studio sẽ build project và cài đặt ứng dụng lên thiết bị hoặc emulator đã chọn. Ứng dụng sẽ tự động khởi chạy sau khi cài đặt thành công.
    * Theo dõi cửa sổ **Logcat** (thường ở phía dưới của Android Studio) để xem các log và thông tin gỡ lỗi của ứng dụng.

