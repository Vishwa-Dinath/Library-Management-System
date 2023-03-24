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
    private Button btnUser;

    @FXML
    void btnAdminOnAction(ActionEvent event) {

    }

    @FXML
    void btnUserOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnUser.getScene().getWindow();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/UserLogInScene.fxml")).load()));
        stage.setTitle("User LogIn");
        stage.show();
        stage.centerOnScreen();
    }

}
