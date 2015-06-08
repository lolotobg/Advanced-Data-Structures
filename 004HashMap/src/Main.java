
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * @author Spas Kyuchukov
 */

public class Main {

    // The file should contain unique lines!
    // A good one: https://my.pcloud.com/publink/show?code=kZlCWXZjXS6eScpvwzDNO1SPDCAlm7cfPx7#folder=28378376
    public static final String FILE_NAME = "wordlist.txt";
    
    public static void main(String[] args) {
        Random rand = new Random();
        HashMap<Integer> myMap = new HashMap<Integer>();
        java.util.HashMap<String, Integer> buildInMap = new java.util.HashMap<>();
        ArrayList<Map.Entry<String, Integer>> words = new ArrayList<Map.Entry<String, Integer>>();
        //myMap.resize();
        
        // Read the file
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(FILE_NAME);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        String strLine;
        try {
            while((strLine = br.readLine()) != null) {
                Integer rndInt = rand.nextInt();
                AbstractMap.SimpleEntry<String, Integer> entry = new AbstractMap.SimpleEntry(strLine, rndInt);
                words.add(entry);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            //Close the input stream
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        
        // Insertions:
        long start = System.nanoTime();
        for (Map.Entry<String, Integer> entry : words) {
            myMap.insert(entry.getKey(), entry.getValue());
        }
        long middle = System.nanoTime();
        for (Map.Entry<String, Integer> entry : words) {
            buildInMap.put(entry.getKey(), entry.getValue());
        }
        long end = System.nanoTime();

        System.out.println("My map - insertion all elements one by one: " + (middle - start) / 1_000_000);
        myMap.printStatistics();
        System.out.println("Build-in map - insertion all elements one by one: " + (end - middle) / 1_000_000);
        
        if(words.size() != myMap.size()){
            System.out.println("Bad myMap.size()!");
        }
        
        if(words.size() != buildInMap.size()){
            System.out.println("Bad buildInMap.size()!");
        }
        
        // Verify get:
        start = System.nanoTime();
        for (Map.Entry<String, Integer> entry : words) {
            if(entry.getValue() != myMap.get(entry.getKey())){
                System.out.println("Bad myMap value for " + entry.getKey());
            }
        }
        middle = System.nanoTime();
        for (Map.Entry<String, Integer> entry : words) {
            if(entry.getValue() != buildInMap.get(entry.getKey())){
                System.out.println("Bad buildInMap value for " + entry.getKey());
            }
        }
        end = System.nanoTime();
        
        System.out.println("My map - get all elements one by one: " + (middle - start) / 1_000_000);
        System.out.println("Size = " + myMap.size() + "; Capacity = " + myMap.capacity());
        System.out.println("Build in map - get all elements one by one: " + (end - middle) / 1_000_000);
        
        if(words.size() != myMap.size()){
            System.out.println("Bad myMap.size()!");
        }
        
        if(words.size() != buildInMap.size()){
            System.out.println("Bad buildInMap.size()!");
        }
        
        
        // Deletions:
        start = System.nanoTime();
        for (Map.Entry<String, Integer> entry : words) {
            myMap.erase(entry.getKey());
        }
        middle = System.nanoTime();
        for (Map.Entry<String, Integer> entry : words) {
            buildInMap.remove(entry.getKey());
        }
        end = System.nanoTime();

        System.out.println("My map - all elements removed one by one: " + (middle - start) / 1_000_000);
        System.out.println("Size = " + myMap.size() + "; Capacity = " + myMap.capacity());
        System.out.println("Build in map - all elements removed one by one: " + (end - middle) / 1_000_000);
        System.out.println("Size = " + buildInMap.size());
        
        if(0 != myMap.size()){
            System.out.println("Bad myMap.size()!");
        }
        
        if(0 != buildInMap.size()){
            System.out.println("Bad buildInMap.size()!");
        }
        
        System.out.println("True rehashes: " + myMap.rehashTrueCount);
        System.out.println("Same bucket rehashes: " + (myMap.rehashAllCount - myMap.rehashTrueCount));
        System.out.println("All element rehashes: " + myMap.rehashAllCount);
    }

}
