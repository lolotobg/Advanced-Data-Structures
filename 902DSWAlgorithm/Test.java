
/**
 * @author Spas Kyuchukov
 */

public class Test {

    public static void main(String[] args) {
        BST tree = new BSTImpl();

        tree.printInOrder();
        tree.balanceDSW();
        tree.printInOrder();
        /*tree.insert(7);
        tree.insert(6);
        tree.insert(5);
        tree.insert(11);
        tree.insert(9);
        tree.insert(10);
        tree.insert(4);
        tree.insert(3);
        tree.insert(8);
        tree.insert(2);
        tree.insert(12);
        tree.insert(1);
        tree.printInOrder();
        tree.balanceDSW();
        tree.printInOrder();
        */
    }
}
