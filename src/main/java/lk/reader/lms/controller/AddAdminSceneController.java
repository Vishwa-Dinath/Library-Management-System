package lk.reader.lms.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.reader.lms.db.DBConnection;
import lk.reader.lms.model.Admin;
import lk.reader.lms.util.Gender;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AddAdminSceneController {
    public TableView<Admin> tblAdmins;
    public Label lblDate;
    public Label lblTopic;
    public Button btnNew;
    public Button btnBack;
    public Button btnDelete;
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
    private ToggleGroup gender;
    @FXML
    private ImageView imgPicture;
    @FXML
    private Label lblGender;
    @FXML
    private Label lblHead;
    @FXML
    private Label lblLogIn;
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
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtPasswordConfirm;
    @FXML
    private TextField txtUsername;
    @FXML
    private VBox vBox1;
    @FXML
    private VBox vbox2;

    public void initialize() {
        Platform.runLater(btnNew::fire);
        tblAdmins.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblAdmins.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("firstName"));

        lblDate.setText("Date & Time: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm")));
        KeyFrame key = new KeyFrame(Duration.minutes(1),event ->{
            lblDate.setText("Date & Time: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  hh:mm")));
        });
        Timeline timeline = new Timeline(key);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

        loadAdminData();

        tblAdmins.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            btnDelete.setDisable(current==null);
            if (current!=null){
//                for (Node node : new Node[]{txtFirstName, txtLastName, txtAddress, txtContact, lstContact, txtUsername, txtPassword, txtPasswordConfirm}) {
//                    node.setDisable(true);
//                }
                try {
                    lstContact.getItems().clear();
                    txtID.setText(current.getId());
                    txtFirstName.setText(current.getFirstName());
                    txtLastName.setText(current.getLastName());
                    txtAddress.setText(current.getAddress());
                    lstContact.getItems().addAll(current.getContactList());
                    rdoMale.getToggleGroup().selectToggle((current.getGender()==Gender.MALE)?rdoMale:rdoFemale);
                    imgPicture.setImage(new Image(current.getPicture().getBinaryStream()));
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR,"Something went wrong, Could not load the image").showAndWait();
                }
            }
        });
    }

    private void loadAdminData() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Admin");
            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM Admin_Contact WHERE id_no=?");
            PreparedStatement stm3 = connection.prepareStatement("SELECT * FROM Admin_Profile WHERE admin_id=?");
            while (rs.next()){
                String id = rs.getString("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String address = rs.getString("address");
                Gender genderAdmin = Gender.valueOf(rs.getString("gender"));
                String username = rs.getString("username");
                String password = rs.getString("password");
                ArrayList<String> contactList = new ArrayList<>();
                stm2.setString(1,id);
                ResultSet rs2 = stm2.executeQuery();
                while (rs2.next()){
                    contactList.add(rs2.getString("contact"));
                }
                stm3.setString(1,id);
                ResultSet rs3 = stm3.executeQuery();
                Blob picture=getBlobImage(new Image("/icon/no-picture.jpeg"));
                if (rs3.next()){
                    picture = rs3.getBlob(2);
                }
                Admin admin = new Admin(id, firstName, lastName, address, contactList, genderAdmin, picture, username, password);
                tblAdmins.getItems().add(admin);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong, Could not load admin data").showAndWait();
            e.printStackTrace();
        }
    }

    public void btnNewOnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtFirstName, txtLastName, txtContact, txtUsername, txtPassword, txtPasswordConfirm}) {
            textField.clear();
            textField.getStyleClass().remove("invalid");
        }
        gender.selectToggle(null);
        txtAddress.clear();
        lstContact.getItems().clear();
        btnClear.fire();
        tblAdmins.getSelectionModel().clearSelection();
        txtFirstName.requestFocus();
        generateId();

    }

    private void generateId() {
        int id = Integer.parseInt(tblAdmins.getItems().get(tblAdmins.getItems().size()-1).getId().substring(1))+1;
        txtID.setText(String.format("A%03d",id));
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
        if (contactExist()) {
            new Alert(Alert.AlertType.ERROR,"This contact number already exists").showAndWait();
            return;
        }
        lstContact.getItems().add(txtContact.getText());
        txtContact.clear();
        txtContact.requestFocus();


    }
    private boolean contactExist() {
        String contact = txtContact.getText();
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            ResultSet rs = connection.createStatement().executeQuery(String.format("SELECT * FROM Admin_Contact WHERE contact='%s'",contact));
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
            PreparedStatement stm = connection.prepareStatement("INSERT INTO Admin (id, first_name, last_name, address, gender, username, password) " +
                    "VALUES (?,?,?,?,?,?,?)");
            stm.setString(1,txtID.getText());
            stm.setString(2, txtFirstName.getText());
            stm.setString(3, txtLastName.getText());
            stm.setString(4,txtAddress.getText());
            stm.setString(5,((gender.getSelectedToggle()==rdoMale)?"MALE":"FEMALE"));
            stm.setString(6, txtUsername.getText());
            stm.setString(7,txtPassword.getText());
            stm.executeUpdate();

            PreparedStatement stm2 = connection.prepareStatement("INSERT INTO Admin_Contact (id_no, contact) VALUES (?,?)");
            for (String each : lstContact.getItems()) {
                stm2.setString(1, txtID.getText());
                stm2.setString(2,each);
                stm2.executeUpdate();
            }
            Blob image = getBlobImage(new Image("/icon/no-picture.jpeg"));
            if (!btnClear.isDisable()){
                PreparedStatement stm3 = connection.prepareStatement("INSERT INTO Admin_Profile (admin_id, picture) VALUES (?,?)");
                stm3.setString(1, txtID.getText());
                Image picture = imgPicture.getImage();
                image = getBlobImage(picture);
                stm3.setBlob(2,image);
                stm3.executeUpdate();

            }
            Admin admin = new Admin(txtID.getText(),txtFirstName.getText(),txtLastName.getText(),txtAddress.getText(),
                    new ArrayList<>(lstContact.getItems()),(rdoMale.getToggleGroup().getSelectedToggle()==rdoMale)?Gender.MALE:Gender.FEMALE,
                    image,txtUsername.getText(),txtPassword.getText());
            tblAdmins.getItems().add(admin);
            btnNew.fire();



        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to Save the data, Please Try again!").showAndWait();
            throw new RuntimeException(e);
        }

    }

    public Blob getBlobImage(Image image){
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image,null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"png",bos);
            byte[] bytes = bos.toByteArray();
            return new SerialBlob(bytes);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
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

        if (!username.matches("[A-z0-9!@#$%^&*]+")){
            txtUsername.selectAll();
            txtUsername.requestFocus();
            txtUsername.getStyleClass().add("invalid");
            isValid=false;
        }

        if (userNameExists()){
            new Alert(Alert.AlertType.ERROR,"Username is already taken, Try another.").showAndWait();
            txtUsername.selectAll();
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

    private boolean userNameExists() {
        String username = txtUsername.getText();
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT username FROM Admin");
            while(rs.next()){
                if (username.equals(rs.getString(1))) return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Admin selectedAdmin = tblAdmins.getSelectionModel().getSelectedItem();
        if (selectedAdmin==null) return;
        String password = JOptionPane.showInputDialog("Enter your password !");
        if (!password.equals(selectedAdmin.getPassword())){
            new Alert(Alert.AlertType.ERROR,"Incorrect Password").showAndWait();
            return;
        }

        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            stm.executeUpdate(String.format("DELETE FROM Admin_Profile WHERE admin_id='%s'",selectedAdmin.getId()));
            stm.executeUpdate(String.format("DELETE FROM Admin_Contact WHERE id_no='%s'", selectedAdmin.getId()));
            stm.executeUpdate(String.format("DELETE FROM Admin WHERE id ='%s'", selectedAdmin.getId()));

            tblAdmins.getItems().remove(selectedAdmin);
            btnNew.fire();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete the selected Admin").showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage)btnBack.getScene().getWindow();
        try {
            stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/AdminMainScene.fxml")).load()));
            stage.setTitle("Welcome to Admin Main Menu");
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setMaximized(true);
            stage.setResizable(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
