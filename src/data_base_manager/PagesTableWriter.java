/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PagesTableWriter extends Thread {
    private Connection connection;
    private Statement stmt;
    private SitesTableReader sitesTableReader;
    private TreeMap<Integer, String> newSitesList;

    public void run(){
        System.out.println("PagesTableWriter beginning...");
        try{
            connect();
            addStandardRobotsTxtReference();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnect();
        }
        System.out.println("PagesTableWriter end");
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

    private void addStandardRobotsTxtReference() throws SQLException{
        sitesTableReader = new SitesTableReader();
        sitesTableReader.start();
        try {
            sitesTableReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newSitesList = sitesTableReader.getNewSitesList();
        int id = 1;
        ResultSet rs = this.stmt.executeQuery("SELECT MAX(ID) FROM Pages");
        id += rs.getInt(1);
        String url;
        int siteId;
        String foundDateTime = null;
        String lastScanDate = null;
        connection.setAutoCommit(false);
        Set<Map.Entry<Integer, String>> set = newSitesList.entrySet();
        for (Map.Entry<Integer, String> o : set) {
            url = "http://" + o.getValue() + "/robots.txt";
            siteId = o.getKey();
            //?
            stmt.executeUpdate("INSERT INTO Pages (ID, Url, SiteID, FoundDateTime, LastScanDate) VALUES ('"
                    + id + "','" + url + "','" + siteId + "','" + foundDateTime + "','" + lastScanDate + "')");
            id++;
        }
        connection.setAutoCommit(true);
    }

    public void insertNewPagesList(TreeMap<String, Integer> list) throws Exception {
        connect();
        int id = 1;
        ResultSet rs = this.stmt.executeQuery("SELECT MAX(ID) FROM Pages");
        id += rs.getInt(1);
        String url;
        int siteId;
        String foundDateTime = null;
        String lastScanDate = null;
        connection.setAutoCommit(false);
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            url = o.getKey();
            siteId = o.getValue();
            //?
            stmt.executeUpdate("INSERT INTO Pages (ID, Url, SiteID, FoundDateTime, LastScanDate) VALUES ('"
                    + id + "','" + url + "','" + siteId + "','" + foundDateTime + "','" + lastScanDate + "')");
            id++;
        }
        connection.setAutoCommit(true);
        disconnect();
    }
}
