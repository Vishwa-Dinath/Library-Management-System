package lk.reader.lms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.reader.lms.db.DBConnection;
import lk.reader.lms.model.BookIssue;
import lk.reader.lms.model.Status;

import java.sql.*;
import java.time.LocalDate;

public class IssuingBookSceneController {

    public AnchorPane root;
    public Button btnNew;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private DatePicker dtpIssue;

    @FXML
    private DatePicker dtpReturn;

    @FXML
    private Label lblTitle;

    @FXML
    private TableView<BookIssue> tblIssue;

    @FXML
    private TextField txtBookID;

    @FXML
    private TextField txtBookName;

    @FXML
    private TextField txtIssueNumber;

    @FXML
    private TextField txtStudentID;

    @FXML
    private TextField txtStudentName;

    public void initialize() {
//        txtBookID.setEditable(false);
        btnDelete.setDisable(true);
        tblIssue.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("issueId"));
        tblIssue.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("bookID"));
        tblIssue.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("bookName"));
        tblIssue.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("studentID"));
        tblIssue.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("studentName"));
        tblIssue.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        tblIssue.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        tblIssue.getColumns().get(7).setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDetails();

        tblIssue.getSelectionModel().selectedItemProperty().addListener((ov,previous,current)->{
            btnDelete.setDisable(current==null);
            if (current==null) return;
            txtIssueNumber.setText(Integer.toString(current.getIssueId()));
            txtBookID.setText(current.getBookID());
            txtBookName.setText(current.getBookName());
            txtStudentID.setText(current.getStudentID());
            txtStudentName.setText(current.getStudentName());
            dtpIssue.setValue(current.getIssueDate());
            dtpReturn.setValue(current.getReturnDate());
        });

    }

    private void loadDetails() {
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            Statement stm2 = connection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Book_Issues");
            while(rs.next()){
                int issueID = rs.getInt("id");
                Date issueDate = rs.getDate("issue_date");
                Date returnDay = rs.getDate("return_day");
                String bookId = rs.getString("book_id");
                ResultSet rs2 = stm2.executeQuery(String.format("SELECT name FROM Books WHERE id ='%s'", bookId));
                rs2.next();
                String bookName = rs2.getString("name");
                String studentId = rs.getString("student_id");
                rs2 = stm2.executeQuery(String.format("SELECT * FROM Student WHERE registration_number='%s'",studentId));
                rs2.next();
                String studentName = rs2.getString("name");
                Status status = Status.valueOf(rs.getString("status"));
                BookIssue bookIssue = new BookIssue(issueID, issueDate.toLocalDate(), returnDay.toLocalDate(), bookId, bookName, studentId, studentName,status);
                tblIssue.getItems().add(bookIssue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not load the details").showAndWait();
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        txtBookID.getStyleClass().remove("invalid");
        txtBookName.getStyleClass().remove("invalid");
        txtStudentID.getStyleClass().remove("invalid");
        txtStudentName.getStyleClass().remove("invalid");
        dtpIssue.getStyleClass().remove("invalid");
        dtpReturn.getStyleClass().remove("invalid");

        if (!dataValid()) return;
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(String.format("SELECT name,quantity FROM Books WHERE id='%s'", txtBookID.getText()));
            rs.next();
            String bookName = rs.getString("name");
            int quantity = rs.getInt("quantity");

            rs = stm.executeQuery(String.format("SELECT * FROM Student WHERE registration_number='%s'", txtStudentID.getText()));
            rs.next();
            String studentName = rs.getString("name");

            connection.setAutoCommit(false);

            PreparedStatement stm2 = connection.prepareStatement("INSERT INTO Book_Issues (issue_date, return_day, book_id, student_id,status) VALUES (?,?,?,?,?)");
            stm2.setDate(1,Date.valueOf(dtpIssue.getValue()));
            stm2.setDate(2,Date.valueOf(dtpReturn.getValue()));
            stm2.setString(3,txtBookID.getText());
            stm2.setString(4,txtStudentID.getText());
            stm2.setString(5,Status.NOT_RETURNED.name());
            stm2.executeUpdate();

            Statement stm3 = connection.createStatement();
            stm3.executeUpdate(String.format("UPDATE Books SET quantity='%d' WHERE id='%s'",quantity-1,txtBookID.getText()));

            try {
                connection.commit();
            }catch (SQLException e){
                connection.rollback();
                e.printStackTrace();
            }finally {
                connection.setAutoCommit(true);
            }
            int issueID = (tblIssue.getItems().size()==0)? 1:tblIssue.getItems().get(tblIssue.getItems().size()-1).getIssueId()+1;
            BookIssue bookIssue = new BookIssue(issueID,dtpIssue.getValue(), dtpReturn.getValue(), txtBookID.getText(), bookName, txtStudentID.getText(), studentName, Status.NOT_RETURNED);
            tblIssue.getItems().add(bookIssue);

            btnNew.fire();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean dataValid() {
        boolean isValid = true;
        if (dtpReturn.getValue()==null){
            dtpReturn.getStyleClass().add("invalid");
            dtpReturn.requestFocus();
            isValid=false;
        }
        if (dtpIssue.getValue()==null){
            dtpIssue.getStyleClass().add("invalid");
            dtpIssue.requestFocus();
            isValid=false;
        }
        if (!txtStudentName.getText().matches("[A-z ]{3,}")){
            txtStudentName.getStyleClass().add("invalid");
            txtStudentName.selectAll();
            txtStudentName.requestFocus();
            isValid=false;
        }
        if (!txtStudentID.getText().matches("[0-9]{6}[A-Z]")){
            txtStudentID.getStyleClass().add("invalid");
            txtStudentID.selectAll();
            txtStudentID.requestFocus();
            isValid=false;
        }
        if (!txtBookName.getText().matches("[A-z-]{3,}")){
            txtBookName.getStyleClass().add("invalid");
            txtBookName.selectAll();
            txtBookName.requestFocus();
            isValid=false;
        }
        if (!txtBookID.getText().matches("[A-Z]-[0-9]{4}")){
            txtBookID.getStyleClass().add("invalid");
            txtBookID.selectAll();
            txtBookID.requestFocus();
            isValid=false;
        }
        return isValid;
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        BookIssue selectedItem = tblIssue.getSelectionModel().getSelectedItem();
        if (selectedItem==null) return;
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            stm.executeUpdate(String.format("DELETE FROM Book_Issues  WHERE id=%d",selectedItem.getIssueId()));

            tblIssue.getItems().remove(selectedItem);
            btnNew.fire();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnNewOnAction(ActionEvent actionEvent) {
        for (Node node : new TextField[]{txtIssueNumber, txtBookID, txtBookName, txtStudentID, txtStudentName}) {
            TextField txt = (TextField) node;
            txt.clear();
        }
        dtpIssue.getEditor().clear();
        dtpReturn.getEditor().clear();
        tblIssue.getSelectionModel().clearSelection();
    }
}
