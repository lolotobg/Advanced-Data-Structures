
/**
 * @author Spas Kyuchukov
 */

import java.util.LinkedList;
import java.util.Queue;

/**
 * @param <T> The type of the values to be stored in the tree.
 */
public class AVLTree<T extends Comparable<T>> extends AVLTreeInterface<T> {

    public static final String NAME = "Spas Kyuchukov";
    public static final String MOODLE_NAME = "XXX";
    public static final String FACULTY_NUMBER = "ZZZ";

    // public methods
    public AVLTree() {
        root = null;
        size = 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Node<T> findNode(T value) {
        Node current = root;
        while (current != null) {
            if (current.value.compareTo(value) == 0) { // curr.val == val
                break;
            } else if (current.value.compareTo(value) < 0) { // curr.val < val
                current = current.rightChild;
            } else { // curr.val > val
                current = current.leftChild;
            }
        }
        return current;
    }

    @Override
    /*
     * Inserts a new element in the AVL tree with the given value.
     * If there are already other elements with this value the new element is
     * always inserted in their left subtrees.
     * The whole multiset requirement is just for testing with random data, 
     * because it can have repeating values and besides that it is nonsense when
     * we don't have separate key and value fields.
     * We could have added a counter in each node instead of adding new
     * node for repeating elements but this would have meant a lot of wasted memory.
     */
    public void insertNode(T value) {
        Node newNode = constructNode(value);
        insertBST(newNode);
        balanceUp(newNode);
        // size may overflow - we may throw exception here, or choose to use a longer type or an arbitary length one
        size++;
    }

    @Override
    public void deleteNode(T value) {
        Node node = findNode(value);
        if(node != null){
            Node forBalance = deleteBST(node);
            balanceUp(forBalance);
            size--;
        }
    }
    
    public String getAVLInfoInLevelOrder(){
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append("AVL tree with size = ");
        sb.append(size);
        sb.append(" and root = ");
        sb.append((root != null) ? root.value : "null");
        sb.append(System.lineSeparator());
        
        Queue<Node> nodes = new LinkedList<Node>();
        if(root != null){
            nodes.add(root);
        }
        while(!nodes.isEmpty()){
            Node top = nodes.poll();
            sb.append(getNodePrintableInfo(top));
            if(top.leftChild != null){
                nodes.add(top.leftChild);
            }
            if(top.rightChild != null){
                nodes.add(top.rightChild);
            }
        }
        
        return sb.toString();
    }
    
    /*
     * Just for testing purposes
     */
    public void updateTreeHeight(){
        if(root != null){
            updateTreeHeightRecursive(root);
        }
    }
    
    private void updateTreeHeightRecursive(Node node){
        if(node.leftChild != null){
            updateTreeHeightRecursive(node.leftChild);
        }
        if(node.rightChild != null){
            updateTreeHeightRecursive(node.rightChild);
        }
        updateNodeHeight(node);
    }
    
    private void balanceUp(Node node){
        Node current = node;
        while(current != null){
            updateNodeHeight(current);
            // unbalanced, node was added to the left subtree or removed from the right subtree
            if (getBalanceFactor(current) == 2){
                // left-right case
                if(getBalanceFactor(current.leftChild) == -1){
                    rotateLeft(current.leftChild.rightChild);
                    updateNodeHeight(current.leftChild.leftChild);
                    updateNodeHeight(current.leftChild);
                }
                // left-left case
                rotateRight(current.leftChild);
                updateNodeHeight(current.rightChild);
                updateNodeHeight(current);
            // unbalanced, node was added to the right subtree or removed from the left subtree    
            } else if (getBalanceFactor(current) == -2){
                // right-left case
                if(getBalanceFactor(current.rightChild) == 1){
                    rotateRight(current.rightChild.leftChild);
                    updateNodeHeight(current.rightChild.rightChild);
                    updateNodeHeight(current.rightChild);
                }
                // right-right case
                rotateLeft(current.rightChild);
                updateNodeHeight(current.leftChild);
                updateNodeHeight(current);
            // balanced so far - no problems up to current
            } else {
                current = current.parent;
            }
        }
    }
    
    private void insertBST(Node node){
        Node current = root;
        if(current == null){
            root = node;
        } else {
            while(true) {
                if(node.value.compareTo(current.value) <= 0){
                    if(current.leftChild == null){
                        current.leftChild = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.leftChild;
                    }
                } else {
                    if(current.rightChild == null){
                        current.rightChild = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.rightChild;
                    }
                }
            }
        }
    }
    
    private Node deleteBST(Node node){
        if(node.leftChild != null && node.rightChild != null){ // node has both subtrees
            Node prev = getInOrderPredecessor(node);
            swapNodeValues(node, prev);
            Node prevParent = prev.parent;
            deleteBST(prev);
            return prevParent;
        } else if(node.leftChild != null){ // node has only left subtree
            if(node.parent != null){
                changeChild(node.parent, node, node.leftChild);
            } else {
                root = node.leftChild;
            }
            node.leftChild.parent = node.parent;
            // just to help the GC
            node.leftChild = null;
        } else if(node.rightChild != null){ // node has only right subtree
            if(node.parent != null){
                changeChild(node.parent, node, node.rightChild);
            } else {
                root = node.rightChild;
            }
            node.rightChild.parent = node.parent;
            // just to help the GC
            node.rightChild = null;
        } else { // node is a leaf
            if(node.parent != null){
                changeChild(node.parent, node, null);
            } else {
                root = null;
            }
        }
        Node nodeParent = node.parent;
        // just to help the GC
        node.parent = null;
        return nodeParent;
    }
    
    /*
     * @param node should have a leftChild (!=null)
     */
    private Node getInOrderPredecessor(Node node){
        Node current = node;
        current = current.leftChild;
        while(current.rightChild != null){
            current = current.rightChild;
        }
        return current;
    }
    
    private static void swapNodeValues(Node n1, Node n2){
        Comparable val = n1.value;
        n1.value = n2.value;
        n2.value = val;
    }
    
    /*
     * @param bottom should have a parent!
     */
    private void rotateLeft(Node bottom){
        Node top = bottom.parent;
        Node totalAncestor = top.parent;
        if(root == top){
            root = bottom;
        }
        
        top.parent = bottom;
        top.rightChild = bottom.leftChild;
        if (top.rightChild != null){
            top.rightChild.parent = top;
        }
        bottom.leftChild = top;
        bottom.parent = totalAncestor;
        if(totalAncestor != null){
            changeChild(totalAncestor, top, bottom);
        }
    }
    
    /*
     * @param bottom should have a parent!
     */
    private void rotateRight(Node bottom){
        Node top = bottom.parent;
        Node totalAncestor = top.parent;
        if(root == top){
            root = bottom;
        }
        
        top.parent = bottom;
        top.leftChild = bottom.rightChild;
        if (top.leftChild != null){
            top.leftChild.parent = top;
        }
        bottom.rightChild = top;
        bottom.parent = totalAncestor;
        if(totalAncestor != null){
            changeChild(totalAncestor, top, bottom);
        }
    }
    
    private static void changeChild(Node node, Node currChild, Node newChild){
        if(node.leftChild == currChild){
            node.leftChild = newChild;
        } else if(node.rightChild == currChild){
            node.rightChild = newChild;
        }
    }
    
    // Because, you know, Node does not have a constructor
    private Node constructNode(T value){
        Node newNode = new Node();
        newNode.value = value;
        newNode.height = 0;
        newNode.leftChild = null;
        newNode.rightChild = null;
        newNode.parent = null;
        return newNode;
    }
    
    // The height of the left subtree - the height of the right subtree
    private static int getBalanceFactor(Node node){
        return getHeight(node.leftChild) - getHeight(node.rightChild);
    }

    private static void updateNodeHeight(Node node){
        if(node != null){
            int leftHeight = getHeight(node.leftChild);
            int rightHeight = getHeight(node.rightChild);
            int bigger = (leftHeight > rightHeight) ? leftHeight : rightHeight;
            node.height = bigger + 1;
        }
    }
    
    private static int getHeight(Node node){
        return (node != null) ? node.height : 0;
    }
    
    private static String getNodePrintableInfo(Node node){
        StringBuilder sb = new StringBuilder();
        if(node != null){
            sb.append("Node: value = ");
            sb.append(node.value);
            sb.append(";\t\t height = ");
            sb.append(node.height);
            sb.append(";\t parent = ");
            sb.append((node.parent != null) ? node.parent.value : "null");
            sb.append(";\t\t leftChild = ");
            sb.append((node.leftChild != null) ? node.leftChild.value : "null");
            sb.append(";\t\t rightChild = ");
            sb.append((node.rightChild != null) ? node.rightChild.value : "null");
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}