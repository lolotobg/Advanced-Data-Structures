
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/*
 * These unit tests are meant to supplement the existing detailed tests that were provided 
 * with the task, so there aren't any test that deal with exact internal properties
 * (except the one for height, that tests the general AVL height)
 *
 * @author Spas Kyuchukov
 */
 
public class AVLTreeTest {
    
    private static Random rand = new Random();
    
    @Test
    public void testEmptyAVLSizeIsZero() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        assertEquals(0, avl.getSize());
    }
    
    @Test
    public void testInsertOneElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        avl.insertNode(5);
        assertEquals(1, avl.getSize());
        assertEquals((int)avl.root.value, 5);
    }
    
    @Test
    public void testInsertMultipleDistinctElements() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = 0; i < 100; i++) {
            avl.insertNode(i);
        }
        assertEquals(100, avl.getSize());
        assertNotNull(avl.root);
    }
    
    @Test
    public void testInsertRepeatingElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        avl.insertNode(5);
        avl.insertNode(5);
        assertEquals(2, avl.getSize());
        assertEquals((int)avl.root.value, 5);
        assertEquals((int)avl.root.leftChild.value, 5);
    }
    
    @Test
    public void testContainsTheOnlyExistingElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        avl.insertNode(-1);
        assertNotNull(avl.findNode(-1));
    }
    
    @Test
    public void testContainsNonExistingElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        avl.insertNode(-1);
        assertNull(avl.findNode(123));
    }
    
    @Test
    public void testContainsMultipleExistingElements() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = -99; i < 100; i++) {
            avl.insertNode(i);
        }
        for (int i = -99; i < 100; i++) {
            assertNotNull(avl.findNode(i));
        }
    }
    
    @Test
    public void testContainsOnEmptyAVL() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        assertNull(avl.findNode(5));
    }
    
    @Test
    public void testRemoveTheOnlyElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        avl.insertNode(5);
        avl.deleteNode(5);
        assertEquals(0, avl.getSize());
        assertNull(avl.root);
    }
    
    @Test
    public void testRemoveSingleElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = 0; i < 100; i++) {
            avl.insertNode(i);
        }
        avl.deleteNode(55);
        assertEquals(99, avl.getSize());
        assertNull(avl.findNode(55));
        for (int i = 0; i < 100; i++) {
            if(i != 55){
                assertNotNull(avl.findNode(i));
            }
        }
        assertNotNull(avl.root);
    }
    
    @Test
    public void testRemoveMultipleElements() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = 0; i < 100; i++) {
            avl.insertNode(i);
        }
        for (int i = 0; i < 50; i++) {
            avl.deleteNode(i);
        }
        
        assertEquals(50, avl.getSize());
        for (int i = 0; i < 50; i++) {
            assertNull(avl.findNode(i));
        }
        for (int i = 50; i < 100; i++) {
            assertNotNull(avl.findNode(i));
        }
        assertNotNull(avl.root);
    }
    
    @Test
    public void testRemoveNonExistingElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = 0; i < 100; i++) {
            avl.insertNode(i);
        }
        avl.deleteNode(1000);
        assertEquals(100, avl.getSize());
        assertNotNull(avl.root);
    }
    
    @Test
    public void testRemoveTwiceTheSameElement() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = 0; i < 100; i++) {
            avl.insertNode(i);
        }
        avl.deleteNode(55);
        avl.deleteNode(55);
        assertEquals(99, avl.getSize());
        assertNull(avl.findNode(55));
    }
    
    @Test
    public void testRemoveThenAddAgain() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        for (int i = 0; i < 100; i++) {
            avl.insertNode(i);
        }
        avl.deleteNode(55);
        avl.insertNode(55);
        assertEquals(100, avl.getSize());
        assertNotNull(avl.findNode(55));
    }
    
    @Test
    public void testRemoveFromEmptyAVL() {
        AVLTree<Integer> avl = new AVLTree<Integer>();
        avl.deleteNode(5);
        assertEquals(0, avl.getSize());
        assertNull(avl.root);
    }
    
    @Test
    public void testBalancingHeight() {
        
        AVLTree<Integer> avl = new AVLTree<Integer>();
        Deque<Integer> stack = new ArrayDeque<Integer>();
        
        for (int i = 0; i < 10_000_000; i++) {
            int currVal = rand.nextInt();
            avl.insertNode(currVal);
            stack.add(currVal);
        }
        for (int i = 0; i < 3_000_000; i++) {
            if(i % 2 == 0){
                avl.deleteNode(stack.pop());
            } else {
                avl.deleteNode(stack.poll());
            }
        }
        for (int i = 0; i < 7_000_000; i++) {
            avl.deleteNode(rand.nextInt());
        }
        
        int heightBeforeUpdate = avl.root.height;
        
        avl.updateTreeHeight();
        
        assertEquals(heightBeforeUpdate, avl.root.height);
        
        int n = avl.getSize();
        double maxHeight = 1.44 * log2(n+2) - 0.328;
        double log2n = log2(n);
        
        System.out.println("\nn = " + avl.getSize() + "; log2n = " + log2n +
                "; height = " + avl.root.height + "; maxHeight = " + maxHeight);
        
        assertTrue("Height = " + avl.root.height + " should be <= than 1.44*log2(n+2)-0.328 =" + maxHeight,
                avl.root.height <= maxHeight);
        assertTrue("Height = " + avl.root.height + " should be >= than log2(n) =" + log2n,
                avl.root.height >= log2n);
    }
    
    private double log2(double x){
        return Math.log10(x)/Math.log10(2);
    }
}