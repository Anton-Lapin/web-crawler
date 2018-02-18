/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package collector;

import data_base_manager.PagesTableReader;
import data_base_manager.PagesTableWriter;
import parser.RobotsTxtParser;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Collector {
    private TreeMap<Integer, String> uncheckedReferencesList;
    private TreeMap<String, Integer> robotsTxtReferenceList = new TreeMap<>();
    private PagesTableReader pagesTableReader;
    private PagesTableWriter pagesTableWriter;
    private RobotsTxtParser robotsTxtParser;

    public void run() {
        System.out.println("Collector beginning...");
        try {
            startPagesTableWriter();
            uncheckedReferencesList = new TreeMap<>();
            uncheckedReferencesList = getUncheckedReferencesListFromDB();
            sortUncheckedListForParsing(uncheckedReferencesList);
            startRobotsTxtParser();
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
            }
        }
    }

    public TreeMap<String, Integer> getRobotsTxtReferenceList() {
        return robotsTxtReferenceList;
    }

    private void startRobotsTxtParser() throws InterruptedException {
        robotsTxtParser = new RobotsTxtParser();
        robotsTxtParser.setUncheckedRobotsTxtReferencesList(robotsTxtReferenceList);
        robotsTxtParser.start();
        robotsTxtParser.join();
    }
}
