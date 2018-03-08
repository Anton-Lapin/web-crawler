package data_base_manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnector {

    private Connection connection;
    private Statement stmt;
    private static Logger log = Logger.getLogger(DBConnector.class.getName());

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:data.db");
            stmt = connection.createStatement();
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Exception :", ex);
        }
    }

    public void disconnect(){
        try {
            connection.close();
        }catch (SQLException ex){
            log.log(Level.SEVERE, "Exception :", ex);
        }
    }
}
