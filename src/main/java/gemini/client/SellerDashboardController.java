package gemini.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SellerDashboardController {
    @FXML
    public void onLogoutButtonClicked(ActionEvent event) {
        System.out.println("-> Seller dang xuat...");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 350));
            stage.show();
        } catch (IOException e) {
            System.out.println("Loi quay ve Login: " + e.getMessage());
        }
    }
}