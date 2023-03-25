package lk.reader.lms.controller.db;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static DBConnection dbConnection;

    private final Connection connection;

    private DBConnection() {
        Properties properties = new Properties();
        File file = new File("application.properties");
        try {
            FileReader fr = new FileReader(file);
            properties.load(fr);
            fr.close();
            String host = properties.getProperty("mysql.host");
            String port = properties.getProperty("mysql.port");
            String database = properties.getProperty("mysql.database");
            String username = properties.getProperty("mysql.username");
            String password = properties.getProperty("mysql.password");

            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&allowMultiQueries=true", host, port, database), username, password);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Configuration file does not exist").showAndWait();
            throw new RuntimeException(e);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Failed to establish the Database Connection, Please try again. If problem persist contact the technical team").showAndWait();
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getDbConnection() {
        return (dbConnection==null)? dbConnection=new DBConnection() : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
