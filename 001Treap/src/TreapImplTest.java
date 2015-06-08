/**
 * @author Spas Kyuchukov
 */
 
public class TreapImplTest extends TreapImpl {
    
    public void insert(int key, float priority) {
        TreapNode newNode = new TreapNode(key);
        newNode.priority = priority;
        insertBST(newNode);
        rotateUpHeapify(newNode);
        size++;
    }
    
    public void printTreapInOrder() {
        System.out.println("Treap with size " + size + " and root.key = " + (root!=null?root.key:"null") + ":");
        printInOrder(root);
    }
    
    protected void printInOrder(TreapNode node) {
        if(node == null){
            return;
        }
        printInOrder(node.left);   
        System.out.print("node key: " + node.key + ", pri: " + node.priority);
        System.out.print(", parent.key: " + ((node.parent != null) ? node.parent.key : "null"));
        System.out.print(", left.key: " + ((node.left != null) ? node.left.key : "null"));
        System.out.print(", right.key: " + ((node.right != null) ? node.right.key : "null"));
        System.out.println("");
        printInOrder(node.right);
    }
}
