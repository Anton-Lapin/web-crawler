/**
 * Класс содержит методы для обхода стандартных ссылок на файлы сайтов robots.txt
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
    private String url;
    private String[] splitContent;

    /**
     * Точка входа в класс
     */

    public void run() {
        System.out.println("RobotsTxtParser beginning...");

//        Set<Map.Entry<String, Integer>> set = uncheckedRobotsTxtReferencesList.entrySet();
//        for (Map.Entry<String, Integer> o : set) {
//            System.out.println(o.getKey() + " " + o.getValue());
//        }
        startDownloader(this.uncheckedRobotsTxtReferencesList);
        System.out.println("RobotsTxtParser end");
    }

    /**
     * метод устанавливает список непроверенных ссылок uncheckedRobotsTxtReferencesList
     * @param list
     */

    public void setUncheckedRobotsTxtReferencesList(TreeMap<String, Integer> list) {
        this.uncheckedRobotsTxtReferencesList = list;
    }

    /**
     * Метод принимает на вход список непроверенных ссылок, инициирует загрузку данных из сети Интернет согласно
     * url адресам, имеющимся в списке
     * @param list
     */

    private void startDownloader(TreeMap<String, Integer> list) {
        this.downloader = new Downloader();
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            this.url = o.getKey();
            this.pageContent = this.downloader.exec(this.url);
            pageContentHandle(this.pageContent, o.getValue());
            System.out.println(pageContent);
            System.out.println("-----------------------------");
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
            if(this.splitContent[i].contains("sitemap") && this.splitContent[i].contains("http")) {
                this.newPagesList.put(this.splitContent[i], siteId);
            }
        }
    }

    /**
     * Метод возвращает список новых страниц
     * @return newPagesList
     */

    public TreeMap<String, Integer> getNewPagesList() {
        return this.newPagesList;
    }
}
