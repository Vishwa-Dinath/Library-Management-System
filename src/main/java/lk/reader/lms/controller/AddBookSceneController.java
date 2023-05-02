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

public class AddBookSceneController {

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
        txtBookId.setEditable(false);
        btnClear.setDisable(true);
        txtSection.textProperty().addListener((ov,previous,current)->{
            txtSection.getStyleClass().remove("invalid");
            if (!current.matches("[A-Z]{1}")){
                txtSection.selectAll();
                txtSection.requestFocus();
                txtSection.getStyleClass().add("invalid");
                return;
            }
            generateBookID();
        });

    }

    private void generateBookID() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM Books WHERE section=?");
            stm.setString(1, txtSection.getText());
            ResultSet rs = stm.executeQuery();
            String id = null;
            while (rs.next()){
                id = rs.getString("id");
            }
            if (id==null){
                String bookID = String.format("%s-0001",txtSection.getText());
                txtBookId.setText(bookID);
                return;
            }
            String bookID = String.format("%s-%04d",txtSection.getText(),Integer.parseInt(id.substring(2))+1);
            txtBookId.setText(bookID);

        } catch (SQLException e) {
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
        txtSection.getStyleClass().remove("invalid");
        txtQuantity.getStyleClass().remove("invalid");
        if (!dataValid()) return;
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Books (id, name, author, section, quantity, picture) " +
                    "VALUES (?,?,?,?,?,?)");
            stm.setString(1,txtBookId.getText());
            stm.setString(2, txtBookName.getText());
            stm.setString(3, txtAuthor.getText());
            stm.setString(4, txtSection.getText());
            stm.setInt(5,Integer.parseInt(txtQuantity.getText()));

            Blob image = null;
            if (!btnClear.isDisable()){
                Image picture = imgPicture.getImage();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(picture, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"png",bos);
                byte[] bytes = bos.toByteArray();
                image = new SerialBlob(bytes);
            }
            stm.setBlob(6,image);
            stm.executeUpdate();

            for (TextField textField : new TextField[]{txtBookName, txtBookId, txtSection, txtAuthor, txtQuantity}) {
                textField.clear();
            }
            btnClear.fire();
            txtBookName.requestFocus();
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
        if (txtSection.getStyleClass().contains("invalid") || txtSection.getText().isEmpty()){
            txtSection.selectAll();
            txtSection.requestFocus();
            txtSection.getStyleClass().add("invalid");
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
