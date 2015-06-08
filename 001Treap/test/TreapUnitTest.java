import org.junit.Test;
import static org.junit.Assert.*;

public class TreapUnitTest {
    
    @Test
    public void testEmptyTreapSizeIsZero() {
        TreapImplTest treap = new TreapImplTest();
        assertEquals(0, treap.size());
    }
    
    @Test
    public void testInsertOneElement() {
        TreapImplTest treap = new TreapImplTest();
        treap.insert(5);
        assertEquals(1, treap.size());
        assertEquals(treap.root.key, 5);
    }
    
    @Test
    public void testInsertMultipleDistinctElements() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = 0; i < 100; i++) {
            treap.insert(i);
        }
        assertEquals(100, treap.size());
        assertNotNull(treap.root);
    }
    
    @Test
    public void testInsertRepeatingElement() {
        TreapImplTest treap = new TreapImplTest();
        treap.insert(5);
        treap.insert(5);
        assertEquals(1, treap.size());
        assertEquals(treap.root.key, 5);
    }
    
    @Test
    public void testContainsTheOnlyExistingElement() {
        TreapImplTest treap = new TreapImplTest();
        treap.insert(-1);
        assertTrue(treap.containsKey(-1));
    }
    
    @Test
    public void testContainsNonExistingElement() {
        TreapImplTest treap = new TreapImplTest();
        treap.insert(-1);
        assertFalse(treap.containsKey(123));
    }
    
    @Test
    public void testContainsMultipleExistingElements() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = -99; i < 100; i++) {
            treap.insert(i);
        }
        for (int i = -99; i < 100; i++) {
            assertTrue(treap.containsKey(i));
        }
    }
    
    @Test
    public void testContainsOnEmptyTreap() {
        TreapImplTest treap = new TreapImplTest();
        assertFalse(treap.containsKey(5));
    }
    
    @Test
    public void testRemoveTheOnlyElement() {
        TreapImplTest treap = new TreapImplTest();
        treap.insert(5);
        treap.remove(5);
        assertEquals(0, treap.size());
        assertNull(treap.root);
    }
    
    @Test
    public void testRemoveSingleElement() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = 0; i < 100; i++) {
            treap.insert(i);
        }
        treap.remove(55);
        assertEquals(99, treap.size());
        assertFalse(treap.containsKey(55));
        for (int i = 0; i < 100; i++) {
            if(i != 55){
                assertTrue(treap.containsKey(i));
            }
        }
        assertNotNull(treap.root);
    }
    
    @Test
    public void testRemoveMultipleElements() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = 0; i < 100; i++) {
            treap.insert(i);
        }
        for (int i = 0; i < 50; i++) {
            treap.remove(i);
        }
        
        assertEquals(50, treap.size());
        for (int i = 0; i < 50; i++) {
            assertFalse(treap.containsKey(i));
        }
        for (int i = 50; i < 100; i++) {
            assertTrue(treap.containsKey(i));
        }
        assertNotNull(treap.root);
    }
    
    @Test
    public void testRemoveNonExistingElement() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = 0; i < 100; i++) {
            treap.insert(i);
        }
        treap.remove(1000);
        assertEquals(100, treap.size());
        assertNotNull(treap.root);
    }
    
    @Test
    public void testRemoveTwiceTheSameElement() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = 0; i < 100; i++) {
            treap.insert(i);
        }
        treap.remove(55);
        treap.remove(55);
        assertEquals(99, treap.size());
        assertFalse(treap.containsKey(55));
    }
    
    @Test
    public void testRemoveThenAddAgain() {
        TreapImplTest treap = new TreapImplTest();
        for (int i = 0; i < 100; i++) {
            treap.insert(i);
        }
        treap.remove(55);
        treap.insert(55);
        assertEquals(100, treap.size());
        assertTrue(treap.containsKey(55));
    }
    
    @Test
    public void testRemoveFromEmptyTreap() {
        TreapImplTest treap = new TreapImplTest();
        treap.remove(5);
        assertEquals(0, treap.size);
        assertNull(treap.root);
    }
}