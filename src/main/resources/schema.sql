CREATE TABLE IF NOT EXISTS Admin(
    id VARCHAR(10) PRIMARY KEY ,
    first_name VARCHAR(50) NOT NULL ,
    last_name VARCHAR(50) NOT NULL ,
    address VARCHAR(200) NOT NULL ,
    gender ENUM('MALE','FEMALE') NOT NULL ,
    username VARCHAR(20) NOT NULL ,
    password VARCHAR(10) NOT NULL,
    picture MEDIUMBLOB,
    CONSTRAINT uk_admin UNIQUE KEY (username)
);

CREATE TABLE IF NOT EXISTS Admin_Contact(
    id_no VARCHAR(10) NOT NULL ,
    contact VARCHAR(15) NOT NULL ,
    CONSTRAINT pk_admin_contact PRIMARY KEY (id_no,contact),
    CONSTRAINT uk_admin_contact UNIQUE KEY (contact),
    CONSTRAINT fk_admin_contact FOREIGN KEY (id_no) REFERENCES Admin(id)
);

CREATE TABLE IF NOT EXISTS Student(
    registration_number VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL ,
    DOB DATE NOT NULL ,
    address VARCHAR(50) NOT NULL ,
    gender ENUM('MALE','FEMALE') NOT NULL ,
    faculty VARCHAR(50) NOT NULL ,
    department VARCHAR(50) NOT NULL,
    picture MEDIUMBLOB
);

CREATE TABLE IF NOT EXISTS Student_Contact(
    registration VARCHAR(10) NOT NULL ,
    contact VARCHAR(15) NOT NULL ,
    CONSTRAINT pk_student_contact PRIMARY KEY (registration,contact),
    CONSTRAINT uk_student_contact UNIQUE KEY (contact),
    CONSTRAINT fk_student_contact FOREIGN KEY (registration)  REFERENCES Student(registration_number)
);

CREATE TABLE IF NOT EXISTS Books(
    id VARCHAR(10) PRIMARY KEY ,
    name VARCHAR(100) NOT NULL ,
    author VARCHAR(50) NOT NULL ,
    section VARCHAR(20) NOT NULL ,
    quantity INT NOT NULL ,
    picture MEDIUMBLOB
)