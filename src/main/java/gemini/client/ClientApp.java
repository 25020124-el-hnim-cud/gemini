package gemini.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("=== DANG KHOI DONG CLIENT JAVAFX ===");
        
        // 1. Load file giao diện register.fxml từ thư mục resources
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = fxmlLoader.load();
        
        // 2. Tao scene (bối cảnh) voi kich thuoc 400x350 pixel
        Scene scene = new Scene(root, 400, 350);
        
        // 3. Cau hinh tieu de va hien thi Stage (màn hình chính)
        stage.setTitle("Ung Dung Ban Hang - Gemini Project");
        stage.setScene(scene);
        stage.setResizable(false); // Khong cho phep phong to/thu nho
        stage.show();
        
        System.out.println("[+] Giao dien da hien len!");
    }

    public static void main(String[] args) {
        // Lệnh này bat JavaFX khoi dong va goi den ham start() o tren
        launch();
    }
}