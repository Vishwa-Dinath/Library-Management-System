CREATE TABLE IF NOT EXISTS Admin(
    id VARCHAR(10) PRIMARY KEY ,
    first_name VARCHAR(50) NOT NULL ,
    last_name VARCHAR(50) NOT NULL ,
    address VARCHAR(200) NOT NULL ,
    gender ENUM('MALE','FEMALE') NOT NULL ,
    password VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS Admin_Profile(
    admin_id VARCHAR(10) PRIMARY KEY ,
    picture MEDIUMBLOB NOT NULL ,
    CONSTRAINT fk_admin_pic FOREIGN KEY (admin_id) REFERENCES Admin(id)
);

CREATE TABLE IF NOT EXISTS Admin_Contact(
    id_no VARCHAR(10) NOT NULL ,
    contact VARCHAR(10) NOT NULL ,
    CONSTRAINT pk_admin_contact PRIMARY KEY (id_no,contact),
    CONSTRAINT uk_admin UNIQUE KEY (contact),
    CONSTRAINT fk_admin_contact FOREIGN KEY (id_no) REFERENCES Admin(id)
);