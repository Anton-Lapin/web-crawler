/**
 *
 * @author Anton Lapin
 * @version date 18 February 2018
 */
package parser;

import downloader.Downloader;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RobotsTxtParser extends Thread {
    private TreeMap<String, Integer> uncheckedRobotsTxtReferencesList = new TreeMap<>();
    private Downloader downloader;
    private String pageContent;
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();

    public void run() {
        System.out.println("RobotsTxtParser beginning...");

        Set<Map.Entry<String, Integer>> set = uncheckedRobotsTxtReferencesList.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            System.out.println(o.getKey() + " " + o.getValue());
        }
        startDownloader(uncheckedRobotsTxtReferencesList);
        System.out.println("RobotsTxtParser end");
    }

    public void setUncheckedRobotsTxtReferencesList(TreeMap<String, Integer> list) {
        uncheckedRobotsTxtReferencesList = list;
    }

    private void startDownloader(TreeMap<String, Integer> list) {
        downloader = new Downloader();
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            String url = o.getKey();
            pageContent = downloader.exec(url);
            pageContentHandle(pageContent, o.getValue());
            System.out.println(pageContent);
            System.out.println("-----------------------------");
        }
    }

    private void pageContentHandle(String pageContent, Integer siteId){
        String[] split = pageContent.split(" ");
        for (int i = 0; i < split.length; i++) {
            if(split[i].contains("sitemap") && split[i].contains("http")) {
                newPagesList.put(split[i], siteId);
            }
        }
    }

    public TreeMap<String, Integer> getNewPagesList() {
        return newPagesList;
    }
}
