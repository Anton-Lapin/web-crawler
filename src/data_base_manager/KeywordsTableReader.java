/**
 *
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;

public class KeywordsTableReader extends Thread {
    private Connection connection;
    private Statement stmt;
    private TreeMap<String, Integer> keywordsList;

    public void run(){
        System.out.println("KeywordsTableReader beginning...");
        try{
            connect();
            createKeywordsList();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            disconnect();
        }
        System.out.println("KeywordsTableReader end");
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

    private void createKeywordsList() throws SQLException {
        keywordsList = new TreeMap<>();
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Keywords;");
        while(rs.next()) {
            System.out.println(rs.getString(2) + " " + rs.getInt(3));
            keywordsList.put(rs.getString(2), rs.getInt(3));
        }
    }

    public TreeMap<String, Integer> getKeywordsList() {
        return keywordsList;
    }
}
