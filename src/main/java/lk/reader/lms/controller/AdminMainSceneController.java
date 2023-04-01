package lk.reader.lms.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.reader.lms.AppInitializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminMainSceneController {

    @FXML
    private Button btnAddAdmin;
    @FXML
    private Button btnAddBook;
    @FXML
    private Button btnAddStudent;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnIssueBook;
    @FXML
    private Button btnLogOut;
    @FXML
    private Button btnReturnBook;
    @FXML
    private Label lblAdmin;
    @FXML
    private Label lblDate;

    public void initialize() {
        lblDate.setText("Date & Time : "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm")));
        KeyFrame key = new KeyFrame(Duration.minutes(1),event ->{
            lblDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm")));
        });
        Timeline timeline = new Timeline(key);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

        lblAdmin.setText("Logged Admin : "+System.getProperties().get("Principal").toString());
    }

    @FXML
    void btnAddAdminOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnAddAdmin.getScene().getWindow();
            stage.setScene(new Scene( new FXMLLoader(AppInitializer.class.getResource("/view/AddAdminScene.fxml")).load()));
            stage.setTitle("Welcome to Wisdom Academy Library");
            stage.setMaximized(true);
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong, Please try again").showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void btnAddBookOnAction(ActionEvent event) {

    }

    @FXML
    void btnAddStudentOnAction(ActionEvent event) {

    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {

    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {

    }

    @FXML
    void btnLogOutOnAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        try {
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AdminLogInScene.fxml")).load()));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong, Could not direct to Log In menu.").showAndWait();
            e.printStackTrace();
        }
        stage.setTitle("Admin LogIn");
        stage.show();
        stage.centerOnScreen();
    }

    public void imgAddAdminOnMouseClicked(MouseEvent mouseEvent) {
        btnAddAdmin.fire();
    }

    public void imgAddStudentOnMouseClicked(MouseEvent mouseEvent) {
        btnAddStudent.fire();
    }

    public void imgAddBookOnMouseClicked(MouseEvent mouseEvent) {
        btnAddBook.fire();
    }

    public void imgIssueBookOnMouseClicked(MouseEvent mouseEvent) {
        btnIssueBook.fire();
    }

    public void imgReturnBookOnMouseClicked(MouseEvent mouseEvent) {
        btnReturnBook.fire();
    }

    public void imgLogOutOnMouseClicked(MouseEvent mouseEvent) {
        btnLogOut.fire();
    }
}
