package lk.reader.lms.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lk.reader.lms.db.DBConnection;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;

public class BookDetailSceneController {

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnSave;

    @FXML
    private ImageView imgPicture;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtAuthor;

    @FXML
    private TextField txtBookId;

    @FXML
    private TextField txtBookName;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtSection;

    @FXML
    private VBox vBox;

    public void initialize() {
        loadDetails();
        txtBookId.setEditable(false);
        txtSection.setEditable(false);
    }

    private void loadDetails() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            String bookID = (String) System.getProperties().get("book");
            ResultSet rs = stm.executeQuery(String.format("SELECT * FROM Books WHERE id='%s'",bookID));
            rs.next();
            txtBookName.setText(rs.getString("name"));
            txtAuthor.setText(rs.getString("author"));
            txtSection.setText(rs.getString("section"));
            txtBookId.setText(bookID);
            txtQuantity.setText(rs.getString("quantity"));
            Blob picture = rs.getBlob("picture");
            if (picture==null){
                Image image = new Image("/icon/no-image-available.png");
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"png",bos);
                picture=new SerialBlob(bos.toByteArray());
            }
            imgPicture.setImage(new Image(picture.getBinaryStream(),230.0,300.0,true,true));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnBrowseOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        if (file==null) return;
        try {
            imgPicture.setImage(new Image(file.toURI().toURL().toString()));
            btnClear.setDisable(false);
        } catch (MalformedURLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to load the image,Please try again!").showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        Image picture = new Image("/icon/no-image-available.png");
        imgPicture.setImage(picture);
        btnClear.setDisable(true);
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        txtBookName.getStyleClass().remove("invalid");
        txtAuthor.getStyleClass().remove("invalid");
        txtQuantity.getStyleClass().remove("invalid");
        if (!dataValid()) return;
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE Books SET name=?, author=?, quantity=?, picture=? WHERE id=?");
            stm.setString(1, txtBookName.getText());
            stm.setString(2, txtAuthor.getText());
            stm.setInt(3,Integer.parseInt(txtQuantity.getText()));
            stm.setString(5,txtBookId.getText());

            Image picture = imgPicture.getImage();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(picture, null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            byte[] bytes = bos.toByteArray();
            Blob image = new SerialBlob(bytes);

            stm.setBlob(4,image);
            stm.executeUpdate();
            new Alert(Alert.AlertType.INFORMATION,"Successfully saved changes !").showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean dataValid() {
        boolean isValid = true;
        if (txtQuantity.getText().isEmpty() || Integer.parseInt(txtQuantity.getText())<=0){
            txtQuantity.selectAll();
            txtQuantity.requestFocus();
            txtQuantity.getStyleClass().add("invalid");
            isValid=false;
        }
        if (!txtAuthor.getText().matches("[A-z ]{3,}")){
            txtAuthor.selectAll();
            txtAuthor.requestFocus();
            txtAuthor.getStyleClass().add("invalid");
            isValid=false;
        }
        if (!txtBookName.getText().matches("[A-z ]{3,}")){
            txtBookName.selectAll();
            txtBookName.requestFocus();
            txtBookName.getStyleClass().add("invalid");
            isValid=false;
        }
        return isValid;
    }

}
