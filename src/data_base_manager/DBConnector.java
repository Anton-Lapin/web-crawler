package data_base_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {

    private Connection connection;
    private Statement stmt;

    public void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:data.db");
        stmt = connection.createStatement();
    }

    public void disconnect(){
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
