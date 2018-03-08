/**
 * Класс содержит методы обхода сайтмапов, загрузке данных по полученным ранее ссылкам, обработке данных с получением
 * новых ссылок.
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

    /**
     * Точка входа в класс
     */

    public void run() {
        System.out.println("SitemapsParser beginning...");

//        Set<Map.Entry<String, Integer>> set = uncheckedSitemapReferencesList.entrySet();
//        for (Map.Entry<String, Integer> o : set) {
//            System.out.println(o.getKey() + " " + o.getValue());
//        }
        startDownloader(this.uncheckedSitemapReferencesList);
        System.out.println("SitemapsParser end");
    }

    /**
     * Метод устанавливает список непроверенных ссылок сайтмапов
     * @param list
     */

    public void setUncheckedSitemapReferencesList(TreeMap<String, Integer> list) {
        this.uncheckedSitemapReferencesList = list;
    }

    /**
     * Метод получает на вход список со ссылками сайтмапов, инициирует загрузку данных из сети Интернет, согласно
     * url адресам, инициирует метод обработки содержимого страниц
     * @param list
     */

    private void startDownloader(TreeMap<String, Integer> list) {
        this.downloader = new Downloader();
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            this.url = o.getKey();
            this.pageContent = this.downloader.exec(this.url);
            pageContentHandle(this.pageContent, o.getValue());
        }
    }

    /**
     * Метод обрабатывает содержимое, скачанное из сети Интернет, под соответствующим идентификатором записывает
     * результат обработки (ссылки на сайтмапы) в список новых страниц newPagesList
     * @param pageContent
     * @param siteId
     */

    private void pageContentHandle(String pageContent, Integer siteId){
        this.splitContent = pageContent.split(" ");
        for (int i = 0; i < this.splitContent.length; i++) {
            if(this.splitContent[i].endsWith(".html") && this.splitContent[i].contains("http")) {
                this.newPagesList.put(this.splitContent[i], siteId);
            }
        }
    }

    /**
     * Метод возвращает список новых ссылок
     * @return newPagesList
     */

    public TreeMap<String, Integer> getNewPagesList() {
        return this.newPagesList;
    }
}
