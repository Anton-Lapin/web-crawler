/**
 * @author Antor Lapin
 * @version date Feb 23, 2018
 */
package file_manager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class FileDownloader {

    public void downloadUsingStream(String urlStr, String file) {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(urlStr);
            bis = new BufferedInputStream(url.openStream());
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, count);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                bis.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
