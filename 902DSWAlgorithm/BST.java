
/**
 * @author Spas Kyuchukov
 */

public interface BST {

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

    public void insert(int key);

    public void remove(int key);

    public boolean containsKey(int key);

    public void printInOrder();

    public void balanceDSW();
}
