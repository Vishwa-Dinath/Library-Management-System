package lk.reader.lms.model;

import javafx.scene.control.DatePicker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookReturn implements Serializable {
    private int issueID;
    private String bookID;
    private String studentID;
    private String studentName;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private DatePicker dtpReturned;
    private Status status;
}
