/**
 * Класс содержит метод, обрабатывающий строки
 * @author Anton Lapin
 * @version date Feb 25, 2018
 */
package file_manager;

public class StringWorker {
    private String[] splitResult;
    private String[] subsplit;
    private StringBuilder heapString;
    private String result;
    private int length;

    /**
     * Метод принимает на вход строку; разбивает ее на подстроки, заносит их в массив; проверяет каждый элемент массива
     * те элементы, что отвечают условию, добавляются в строку - кучу (чистые ссылки) через пробел; возвращает строку,
     * состоящую только из ссылок
     * @param str
     * @return result
     */

    public String handlingString(String str) {
        this.heapString = new StringBuilder("");
        this.splitResult = str.split("<loc>");
        this.length = this.splitResult.length;
        for (int i = 0; i < this.length; i++) {
            if (this.splitResult[i].contains("</loc>")){
                this.subsplit = this.splitResult[i].split("</loc>");
                this.heapString.append(this.subsplit[0]);
                this.heapString.append(" ");
            }
        }
        this.result = String.valueOf(this.heapString);
        this.subsplit = null;
        return this.result;
    }
}
