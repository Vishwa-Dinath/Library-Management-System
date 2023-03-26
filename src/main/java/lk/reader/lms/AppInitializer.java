package lk.reader.lms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lk.reader.lms.db.DBConnection;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        generateSchemaIfNotExist();
       loadMainLogInScene(primaryStage);
    }

    public static void loadMainLogInScene(Stage stage){
        try {
            String src = (adminExists())? "/view/MainLogInScene.fxml" : "/view/AdminCreateScene.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(AppInitializer.class.getResource(src));
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle("Welcome to Wisdom Academy Library");
            stage.show();
            stage.centerOnScreen();
            stage.setResizable(!adminExists());
            stage.setMaximized(!adminExists());
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"Could not direct to Main menu").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private static boolean adminExists() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Admin");
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSchemaIfNotExist() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery("SHOW TABLES ");
            HashSet<String> tableSet = new HashSet<>();
            while (rs.next()) {
                tableSet.add(rs.getString(1));
            }
            if (!tableSet.containsAll(Set.of("Admin","Admin_Profile","Admin_Contact"))){
                stm.execute(readDbScript());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String readDbScript() {
        InputStream is = getClass().getResourceAsStream("/schema.sql");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            StringBuilder dbScript = new StringBuilder();
            while ((line=br.readLine())!=null){
                dbScript.append(line).append("\n");
            }
            return dbScript.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
