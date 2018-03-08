/**
 * Класс подключается к базе данных, считывает из таблицы Keywords список ключевых слов
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KeywordsTableReader extends Thread {
    private static Logger log = Logger.getLogger(KeywordsTableReader.class.getName());
    private DBConnector connector = new DBConnector();
    private Statement stmt;
    private TreeMap<String, Integer> keywordsList;

    /**
     * Точка входа в класс
     */

    public void run(){
        System.out.println("KeywordsTableReader is beginning...");
        connector.connect();
        try {
            createKeywordsList();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Exception : ", ex);
        }
        connector.disconnect();
        System.out.println("KeywordsTableReader end");
    }

    /**
     * Метод делает запрос в БД в таблицу Keywords; записывает полученные данные в список ключевых слов keywordsList
     * @throws SQLException
     */

    private void createKeywordsList() throws SQLException {
        this.keywordsList = new TreeMap<>();
        ResultSet rs = this.stmt.executeQuery("SELECT * FROM Keywords;");
        while(rs.next()) {
            System.out.println(rs.getString(2) + " " + rs.getInt(3));
            this.keywordsList.put(rs.getString(2), rs.getInt(3));
        }
    }

    /**
     * Метод возвращает список ключевых слов
     * @return keywordsList
     */

    public TreeMap<String, Integer> getKeywordsList() {
        return this.keywordsList;
    }
}
