package lk.reader.lms.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.reader.lms.db.DBConnection;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class BookMainSceneController {

    public TextField txtSearch;
    public Label lblTitle;
    @FXML
    private FlowPane flowPlane;

    @FXML
    private AnchorPane root;

    public void initialize() {
        loadBookDetails();
        txtSearch.textProperty().addListener((ov,previous,current)->{
            if (current==null) return;;
            flowPlane.getChildren().removeAll(flowPlane.getChildren());
            Connection connection = DBConnection.getDbConnection().getConnection();
            try {
                PreparedStatement stm = connection.prepareStatement("SELECT id,picture FROM Books WHERE id LIKE ? OR name LIKE ? OR author LIKE ?" +
                        "OR section LIKE ?");
                stm.setString(1,"%"+current+"%");
                stm.setString(2,"%"+current+"%");
                stm.setString(3,"%"+current+"%");
                stm.setString(4,"%"+current+"%");
                ResultSet rs = stm.executeQuery();
                while (rs.next()){
                    Blob picture = rs.getBlob("picture");
                    String id = rs.getString("id");
                    ImageView imgBook = new ImageView();
                    imgBook.setImage(new Image(picture.getBinaryStream(),220,280,true,true));
                    Button btnBook = new Button();
                    btnBook.setPadding(new Insets(20.0,20.0,20.0,20.0));
                    btnBook.setGraphic(imgBook);
                    btnBook.setCursor(Cursor.HAND);
                    btnBook.setOnAction(actionEvent -> {
                        System.getProperties().put("book",id);
                        Stage stage = new Stage();
                        stage.setMaximized(false);
                        stage.setResizable(true);
                        stage.setTitle("Students");
                        try {
                            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/BookDetailScene.fxml")).load()));
                        } catch (IOException e) {
                            new Alert(Alert.AlertType.ERROR,"Something went wrong. Failed to load details of selected book. Please try again.").showAndWait();
                            throw new RuntimeException(e);
                        }
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(btnBook.getScene().getWindow());
                        stage.show();
                        stage.centerOnScreen();
                    });
                    flowPlane.getChildren().add(btnBook);
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR,"Failed to search the entered items").showAndWait();
                e.printStackTrace();
            }
        });

    }

    public void loadBookDetails() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT picture,id from Books");
            while (rs.next()){
                Blob picture = rs.getBlob("picture");
                if (picture==null){
                    Image image = new Image("/icon/no-image-available.png");
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage,"png",bos);
                    byte[] bytes = bos.toByteArray();
                    picture = new SerialBlob(bytes);
                }
                String id = rs.getString("id");
                ImageView preview = new ImageView();
                preview.setImage(new Image(picture.getBinaryStream(),220,280,true,true));
                Button btnBook = new Button();
                btnBook.setPadding(new Insets(20.0,20.0,20.0,20.0));
                btnBook.setGraphic(preview);
                btnBook.setCursor(Cursor.HAND);
                btnBook.setOnAction(actionEvent -> {
                    System.getProperties().put("book",id);
                    Stage stage = new Stage();
                    stage.setMaximized(false);
                    stage.setResizable(true);
                    stage.setTitle("Book Details");
                    try {
                        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/BookDetailScene.fxml")).load()));
                    } catch (IOException e) {
                        new Alert(Alert.AlertType.ERROR,"Something went wrong. Failed to load details of selected book. Please try again.").showAndWait();
                        throw new RuntimeException(e);
                    }
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initOwner(btnBook.getScene().getWindow());
                    stage.show();
                    stage.centerOnScreen();
                });
                flowPlane.getChildren().add(btnBook);
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong. Failed to load books").showAndWait();
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
