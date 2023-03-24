package lk.reader.lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lk.reader.lms.controller.db.DBConnection;

import java.io.IOException;
import java.sql.Connection;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
       loadMainLogInScene(primaryStage);
        Connection connection = DBConnection.getDbConnection().getConnection();
        System.out.println(connection);

    }

    public static void loadMainLogInScene(Stage stage){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("/view/MainLogInScene.fxml"));
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle("Reader Library");
            stage.show();
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"Could not direct to Main menu").showAndWait();
            throw new RuntimeException(e);
        }
    }
}
