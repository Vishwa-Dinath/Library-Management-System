package lk.reader.lms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lk.reader.lms.AppInitializer;

public class UserLogInSceneController {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnLogIn;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUsername;
    @FXML
    void btnBackOnAction(ActionEvent event) {
        AppInitializer.loadMainLogInScene((Stage) btnBack.getScene().getWindow());
    }

    @FXML
    void btnLogInOnAction(MouseEvent event) {

    }

}
