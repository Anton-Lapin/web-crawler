/**
 *
 * @author Anton Lapin
 * @version date 16 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;

public class SitesTableReader {
    private Connection connection;
    private Statement stmt;
    private TreeMap<Integer, String> newSitesList = new TreeMap<>();

    public void run(){
        try{
            connect();
            searchNewSites();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnect();
        }
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

    private void searchNewSites() throws SQLException {
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Sites\n" +
                "   LEFT JOIN Pages ON Pages.SiteID = Sites.ID\n" +
                "   WHERE Pages.Url IS NULL;");
        while(rs.next()) {
            newSitesList.put(rs.getInt(1), rs.getString(2));
        }
    }

    public TreeMap<Integer, String> getNewSitesList() {
        return newSitesList;
    }
}
