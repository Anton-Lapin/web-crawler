/**
 * Класс подключается к базе данных; запускает нить SitesTable Reader, в результате его работы получает из класса список
 * названий новых сайтов, у которых еще нет ссылки на robots.txt; создает для них стандартные ссылки и записывает их
 * в таблицу Pages; записывает в таблицу Pages список новых ссылок, полученных в результате обхода предыдущих ссылок.
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagesTableWriter extends Thread {
    private static Logger log = Logger.getLogger(PagesTableReader.class.getName());
    private DBConnector connector = new DBConnector();
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

    /**
     * Точка входа в класс
     */

    public void run(){
        System.out.println("PagesTableWriter is beginning...");
        try{
            connector.connect();
            getNewSitesListFromSTR();
            if(!newSitesList.isEmpty()) addStandardRobotsTxtReference();
        }catch (SQLException ex){
            log.log(Level.SEVERE, "Exception : ", ex);
        }finally {
            connector.disconnect();
        }
        System.out.println("PagesTableWriter end");
    }

    /**
     * Метод инициирует нить SitesTableReader; запускает ее; ждет окончания работы нити; получает из SitesTableReader
     * список названий сайтов, у которых еще нет robots.txt
     */

    private void getNewSitesListFromSTR() {
        sitesTableReader = new SitesTableReader();
        sitesTableReader.start();
        try {
            sitesTableReader.join();
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, "Exception: ", ex);
        }
        newSitesList = sitesTableReader.getNewSitesList();
    }

    /**
     * Метод добавляет в таблицу Pages стандартную ссылку на robots.txt с подстановкой названия сайта
     * @throws SQLException
     */

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
            this.id++;
        }
        connection.setAutoCommit(true);
    }

    /**
     * Метод подключается к базе данных; записывает список новых ссылок в таблицу Pages, прописывая время нахождения
     * ссылки
     * @param list
     * @throws Exception
     */

    public void insertNewPagesList(TreeMap<String, Integer> list) throws SQLException {
        connector.connect();
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
            this.id++;
        }
        connection.setAutoCommit(true);
        connector.disconnect();
    }
}
