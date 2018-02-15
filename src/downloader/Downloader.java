/**
 *
 * @author Anton Lapin
 * @version date 13 February 2018
 */
package downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Downloader {

    private String result;
    private StringBuilder stringSum = new StringBuilder();
    private BufferedReader reader = null;
    private URL site = null;

    public static void main(String[] args) {
        System.out.println(new Downloader().exec("file:///D:/HTML/MyTestCite.html"));
    }

    public String exec(String url) {
        try {
            site = new URL(url);
            reader = new BufferedReader(new InputStreamReader(site.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringSum.append(line);
                stringSum.append(" ");
            }
            result = stringSum.toString();
            reader.close();
        } catch (IOException ex) {
            //...
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                //...
            }
        }
        return result;
    }
}
