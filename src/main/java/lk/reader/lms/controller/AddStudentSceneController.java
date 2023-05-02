package lk.reader.lms.controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lk.reader.lms.db.DBConnection;
import lk.reader.lms.util.Gender;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class AddStudentSceneController {
    public AnchorPane root;
    public Button btnAdd;
    public Button btnBrowse;
    public Button btnClear;
    public Button btnNew;
    public Button btnRemove;
    public Button btnSave;
    public ComboBox<String> cmbDepartment;
    public ComboBox<String> cmbFaculty;
    public DatePicker dtpBOD;
    public ImageView imgPicture;
    public Label lblDOB;
    public Label lblGender;
    public Label lblTitle;
    public ListView<String> lstContact;
    public RadioButton rdoFemale;
    public RadioButton rdoMale;
    public ToggleGroup tglGender;
    public TextArea txtAddress;
    public TextField txtConfirmPassword;
    public TextField txtContact;
    public TextField txtFullName;
    public TextField txtMemberId;
    public TextField txtPassword;
    public TextField txtRegistration;
    public TextField txtUsername;
    public VBox vBox1;
    public VBox vBox2;

    public void initialize() {
//        Platform.runLater(btnNew::fire);
        btnClear.setDisable(true);
        btnRemove.setDisable(true);
        addItems();

        txtRegistration.textProperty().addListener((ov,previous,current)->{
            txtRegistration.getStyleClass().remove("invalid");
            String registrationNumber = txtRegistration.getText();
            if (registrationNumber.length()>=7){
                if (registrationNumber.length()>7 || Character.isDigit(registrationNumber.charAt(6)) || isLetter(registrationNumber.substring(0,6))){
                    txtRegistration.getStyleClass().add("invalid");
                }
            }
        });
        txtFullName.textProperty().addListener((ov,previous,current)->{
            txtFullName.getStyleClass().remove("invalid");
            String fullName = txtFullName.getText();
            if (fullName.length()<3 || !isLetter(fullName)) txtFullName.getStyleClass().add("invalid");
        });
        txtContact.textProperty().addListener((ov,previous,current)->{
            txtContact.getStyleClass().remove("invalid");
            if (!current.isEmpty()) {
                String contact = txtContact.getText();
                if (contact.length()!=11 || !contact.matches("0\\d{2}-\\d{7}")){
                    txtContact.getStyleClass().add("invalid");
                }
            }
        });
        lstContact.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            btnRemove.setDisable(current==null);
            if (current==null) return;
            txtContact.setText(current);
        });
    }

    private void addItems() {
        cmbFaculty.getItems().addAll("Select a faculty","Engineering","Information Technology","Business","Architecture","Medicine");
        cmbFaculty.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            if (current==null)return;
            switch (current) {
                case "Engineering":
                    cmbDepartment.getItems().clear();
                    cmbDepartment.getItems().addAll("Select a department","Electronic & Telecommunication","Computer Science","Civil",
                            "Mechanical","Electrical","Material Science","Chemical","Earth Resources","Textile","Transport & Logistics");
                    break;
                case "Information Technology":
                    cmbDepartment.getItems().clear();
                    cmbDepartment.getItems().addAll("Select a department","IT","ITM");
                    break;
                case "Business":
                    cmbDepartment.getItems().clear();
                    cmbDepartment.getItems().addAll("Select a department","Accounting","Business Studies","Management");
                    break;
                case "Architecture":
                    cmbDepartment.getItems().clear();
                    cmbDepartment.getItems().addAll("Select a department","Quantity Surveying","Landscape","Town and country Planing","Facility Management");
                    break;
                case "Medicine":
                    cmbDepartment.getItems().clear();
                    cmbDepartment.getItems().addAll("Select a department","Anatomy","Physiology","Surgery and Anaesthesia","Pharmacology","Medical Technology",
                            "Pathology and Forensic Medicine","Microbiology and Parasitology");
                    break;
                default :
                    cmbDepartment.getItems().clear();
                    cmbDepartment.getItems().addAll("Select a Faculty");

            }
        });
    }

    private boolean isLetter(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) return false;
        }
        return true;
    }
    public void btnNewOnAction(ActionEvent event) {
        for (TextField textField : new TextField[]{txtFullName,txtRegistration,txtContact}) {
            textField.clear();
            textField.getStyleClass().remove("invalid");
        }
        dtpBOD.setValue(null);
        tglGender.selectToggle(null);
        txtAddress.clear();
        lstContact.getItems().clear();
        cmbDepartment.getSelectionModel().clearSelection();
        cmbDepartment.getItems().clear();

        cmbFaculty.getSelectionModel().clearSelection();
        cmbFaculty.getItems().clear();
        addItems();
        btnClear.fire();
        txtRegistration.requestFocus();
    }

    public void btnAddOnAction(ActionEvent event) {
        txtContact.getStyleClass().remove("invalid");
        String contact = txtContact.getText();
        if (contact.isEmpty() || txtContact.getStyleClass().contains("invalid")) {
            txtContact.selectAll();
            txtContact.getStyleClass().add("invalid");
            txtContact.requestFocus();
            return;
        }
        if (contactExist()) {
            new Alert(Alert.AlertType.ERROR,"This contact number already exists").showAndWait();
            return;
        }

        lstContact.getItems().add(contact);
        txtContact.clear();
        txtContact.requestFocus();


    }

    private boolean contactExist() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Student_Contact WHERE contact=? ");
            stm.setString(1,txtContact.getText());
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void btnRemoveOnAction(ActionEvent event) {
        String selectedContact = lstContact.getSelectionModel().getSelectedItem();
        if (selectedContact==null) return;
        lstContact.getItems().remove(selectedContact);

    }

    public void btnBrowseOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Picture");
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

    public void btnClearOnAction(ActionEvent event) {
        Image picture = new Image("/icon/no-picture.jpeg");
        imgPicture.setImage(picture);
        btnClear.setDisable(true);
    }

    public void btnSaveOnAction(ActionEvent event) {
        txtRegistration.getStyleClass().remove("invalid");
        txtFullName.getStyleClass().remove("invalid");
        dtpBOD.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");
        lstContact.getStyleClass().remove("invalid");
        lblGender.setTextFill(Color.BLACK);
        cmbFaculty.getStyleClass().remove("invalid");
        cmbDepartment.getStyleClass().remove("invalid");
        if (!dataValid()) return;
        if (registrationExists()){
            new Alert(Alert.AlertType.ERROR,"This registration number already exists, Please check your number again").showAndWait();
            return;
        }
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Student " +
                    "(registration_number, name, DOB, address, gender, faculty, department, picture) VALUES (?,?,?,?,?,?,?,?)");
            stm.setString(1,txtRegistration.getText());
            stm.setString(2,txtFullName.getText());
            stm.setDate(3, new Date(java.sql.Date.valueOf(dtpBOD.getValue()).getTime()));
            stm.setString(4,txtAddress.getText());
            stm.setString(5,(tglGender.getSelectedToggle()==rdoMale)?"MALE":"FEMALE");
            stm.setString(6,cmbFaculty.getSelectionModel().getSelectedItem());
            stm.setString(7,cmbDepartment.getSelectionModel().getSelectedItem());

            Blob image = null;
            if (!btnClear.isDisable()){
                Image picture = imgPicture.getImage();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(picture, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"png",bos);
                byte[] bytes = bos.toByteArray();
                image = new SerialBlob(bytes);
            }
            stm.setBlob(8,image);
            stm.executeUpdate();

            PreparedStatement stm2 = connection.prepareStatement("INSERT INTO Student_Contact (registration, contact) VALUES (?,?)");
            for (String each : lstContact.getItems()) {
                stm2.setString(1, txtRegistration.getText());
                stm2.setString(2,each);
                stm2.executeUpdate();
            }
            btnNew.fire();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the picture, Please try again").showAndWait();
            e.printStackTrace();
        }

    }

    private boolean registrationExists() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Student_Contact WHERE registration=?");
            stm.setString(1, txtRegistration.getText());
            ResultSet rs = stm.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean dataValid() {
        boolean isValid = true;
        if (cmbDepartment.getSelectionModel().getSelectedItem() == null) {
            cmbDepartment.getStyleClass().add("invalid");
            cmbDepartment.requestFocus();
            isValid=false;
        }
        if (cmbFaculty.getSelectionModel().getSelectedItem() == null) {
            cmbFaculty.getStyleClass().add("invalid");
            cmbFaculty.requestFocus();
            isValid=false;
        }
        if (tglGender.getSelectedToggle()==null){
            lblGender.setTextFill(Color.RED);
            isValid=false;
        }
        if (lstContact.getItems().size() == 0) {
            lstContact.getStyleClass().add("invalid");
            txtContact.requestFocus();
            isValid = false;
        }
        if (!txtAddress.getText().matches("[A-z]{3,}")){
            txtAddress.selectAll();
            txtAddress.requestFocus();
            txtAddress.getStyleClass().add("invalid");
            isValid=false;
        }
        if (dtpBOD.getValue()==null){
            dtpBOD.getStyleClass().add("invalid");
            dtpBOD.requestFocus();
            isValid=false;
        }
        if (txtFullName.getText().isEmpty() || txtFullName.getStyleClass().contains("invalid")){
            txtFullName.selectAll();
            txtFullName.requestFocus();
            txtFullName.getStyleClass().add("invalid");
            isValid = false;
        }
        if (txtRegistration.getText().isEmpty() || txtRegistration.getStyleClass().contains("invalid")) {
            txtRegistration.selectAll();
            txtRegistration.requestFocus();
            txtRegistration.getStyleClass().add("invalid");
            isValid=false;
        }
        return isValid;
    }
}










