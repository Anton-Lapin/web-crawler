/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import sun.reflect.generics.tree.Tree;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.SimpleFormatter;

public class PagesTableReader extends Thread {
    private Connection connection;
    private Statement stmt;
    private TreeMap<Integer, String> uncheckedReferencesList;
    private long currentTime;
    private String lastScanDate;

    public void run(){
        System.out.println("PagesTableReader beginning...");
        try{
            connect();
            searchUncheckedReferences();
            if(this.uncheckedReferencesList.isEmpty()) searchOldReferences();
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
        this.uncheckedReferencesList = new TreeMap<>();
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Pages\n" +
                "   WHERE LastScanDate = 'null';");
        while(rs.next()) {
            //System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            this.uncheckedReferencesList.put(rs.getInt(1), rs.getString(2) + " " + rs.getInt(3));
        }

        this.currentTime = System.currentTimeMillis();
        this.lastScanDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.currentTime);
        stmt.executeUpdate("UPDATE Pages SET LastScanDate = '" + this.lastScanDate + "' WHERE LastScanDate = 'null';");
    }

    private void searchOldReferences() throws SQLException {
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Pages\n" +
                "    WHERE MIN(LastScanDate);");//?проверить правильность запроса
        while (rs.next()) {
            if(rs.getString(2).contains("sitemap")) {
                this.uncheckedReferencesList.put(rs.getInt(1), rs.getString(2) + " " + rs.getInt(3));
            }
        }
    }

    public TreeMap<Integer, String> getUncheckedReferencesList() {
        return this.uncheckedReferencesList;
    }
}
