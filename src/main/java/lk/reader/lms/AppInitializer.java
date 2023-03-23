package lk.reader.lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainLogInScene.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.show();
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);

    }
}
