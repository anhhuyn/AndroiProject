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



