
/**
 * @author Spas Kyuchukov
 */

import java.util.LinkedList;
import java.util.Queue;

public class SplayTree {

    private Node root;
    private int size;

    private class Node {
        Node parent;
	Node leftChild;
        Node rightChild;
	int value;

        public Node(int value){
            this.value = value;
            this.parent = null;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    public SplayTree(){
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean contains(int value) {
        Node node =  findNode(value);
        splay(node);
        return node != null && node.value == value;
    }

    public void insertNode(int value) {
        Node newNode = new Node(value);
        insertBST(newNode);
        splay(newNode);
        // size may overflow - we may throw exception here, or choose to use a longer type or an arbitary length one
        size++;
    }

    public void deleteNode(int value) {
        Node node = findNode(value);
        if(node != null){
            Node nodeParent = deleteBST(node);
            splay(nodeParent);
            size--;
        }
    }

    public String getTreeInfoInLevelOrder(){
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append("AVL tree with size = ");
        sb.append(size);
        sb.append(" and root = ");
        sb.append((root != null) ? root.value : "null");
        sb.append(System.lineSeparator());

        Queue<Node> nodes = new LinkedList<>();
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

    private void splay(Node node){
        while(root != node){
            if(isLeftChild(node)){
                if(isLeftChild(node.parent)){ // zig-zig left-left
                    rotateRight(node.parent);
                    rotateRight(node);
                } else if (isRightChild(node.parent)){ // zig-zag right-left
                    rotateRight(node);
                    rotateLeft(node);
                } else { // zig left
                    rotateRight(node);
                }
            } else if(isRightChild(node)){
                if(isRightChild(node.parent)){ // zig-zig right-right
                    rotateLeft(node.parent);
                    rotateLeft(node);
                } else if (isLeftChild(node.parent)){ // zig-zag left-right
                    rotateLeft(node);
                    rotateRight(node);
                } else { // zig right
                    rotateLeft(node);
                }
            }
        }
    }

    private Node findNode(int value) {
        Node current = root;
        while (current != null) {
            if (current.value == value) {
                break;
            } else if (current.value < value) {
                if (current.rightChild == null){
                    break;
                }
                current = current.rightChild;
            } else { // curr.val > val
                if (current.leftChild == null){
                    break;
                }
                current = current.leftChild;
            }
        }
        return current;
    }

    private void insertBST(Node node){
        Node current = root;
        if(current == null){
            root = node;
        } else {
            while(true) {
                if(node.value == current.value){
                    break;
                } else if(node.value < current.value){
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
        int val = n1.value;
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

    private static String getNodePrintableInfo(Node node){
        StringBuilder sb = new StringBuilder();
        if(node != null){
            sb.append("Node: value = ");
            sb.append(node.value);
            sb.append(";\t\t parent = ");
            sb.append((node.parent != null) ? node.parent.value : "null");
            sb.append(";\t\t leftChild = ");
            sb.append((node.leftChild != null) ? node.leftChild.value : "null");
            sb.append(";\t\t rightChild = ");
            sb.append((node.rightChild != null) ? node.rightChild.value : "null");
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static boolean isLeftChild(Node node){
        if(node != null && node.parent != null){
            return node.parent.leftChild == node;
        }
        return false;
    }

    private static boolean isRightChild(Node node){
        if(node != null && node.parent != null){
            return node.parent.rightChild == node;
        }
        return false;
    }
}
