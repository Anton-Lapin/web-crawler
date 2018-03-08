/**
 * Класс содержит методы для работы с файлами с расширением .xml, скачивание из сети Интернет, чтение и обработка
 * содержимого
 * @author Anton Lapin
 * @version date Feb 23, 2018
 */
package file_manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XmlFileManager extends Thread {
    private static Logger log = Logger.getLogger(XmlFileManager.class.getName());
    private TreeMap<String, Integer> xmlFilesList = new TreeMap<>();
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();
    private FileDownloader fileDownloader;
    private int count;
    private String xmlFileDir;
    private String result;
    private String[] splitResult1;
    private String string;
    private String handledString;
    private File file1;
    private char[] cbuffer;
    private InputStreamReader isr;

    /**
     * Точка входа в класс
     */

    public void run() {
        System.out.println("XmlFileManager is beginning...");
        initSitemapXMlFiles();
        System.out.println("XmlFileManager end");
    }

    /**
     * Метод устанавливает список ссылок на .xml файлы
     * @param xmlFilesList
     */

    public void setXmlFilesList(TreeMap<String, Integer> xmlFilesList) {
        this.xmlFilesList = xmlFilesList;
    }

    /**
     * Метод инициирует загрузку и открытие .xml файлов согласно списка ссылок, инициирует чтение содержимого,
     * обработку; заносит новые ссылки в список newPagesList
     */

    private void initSitemapXMlFiles() {
        this.count = 1;
        Set<Map.Entry<String, Integer>> pair = this.xmlFilesList.entrySet();
        for (Map.Entry<String, Integer> item : pair) {
            this.fileDownloader = new FileDownloader();
            this.xmlFileDir = "d:/forSitemaps/sm" + this.count + ".xml";
            this.fileDownloader.downloadUsingStream(item.getKey(), this.xmlFileDir);
            this.result = openXMLFile(this.xmlFileDir);
            this.splitResult1 = this.result.split(" ");
            for (int i = 0; i < this.splitResult1.length; i++) {
                this.newPagesList.put(this.splitResult1[i], item.getValue());
            }
            this.count++;
        }
    }

    /**
     * Метод на вход принимает директиву имеющегося файла, открывает его, считывает содержимое буферным методом
     * в строку, инициирует обработку строки, возвращает обработанную строку
     * @param file
     * @return handleString
     */

    public String openXMLFile(String file) {
        this.string = "";
        this.handledString = "";
        try {
            this.file1 = new File(file);
            final int length = (int) this.file1.length();
            if (length != 0) {
                this.cbuffer = new char[length];
                this.isr = new InputStreamReader(new FileInputStream(this.file1), "UTF-8");
                final int read = this.isr.read(this.cbuffer);
                this.string = new String(this.cbuffer, 0, read);
                this.isr.close();
            }
            this.handledString = new StringWorker().handlingString(this.string);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
        return this.handledString;
    }

    /**
     * Метод возвращает список новых ссылок на страницы
     * @return newPagesList
     */

    public TreeMap<String, Integer> getNewPagesList() {
        return this.newPagesList;
    }
}
