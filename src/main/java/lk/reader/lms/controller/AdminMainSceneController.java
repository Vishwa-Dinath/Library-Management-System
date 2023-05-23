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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.reader.lms.AppInitializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminMainSceneController {
    public Button btnBooks;
    public ImageView imgAddStudent;
    public ImageView imgAddBook;
    public Button btnStudents;
    public ImageView imgStudent;
    public ImageView imgBook;
    public ImageView imgIssueBook;
    public ImageView imgReturnBook;
    public ImageView imgLogOut;
    public Button btnAddBook;
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

//        lblAdmin.setText("Logged Admin : "+System.getProperties().get("Principal").toString());
    }

    @FXML
    void btnAddStudentOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Add a student");
        stage.setMaximized(false);
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AddStudentScene.fxml")).load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnAddStudent.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
    }

    public void btnStudentsOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setMaximized(false);
        stage.setResizable(true);
        stage.setTitle("Students");
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/StudentViewScene.fxml")).load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnStudents.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
    }


    public void btnAddBookOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setMaximized(false);
        stage.setResizable(true);
        stage.setTitle("Add Books");
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AddBookScene.fxml")).load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnBooks.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
    }

    public void btnBooksOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.setTitle("Books");
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/BookMainScene.fxml")).load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnBooks.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.setTitle("Issue Books");
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/IssuingBookScene.fxml")).load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnBooks.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
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

    public void imgAddStudentOnMouseClicked(MouseEvent mouseEvent) {
        btnAddStudent.fire();
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

    public void imgBookOnMouseClicked(MouseEvent mouseEvent) { btnBooks.fire();
    }

    public void imgStudentOnMouseClicked(MouseEvent mouseEvent) { btnStudents.fire();
    }

    public void imgAddBookOnMouseClicked(MouseEvent mouseEvent) {
        btnAddBook.fire();
    }
}
