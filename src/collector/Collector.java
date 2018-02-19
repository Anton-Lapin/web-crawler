/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package collector;

import data_base_manager.PagesTableReader;
import data_base_manager.PagesTableWriter;
import parser.RobotsTxtParser;
import parser.SitemapsParser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Collector {
    private TreeMap<Integer, String> uncheckedReferencesList;
    private TreeMap<String, Integer> robotsTxtReferenceList = new TreeMap<>();
    private TreeMap<String, Integer> sitemapReferenceList = new TreeMap<>();
    private PagesTableReader pagesTableReader;
    private PagesTableWriter pagesTableWriter;
    private RobotsTxtParser robotsTxtParser;
    private SitemapsParser sitemapsParser;
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();

    public void run() {
        System.out.println("Collector beginning...");
        try {
            startPagesTableWriter();

            uncheckedReferencesList = new TreeMap<>();
            uncheckedReferencesList = getUncheckedReferencesListFromDB();

            sortUncheckedListForParsing(uncheckedReferencesList);

            startRobotsTxtParser();
            startSitemapsParser();

            insertNewPagesListIntoDB();
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

    private void sortUncheckedListForParsing(TreeMap<Integer, String> list) {
        Set<Map.Entry<Integer, String>> set = list.entrySet();
        for (Map.Entry<Integer, String> o : set) {
            String reference = o.getValue();
            String[] splittedValue = reference.split(" ");
            Integer id = Integer.parseInt(splittedValue[1]);
            if(splittedValue[0].contains("robots.txt")) {
                robotsTxtReferenceList.put(splittedValue[0], id);
            } else if(splittedValue[0].contains("sitemap")) {
                sitemapReferenceList.put(splittedValue[0], id);
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

    private void insertNewPagesListIntoDB() {
        pagesTableWriter = new PagesTableWriter();
        try {
            pagesTableWriter.insertNewPagesList(newPagesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        newPagesList.clear();
    }

}
