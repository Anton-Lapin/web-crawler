/**
 *
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package parser;

import downloader.Downloader;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HtmlsParser extends Thread {
    private TreeMap<String, Integer> uncheckedHtmlsReferencesList = new TreeMap<>();
    private TreeMap<String, Integer> keywordsList = new TreeMap<>();
    private TreeMap<String, Integer> personPageRankList = new TreeMap<>();
    private Downloader downloader;

    public void run() {
        calculatePersonRank();
    }

    public void setUncheckedHtmlsReferencesList(TreeMap<String, Integer> uncheckedHtmlsReferencesList) {
        this.uncheckedHtmlsReferencesList = uncheckedHtmlsReferencesList;
    }

    public void setKeywordsList(TreeMap<String, Integer> keywordsList) {
        this.keywordsList = keywordsList;
    }

    private void calculatePersonRank() {
        downloader = new Downloader();
        Set<Map.Entry<String, Integer>> set = uncheckedHtmlsReferencesList.entrySet();
        Set<Map.Entry<String, Integer>> set1 = keywordsList.entrySet();
        int rank;
        for (Map.Entry<String, Integer> o : set) {
            rank = 0;
            String url = o.getKey();
            String content = downloader.exec(url);
            String[] splitContent = content.split(" ");
            for (Map.Entry<String, Integer> o1 : set1) {
                for (String word : splitContent) {
                    if (word.equals(o1.getKey())) {
                        rank++;
                    }
                }
                int personID = o1.getValue();
                int siteID = o.getValue();
                personPageRankList.put(personID + " " + siteID, rank);
            }
        }
    }

    public TreeMap<String, Integer> getPersonPageRankList() {
        return personPageRankList;
    }
}
