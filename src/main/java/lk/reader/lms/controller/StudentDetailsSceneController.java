package lk.reader.lms.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.ArrayList;

public class StudentDetailsSceneController {
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnSave;
    @FXML
    private ComboBox<String> cmbDepartment;
    @FXML
    private ComboBox<String> cmbFaculty;
    @FXML
    private DatePicker dtpBOD;
    @FXML
    private ImageView imgPicture;
    @FXML
    private Label lblGender;
    @FXML
    private ListView<String> lstContact;
    @FXML
    private RadioButton rdoFemale;
    @FXML
    private RadioButton rdoMale;
    @FXML
    private ToggleGroup tglGender;
    @FXML
    private TextArea txtAddress;
    @FXML
    private TextField txtContact;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtRegistration;

    public void initialize() {
        btnRemove.setDisable(true);
        txtRegistration.setEditable(false);
        txtName.requestFocus();
        System.out.println(System.getProperties().get("principal"));
        addItems();
        loadStudentDetails();
        lstContact.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            btnRemove.setDisable(current==null);
            if (current==null) return;
            txtContact.setText(current);
        });

        txtName.textProperty().addListener((ov,previous,current)->{
            txtName.getStyleClass().remove("invalid");
            String fullName = txtName.getText();
            if (fullName.length()<3 || !isLetter(fullName)) txtName.getStyleClass().add("invalid");
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
    }
    private boolean isLetter(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) return false;
        }
        return true;
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

    private void loadStudentDetails() {
        String registrationNumber = (String) System.getProperties().get("principal");
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(String.format("SELECT * FROM Student WHERE registration_number='%s'", registrationNumber));
            rs.next();
            String name = rs.getString("name");
            Date dob = rs.getDate("DOB");
            String address = rs.getString("address");
            Gender gender = Gender.valueOf(rs.getString("gender"));
            String faculty = rs.getString("faculty");
            String department = rs.getString("department");
            Blob picture = rs.getBlob("picture");

            txtRegistration.setText(registrationNumber);
            txtName.setText(name);
            dtpBOD.setValue(dob.toLocalDate());
            txtAddress.setText(address);
            tglGender.selectToggle((gender==Gender.MALE)?rdoMale:rdoFemale);
            cmbFaculty.getSelectionModel().select(faculty);
            cmbDepartment.getSelectionModel().select(department);

            if (picture==null){
                Image image = new Image("/icon/no-picture.jpeg");
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"png",bos);
                picture=new SerialBlob(bos.toByteArray());
            }
            imgPicture.setImage(new Image(picture.getBinaryStream(),150.0,150.0,true,true));

            ResultSet rs2 = stm.executeQuery(String.format("SELECT * FROM Student_Contact WHERE registration='%s'", registrationNumber));
            ArrayList<String> contactList = new ArrayList<>();
            while (rs2.next()){
                contactList.add(rs2.getString("contact"));
            }
            lstContact.getItems().addAll(contactList);


        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to load the data,Please go back and try again!").showAndWait();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
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

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String selectedContact = lstContact.getSelectionModel().getSelectedItem();
        if (selectedContact==null) return;
        lstContact.getItems().remove(selectedContact);
    }

    public void imgPictureOnMouseClicked(MouseEvent mouseEvent) throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(imgPicture.getScene().getWindow());
        if (file==null) return;
        imgPicture.setImage(new Image(file.toURI().toURL().toString()));
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        txtName.getStyleClass().remove("invalid");
        dtpBOD.getStyleClass().remove("invalid");
        txtAddress.getStyleClass().remove("invalid");
        lstContact.getStyleClass().remove("invalid");
        lblGender.setTextFill(Color.BLACK);
        cmbFaculty.getStyleClass().remove("invalid");
        cmbDepartment.getStyleClass().remove("invalid");
        if (!dataValid()) return;

        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("UPDATE Student SET name=?, DOB=?, address=?, " +
                    "gender=?, faculty=?, department=?, picture=? WHERE registration_number=?");
            stm.setString(1,txtName.getText());
            stm.setDate(2, new Date(java.sql.Date.valueOf(dtpBOD.getValue()).getTime()));
            stm.setString(3,txtAddress.getText());
            stm.setString(4,(tglGender.getSelectedToggle()==rdoMale)?"MALE":"FEMALE");
            stm.setString(5,cmbFaculty.getSelectionModel().getSelectedItem());
            stm.setString(6,cmbDepartment.getSelectionModel().getSelectedItem());

            Image picture = imgPicture.getImage();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(picture, null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            byte[] bytes = bos.toByteArray();
            Blob image = new SerialBlob(bytes);

            stm.setBlob(7,image);
            stm.setString(8,txtRegistration.getText());
            stm.executeUpdate();

            PreparedStatement stm2 = connection.prepareStatement("DELETE FROM Student_Contact WHERE registration=?");
            stm2.setString(1,txtRegistration.getText());
            stm2.executeUpdate();
            PreparedStatement stm3 = connection.prepareStatement("INSERT INTO Student_Contact (registration, contact) VALUES (?,?)");
            for (String each : lstContact.getItems()) {
                stm3.setString(1, txtRegistration.getText());
                stm3.setString(2,each);
                stm3.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the picture, Please try again").showAndWait();
            e.printStackTrace();
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
        if (txtName.getText().isEmpty() || txtName.getStyleClass().contains("invalid")){
            txtName.selectAll();
            txtName.requestFocus();
            txtName.getStyleClass().add("invalid");
            isValid = false;
        }
        return isValid;
    }
}
