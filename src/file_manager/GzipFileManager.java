/**
 * @author Anton Lapin
 * @version Feb 25, 2018
 */

package file_manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public class GzipFileManager extends Thread {

    private final String MAIN_FILES_PATH = "d:/forSitemaps/";

    private TreeMap<String, Integer> gzipArchivesList = new TreeMap<>();
    private TreeMap<String, Integer> container = new TreeMap<>();
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();
    private TreeMap<String, Integer> childGZsitemap;
    private FileDownloader fileDownloader;
    private XmlFileManager xmlFileManager;
    private String gzFileDir;
    private String xmlFileDir;
    private int count;
    private String result;
    private FileInputStream fis;
    private GZIPInputStream gis;
    private FileOutputStream fos;
    private byte[] buffer;
    private int len;

    public void run() {
        System.out.println("GzipFileManager begining...");
        initGZFiles();
        System.out.println("GzipFileManager end");
    }

    private void initGZFiles() {
        this.container = openGZArchiveFile();
        Set<Map.Entry<String, Integer>> contents = this.container.entrySet();
        for (Map.Entry<String, Integer> cont : contents) {
            String[] splitResult1 = cont.getKey().split(" ");
            for (int i = 0; i < splitResult1.length; i++) {
                this.newPagesList.put(splitResult1[i], cont.getValue());
            }
        }
        this.container.clear();
    }

    private TreeMap<String, Integer> openGZArchiveFile(){
        this.count = 0;
        this.childGZsitemap = new TreeMap<>();
        Set<Map.Entry<String, Integer>> names = this.gzipArchivesList.entrySet();
        for (Map.Entry<String, Integer> gzsitemap : names) {
            this.fileDownloader = new FileDownloader();
            this.count++;
            this.gzFileDir = MAIN_FILES_PATH + "siteMap" + count + ".xml.gz";
            this.fileDownloader.downloadUsingStream(gzsitemap.getKey(), gzFileDir);
            this.xmlFileDir =  MAIN_FILES_PATH + "sm" + count + ".xml";
            decompressGzipFile(this.gzFileDir, this.xmlFileDir);
            this.xmlFileManager = new XmlFileManager();
            this.result = this.xmlFileManager.openXMLFile(this.xmlFileDir);
            this.childGZsitemap.put(this.result, gzsitemap.getValue());
        }
        return this.childGZsitemap;
    }

    public void setGzipArchivesList(TreeMap<String, Integer> gzipArchivesList) {
        this.gzipArchivesList = gzipArchivesList;
    }

    private void decompressGzipFile(String gzipFile, String newFile) {
        try {
            this.fis = new FileInputStream(gzipFile);
            this.gis = new GZIPInputStream(this.fis);
            this.fos = new FileOutputStream(newFile);
            this.buffer = new byte[16384];
            while((this.len = this.gis.read(this.buffer)) != -1){
                this.fos.write(this.buffer, 0, this.len);
            }
            this.fos.close();
            this.gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String, Integer> getNewPagesList() {
        return this.newPagesList;
    }
}
