
/**
 * @author Spas Kyuchukov
 */

public class BSTImpl implements BST {

    private BSTNode root;
    // may overflow
    private int size;

    public static class BSTNode {

        int key; // key provided by user
        BSTNode left; // pointer for the left subtree
        BSTNode right; // pointer for the right subtree
        BSTNode parent; // pointer to the parent node

        BSTNode(int key) {
            this.key = key;
            left = null;
            right = null;
            this.parent = null;
        }
    };

    public BSTImpl() {
        root = null;
        size = 0;
    }

    @Override
    public void insert(int key) {
        BSTNode newNode = new BSTNode(key);
        insertBST(newNode);
        size++;
    }

    @Override
    public void remove(int key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsKey(int key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void printInOrder() {
        System.out.println("BST with size " + size + " and root.key = " + (root!=null?root.key:"null") + ":");
        printInOrder(root);
        System.out.println();
    }

    @Override
    public void balanceDSW(){
        DSWFlatten();

        System.out.print("FLATTENED: ");
        printInOrder();

        //size + 1 - 2 ^ floor(log2(size+1))
        int lastLevelLeavesCount = (int) (size + 1 - Math.pow(2, Math.floor(Math.log(size+1)/Math.log(2))));
        DSWCompress(root, lastLevelLeavesCount);

        System.out.print("AFTER LEAVES COMPRESS WITH " + lastLevelLeavesCount + " NODES: ");
        printInOrder();

        int otherNodesCount = size - lastLevelLeavesCount;
        while((otherNodesCount/2) > 0){
            DSWCompress(root, otherNodesCount/2);

            System.out.print("AFTER COMPRESS WITH " + otherNodesCount/2 + " NODES: ");
            printInOrder();

            otherNodesCount/=2;
        }
    }

    private void DSWFlatten()
    {
        if(root==null){
            return;
        }
        BSTNode rootNode = new BSTNode(-1);
        rootNode.right = root;
        BSTNode current = rootNode;
        root.parent = rootNode;
        while(current.right!=null){
            if(current.right.left != null){
                rotateRight(current.right.left);
            } else {
                current = current.right;
            }
        }

        root = rootNode.right;
        root.parent = null;
    }

    private void DSWCompress(BSTNode node, int count){
        BSTNode current = node;
        for (int i = 0; i < count; i++) {
            current = rotateLeft(current.right);
            current = current.right;
        }
    }

    private void printInOrder(BSTNode node) {
        if(node == null){
            return;
        }
        printInOrder(node.left);
        System.out.print("node key: " + node.key);

        System.out.print(", parent.key: " + ((node.parent != null) ? node.parent.key : "null"));
        System.out.print(", left.key: " + ((node.left != null) ? node.left.key : "null"));
        System.out.print(", right.key: " + ((node.right != null) ? node.right.key : "null"));
        System.out.println("");

        printInOrder(node.right);
    }

    private void insertBST(BSTNode node){
        BSTNode current = root;
        if(current == null){
            root = node;
        } else {
            while(true) {
                if(node.key <= current.key){
                    if(current.left == null){
                        current.left = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.left;
                    }
                } else {
                    if(current.right == null){
                        current.right = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.right;
                    }
                }
            }
        }
    }

    /* @param node; node should have a parent!
     *
     */
    private BSTNode rotateLeft(BSTNode node){
        BSTNode top = node.parent;
        BSTNode totalAncestor = top.parent;
        if(root == top){
            root = node;
        }

        top.parent = node;
        top.right = node.left;
        if (top.right != null){
            top.right.parent = top;
        }
        node.left = top;
        node.parent = totalAncestor;
        if(totalAncestor != null){
            changeChild(totalAncestor, top, node);
        }

        return node;
    }

    /* @param node; node should have a parent!
     *
     */
    private BSTNode rotateRight(BSTNode node){
        BSTNode top = node.parent;
        BSTNode totalAncestor = top.parent;
        if(root == top){
            root = node;
        }

        top.parent = node;
        top.left = node.right;
        if (top.left != null){
            top.left.parent = top;
        }
        node.right = top;
        node.parent = totalAncestor;
        if(totalAncestor != null){
            changeChild(totalAncestor, top, node);
        }

        return node;
    }

    private void changeChild(BSTNode node, BSTNode currChild, BSTNode newChild){
        if(node.left == currChild){
            node.left = newChild;
        } else if(node.right == currChild){
            node.right = newChild;
        }
    }
}
