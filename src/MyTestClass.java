import collector.Collector;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyTestClass {
    public static void main(String[] args) {
        System.out.println("Test starting...");
        Collector collector = new Collector();
        collector.run();
        System.out.println("Test end");
    }
}
