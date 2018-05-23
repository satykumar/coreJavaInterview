package java8.test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Java8TestMapAndListWithSet {

    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "linode.com");
        map.put(2, "heroku.com");
            
        String result = "";
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if("something".equals(entry.getValue())){
                result = entry.getValue();
            }
        }
        
        String result1 = map.entrySet().stream()
                .filter(x -> "something".equals(x.getValue()))
                .map(x->x.getValue())
                .collect(Collectors.joining());
        System.out.println("result1"+result1);
            //Map -> Stream -> Filter -> MAP
            Map<Integer, String> collect = map.entrySet().stream()
                .filter(x -> x.getKey() == 2)
                .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
                System.out.println("reslut2"+collect);
            // or like this
            Map<Integer, String> collect1 = map.entrySet().stream()
                .filter(x -> x.getKey() == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            System.out.println("result3"+collect1);
    }
}
