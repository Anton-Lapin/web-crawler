/**
 * Класс подключается к БД; считывает из таблицы Sites данные, у которых нет ни одной ссылки по первичному ключу
 * в таблице Pages; заносит их в список названий сайтов.
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SitesTableReader extends Thread {
    private static Logger log = Logger.getLogger(SitesTableReader.class.getName());
    private DBConnector connector = new DBConnector();
    private Connection connection;
    private Statement stmt;
    private TreeMap<Integer, String> newSitesList = new TreeMap<>();

    /**
     * Точка входа в класс
     */

    public void run(){
        System.out.println("SitesTableReader is beginning...");
        try{
            connector.connect();
            searchNewSites();
        }catch (SQLException e){
            log.log(Level.SEVERE, "Exception: ", e);
        }finally {
            connector.disconnect();
        }
        System.out.println("SitesTableReader end");
    }

    /**
     * Метод делает запрос в таблицу Sites, считывает результат запроса в список newSitesList
     * @throws SQLException
     */

    private void searchNewSites() throws SQLException {
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Sites\n" +
                "   LEFT JOIN Pages ON Pages.SiteID = Sites.ID\n" +
                "   WHERE Pages.Url IS NULL;");
        while(rs.next()) {
            System.out.println(rs.getInt(1) + " " + rs.getString(2));
            this.newSitesList.put(rs.getInt(1), rs.getString(2));
        }
    }

    /**
     * Метод возвращает список названий новых сайтов
     * @return newSitesList
     */

    public TreeMap<Integer, String> getNewSitesList() {
        return this.newSitesList;
    }
}
