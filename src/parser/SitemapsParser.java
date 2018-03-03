/**
 *
 * @author Anton Lapin
 * @version date 13 February 2018
 */
package parser;

import downloader.Downloader;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SitemapsParser extends Thread {
    private TreeMap<String, Integer> uncheckedSitemapReferencesList = new TreeMap<>();
    private Downloader downloader;
    private String pageContent;
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();
    private String url;
    private String[] splitContent;

    public void run() {
        System.out.println("SitemapsParser beginning...");

//        Set<Map.Entry<String, Integer>> set = uncheckedSitemapReferencesList.entrySet();
//        for (Map.Entry<String, Integer> o : set) {
//            System.out.println(o.getKey() + " " + o.getValue());
//        }
        startDownloader(this.uncheckedSitemapReferencesList);
        System.out.println("SitemapsParser end");
    }

    public void setUncheckedSitemapReferencesList(TreeMap<String, Integer> list) {
        this.uncheckedSitemapReferencesList = list;
    }

    private void startDownloader(TreeMap<String, Integer> list) {
        this.downloader = new Downloader();
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            this.url = o.getKey();
            this.pageContent = this.downloader.exec(this.url);
            pageContentHandle(this.pageContent, o.getValue());
        }
    }

    private void pageContentHandle(String pageContent, Integer siteId){
        this.splitContent = pageContent.split(" ");
        for (int i = 0; i < this.splitContent.length; i++) {
            if(this.splitContent[i].endsWith(".html") && this.splitContent[i].contains("http")) {
                this.newPagesList.put(this.splitContent[i], siteId);
            }
        }
    }

    public TreeMap<String, Integer> getNewPagesList() {
        return this.newPagesList;
    }
}
