import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Spas Kyuchukov
 */
public class SkewHeapPerformanceTest {
    
    public static final int SIZE = 10_000_000;
        
    @Test
    public void testInsertManyConsequtiveElementsInAscendingOrder() {
        SkewHeap instance = new SkewHeap();
        long timeNanos = 0;
        for (int i = 0; i < SIZE; i++) {
            long beginT = System.nanoTime();
            instance.add(i);
            long endT = System.nanoTime();
            timeNanos += (endT - beginT);
        }
        assertEquals(SIZE, instance.size());
        assertNotNull(instance.getRoot());
        assertEquals(0, instance.getRoot().value);
        long timeMilliSecs = timeNanos/1_000_000;
        int rightLength = instance.getRightPathLength();
        double log2Size = log2(SIZE);
        System.out.println("Time for inserting " + SIZE + " consequtive elements ascending: " + timeMilliSecs +
                " ms. Right path length: " + rightLength + ". Log2(SIZE): " + log2Size);
    }
    
    @Test
    public void testInsertManyConsequtiveElementsInDescendingOrder() {
        SkewHeap instance = new SkewHeap();
        long timeNanos = 0;
        for (int i = 0; i < SIZE; i++) {
            long beginT = System.nanoTime();
            instance.add(SIZE-i-1);
            long endT = System.nanoTime();
            timeNanos += (endT - beginT);
        }

        assertEquals(SIZE, instance.size());
        assertNotNull(instance.getRoot());
        assertEquals(0, instance.getRoot().value);
        long timeMilliSecs = timeNanos/1_000_000;
        int rightLength = instance.getRightPathLength();
        double log2Size = log2(SIZE);
        System.out.println("Time for inserting " + SIZE + " consequtive elements descending: " + timeMilliSecs +
                " ms. Right path length: " + rightLength + ". Log2(SIZE): " + log2Size);
    }
    
    @Test
    public void testInsertManyRandomElements() {
        java.util.Random rnd = new java.util.Random();
        SkewHeap instance = new SkewHeap();
        long timeNanos = 0;
        int min = Integer.MAX_VALUE;
        
        for (int i = 0; i < SIZE; i++) {
            int num = rnd.nextInt();
            long beginT = System.nanoTime();
            instance.add(num);
            long endT = System.nanoTime();
            timeNanos += (endT - beginT);
            if(num < min){
                min = num;
            }
        }
        
        assertEquals(SIZE, instance.size());
        assertNotNull(instance.getRoot());
        assertEquals(min, instance.getRoot().value);
        long timeMilliSecs = timeNanos/1_000_000;
        int rightLength = instance.getRightPathLength();
        double log2Size = log2(SIZE);
        System.out.println("Time for inserting " + SIZE + " random elements: " + timeMilliSecs +
                " ms. Right path length: " + rightLength + ". Log2(SIZE): " + log2Size);
    }
    
    private double log2(double x){
        return Math.log10(x)/Math.log10(2);
    }
}