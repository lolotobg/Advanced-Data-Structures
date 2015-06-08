import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Spas Kyuchukov
 *
 * Some tests for the persistent vector.
 */
 
public class TestPersistentVector {
    
    @Test
    public void testAppendSingleElement(){
        PersistentVector<Integer> vec = new PersistentVector<>();
        PersistentVector<Integer> newVec = vec.append(42);
        assertEquals(0, vec.size());
        assertEquals(1, newVec.size());
    }
    
    @Test
    public void testAppendSingleElementAndGetIt(){
        PersistentVector<Integer> vec = new PersistentVector<>();
        vec = vec.append(42);
        assertEquals(1, vec.size());
        assertEquals((Integer)42, vec.get(0));
        assertEquals(1, vec.size());
    }
    
    @Test
    public void testAppendSingleElementUpdateItAndGetIt(){
        PersistentVector<Integer> vec = new PersistentVector<>();
        vec = vec.append(42);
        PersistentVector<Integer> newVec = vec.update(0, -42);
        assertEquals(1, vec.size());
        assertEquals(1, newVec.size());
        assertEquals((Integer)(42), vec.get(0));
        assertEquals((Integer)(-42), newVec.get(0));
        assertEquals(1, vec.size());
        assertEquals(1, newVec.size());
    }
    
    @Test
    public void testAppendMultipleElementsAndGetThem(){
        int testSize = 32 * 32 * 32 * 32 * 32 + 2;
        PersistentVector<Integer> vec = new PersistentVector<>();
        for (int i = 0; i < testSize; i++) {
            vec = vec.append(i);
        }
        assertEquals(testSize, vec.size());
        
        int actualHeight = vec.getLeftPathLength();
        int expHeight = vec.calculateHeight(vec.size());
        System.out.println(expHeight);
        
        assertEquals("Bad height for " + vec.size() + " elements.", expHeight, actualHeight);
        
        for (int i = 0; i < testSize; i++) {
            Integer actual = vec.get(i);
            assertEquals((Integer)i, actual);
        }
        assertEquals(testSize, vec.size());
    }
    
    @Test
    public void testAppendUpdateAndGetMultipleElements2Times(){
        int testSize = 7_500_000;
        
        PersistentVector<Integer> vec = new PersistentVector<>();
        for (int i = 0; i < testSize; i++) {
            vec = vec.append(i);
        }
        assertEquals(testSize, vec.size());
        int actualHeight = vec.getLeftPathLength();
        int expHeight = vec.calculateHeight(vec.size());
        assertEquals("Bad height for " + vec.size() + " elements.", expHeight, actualHeight);
        
        for (int i = 0; i < testSize; i++) {
            vec = vec.update(i, i * (i % 3 + 1));
        }
        assertEquals(testSize, vec.size());
        
        for (int i = 0; i < testSize; i++) {
            Integer actual = vec.get(i);
            assertEquals((Integer)(i * (i % 3 + 1)), actual);
        }
        assertEquals(testSize, vec.size());
        
        for (int i = testSize; i < 2 * testSize; i++) {
            vec = vec.append(i);
        }
        assertEquals(2 * testSize, vec.size());
        
        for (int i = testSize; i < 2 * testSize; i++) {
            Integer actual = vec.get(i);
            assertEquals((Integer)(i), actual);
        }
        assertEquals(2 * testSize, vec.size());
        
        for (int i = 0; i < 2 * testSize; i++) {
            vec = vec.update(i, i * (i % 7 + 1));
        }
        assertEquals(2 * testSize, vec.size());
        
        for (int i = 0; i < 2 * testSize; i++) {
            Integer actual = vec.get(i);
            assertEquals((Integer)(i * (i % 7 + 1)), actual);
        }
        assertEquals(2 * testSize, vec.size());
        
    }
    
    @Test
    public void testAppendMultipleElementsPopSomeAndGetTheRest(){
        int testSize = 10_000_000;
        PersistentVector<Integer> vec = new PersistentVector<>();
        for (int i = 0; i < 2 * testSize; i++) {
            vec = vec.append(i);
        }
        assertEquals(2 * testSize, vec.size());
        
        for (int i = 0; i < testSize; i++) {
            vec = vec.pop();
        }
        assertEquals(testSize, vec.size());
        int actualHeight = vec.getLeftPathLength();
        int expHeight = vec.calculateHeight(vec.size());
        assertEquals("Bad height for " + vec.size() + " elements.", expHeight, actualHeight);
        
        for (int i = 0; i < testSize; i++) {
            Integer actual = vec.get(i);
            assertEquals((Integer)i, actual);
        }
        assertEquals(testSize, vec.size());
    }
    
    @Test
    public void testAppendAndGetReversed(){
        int testSize = 10_000_000;
        PersistentVector<Integer> vec = new PersistentVector<>();
        for (int i = 0; i < 2 * testSize; i++) {
            vec = vec.append(i * 2);
        }
        assertEquals(2 * testSize, vec.size());

        for (int i = 2 * testSize - 1; i >= 0; --i) {
            Integer actual = vec.get(i);
            assertEquals((Integer)(i * 2), actual);
        }
        assertEquals(2 * testSize, vec.size());
    }
    
    @Test
    public void testAppendAndPopSingleElement(){
        PersistentVector<Integer> vec = new PersistentVector<>();
        vec = vec.append(42);
        assertEquals(1, vec.size());
        PersistentVector<Integer> vecNew = vec.pop();
        assertEquals(0, vecNew.size());
        assertEquals(1, vec.size());
    }
    
    @Test
    public void testAppendAndPopMultipleElements(){
        int testSize = 32 * 32 * 32 * 32 * 32 + 2;
        PersistentVector<Integer> vec = new PersistentVector<>();
        for (int i = 0; i < testSize; i++) {
            vec = vec.append((testSize - i) * 2);
        }
        assertEquals(testSize, vec.size());
        int actualHeight = vec.getLeftPathLength();
        int expHeight = vec.calculateHeight(vec.size());
        assertEquals("Bad height for " + vec.size() + " elements.", expHeight, actualHeight);
        
        PersistentVector<Integer> vec2 = vec.pop();
        
        for (int i = 0; i < testSize - 1; i++) {
            vec2 = vec2.pop();
        }
        assertEquals(0, vec2.size());
        actualHeight = vec2.getLeftPathLength();
        expHeight = vec2.calculateHeight(vec2.size());
        assertEquals("Bad height for " + vec2.size() + " elements.", expHeight, actualHeight);
        
        // original vec
        assertEquals(testSize, vec.size());
        actualHeight = vec.getLeftPathLength();
        expHeight = vec.calculateHeight(vec.size());
        assertEquals("Bad height for " + vec.size() + " elements.", expHeight, actualHeight);
        for (int i = 0; i < testSize; i++) {
            Integer actual = vec.get(i);
            assertEquals((Integer)((testSize - i) * 2), actual);
        }
    }
}