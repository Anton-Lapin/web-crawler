/**
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

public class XmlFileManager extends Thread {

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

    public void run() {
        System.out.println("XmlFileManager begining...");
        initSitemapXMlFiles();
        System.out.println("XmlFileManager end");
    }

    public void setXmlFilesList(TreeMap<String, Integer> xmlFilesList) {
        this.xmlFilesList = xmlFilesList;
    }

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
            e.printStackTrace();
        }
        return this.handledString;
    }

    public TreeMap<String, Integer> getNewPagesList() {
        return this.newPagesList;
    }
}
