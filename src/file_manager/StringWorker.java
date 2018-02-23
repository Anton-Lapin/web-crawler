package file_manager;

public class StringWorker {
    private String[] splitResult;
    private String[] subsplit;
    private StringBuilder heapString;
    private String result;
    private int length;

    /**
     * Метод обработки строки; вычленение из неё ссылок веб-страниц
     * @param str
     * @return
     */

    public String handlingString(String str) {
        this.heapString = new StringBuilder("");
        this.splitResult = str.split("<loc>");//делим на куски и кладем их в массив строк
        this.length = this.splitResult.length;//количество кусков
        for (int i = 0; i < this.length; i++) {
            if (this.splitResult[i].contains("</loc>")){
                this.subsplit=this.splitResult[i].split("</loc>");//делим подстроку на подподстроки
//                System.out.println(this.subsplit[0]);
                this.heapString.append(this.subsplit[0]);
                this.heapString.append(" "); //и кладем первую
//                подподстроку в текущую строку  (и будет являться ссылкой)
            }
        }
        result = String.valueOf(heapString);
        this.subsplit = null;
        return this.result;
    }
}
