# 🛒 HỆ THỐNG QUẢN LÝ CỬA HÀNG

## Giới thiệu
Hệ thống quản lý cửa hàng được xây dựng bằng ngôn ngữ **Kotlin**, áp dụng các kiến thức từ cơ bản đến nâng cao.  
Mục tiêu: quản lý **sản phẩm**, **khách hàng**, và **đơn hàng** một cách hiệu quả.  

---

## Chức năng chính
### 1. Quản lý sản phẩm
- Thêm sản phẩm (electronics, clothing)  
- Tìm kiếm sản phẩm  
- Cập nhật số lượng kho  
- Hiển thị thông tin sản phẩm
- Báo cáo sản phẩm

### 2. Quản lý khách hàng
- Thêm khách hàng vào hệ thống  
- Cập nhật thông tin khách hàng  
- Hiển thị nhóm khách hàng  
- Gửi thông báo cho khách hàng  

### 3. Quản lý đơn hàng
- Tạo đơn hàng  
- Xem tất cả đơn hàng  
- Tìm kiếm đơn hàng  
- Báo cáo đơn hàng
  
### 4. Phân tích và báo cáo
- Báo cáo kho
- Báo cáo doanh thu

---

## Cấu trúc dự án

<p align="center">
  <img src="images/project-structure.png" alt="Cấu trúc dự án" width="500"/>
</p>

---

## Kiến thức áp dụng trong Kotlin

### 1. Cú pháp cơ bản
- **Biến & Hằng số**
- **Kiểu dữ liệu**: `Int`, `Float`, `Boolean`, `String`, `Char`, `Double`, `Long`  
- **Toán tử**: `+`, `-`, `*`, `/`, `%`, `+=`, `-=`, `==`, `!=`, `>`, `<`, `&&`, `||`  
- **Ép kiểu**: `toInt()`, `toString()`, `toDouble()`, ...  
- **Chuỗi & String Template**: `"Hello, $name"`  

### 2. Cấu trúc điều kiện & vòng lặp
- `if - else`  
- `when` (tương tự `switch-case` trong Java)  
- `for (i in 1..10) { }`  
- `while` & `do-while`  

### 3. Collection (Danh sách dữ liệu)
- `List` (Danh sách)  
- `Set` (Tập hợp không trùng lặp)  
- `Map` (Dạng key-value)  
- Một số hàm quan trọng:  
  - `map()`, `filter()`, `reduce()`  
  - `forEach()`, `first()`, `last()`  

### 4. Hàm & Extension Function
- Hàm thông thường  
- Hàm một dòng  
- **Default Parameter** & **Named Argument**  
- **Extension Function**  

### 5. Null Safety 
- **Nullable (?)** và **Non-nullable**  
- **Elvis Operator (`?:`)**  
- **Safe Call (`?.`)**  
- **Not-null Assertion (`!!`)**  

### 6. Lập trình hướng đối tượng (OOP)
- **Class & Object**  
- **Constructor** (Primary & Secondary)  
- **Kế thừa** (`open class`, `override`)  
- **Interface & Abstract Class**  
- **Data Class**  
- **Singleton & Companion Object**  

---

## Công nghệ
- **Ngôn ngữ**: Kotlin  
- **Paradigm**: OOP + Functional features của Kotlin  
