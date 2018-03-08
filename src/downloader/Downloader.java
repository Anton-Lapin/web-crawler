/**
 * Класс содержит метод загрузки из сети Интернет содержимого страницы, соответствующей заданному url - адресу
 * @author Anton Lapin
 * @version date 15 February 2018
 */
package downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Downloader {
    private Logger log = Logger.getLogger(Downloader.class.getName());
    private String result;
    private StringBuilder stringSum = new StringBuilder();
    private BufferedReader reader;
    private URL site;
    private String line;

    /**
     * Метод принимает на вход url-адрес, инициирует загрузку страницы из сети Интернет по данному адресу;
     * кусочным методом собирает подстроки в общую строку; возвращает результат в виде этой общей строки.
     * @param url
     * @return result
     */

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
            log.log(Level.SEVERE, "Exception: ", ex);
        } finally {
            try {
                this.reader.close();
            } catch (IOException ex) {
                log.log(Level.SEVERE, "Exception: ", ex);
            }
        }
        return this.result;
    }
}
