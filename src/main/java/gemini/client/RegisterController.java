package gemini.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RegisterController {

    // --- CÁC BIẾN LIÊN KẾT VỚI FILE .FXML QUA fx:id ---
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ChoiceBox<String> cbRole;
    @FXML
    private Label lblMessage; // Nhãn thông báo trên giao diện

    private Gson gson = new Gson(); // Anh thợ đóng/mở JSON GSON

    // Hàm này sẽ tự động chạy khi giao diện được load xong
    @FXML
    public void initialize() {
        lblMessage.setText("Moi nhap thong tin...");
    }

    // --- HÀM XỬ LÝ SỰ KIỆN KHI BẤM NÚT "DANG KY" ---
    @FXML
    public void onRegisterButtonClicked() {
        
        // 1. Lay du lieu tu giao dien thuc te
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cbRole.getValue();

        // Kiem tra hop le co ban (Chua băm Bcrypt)
        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Loi: Tai khoan va mat khau khong duoc de trong!");
            lblMessage.setTextFill(Color.RED);
            return;
        }

        lblMessage.setText("Dang ket noi den Server...");
        lblMessage.setTextFill(Color.BLACK);

        // 2. Chay luong mang ngam (Networking phai chay ngam de tranh bi 'Do' giao dien)
        new Thread(() -> {
            
            // Dong goi du lieu thanh JSON bang GSON xir xo
            UserRequest requestData = new UserRequest("register", username, password, role);
            String jsonRequest = gson.toJson(requestData);

            System.out.println("-> Client chuan bi gui: " + jsonRequest);

            // 3. Ket noi den Server va quăng JSON sang cổng 8080
            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                System.out.println("[+] Da ket noi voi Server thanh cong!");
                
                // Gui dữ liệu
                out.println(jsonRequest);

                // Cho đọc phản hồi từ Server (cũng là chuẩn JSON)
                String jsonResponse = in.readLine();
                System.out.println("<- Server tra loi: " + jsonResponse);
                
                // Bóc hộp phản hồi của Server
                UserResponse responseData = gson.fromJson(jsonResponse, UserResponse.class);

                // 4. Cap nhat ket qua len giao dien (Phai dung Platform.runLater vi dang chay o luong ngam)
                Platform.runLater(() -> {
                    if ("success".equals(responseData.status)) {
                        lblMessage.setText(responseData.message);
                        lblMessage.setTextFill(Color.GREEN);
                        
                        // Hien Popup thong bao xịn sò
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("THONG BAO");
                        alert.setHeaderText(null);
                        alert.setContentText("Chúc mừng! Bạn đã đăng ký thành công tài khoản: " + username);
                        alert.showAndWait();
                        
                        // Code chuyen man hinh Login se o day sau nay...
                    } else {
                        lblMessage.setText(responseData.message);
                        lblMessage.setTextFill(Color.RED);
                    }
                });

            } catch (IOException e) {
                // Neu khong tim thay Server
                Platform.runLater(() -> {
                    lblMessage.setText("LOI KET NOI: Khong tim thay Server. Ban da bat Server chua?");
                    lblMessage.setTextFill(Color.RED);
                });
                System.out.println("[!] LOI KET NOI: " + e.getMessage());
            }
        }).start(); // Bắt đầu chạy luồng ngầm
    }

    // Nho import thu vien giong ben LoginController

    @FXML
    public void switchToLogin(javafx.event.ActionEvent event) {
        try {
            System.out.println("-> Chuyen sang man hinh Dang nhap...");
            // Load giao dien Dang nhap
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            // Lay ra cai Cua so (Stage) hien tai
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Gan bối cảnh (Scene) moi vao Cua so
            stage.setScene(new Scene(root, 400, 350));
            stage.show();
        } catch (IOException e) {
            System.out.println("Loi chuyen man hinh: " + e.getMessage());
        }
    }

    // Class nội bộ đơn giản để GSON bóc hộp phản hồi của Server
    private class UserResponse {
        public String status;
        public String message;
    }
}