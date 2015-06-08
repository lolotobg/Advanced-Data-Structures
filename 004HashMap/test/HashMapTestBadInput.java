import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 * @author Spas Kyuchukov
 *
 * Test with a known bad pattern for simple hashes with MOD 2^x
 *	see: http://codeforces.com/blog/entry/4898
 */

public class HashMapTestBadInput {
    
    private static final int BEGIN_LENGTH = 8;
    private static final int END_LENGTH = 28;
    private static final int TOTAL_LENGTH = END_LENGTH - BEGIN_LENGTH + 1;
    
    private static List<String> badStrings;
    private static List<String> normalStrings;
            
    @BeforeClass
    public static void SetUp(){
        badStrings = new ArrayList<>(TOTAL_LENGTH);
        normalStrings = new ArrayList<>(TOTAL_LENGTH);
        
        // Generate normal keys
        for (int i = 1; i <= TOTAL_LENGTH; i++) {
            normalStrings.add("key" + i);
        }
        
        // Generate bad strings
        for (int i = BEGIN_LENGTH; i <= END_LENGTH; i++) {
            String bad = generateBadString(i);
            badStrings.add(bad);
        }
    }
    
    @Test
    public void testBadInput() {
        HashMap<Integer> map = new HashMap<Integer>();
        java.util.HashMap<String, Integer> builtInMap = new java.util.HashMap<>();

        
        // Insert bad strings
        long begin, end;
        int indx = 1;
        begin = System.nanoTime();
        for (String bad : badStrings) {
            map.insert(bad, indx);
            indx++;
        }
        end = System.nanoTime();
        assertEquals(TOTAL_LENGTH, map.size());
        System.out.println("My map, time for inserting " + (TOTAL_LENGTH) +
                " bad inputs: " + (end - begin)/1_000_000 + " ms.");
        map.printStatistics();
        
        indx = 1;
        begin = System.nanoTime();
        for (String bad : badStrings) {
            builtInMap.put(bad, indx);
            indx++;
        }
        end = System.nanoTime();
        assertEquals(TOTAL_LENGTH, builtInMap.size());
        System.out.println("Built-in map, time for inserting " + (TOTAL_LENGTH) +
                " bad inputs: " + (end - begin) + " NANO seconds.");

        
        // Get bad strings
        indx = 1;
        begin = System.nanoTime();
        for (String bad : badStrings) {
            assertEquals(indx, (int)map.get(bad));
            indx++;
        }
        end = System.nanoTime();
        System.out.println("My map, time for getting the " + (TOTAL_LENGTH) +
                " bad inputs: " + (end - begin)/1_000_000 + " ms.");
        
        indx = 1;
        begin = System.nanoTime();
        for (String bad : badStrings) {
            assertEquals(indx, (int)builtInMap.get(bad));
            indx++;
        }
        end = System.nanoTime();
        System.out.println("Built-in map, time for getting the " + (TOTAL_LENGTH) +
                " bad inputs: " + (end - begin) + " NANO seconds.");
        
        
        // Insert normal strings
        indx = 1;
        begin = System.nanoTime();
        for (String key : normalStrings) {
            map.insert(key, indx);
            ++indx;
        }
        end = System.nanoTime();
        assertEquals(2 * TOTAL_LENGTH, map.size());
        System.out.println("My map, time for inserting " + (TOTAL_LENGTH) +
                " normal elements: " + (end - begin) + " NANO seconds.");
        map.printStatistics();
        
        indx = 1;
        begin = System.nanoTime();
        for (String key : normalStrings) {
            builtInMap.put(key, indx);
            ++indx;
        }
        end = System.nanoTime();
        assertEquals(2 * TOTAL_LENGTH, builtInMap.size());
        System.out.println("Built-in map, time for inserting " + (TOTAL_LENGTH) +
                " normal elements: " + (end - begin) + " NANO seconds.");
        
        
        // Get normal strings
        indx = 1;
        begin = System.nanoTime();
        for (String key : normalStrings) {
            assertEquals(indx, (int)map.get(key));
            ++indx;
        }
        end = System.nanoTime();
        System.out.println("My map, time for getting the " + (TOTAL_LENGTH) +
                " normal elements: " + (end - begin) + " NANO seconds.");
        
        indx = 1;
        begin = System.nanoTime();
        for (String key : normalStrings) {
            assertEquals(indx, (int)builtInMap.get(key));
            ++indx;
        }
        end = System.nanoTime();
        System.out.println("Built-in map, time for getting the " + (TOTAL_LENGTH) +
                " normal elements: " + (end - begin) + " NANO seconds.");
    }
        
    private static String generateBadString(int lengthPowerOfTwo){
        final int N = 1 << lengthPowerOfTwo;
    
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++){
            sb.append((char)('A' + Integer.bitCount(i) % 2));
        }
        return sb.toString();
    }
}