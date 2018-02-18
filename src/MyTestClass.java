import collector.Collector;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MyTestClass {
    public static void main(String[] args) {
        System.out.println("Test starting...");
        Collector collector = new Collector();
        collector.run();
        TreeMap<String, Integer> list;
        list = collector.getRobotsTxtReferenceList();
        Set<Map.Entry<String, Integer>> set = list.entrySet();
        for (Map.Entry<String, Integer> o : set) {
            System.out.println(o.getKey() + " " + o.getValue());
        }
        System.out.println("Test end");
    }
}
