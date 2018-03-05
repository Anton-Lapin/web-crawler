import collector.Collector;
import data_base_manager.PagesTableReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyTestClass {
    private static Connection connection;
    private static Statement stmt;

    public static void main(String[] args) {
        System.out.println("Clear pages...");
        clearTable();
        System.out.println("Clear complete!");

        System.out.println("Test starting...");
        Collector collector = new Collector();
        collector.run();
        System.out.println("Test end");
    }

    private static void connect() throws Exception{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:data.db");
        stmt = connection.createStatement();
    }

    private static void disconnect(){
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void clearTable() {
        try {
            connect();
            stmt.executeUpdate("DELETE FROM Pages");
        } catch (Exception e) {

        } finally {
            disconnect();
        }
    }
}
