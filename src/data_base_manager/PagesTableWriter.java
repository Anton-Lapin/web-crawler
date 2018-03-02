/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PagesTableWriter extends Thread {
    private Connection connection;
    private Statement stmt;
    private SitesTableReader sitesTableReader;
    private TreeMap<Integer, String> newSitesList;
    private int id;
    private String url;
    private int siteId;
    private long currentTime;
    private String foundDateTime;
    private String lastScanDate = null;

    public void run(){
        System.out.println("PagesTableWriter beginning...");
        try{
            connect();
            getNewSitesListFromSTR();
            if(!newSitesList.isEmpty()) addStandardRobotsTxtReference();
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

    private void getNewSitesListFromSTR() {
        sitesTableReader = new SitesTableReader();
        sitesTableReader.start();
        try {
            sitesTableReader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newSitesList = sitesTableReader.getNewSitesList();
    }

    private void addStandardRobotsTxtReference() throws SQLException{
        this.id = 1;
        ResultSet rs = this.stmt.executeQuery("SELECT MAX(ID) FROM Pages");
        this.id += rs.getInt(1);
        connection.setAutoCommit(false);
        Set<Map.Entry<Integer, String>> set = newSitesList.entrySet();
        for (Map.Entry<Integer, String> o : set) {
            this.url = "http://" + o.getValue() + "/robots.txt";
            this.siteId = o.getKey();
            this.currentTime = System.currentTimeMillis();
            this.foundDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.currentTime);
            stmt.executeUpdate("INSERT INTO Pages (ID, Url, SiteID, FoundDateTime, LastScanDate) VALUES ('"
                    + this.id + "','" + this.url + "','" + this.siteId + "','" + this.foundDateTime + "','"
                    + this.lastScanDate + "')");
            id++;
        }
        connection.setAutoCommit(true);
    }

    public void insertNewPagesList(TreeMap<String, Integer> list) throws Exception {
        connect();
        this.id = 1;
        ResultSet rs = this.stmt.executeQuery("SELECT MAX(ID) FROM Pages");
        this.id += rs.getInt(1);
        connection.setAutoCommit(false);
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            this.url = o.getKey();
            this.siteId = o.getValue();
            this.currentTime = System.currentTimeMillis();
            this.foundDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.currentTime);
            stmt.executeUpdate("INSERT INTO Pages (ID, Url, SiteID, FoundDateTime, LastScanDate) VALUES ('"
                    + this.id + "','" + this.url + "','" + this.siteId + "','" + this.foundDateTime + "','"
                    + this.lastScanDate + "')");
            id++;
        }
        connection.setAutoCommit(true);
        disconnect();
    }
}
