/**
 * Класс подключается к базе данных, считывает из таблицы Keywords список ключевых слов
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package data_base_manager;

import java.sql.*;
import java.util.TreeMap;

public class KeywordsTableReader extends Thread {
    private DBConnector connector = new DBConnector();
    private Statement stmt;
    private TreeMap<String, Integer> keywordsList;

    /**
     * Точка входа в класс
     */

    public void run(){
        System.out.println("KeywordsTableReader beginning...");
        try{
            connector.connect();
            createKeywordsList();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            connector.disconnect();
        }
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
