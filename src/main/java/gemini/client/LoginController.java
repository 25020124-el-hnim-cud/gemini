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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    private Gson gson = new Gson();

    @FXML
    public void initialize() {
        lblMessage.setText("Moi dang nhap...");
    }

    @FXML
    public void onLoginButtonClicked() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Loi: Vui long nhap du thong tin!");
            lblMessage.setTextFill(Color.RED);
            return;
        }

        lblMessage.setText("Dang xac thuc...");
        lblMessage.setTextFill(Color.BLACK);

        new Thread(() -> {
            // Action bay gio la "login". Role minh de null vi dang can tim tren Server.
            UserRequest requestData = new UserRequest("login", username, password, null);
            String jsonRequest = gson.toJson(requestData);

            try (Socket socket = new Socket("localhost", 8080);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                out.println(jsonRequest);

                String jsonResponse = in.readLine();
                
                // Boc hop phan hoi tu Server (Co them thuoc tinh role)
                LoginResponse responseData = gson.fromJson(jsonResponse, LoginResponse.class);

                Platform.runLater(() -> {
                    if ("success".equals(responseData.status)) {
                        lblMessage.setText(responseData.message);
                        lblMessage.setTextFill(Color.GREEN);
                        
                        System.out.println("-> Chuyen huong vao man hinh: " + responseData.role);
                        
                        try {
                            // 1. Kiem tra Role la gi de chon dung file FXML
                            String fxmlFile = "";
                            if ("As A Seller".equals(responseData.role)) {
                                fxmlFile = "seller_dashboard.fxml";
                            } else if ("As A Bidder".equals(responseData.role)) {
                                fxmlFile = "bidder_dashboard.fxml";
                            } else {
                                // Neu role khong xac dinh, huy bo
                                System.out.println("Loi: Role khong hop le!");
                                return;
                            }
                            
                            // 2. Load file FXML da chon
                            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
                            
                            // 3. Lay cua so hien tai qua cai Label (vi ham nay chay tu dong ngam, khong co ActionEvent)
                            Stage stage = (Stage) lblMessage.getScene().getWindow();
                            
                            // 4. Gan scene moi (Toi de kich thuoc to hon chut 600x400 cho giong Dashboard)
                            stage.setScene(new Scene(root, 600, 400));
                            stage.show();
                            
                        } catch (IOException e) {
                            System.out.println("Loi load man hinh Dashboard: " + e.getMessage());
                            e.printStackTrace();
                        }
                        
                    } else {
                        lblMessage.setText(responseData.message);
                        lblMessage.setTextFill(Color.RED);
                    }
                });

            } catch (IOException e) {
                Platform.runLater(() -> {
                    lblMessage.setText("LOI KET NOI: Khong tim thay Server!");
                    lblMessage.setTextFill(Color.RED);
                });
            }
        }).start();
    }

    @FXML
    public void switchToRegister(javafx.event.ActionEvent event) {
        try {
            System.out.println("-> Chuyen sang man hinh Dang ky...");
            // Load giao dien Dang ky
            Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
            // Lay ra cai Cua so (Stage) hien tai
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Gan bối cảnh (Scene) moi vao Cua so
            stage.setScene(new Scene(root, 400, 350));
            stage.show();
        } catch (IOException e) {
            System.out.println("Loi chuyen man hinh: " + e.getMessage());
        }
    }

    // Class noi bo nay co them bien 'role' de hung du lieu tu Server
    private class LoginResponse {
        public String status;
        public String message;
        public String role; 
    }
}