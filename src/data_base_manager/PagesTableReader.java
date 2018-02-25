/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;

public class PagesTableReader extends Thread {
    private Connection connection;
    private Statement stmt;
    private TreeMap<Integer, String> uncheckedReferencesList;

    public void run(){
        System.out.println("PagesTableReader beginning...");
        try{
            connect();
            searchUncheckedReferences();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnect();
        }
        System.out.println("PagesTableReader end");
    }

    private void connect() throws Exception{
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:data.db");
        stmt = connection.createStatement();
    }

    private void disconnect(){
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void searchUncheckedReferences() throws SQLException {
        uncheckedReferencesList = new TreeMap<>();
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Pages\n" +
                "   WHERE LastScanDate = 'null';");
        while(rs.next()) {
            System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            uncheckedReferencesList.put(rs.getInt(1), rs.getString(2) + " " + rs.getInt(3));
        }
        //drop table (unnesessary)
        stmt.executeUpdate("DELETE FROM Pages");
    }

    public void clearTable() {
        try {
            connect();
            stmt.executeUpdate("DELETE FROM Pages");
        } catch (Exception e) {

        } finally {
            disconnect();
        }
    }

    public TreeMap<Integer, String> getUncheckedReferencesList() {
        return uncheckedReferencesList;
    }
}
