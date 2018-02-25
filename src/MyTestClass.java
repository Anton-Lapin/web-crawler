import collector.Collector;
import data_base_manager.PagesTableReader;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyTestClass {
    public static void main(String[] args) {
//        PagesTableReader ptr = new PagesTableReader();
//        ptr.clearTable();

        System.out.println("Test starting...");
        Collector collector = new Collector();
        collector.run();
        System.out.println("Test end");
    }
}
