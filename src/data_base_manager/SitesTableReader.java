/**
 * 
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;

public class SitesTableReader extends Thread {
    private DBConnector connector = new DBConnector();
    private Connection connection;
    private Statement stmt;
    private TreeMap<Integer, String> newSitesList = new TreeMap<>();

    public void run(){
        System.out.println("SitesTableReader beginning...");
        try{
            connector.connect();
            searchNewSites();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            connector.disconnect();
        }
        System.out.println("SitesTableReader end");
    }

    private void searchNewSites() throws SQLException {
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Sites\n" +
                "   LEFT JOIN Pages ON Pages.SiteID = Sites.ID\n" +
                "   WHERE Pages.Url IS NULL;");
        while(rs.next()) {
            System.out.println(rs.getInt(1) + " " + rs.getString(2));
            this.newSitesList.put(rs.getInt(1), rs.getString(2));
        }
    }

    public TreeMap<Integer, String> getNewSitesList() {
        return this.newSitesList;
    }
}
