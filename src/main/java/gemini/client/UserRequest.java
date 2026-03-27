package gemini.client;

public class UserRequest {
    public String action;    // Phân biệt 'register' hay 'login'
    public String username;  // Tài khoản
    public String password;  // Mật khẩu
    public String role;      // Vai trò (As A Bidder / As A Seller)

    // Constructor trống cần thiết cho GSON
    public UserRequest() {}

    // Constructor tiện ích để tạo nhanh đối tượng
    public UserRequest(String action, String username, String password, String role) {
        this.action = action;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}