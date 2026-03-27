module gemini.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson; // Thêm dòng này để GSON hoạt động được
    requires java.sql;

    // Mở package này ra để JavaFX FXMLLoader và GSON có thể truy cập
    opens gemini.client to javafx.fxml, com.google.gson;
    exports gemini.client;
    opens gemini to com.google.gson;
    exports gemini;
}