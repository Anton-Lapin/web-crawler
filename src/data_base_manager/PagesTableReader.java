/**
 * Класс подключается к базе данных и считывает из таблицы Pages список непроверенных ссылок.
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package data_base_manager;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PagesTableReader extends Thread {
    private static Logger log = Logger.getLogger(PagesTableReader.class.getName());
    private DBConnector connector = new DBConnector();
    private Statement stmt;
    private TreeMap<Integer, String> uncheckedReferencesList;
    private long currentTime;
    private String lastScanDate;

    /**
     * Точка входа в класс
     */

    public void run() {
        System.out.println("PagesTableReader is beginning...");
        try {
            connector.connect();
            searchUncheckedReferences();
            if(this.uncheckedReferencesList.isEmpty()) searchOldReferences();
            this.uncheckedReferencesList.clear();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Exception: ", ex);
        } finally {
            connector.disconnect();
        }
        System.out.println("PagesTableReader end");
    }

    /**
     * Метод запрашивает из базы данных данные, у которых остсутствует время последней проверки; заносит их в
     * список неповеренных ссылок uncheckedReferencesList; обновляет у выбранных данных колонку времени последней
     * проверки на текущее время
     * @throws SQLException
     */

    private void searchUncheckedReferences() throws SQLException {
        this.uncheckedReferencesList = new TreeMap<>();
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Pages\n" +
                "   WHERE LastScanDate = 'null';");
        while(rs.next()) {
            //System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            this.uncheckedReferencesList.put(rs.getInt(1), rs.getString(2) + " "
                    + rs.getInt(3));
        }

        this.currentTime = System.currentTimeMillis();
        this.lastScanDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.currentTime);
        stmt.executeUpdate("UPDATE Pages SET LastScanDate = '" + this.lastScanDate +
                "' WHERE LastScanDate = 'null';");
    }

    /**
     * Метод запрашивает из базы данных данные, у которых время последней проверки наиболее устаревшее; заносит их в
     * список неповеренных ссылок uncheckedReferencesList
     * @throws SQLException
     */

    private void searchOldReferences() throws SQLException {
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Pages\n" +
                "    WHERE MIN(LastScanDate);");//?проверить правильность запроса
        while (rs.next()) {
            if(rs.getString(2).contains("sitemap")) {
                this.uncheckedReferencesList.put(rs.getInt(1), rs.getString(2) + " " +
                        rs.getInt(3));
            }
        }
    }

    /**
     * Метод возвращает список непроверенных ссылок
     * @return uncheckedReferencesList
     */

    public TreeMap<Integer, String> getUncheckedReferencesList() {
        return this.uncheckedReferencesList;
    }
}
