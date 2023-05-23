package lk.reader.lms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookIssue implements Serializable {
    private int issueId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private String bookID;
    private String bookName;
    private String studentID;
    private String studentName;
    private Status status;
}
