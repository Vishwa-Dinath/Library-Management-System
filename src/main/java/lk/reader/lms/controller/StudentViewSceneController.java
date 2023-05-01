package lk.reader.lms.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.reader.lms.db.DBConnection;
import lk.reader.lms.model.Student;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class StudentViewSceneController {
    public Button btnDelete;
    public Button btnRefresh;
    public ImageView imgRefresh;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnSeeMore;
    @FXML
    private ImageView imgSearch;
    @FXML
    private Label lblTitle;
    @FXML
    private TableView<Student> tblStudents;
    @FXML
    private TextField txtSearch;

    public void initialize() {
        tblStudents.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        tblStudents.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblStudents.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("picture"));

        loadStudents();

        tblStudents.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            btnSeeMore.setDisable(current==null);
            btnDelete.setDisable(current==null);
        });
        txtSearch.textProperty().addListener((ov,previous,current)->{
            if (current.isEmpty()){
                loadStudents();
            }
        });
    }

    private void loadStudents() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Student");
            while (rs.next()){
                String registrationNumber = rs.getString("registration_number");
                String name = rs.getString("name");
                Blob picture = rs.getBlob("picture");
                if (picture==null){
                    Image image = new Image("/icon/no-picture.jpeg");
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage,"png",bos);
                    picture=new SerialBlob(bos.toByteArray());
                }
                ImageView preview = new ImageView();
                preview.setImage(new Image(picture.getBinaryStream(),150.0,150.0,true,true));
                Student student = new Student(registrationNumber, name, preview);
                tblStudents.getItems().add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String search = txtSearch.getText();
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Student WHERE registration_number LIKE ? OR name LIKE ? " +
                    "OR DOB LIKE ? OR address LIKE ? OR gender LIKE ? OR faculty LIKE ? OR department LIKE ?");
            stm.setString(1,search+"%");
            stm.setString(2,search+"%");
            stm.setString(3,search+"%");
            stm.setString(4,search+"%");
            stm.setString(5,search+"%");
            stm.setString(6,search+"%");
            stm.setString(7,search+"%");
            ResultSet rs = stm.executeQuery();
            tblStudents.getItems().clear();
            while (rs.next()){
                String registrationNumber = rs.getString("registration_number");
                String name = rs.getString("name");
                Blob picture = rs.getBlob("picture");
                if (picture==null){
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(new Image("/icon/no-picture.jpeg"), null);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage,"png",bos);
                    picture = new SerialBlob(bos.toByteArray());
                }
                ImageView preview = new ImageView();
                preview.setImage(new Image(picture.getBinaryStream(), 150.0, 150.0, true, true));
                Student student = new Student(registrationNumber, name, preview);
                tblStudents.getItems().add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnSeeMoreOnAction(ActionEvent event) throws IOException {
        System.getProperties().put("principal", tblStudents.getSelectionModel().getSelectedItem().getRegistrationNumber());
        Stage stage = new Stage();
        stage.setResizable(true);
        stage.setMaximized(false);
        stage.setTitle("Student Details");
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/StudentDetailsScene.fxml")).load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(btnSeeMore.getScene().getWindow());
        stage.show();
        stage.centerOnScreen();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Student selectedStudent = tblStudents.getSelectionModel().getSelectedItem();
        if (selectedStudent==null) return;
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            stm.executeUpdate(String.format("DELETE FROM Student_Contact WHERE registration='%s'",selectedStudent.getRegistrationNumber()));
            stm.executeUpdate(String.format("DELETE FROM Student WHERE registration_number='%s'", selectedStudent.getRegistrationNumber()));
            tblStudents.getItems().remove(selectedStudent);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to delete the selected student, Please try again!").showAndWait();
            e.printStackTrace();
        }
    }

    public void btnRefreshOnAction(ActionEvent actionEvent) {
        tblStudents.getItems().clear();
        loadStudents();
    }

    @FXML
    void imgSearchOnMouseClicked(MouseEvent event) {btnSearch.fire();}

    public void imgRefreshOnMouseClicked(MouseEvent mouseEvent) {
        btnRefresh.fire();
    }
}
