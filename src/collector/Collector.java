/**
 * Класс является точкой входа в алгоритм работы веб-краулера, выполняет функции:
 * 1.Запуск нитей, работающих с базой данных.
 * 2.Запуск нитей, производящих обход ссылок веб-страниц и файлов.
 * 3.Транзит данных в списках от классов из п.1 в классы из п.2 и обратно до окончания цикла работы веб-краулера.
 * @author Anton Lapin
 * @version date Feb 23 2018
 */
package collector;

import data_base_manager.KeywordsTableReader;
import data_base_manager.PagesTableReader;
import data_base_manager.PagesTableWriter;
import data_base_manager.PersonPageRankWriter;
import file_manager.GzipFileManager;
import file_manager.XmlFileManager;
import parser.HtmlsParser;
import parser.RobotsTxtParser;
import parser.SitemapsParser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Collector extends Thread {
    private TreeMap<Integer, String> uncheckedReferencesList = new TreeMap<>();
    private TreeMap<String, Integer> robotsTxtReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> sitemapReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> htmlsReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> xmlFilesList = new TreeMap<>();
    private TreeMap<String, Integer> gzArchivesList = new TreeMap<>();
    private TreeMap<String, Integer> keywordsList = new TreeMap<>();
    private PagesTableReader pagesTableReader;
    private PagesTableWriter pagesTableWriter;
    private RobotsTxtParser robotsTxtParser;
    private SitemapsParser sitemapsParser;
    private GzipFileManager gzipFileManager;
    private XmlFileManager xmlFileManager;
    private KeywordsTableReader keywordsTableReader;
    private HtmlsParser htmlsParser;
    private PersonPageRankWriter personPageRankWriter;
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();
    private TreeMap<String, Integer> personPageRankList = new TreeMap<>();

    private String reference;
    private String[] splittedValue;
    private int id;

    /**
     * Метод - точка входа в класс.
     */

    public void run() {
        System.out.println("Collector beginning...");
        try {
            startPagesTableWriter();
            do {
                this.uncheckedReferencesList = getUncheckedReferencesListFromDB();
                sortUncheckedListForParsing(this.uncheckedReferencesList);
                startAllParsersAndFileManagers();
                insertNewPagesListIntoDB();
                insertNewRankListIntoDB();
                clearAllLists();
            } while(!this.uncheckedReferencesList.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Collector end");
    }

    /**
     * Метод запускает нить PagesTableWriter.
     */

    private void startPagesTableWriter() {
        this.pagesTableWriter = new PagesTableWriter();
        this.pagesTableWriter.start();
        try {
            this.pagesTableWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод запускает нить PagesTableReader; ждет, когда нить закончит работу; получает из класса
     * список непроверенных ссылок.
     * @return список непроверенных ссылок
     * @throws InterruptedException
     */

    private TreeMap<Integer, String> getUncheckedReferencesListFromDB() throws InterruptedException {
        this.pagesTableReader = new PagesTableReader();
        this.pagesTableReader.start();
        this.pagesTableReader.join();
        return this.pagesTableReader.getUncheckedReferencesList();
    }

    /**
     * Метод запускает нить KeywordsTableReader; ждет, когда нить закончит работу; получает из класса
     * список ключевых слов.
     * @return список ключевых слов
     * @throws InterruptedException
     */

    private TreeMap<String, Integer> getKeywordsListFromDB() throws InterruptedException {
        this.keywordsTableReader = new KeywordsTableReader();
        this.keywordsTableReader.start();
        this.keywordsTableReader.join();
        return this.keywordsTableReader.getKeywordsList();
    }

    /**
     * Метод на вход принимает список, полученный в результате работы нити PagesTableReader; сортирует его элементы по
     * характерным признакам в специальные списки нитей обхода ссылок.
     * @param list
     */

    private void sortUncheckedListForParsing(TreeMap<Integer, String> list) {
        Set<Map.Entry<Integer, String>> set = list.entrySet();
        for (Map.Entry<Integer, String> o : set) {
            this.reference = o.getValue();
            this.splittedValue = this.reference.split(" ");
            this.id = Integer.parseInt(this.splittedValue[1]);
            if(this.splittedValue[0].contains("robots.txt")) {
                this.robotsTxtReferenceList.put(this.splittedValue[0], this.id);
            } else if(this.splittedValue[0].endsWith(".gz")) {
                this.gzArchivesList.put(this.splittedValue[0], this.id);
            } else if(this.splittedValue[0].endsWith(".xml")) {
                this.xmlFilesList.put(this.splittedValue[0], this.id);
            } else if(this.splittedValue[0].contains("sitemap")) {
                this.sitemapReferenceList.put(this.splittedValue[0], this.id);
            } else if(this.splittedValue[0].endsWith(".html")) {
                this.htmlsReferenceList.put(this.splittedValue[0], this.id);
            }
        }
    }

    /**
     * Метод запускает все возможные нити обхода ссылок, учитывая наличие тех или иных видов ссылок.
     * @throws InterruptedException
     */

    private void startAllParsersAndFileManagers() throws  InterruptedException {
        if (!this.robotsTxtReferenceList.isEmpty()) startRobotsTxtParser();
        if (!this.sitemapReferenceList.isEmpty()) startSitemapsParser();
        if (!this.gzArchivesList.isEmpty()) startGzipFileManager();
        if (!this.xmlFilesList.isEmpty()) startXmlFileManager();
        if (!this.htmlsReferenceList.isEmpty()) {
            this.keywordsList = new TreeMap<>();
            this.keywordsList = getKeywordsListFromDB();
            startHtmlsParser();
        }
    }

    /**
     * Метод устанавливает список ссылок типа robots.txt в класс RobotsTxtParser; запускает нить RobotsTxtParser;
     * ждет завершения нити; получает из класса RobotsTxtParser; вносит новые ссылки в список newPagesList
     * @throws InterruptedException
     */

    private void startRobotsTxtParser() throws InterruptedException {
        this.robotsTxtParser = new RobotsTxtParser();
        this.robotsTxtParser.setUncheckedRobotsTxtReferencesList(this.robotsTxtReferenceList);
        this.robotsTxtParser.start();
        this.robotsTxtParser.join();
        this.newPagesList.putAll(this.robotsTxtParser.getNewPagesList());
    }

    /**
     * Метод устанавливает список ссылок типа sitemap в класс SitemapsParser; запускает нить SitemapsParser;
     * ждет завершения нити; получает список ссылок из класса SitemapsParser; вносит новые ссылки в список newPagesList
     * @throws InterruptedException
     */

    private void startSitemapsParser() throws InterruptedException {
        this.sitemapsParser = new SitemapsParser();
        this.sitemapsParser.setUncheckedSitemapReferencesList(this.sitemapReferenceList);
        this.sitemapsParser.start();
        this.sitemapsParser.join();
        this.newPagesList.putAll(this.sitemapsParser.getNewPagesList());
    }

    /**
     * Метод устанавливает список ссылок типа .xml.gz в класс GzipFileManager; запускает нить GzipFileManager;
     * ждет завершения нити; получает список ссылок из класса GzipFileManager; вносит новые ссылки в список newPagesList
     * @throws InterruptedException
     */

    private void startGzipFileManager() throws InterruptedException {
        this.gzipFileManager = new GzipFileManager();
        this.gzipFileManager.setGzipArchivesList(this.gzArchivesList);
        this.gzipFileManager.start();
        this.gzipFileManager.join();
        this.newPagesList.putAll(this.gzipFileManager.getNewPagesList());
    }

    /**
     * Метод устанавливает список ссылок типа .xml в класс XmlFileManager; запускает нить XmlFileManager;
     * ждет завершения нити; получает список ссылок из класса XmlFileManager; вносит новые ссылки в список newPagesList
     * @throws InterruptedException
     */

    private void startXmlFileManager() throws InterruptedException {
        this.xmlFileManager = new XmlFileManager();
        this.xmlFileManager.setXmlFilesList(this.xmlFilesList);
        this.xmlFileManager.start();
        this.xmlFileManager.join();
        this.newPagesList.putAll(this.xmlFileManager.getNewPagesList());
    }

    /**
     * Метод устанавливает список ссылок типа .html и список ключевых слов в класс HtmlsParser; запускает нить
     * HtmlsParser; ждет завершения нити; получает список рейтингов личностей из класса HtmlsParser; вносит новые
     * ссылки в список personPageRankList
     * @throws InterruptedException
     */

    private void startHtmlsParser() throws InterruptedException {
        this.htmlsParser = new HtmlsParser();
        this.htmlsParser.setUncheckedHtmlsReferencesList(this.htmlsReferenceList);
        this.htmlsParser.setKeywordsList(this.keywordsList);
        this.htmlsParser.start();
        this.htmlsParser.join();
        this.personPageRankList.putAll(this.htmlsParser.getPersonPageRankList());
    }

    /**
     * Метод инициирует класс pagesTableWriter; устанавливает список новых ссылок newPages в метод
     * insertNewPagesList и вызывает его; очищает список новых ссылок newPagesList
     */

    private void insertNewPagesListIntoDB() {
        this.pagesTableWriter = new PagesTableWriter();
        try {
            this.pagesTableWriter.insertNewPagesList(this.newPagesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.newPagesList.clear();
    }

    /**
     * Метод инициирует класс personPageRankWriter; устанавливает список рейтингов личностей personPageRancList в метод
     * insertNewPersonPageRankList и вызывает его
     */

    private void insertNewRankListIntoDB() {
        this.personPageRankWriter = new PersonPageRankWriter();
        try {
            this.personPageRankWriter.insertNewPersonPageRankList(this.personPageRankList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод очищает все текущие списки
     */

    private void clearAllLists() {
        this.robotsTxtReferenceList.clear();
        this.gzArchivesList.clear();
        this.xmlFilesList.clear();
        this.sitemapReferenceList.clear();
        this.htmlsReferenceList.clear();
        this.keywordsList.clear();
        this.personPageRankList.clear();
    }
}
