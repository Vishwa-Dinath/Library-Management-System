package lk.reader.lms.controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.reader.lms.db.DBConnection;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.regex.Pattern;

public class AdminCreateSceneController {
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnSave;
    @FXML
    private ImageView imgPicture;
    @FXML
    private Label lblGender;
    @FXML
    private Label lblHead;
    @FXML
    private ListView<String> lstContact;
    @FXML
    private RadioButton rdoFemale;
    @FXML
    private RadioButton rdoMale;
    @FXML
    private TextArea txtAddress;
    @FXML
    private TextField txtContact;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtID;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtUsername;
    @FXML
    private ToggleGroup gender;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtPasswordConfirm;

    public void initialize() {
        txtID.setText("A001");
        btnRemove.setDisable(true);
        btnClear.setDisable(true);
        txtID.setEditable(false);
        btnSave.setDefaultButton(true);

        lstContact.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            btnRemove.setDisable(current==null);
            txtContact.setText(lstContact.getSelectionModel().getSelectedItem());
            if (lstContact.getItems().size()>0 && lstContact.getStyleClass().contains("invalid")){
                lstContact.getStyleClass().remove("invalid");
            }
        });


        txtFirstName.requestFocus();
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        txtContact.getStyleClass().remove("invalid");
        if (!txtContact.getText().matches("0\\d{2}-\\d{7}" ) || lstContact.getItems().contains(txtContact.getText())){
            txtContact.selectAll();
            txtContact.requestFocus();
            txtContact.getStyleClass().add("invalid");
            return;
        }
        lstContact.getItems().add(txtContact.getText());
        txtContact.clear();
        txtContact.requestFocus();

    }

    @FXML
    void btnBrowseOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));
        File imageFile = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        if (imageFile!=null){
            try {
                imgPicture.setImage(new Image(imageFile.toURI().toURL().toString()));
                btnClear.setDisable(false);
            } catch (MalformedURLException e) {
                new Alert(Alert.AlertType.ERROR,"Failed to load the image,Please try again!").showAndWait();
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        imgPicture.setImage(new Image("/icon/no-picture.jpeg"));
        btnClear.setDisable(true);
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String selectedContact = lstContact.getSelectionModel().getSelectedItem();
        if (selectedContact==null) return;
        lstContact.getItems().remove(selectedContact);

    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        txtFirstName.getStyleClass().remove("invalid");
        txtLastName.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");
        lstContact.getStyleClass().remove("invalid");
        lblGender.setTextFill(Color.BLACK);
        txtUsername.getStyleClass().remove("invalid");
        txtPassword.getStyleClass().remove("invalid");
        txtPasswordConfirm.getStyleClass().remove("invalid");
        if (!dataValid()) return;
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Admin (id, first_name, last_name, address, gender, username, password,picture) " +
                    "VALUES (?,?,?,?,?,?,?,?)");
            stm.setString(1,txtID.getText());
            stm.setString(2, txtFirstName.getText());
            stm.setString(3, txtLastName.getText());
            stm.setString(4,txtAddress.getText());
            stm.setString(5,((gender.getSelectedToggle()==rdoMale)?"MALE":"FEMALE"));
            stm.setString(6, txtUsername.getText());
            stm.setString(7,txtPassword.getText());

            Blob image = null;
            if (!btnClear.isDisable()){
                Image picture = imgPicture.getImage();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(picture,null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"png",bos);
                byte[] bytes = bos.toByteArray();
                image = new SerialBlob(bytes);
            }
            stm.setBlob(8,image);
            stm.executeUpdate();

            PreparedStatement stm2 = connection.prepareStatement("INSERT INTO Admin_Contact (id_no, contact) VALUES (?,?)");
            for (String each : lstContact.getItems()) {
                stm2.setString(1, txtID.getText());
                stm2.setString(2,each);
                stm2.executeUpdate();
            }

            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AdminLogInScene.fxml")).load()));
//            stage.setResizable(false);
            stage.setTitle("Admin LogIn");
            stage.show();
            stage.centerOnScreen();


        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to Save the data, Please Try again!").showAndWait();
            throw new RuntimeException(e);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to Save the picture, Please Try again!").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private boolean dataValid() {
        boolean isValid = true;
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String address = txtAddress.getText();
        Toggle selectedToggle = gender.getSelectedToggle();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtPasswordConfirm.getText();

        Pattern regex4UpperCase = Pattern.compile("[A-Z]+");
        Pattern regex4LowerCase = Pattern.compile("[a-z]+");
        Pattern regex4numbers = Pattern.compile("[0-9]+");
        Pattern regex4Symbols = Pattern.compile("[!@#$%^&*()_+]+");

        if (password.isEmpty() || !password.equals(confirmPassword)){
            txtPasswordConfirm.selectAll();
            txtPasswordConfirm.requestFocus();
            txtPasswordConfirm.getStyleClass().add("invalid");
            isValid=false;
        }

        if (!regex4UpperCase.matcher(password).find() || !regex4LowerCase.matcher(password).find()
                || !regex4numbers.matcher(password).find() || !regex4Symbols.matcher(password).find()){
            txtPassword.requestFocus();
            txtPassword.selectAll();
            txtPassword.getStyleClass().add("invalid");
            isValid=false;
        }
        System.out.println(address);
        if (!username.matches("[A-z0-9!@#$%^&*]+")){
            txtUsername.selectAll();
            txtUsername.requestFocus();
            txtUsername.getStyleClass().add("invalid");
            isValid=false;
        }

        if (selectedToggle==null){
            lblGender.setTextFill(Color.RED);
            rdoMale.requestFocus();
            isValid=false;
        }

        if (lstContact.getItems().size()==0){
            lstContact.getStyleClass().add("invalid");
            txtContact.requestFocus();
            isValid=false;
        }

        if (!address.matches("[A-z]{5,}")){
            txtAddress.selectAll();
            txtAddress.requestFocus();
            txtAddress.getStyleClass().add("invalid");
            isValid=false;
        }

        if (!lastName.matches("[A-z]{3,}")){
            txtLastName.selectAll();
            txtLastName.requestFocus();
            txtLastName.getStyleClass().add("invalid");
            isValid=false;
        }

        if (!firstName.matches("[A-z]{3,}")){
            txtFirstName.selectAll();
            txtFirstName.requestFocus();
            txtFirstName.getStyleClass().add("invalid");
            isValid=false;
        }

        return isValid;
    }

}
