/**
 *
 * @author Anton Lapin
 * @version date 15 February 2018
 */
package downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Downloader {

    private String result;
    private StringBuilder stringSum = new StringBuilder();
    private BufferedReader reader;
    private URL site;
    private String line;

    public String exec(String url) {
        try {
            this.site = new URL(url);
            this.reader = new BufferedReader(new InputStreamReader(site.openStream()));
            while ((this.line = this.reader.readLine()) != null) {
                this.stringSum.append(line);
                this.stringSum.append(" ");
            }
            this.result = stringSum.toString();
            this.reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                this.reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return this.result;
    }
}
