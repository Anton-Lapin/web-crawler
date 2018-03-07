/**
 * Класс содержит метод загрузки файлов из сети Интернет
 * @author Antor Lapin
 * @version date Feb 23, 2018
 */
package file_manager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class FileDownloader {
    private BufferedInputStream bis;
    private FileOutputStream fos;
    private URL url;
    private byte[] buffer;
    private int count;

    /**
     * Метод принимает на вход строку url-адреса и строку места расположения скачиваемого файла на ПК; буферным методом
     * скачивает файл по указанному адресу из источника в указанную директиву на ПК.
     * @param urlStr
     * @param file
     */

    public void downloadUsingStream(String urlStr, String file) {
        this.bis = null;
        this.fos = null;
        try {
            this.url = new URL(urlStr);
            this.bis = new BufferedInputStream(url.openStream());
            this.fos = new FileOutputStream(file);
            this.buffer = new byte[1024];
            this.count = 0;
            while ((this.count = this.bis.read(this.buffer, 0, 1024)) != -1) {
                this.fos.write(this.buffer, 0, this.count);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                this.fos.close();
                this.bis.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
