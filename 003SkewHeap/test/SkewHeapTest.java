import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Spas Kyuchukov
 */
public class SkewHeapTest {

    @Test
    public void testNewlyCreatedHeapIsEmpty() {
        SkewHeap instance = new SkewHeap();
        assertTrue(instance.empty());
    }
    
    @Test
    public void testNewlyCreatedHeapSizeIsZero() {
        SkewHeap instance = new SkewHeap();
        assertEquals(0, instance.size());
    }
    
    @Test
    public void testEmptiedHeapIsEmpty() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 10; i++) {
            instance.add(i);
        }
        for (int i = 0; i < 10; i++) {
            instance.removeMin();
        }
        assertTrue(instance.empty());
    }
    
    @Test
    public void testEmptiedHeapSizeIsZero() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 10; i++) {
            instance.add(i);
        }
        for (int i = 0; i < 10; i++) {
            instance.removeMin();
        }
        assertEquals(0, instance.size());
    }
    
    @Test
    public void testAddOneElement() {
        SkewHeap instance = new SkewHeap();
        instance.add(5);
        assertEquals(1, instance.size());
        assertFalse(instance.empty());
        assertEquals(instance.getRoot().value, 5);
    }
    
    @Test
    public void testAddMultipleDistinctElements() {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 100; i++) {
            instance.add(i);
        }
        assertEquals(100, instance.size());
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
    }
    
    @Test
    public void testAddRepeatingElement() {
        SkewHeap instance = new SkewHeap();
        instance.add(5);
        instance.add(5);
        assertEquals(2, instance.size());
        assertEquals((int)instance.getRoot().value, 5);
        assertEquals((int)instance.getRoot().left.value, 5);
    }
    
    @Test
    public void testGetMinWithOnlyOneExistingElement() throws Exception {
        SkewHeap instance = new SkewHeap();
        instance.add(-1);
        assertEquals(-1, instance.removeMin());
    }
    
    @Test(expected = Exception.class)
    public void testGetMinFromEmptyHeap() throws Exception {
        SkewHeap instance = new SkewHeap();
        try {
            instance.removeMin();
        } catch (Exception ex) {
            String actualExMsg = ex.getMessage();
            String expectedExMsg = SkewHeap.EMPTY_HEAP_REMOVE_EXCEPTION_MESSAGE;
            assertEquals(expectedExMsg, actualExMsg);
            throw ex;
        }
    }
    
    @Test
    public void testInsertAndGetMinMultipleTimes() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = -99; i < 100; i++) {
            instance.add(i);
        }
        for (int i = -99; i < 100; i++) {
            assertEquals(i, instance.removeMin());
        }
        assertEquals(0, instance.size());
        assertNull(instance.getRoot());
    }
    
    @Test
    public void testGetMinElementInsertedFirst() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 100; i++) {
            instance.add(i);
        }
        assertEquals(0, instance.removeMin());
        assertEquals(99, instance.size());
        assertNotNull(instance.getRoot());
    }
    
    @Test
    public void testGetMinElementInsertedLast() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 100; i++) {
            instance.add(i);
        }
        instance.add(-1);
        assertEquals(-1, instance.removeMin());
        assertEquals(100, instance.size());
        assertNotNull(instance.getRoot());
    }
    
    @Test
    public void testGetMinElementInsertedInTheMiddle() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = 50; i < 100; i++) {
            instance.add(i);
        }
        instance.add(33);
        for (int i = 100; i < 150; i++) {
            instance.add(i);
        }
        assertEquals(33, instance.removeMin());
        assertEquals(100, instance.size());
        assertNotNull(instance.getRoot());
    }

    @Test
    public void testGetMinThenAddAgainTheSameElementAndGetMinAgain() throws Exception {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 100; i++) {
            instance.add(i);
        }
        int actualMin = instance.removeMin();
        assertEquals(0, actualMin);
        assertEquals(99, instance.size());
        assertNotNull(instance.getRoot());
        instance.add(actualMin);
        assertEquals(100, instance.size());
        assertNotNull(instance.getRoot());
        assertEquals(actualMin, instance.removeMin());
        assertEquals(99, instance.size());
        assertNotNull(instance.getRoot());
    }
    
    @Test
    public void testMergeEmptyWithNull() {
        SkewHeap instance = new SkewHeap();
        assertTrue(instance.empty());
        assertNull(instance.getRoot());
        SkewHeap other = null;
        instance.merge(other);
        assertTrue(instance.empty());
        assertNull(instance.getRoot());
    }
    
    @Test
    public void testMergeEmptyWithEmpty() {
        SkewHeap instance = new SkewHeap();
        assertTrue(instance.empty());
        assertNull(instance.getRoot());
        SkewHeap other = new SkewHeap();
        instance.merge(other);
        assertTrue(instance.empty());
        assertNull(instance.getRoot());
    }
    
    @Test
    public void testMergeEmptyWithNonEmpty() {
        SkewHeap instance = new SkewHeap();
        SkewHeap other = new SkewHeap();
        for (int i = 50; i < 100; i++) {
            other.add(i);
        }
        assertTrue(instance.empty());
        assertNull(instance.getRoot());
        instance.merge(other);
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(50, instance.size());
        assertTrue(other.empty());
        assertNull(other.getRoot());
    }
    
    @Test
    public void testMergeNonEmptyWithNull() {
        SkewHeap instance = new SkewHeap();
        for (int i = 50; i < 100; i++) {
            instance.add(i);
        }
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(50, instance.size());
        SkewHeap other = null;
        instance.merge(other);
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(50, instance.size());
    }
    
    @Test
    public void testMergeNonEmptyWithEmpty() {
        SkewHeap instance = new SkewHeap();
        for (int i = 50; i < 100; i++) {
            instance.add(i);
        }
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(50, instance.size());
        SkewHeap other = new SkewHeap();
        instance.merge(other);
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(50, instance.size());
    }
    
    @Test
    public void testMergeNonEmptyWithNonEmpty() {
        SkewHeap instance = new SkewHeap();
        for (int i = 0; i < 50; i++) {
            instance.add(i);
        }
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(50, instance.size());
        SkewHeap other = new SkewHeap();
        for (int i = 40; i < 90; i++) {
            other.add(i);
        }
        assertFalse(other.empty());
        assertNotNull(other.getRoot());
        assertEquals(50, other.size());
        
        instance.merge(other);
        
        assertFalse(instance.empty());
        assertNotNull(instance.getRoot());
        assertEquals(100, instance.size());
        
        assertTrue(other.empty());
        assertNull(other.getRoot());
        assertEquals(0, other.size());
    }
}