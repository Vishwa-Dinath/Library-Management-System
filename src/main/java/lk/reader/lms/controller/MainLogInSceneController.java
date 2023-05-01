package lk.reader.lms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainLogInSceneController {
    @FXML
    private Button btnAdmin;

    @FXML
    void btnAdminOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnAdmin.getScene().getWindow();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AdminLogInScene.fxml")).load()));
        stage.setTitle("Admin LogIn");
        stage.show();
        stage.centerOnScreen();
    }

}
