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

    public void run() {
        System.out.println("Collector beginning...");
        try {
            startPagesTableWriter();

            uncheckedReferencesList = new TreeMap<>();
            uncheckedReferencesList = getUncheckedReferencesListFromDB();
            keywordsList = new TreeMap<>();
            keywordsList = getKeywordsListFromDB();

            sortUncheckedListForParsing(uncheckedReferencesList);

            if(!robotsTxtReferenceList.isEmpty()) startRobotsTxtParser();
            if(!sitemapReferenceList.isEmpty()) startSitemapsParser();
            if(!gzArchivesList.isEmpty()) startGzipFileManager();
            if(!xmlFilesList.isEmpty()) startXmlFileManager();
            if(!htmlsReferenceList.isEmpty()) startHtmlsParser();

            clearAllLists();
            insertNewPagesListIntoDB();
            insertNewRankListIntoDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Collector end");
    }

    private void startPagesTableWriter() {
        pagesTableWriter = new PagesTableWriter();
        pagesTableWriter.start();
        try {
            pagesTableWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private TreeMap<Integer, String> getUncheckedReferencesListFromDB() throws InterruptedException {
        pagesTableReader = new PagesTableReader();
        pagesTableReader.start();
        pagesTableReader.join();
        return pagesTableReader.getUncheckedReferencesList();
    }

    private TreeMap<String, Integer> getKeywordsListFromDB() throws InterruptedException {
        keywordsTableReader = new KeywordsTableReader();
        keywordsTableReader.start();
        keywordsTableReader.join();
        return keywordsTableReader.getKeywordsList();
    }

    private void sortUncheckedListForParsing(TreeMap<Integer, String> list) {
        Set<Map.Entry<Integer, String>> set = list.entrySet();
        for (Map.Entry<Integer, String> o : set) {
            String reference = o.getValue();
            String[] splittedValue = reference.split(" ");
            Integer id = Integer.parseInt(splittedValue[1]);
            if(splittedValue[0].contains("robots.txt")) {
                robotsTxtReferenceList.put(splittedValue[0], id);
            } else if(splittedValue[0].endsWith(".gz")) {
                gzArchivesList.put(splittedValue[0], id);
            } else if(splittedValue[0].endsWith(".xml")) {
                xmlFilesList.put(splittedValue[0], id);
            } else if(splittedValue[0].contains("sitemap")) {
                sitemapReferenceList.put(splittedValue[0], id);
            } else if(splittedValue[0].endsWith(".html")) {
                htmlsReferenceList.put(splittedValue[0], id);
            }
        }
    }

    private void startRobotsTxtParser() throws InterruptedException {
        robotsTxtParser = new RobotsTxtParser();
        robotsTxtParser.setUncheckedRobotsTxtReferencesList(robotsTxtReferenceList);
        robotsTxtParser.start();
        robotsTxtParser.join();
        newPagesList.putAll(robotsTxtParser.getNewPagesList());
    }

    private void startSitemapsParser() throws InterruptedException {
        sitemapsParser = new SitemapsParser();
        sitemapsParser.setUncheckedSitemapReferencesList(sitemapReferenceList);
        sitemapsParser.start();
        sitemapsParser.join();
        newPagesList.putAll(sitemapsParser.getNewPagesList());
    }

    private void startGzipFileManager() throws InterruptedException {
        gzipFileManager = new GzipFileManager();
        gzipFileManager.setGzipArchivesList(gzArchivesList);
        gzipFileManager.start();
        gzipFileManager.join();
        newPagesList.putAll(gzipFileManager.getNewPagesList());
    }

    private void startXmlFileManager() throws InterruptedException {
        xmlFileManager = new XmlFileManager();
        xmlFileManager.setXmlFilesList(xmlFilesList);
        xmlFileManager.start();
        xmlFileManager.join();
        newPagesList.putAll(xmlFileManager.getNewPagesList());
    }

    private void startHtmlsParser() throws InterruptedException {
        htmlsParser = new HtmlsParser();
        htmlsParser.setUncheckedHtmlsReferencesList(htmlsReferenceList);
        htmlsParser.setKeywordsList(keywordsList);
        htmlsParser.start();
        htmlsParser.join();
        personPageRankList.putAll(htmlsParser.getPersonPageRankList());
    }



    private void insertNewPagesListIntoDB() {
        pagesTableWriter = new PagesTableWriter();
        try {
            pagesTableWriter.insertNewPagesList(newPagesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newPagesList.clear();
    }

    private void insertNewRankListIntoDB() {
        personPageRankWriter = new PersonPageRankWriter();
        try {
            personPageRankWriter.insertNewPersonPageRankList(personPageRankList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearAllLists() {
        robotsTxtReferenceList.clear();
        gzArchivesList.clear();
        xmlFilesList.clear();
        sitemapReferenceList.clear();
        htmlsReferenceList.clear();
        //personPageRankList.clear();
        //newPagesList.clear();
    }

}
