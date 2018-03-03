/**
 *
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

public class Collector {
    private TreeMap<Integer, String> uncheckedReferencesList;
    private TreeMap<String, Integer> robotsTxtReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> sitemapReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> htmlsReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> xmlFilesList = new TreeMap<>();
    private TreeMap<String, Integer> gzArchivesList = new TreeMap<>();
    private TreeMap<String, Integer> keywordsList;
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

    public void run() {
        System.out.println("Collector beginning...");
        try {
            startPagesTableWriter();

            this.uncheckedReferencesList = new TreeMap<>();
            this.uncheckedReferencesList = getUncheckedReferencesListFromDB();

            sortUncheckedListForParsing(this.uncheckedReferencesList);

            if(!this.robotsTxtReferenceList.isEmpty()) startRobotsTxtParser();
            if(!this.sitemapReferenceList.isEmpty()) startSitemapsParser();
            if(!this.gzArchivesList.isEmpty()) startGzipFileManager();
            if(!this.xmlFilesList.isEmpty()) startXmlFileManager();
            if(!this.htmlsReferenceList.isEmpty()){
                this.keywordsList = new TreeMap<>();
                this.keywordsList = getKeywordsListFromDB();
                startHtmlsParser();
            }

            insertNewPagesListIntoDB();
            insertNewRankListIntoDB();
            clearAllLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Collector end");
    }

    private void startPagesTableWriter() {
        this.pagesTableWriter = new PagesTableWriter();
        this.pagesTableWriter.start();
        try {
            this.pagesTableWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private TreeMap<Integer, String> getUncheckedReferencesListFromDB() throws InterruptedException {
        this.pagesTableReader = new PagesTableReader();
        this.pagesTableReader.start();
        this.pagesTableReader.join();
        return this.pagesTableReader.getUncheckedReferencesList();
    }

    private TreeMap<String, Integer> getKeywordsListFromDB() throws InterruptedException {
        this.keywordsTableReader = new KeywordsTableReader();
        this.keywordsTableReader.start();
        this.keywordsTableReader.join();
        return this.keywordsTableReader.getKeywordsList();
    }

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

    private void startRobotsTxtParser() throws InterruptedException {
        this.robotsTxtParser = new RobotsTxtParser();
        this.robotsTxtParser.setUncheckedRobotsTxtReferencesList(this.robotsTxtReferenceList);
        this.robotsTxtParser.start();
        this.robotsTxtParser.join();
        this.newPagesList.putAll(this.robotsTxtParser.getNewPagesList());
    }

    private void startSitemapsParser() throws InterruptedException {
        this.sitemapsParser = new SitemapsParser();
        this.sitemapsParser.setUncheckedSitemapReferencesList(this.sitemapReferenceList);
        this.sitemapsParser.start();
        this.sitemapsParser.join();
        this.newPagesList.putAll(this.sitemapsParser.getNewPagesList());
    }

    private void startGzipFileManager() throws InterruptedException {
        this.gzipFileManager = new GzipFileManager();
        this.gzipFileManager.setGzipArchivesList(this.gzArchivesList);
        this.gzipFileManager.start();
        this.gzipFileManager.join();
        this.newPagesList.putAll(this.gzipFileManager.getNewPagesList());
    }

    private void startXmlFileManager() throws InterruptedException {
        this.xmlFileManager = new XmlFileManager();
        this.xmlFileManager.setXmlFilesList(this.xmlFilesList);
        this.xmlFileManager.start();
        this.xmlFileManager.join();
        this.newPagesList.putAll(this.xmlFileManager.getNewPagesList());
    }

    private void startHtmlsParser() throws InterruptedException {
        this.htmlsParser = new HtmlsParser();
        this.htmlsParser.setUncheckedHtmlsReferencesList(this.htmlsReferenceList);
        this.htmlsParser.setKeywordsList(this.keywordsList);
        this.htmlsParser.start();
        this.htmlsParser.join();
        this.personPageRankList.putAll(this.htmlsParser.getPersonPageRankList());
    }



    private void insertNewPagesListIntoDB() {
        this.pagesTableWriter = new PagesTableWriter();
        try {
            this.pagesTableWriter.insertNewPagesList(this.newPagesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.newPagesList.clear();
    }

    private void insertNewRankListIntoDB() {
        this.personPageRankWriter = new PersonPageRankWriter();
        try {
            this.personPageRankWriter.insertNewPersonPageRankList(this.personPageRankList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAllLists() {
        this.robotsTxtReferenceList.clear();
        this.gzArchivesList.clear();
        this.xmlFilesList.clear();
        this.sitemapReferenceList.clear();
        this.htmlsReferenceList.clear();
        this.keywordsList.clear();
        this.personPageRankList.clear();
        this.newPagesList.clear();
    }
}
