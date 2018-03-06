/**
 * Класс подключается к БД; записывает в таблицу PersonPageRank список рейтингов популярности личностей по сайтам
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PersonPageRankWriter {
    private DBConnector connector = new DBConnector();
    private Connection connection;
    private Statement stmt;
    private int personID;
    private int siteID;
    private int rank;

    /**
     * Метод подключается к БД; очищает таблицу PersonPageRank от старых данных; записывает новый список рейтингов
     * @param list
     * @throws Exception
     */

    public void insertNewPersonPageRankList(TreeMap<String, Integer> list) throws Exception {
        connector.connect();
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
        connector.disconnect();
    }

}
