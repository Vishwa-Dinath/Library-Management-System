package lk.reader.lms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.reader.lms.AppInitializer;
import lk.reader.lms.db.DBConnection;
import lk.reader.lms.util.PasswordEncoder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLogInSceneController {
    @FXML
    private Button btnBack;
    @FXML
    private Button btnLogIn;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUsername;
    @FXML
    private VBox vBox;

    public void initialize() {
        btnLogIn.setDefaultButton(true);
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        AppInitializer.loadMainLogInScene((Stage) btnBack.getScene().getWindow());
    }

    @FXML
    void btnLogInOnAction(ActionEvent event) {
        String username = txtUsername.getText();
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Admin WHERE username=?");
            stm.setString(1,username);
            ResultSet rs = stm.executeQuery();
            rs.next();
            if (!PasswordEncoder.matches(txtPassword.getText(),rs.getString("password"))){
                new Alert(Alert.AlertType.ERROR, "Incorrect Password, Please try again").showAndWait();
                return;
            }
            System.getProperties().put("Principal",rs.getString("username"));
            Stage stage = (Stage)btnLogIn.getScene().getWindow();
            stage.setMaximized(true);
            stage.setResizable(true);
//            stage.sizeToScene();
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AdminMainScene.fxml")).load()));
            stage.setTitle("Welcome to Admin Main Menu");
            stage.show();
            stage.centerOnScreen();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Username does not exist. Please enter a valid username").showAndWait();
            e.printStackTrace();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not direct to Admin Main Menu, Please try again").showAndWait();
            e.printStackTrace();
        }

    }
}


