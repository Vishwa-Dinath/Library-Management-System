package lk.reader.lms.model;

import lk.reader.lms.util.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Admin implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private ArrayList<String> contactList;
    private Gender gender;
    private Blob picture;
    private String username;
    private String password;

}
