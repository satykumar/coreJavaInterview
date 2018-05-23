package ocuurance.word;

import java.util.HashMap;
import java.util.Map;

public class JavaWordOccurrenceExample {
    public static void main(String[] args) {
    String wordOccurrence = "string are count , java can see count java are not";
    
    // This will get all word in string and then find number of occurrence 
    // in string sentence. We need to find same word number of times
    String[] words = wordOccurrence.toLowerCase().split("s+");
  
    // Here we set number of String word occurrence in map
    Map<String, Integer> wordCounts = new HashMap<String, Integer>();
  
    for (String word : words) {
       Integer count = wordCounts.get(word);
            if (count == null) {
             count = 0;
         }
         wordCounts.put(word, count + 1);
     }
      System.out.println(wordCounts);
   }
}

