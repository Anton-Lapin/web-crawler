/**
 *
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PersonPageRankWriter {
    private Connection connection;
    private Statement stmt;
    private int personID;
    private int siteID;
    private int rank;

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

    public void insertNewPersonPageRankList(TreeMap<String, Integer> list) throws Exception {
        connect();
        stmt.executeUpdate("DELETE FROM PersonPageRank");
        connection.setAutoCommit(false);
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            String[] split = o.getKey().split(" ");
            this.personID = Integer.parseInt(split[0]);
            this.siteID = Integer.parseInt(split[1]);
            this.rank = o.getValue();
            stmt.executeUpdate("INSERT INTO PersonPageRank (PersonID, PageID, Rank) VALUES ('"
                    + this.personID + "','" + this.siteID + "','" + this.rank + "')");
        }
        connection.setAutoCommit(true);
        disconnect();
    }

}
