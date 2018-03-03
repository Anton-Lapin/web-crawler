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
    private int rank;
    private String url;
    private String content;
    private String[] splitContent;
    private int personID;
    private int siteID;

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
        this.downloader = new Downloader();
        Set<Map.Entry<String, Integer>> set = this.uncheckedHtmlsReferencesList.entrySet();
        Set<Map.Entry<String, Integer>> set1 = this.keywordsList.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            this.rank = 0;
            this.url = o.getKey();
            this.content = this.downloader.exec(this.url);
            this.splitContent = this.content.split(" ");
            for (Map.Entry<String, Integer> o1 : set1) {
                for (String word : this.splitContent) {
                    if (word.equals(o1.getKey())) {
                        this.rank++;
                    }
                }
                this.personID = o1.getValue();
                this.siteID = o.getValue();
                this.personPageRankList.put(this.personID + " " + this.siteID, this.rank);
            }
        }
    }

    public TreeMap<String, Integer> getPersonPageRankList() {
        return this.personPageRankList;
    }
}
