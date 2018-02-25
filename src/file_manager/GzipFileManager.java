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

    private TreeMap<String, Integer> gzipArchivesList = new TreeMap<>();
    private TreeMap<String, Integer> container = new TreeMap<>();
    private TreeMap<String, Integer> newPagesList = new TreeMap<>();
    private FileDownloader fileDownloader;
    private XmlFileManager xmlFileManager;

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
        int count = 0;
        String result;
        TreeMap<String, Integer> childGZsitemap = new TreeMap<>();
        Set<Map.Entry<String, Integer>> names = this.gzipArchivesList.entrySet();
        for (Map.Entry<String, Integer> gzsitemap : names) {
            fileDownloader = new FileDownloader();
            count++;
            String gzFileDir = "d:/forSitemaps/siteMap" + count + ".xml.gz";
            this.fileDownloader.downloadUsingStream(gzsitemap.getKey(), gzFileDir);
            String xmlFileDir = "d:/forSitemaps/sm" + count + ".xml";
            decompressGzipFile(gzFileDir, xmlFileDir);
            this.xmlFileManager = new XmlFileManager();
            result = xmlFileManager.openXMLFile(xmlFileDir);
            childGZsitemap.put(result, gzsitemap.getValue());
        }
        return childGZsitemap;
    }

    public void setGzipArchivesList(TreeMap<String, Integer> gzipArchivesList) {
        this.gzipArchivesList = gzipArchivesList;
    }

    private void decompressGzipFile(String gzipFile, String newFile) {
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[16384];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TreeMap<String, Integer> getNewPagesList() {
        return newPagesList;
    }
}
