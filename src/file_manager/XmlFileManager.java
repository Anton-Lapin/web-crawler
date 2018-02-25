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

    public void run() {
        System.out.println("XmlFileManager begining...");
        initSitemapXMlFiles();
        System.out.println("XmlFileManager end");
    }

    public void setXmlFilesList(TreeMap<String, Integer> xmlFilesList) {
        this.xmlFilesList = xmlFilesList;
    }

    private void initSitemapXMlFiles() {
        int count = 1;
        Set<Map.Entry<String, Integer>> pair = this.xmlFilesList.entrySet();
        for (Map.Entry<String, Integer> item : pair) {
            this.fileDownloader = new FileDownloader();
            String xmlFileDir = "d:/forSitemaps/sm" + count + ".xml";
            this.fileDownloader.downloadUsingStream(item.getKey(), xmlFileDir);
            String result = openXMLFile(xmlFileDir);
            String[] splitResult1 = result.split(" ");
            for (int i = 0; i < splitResult1.length; i++) {
                this.newPagesList.put(splitResult1[i], item.getValue());
            }
            count++;
        }
    }

    public String openXMLFile(String file) {
        String str = "";
        String hstr = "";
        try {
            File f = new File(file);
            final int length = (int) f.length();
            if (length != 0) {
                char[] cbuf = new char[length];
                InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "UTF-8");
                final int read = isr.read(cbuf);
                str = new String(cbuf, 0, read);
                isr.close();
            }
            hstr = new StringWorker().handlingString(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hstr;
    }

    public TreeMap<String, Integer> getNewPagesList() {
        return newPagesList;
    }
}
