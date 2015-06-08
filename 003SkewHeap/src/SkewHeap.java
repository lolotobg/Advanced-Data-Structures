
import java.util.LinkedList;
import java.util.Queue;

/**
 * Data type that represents a min-heap implementation of Skew heap data
 * structure
 *
 * @author Spas Kyuchukov
 */
 
public class SkewHeap {
    public static final String EMPTY_HEAP_REMOVE_EXCEPTION_MESSAGE =
            "Error! You must not call removeMin() on an empty SkewHeap!";

    /**
     * Reference to the root for the current skew heap
     */
    private Node root;
    private int size;

    /**
     * Data type that represents a node in the Skew heap
     * It is public for testing purposes
     */
    public static class Node {

        int value; // data value
        Node left; // left subtree
        Node right; // right subtree
        //Node parent;

        Node(int value) {
            this.value = value;
            this.left = null;
            this.right = null;
            //this.parent = null;
        }
    }

    public SkewHeap() {
        this.size = 0;
        this.root = null;
    }

    /**
     * Method that adds a node with data provided by @value in the current skew
     * heap.
     *
     * @param value: value to be added in the heap
     */
    public void add(int value) {
        Node newNode = new Node(value);
        root = merge(root, newNode);
        ++size;
    }

    /**
     * This method removes and returns the smallest element in the current skew
     * heap.
     *
     * @returns the removed element
     *
     * @throws Exception if there are no elements, but minimum was tried to be
     * removed
     */
    public int removeMin() throws Exception {
        if (root == null) {
            // Of course it is better to throw a more meaningful exception class, but the specs demand it like this
            throw new Exception(EMPTY_HEAP_REMOVE_EXCEPTION_MESSAGE);
        }
        int val = root.value;
        root = merge(root.left, root.right);
        --size;
        return val;
    }

    /**
     * Tests whether there are any elements in the current heap.
     *
     * @returns true, if there are any elements and false, otherwise
     */
    public boolean empty() {
        return size == 0;
    }
    
    public int size(){
        return size;
    }
    
    /*
     * Just for testing purposes
     */
    public Node getRoot(){
        return root;
    }

    /**
     * Method that merges the current skew heap with the given by
     *
     * @other. This method destructs the
     * @other skew heap while merging it.
     *
     * @param other : reference to the skew heap data structure that will be
     * merged with the current one
     */
    public void merge(SkewHeap other) {
        if (other != null) {
            root = merge(root, other.root);
            size = size + other.size;
            other.root = null;
            other.size = 0;
        }
    }
    
    /*
     * Measures the length of the rightmost path in number of nodes.
     */
    public int getRightPathLength(){
        Node current = root;
        int length = 0;
        while(current != null){
            ++length;
            current = current.right;
        }
        
        return length;
    }
    
    public String getHeapInfoInLevelOrder(){
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append("SkewHeap with size = ");
        sb.append(size);
        sb.append(" and root = ");
        sb.append((root != null) ? root.value : "null");
        sb.append(". Right path length: ");
        int rightLegnth = getRightPathLength();
        sb.append(rightLegnth);
        double log2Size = log2(size);
        sb.append(". Log2(size): ");
        sb.append(log2Size);
        sb.append(System.lineSeparator());
        
        Queue<Node> nodes = new LinkedList<Node>();
        if(root != null){
            nodes.add(root);
        }
        while(!nodes.isEmpty()){
            Node top = nodes.poll();
            sb.append(getNodePrintableInfo(top));
            if(top.left != null){
                nodes.add(top.left);
            }
            if(top.right != null){
                nodes.add(top.right);
            }
        }
        
        return sb.toString();
    }

    /**
     * Method that merges two skew heap data structures referenced to their
     * roots by
     *
     * @root1 and
     * @root2
     *
     * @param root1: reference to the root of the first skew heap
     *
     * @param root2: reference to the root of the second skew heap
     *
     * @returns a reference to the root of the merged data structure
     */
    private Node merge(Node root1, Node root2) {
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        
        Node newRoot;
        if (root1.value < root2.value) {
            newRoot = root1;
            // using root1 as "pointer" to current node in the tree with root = root1
            root1 = root1.right;
        } else {
            newRoot = root2;
            // using root2 as "pointer" to current node in the tree with root = root2
            root2 = root2.right;
        }
        Node ptrNew = newRoot;

        while (root1 != null && root2 != null) {
            if (root1.value < root2.value) {
                ptrNew.right = root1;
                root1 = root1.right;
            } else {
                ptrNew.right = root2;
                root2 = root2.right;
            }
            swapChildren(ptrNew);
            // because we have swapped left and right children
            ptrNew = ptrNew.left;
        }

        while (root1 != null) {
            ptrNew.right = root1;
            root1 = root1.right;
            swapChildren(ptrNew);
            ptrNew = ptrNew.left;
            
        }

        while (root2 != null) {
            ptrNew.right = root2;
            root2 = root2.right;
            swapChildren(ptrNew);
            ptrNew = ptrNew.left;
            
        }

        return newRoot;
    }
    
    private void swapChildren(Node node){
        if(node != null){
            Node oldLeftChild = node.left;
            node.left = node.right;
            node.right = oldLeftChild;
        }
    }
    
    private static String getNodePrintableInfo(Node node){
        StringBuilder sb = new StringBuilder();
        if(node != null){
            sb.append("Node: value = ");
            sb.append(node.value);
            sb.append(";\t left = ");
            sb.append((node.left!= null) ? node.left.value : "null");
            sb.append(";\t right = ");
            sb.append((node.right != null) ? node.right.value : "null");
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
    
    private double log2(double x){
        return Math.log10(x)/Math.log10(2);
    }
}