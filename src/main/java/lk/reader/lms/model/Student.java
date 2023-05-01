package lk.reader.lms.model;

import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Blob;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student implements Serializable {
    private String registrationNumber;
    private String name;
    private ImageView preview;

    public ImageView getPicture() {
        return preview;
    }
}
