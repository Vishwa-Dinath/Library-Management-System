package lk.reader.lms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lk.reader.lms.db.DBConnection;
import lk.reader.lms.model.BookReturn;
import lk.reader.lms.model.Status;

import java.math.BigDecimal;
import java.sql.*;

public class ReturningBookSceneController {

    @FXML
    private Button btnComplete;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<BookReturn> tblReturns;

    @FXML
    private TextField txtSearch;

    @FXML
    private VBox vBox;

    public void initialize(){
        tblReturns.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("issueID"));
        tblReturns.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("bookID"));
        tblReturns.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("studentID"));
        tblReturns.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("studentName"));
        tblReturns.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        tblReturns.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        tblReturns.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("dtpReturned"));
        tblReturns.getColumns().get(7).setCellValueFactory(new PropertyValueFactory<>("status"));
        loadDetails();


    }

    private void loadDetails() {

        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            Statement stm2 = connection.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Book_Issues");
            while (rs.next()){
                int issueID = rs.getInt("id");
                String bookID = rs.getString("book_id");
                String studentID = rs.getString("student_id");
                Date issuedDate = rs.getDate("issue_date");
                Date returnDate = rs.getDate("return_day");
                Status status = Status.valueOf(rs.getString("status"));
                ResultSet rs2 = stm2.executeQuery(String.format("SELECT name FROM Student WHERE registration_number='%s'", studentID));
                rs2.next();
                String studentName = rs2.getString("name");
                DatePicker dtpReturned = new DatePicker();
                if (status==Status.RETURNED){
                    dtpReturned=null;
                }
                BookReturn bookReturn = new BookReturn(issueID,bookID,studentID,studentName,issuedDate.toLocalDate(),returnDate.toLocalDate(),dtpReturned,status);
                tblReturns.getItems().add(bookReturn);
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to load the data").showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void btnCompleteOnAction(ActionEvent event) {
        BookReturn selectedItem = tblReturns.getSelectionModel().getSelectedItem();
        if (selectedItem==null || selectedItem.getDtpReturned()==null)return;
        DatePicker dtpReturned = new DatePicker();
        dtpReturned.setValue(selectedItem.getDtpReturned().getValue());
        tblReturns.getItems().set(tblReturns.getSelectionModel().getSelectedIndex(),new BookReturn(
                selectedItem.getIssueID(), selectedItem.getBookID(), selectedItem.getStudentID(), selectedItem.getStudentName(),
                selectedItem.getIssueDate(),selectedItem.getReturnDate(),dtpReturned,Status.RETURNED
        ));
        int issueId = selectedItem.getIssueID();
        Connection connection = DBConnection.getDbConnection().getConnection();
        try {
            Statement stm = connection.createStatement();
            stm.executeUpdate(String.format("UPDATE Book_Issues SET status='RETURNED' WHERE id='%s'",issueId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
