import static AuxiliaryFunctions.get_time;
import static AuxiliaryFunctions.print;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Augments the ordinary AVL tree with method that will test its operations.
 *
 * @author Boris Strandjev
 *
 * @param <T> The type of values that will be stored in the tree
 */
public class TestAvlTree<T extends Comparable<T>> extends AVLTree<T> {
    public static final boolean MEASURE_TIME = false;

    private double avlPropertyTime, nodesLinksTime, hightsCorrectnessTime, correctValuesTime;

    public TestAvlTree() {
        avlPropertyTime = nodesLinksTime = hightsCorrectnessTime = correctValuesTime = 0;
    }

    public void printTimes() {
        print("\nTime taken in avl property check: %.5f\n", avlPropertyTime);
        print("Time taken in node links check:  %.5f\n", nodesLinksTime);
        print("Time taken in hights correctness check:  %.5f\n", hightsCorrectnessTime);
        print("Time taken in correct values check:  %.5f\n", correctValuesTime);
    }

    // assertion methods
    private boolean isAVLPropertyHeld() {
        return isAVLPropertyHeldRec(root);
    }

    private boolean isAVLPropertyHeldRec(Node<T> node) {
        if (node == null)
            return true;
        if (Math.abs(getHeight(node.leftChild) - getHeight(node.rightChild)) > 1) {
            print("\nA node has its children with height of difference more than one\n");
            return false;
        }
        if (node.leftChild != null && node.leftChild.value.compareTo(node.value) > 0) {
            print("\nA node has smaller value than its left child\n");
            return false;
        }
        if (node.rightChild != null && node.rightChild.value.compareTo(node.value) < 0) {
            print("\nA node has greater value than its right child");
            return false;
        }
        return isAVLPropertyHeldRec(node.leftChild) && isAVLPropertyHeldRec(node.rightChild);
    }

    private int visitedNodes;

    private boolean areAllNodesLinkedCorrectly() {
        visitedNodes = 0;
        if (!areAllNodesLinkedCorrectlyRec(null, root))
            return false;

        if (visitedNodes != size) {
            print("\nThe number of linked nodes does not correspond the tree size\n");
            return false;
        }
        return true;
    }

    private boolean areAllNodesLinkedCorrectlyRec(Node<T> parent, Node<T> node) {
        if (node == null)
            return true;
        visitedNodes++;
        if (node.parent != parent) {
            print("\nA node has inaccurate parent\n");
            return false;
        }
        if (!areAllNodesLinkedCorrectlyRec(node, node.leftChild)) {
            return false;
        }
        return areAllNodesLinkedCorrectlyRec(node, node.rightChild);
    }

    private boolean areAllHeightsAccurate() {
        return areAllHeightsAccurateRec(root);
    }

    private boolean areAllHeightsAccurateRec(Node<T> node) {
        if (node == null)
            return true;
        if (1 + Math.max(getHeight(node.rightChild), getHeight(node.leftChild)) != node.height) {
            print("\nFound node with inaccurate height property\n");
            return false;
        }
        return areAllHeightsAccurateRec(node.leftChild) && areAllHeightsAccurateRec(node.rightChild);
    }

    //private boolean hasTreeCorrectValues(List<T> values) {
    private boolean hasTreeCorrectValues(TreeMap<T, T> values) {
        List<T> treeValues = new ArrayList<T>();
        getValuesRec(root, treeValues);
        if (values.size() != treeValues.size()) {
            print("\nThe values in the tree are not as expected in number\n");
            return false;
        }
        Collections.sort(treeValues);
        List<T> valuesList = new ArrayList<>(); 
        for (T I: values.values()) {
           valuesList.add(I);
        }
        Collections.sort(valuesList);
        if (!treeValues.equals(valuesList)) {
            print("\nThe vlaues in the tree are not as expected\n");
            return false;
        }
        return true;
    }

    private void getValuesRec(Node<T> node, List<T> values) {

        if (node != null) {
            values.add(node.value);
            getValuesRec(node.leftChild, values);
            getValuesRec(node.rightChild, values);
        }
    }
    
	private int getHeight(Node<T> node) {
		if (node == null) {
			return 0;
		} else {
			return node.height;
		}
	}

    //public boolean checkTree(List<T> values) {
      public boolean checkTree(TreeMap<T, T> values) {
        if (values.size() != size) {
            print("\nThe size of the tree is not as expected\n");
            return false;
        }
        boolean treeCorrect = true;

        double begin, end;
        if (MEASURE_TIME) {
            begin = get_time();
        }

        treeCorrect = isAVLPropertyHeld();

        if (MEASURE_TIME) {
            end = get_time();
            avlPropertyTime += end - begin;
            begin = end;
        }

        if (!treeCorrect)
            return false;
        treeCorrect = areAllNodesLinkedCorrectly();

        if (MEASURE_TIME) {
            end = get_time();
            nodesLinksTime += end - begin;
            begin = end;
        }

        if (!treeCorrect)
            return false;
        treeCorrect = areAllHeightsAccurate();

        if (MEASURE_TIME) {
            end = get_time();
            hightsCorrectnessTime += end - begin;
            begin = end;
        }

        if (!treeCorrect)
            return false;
        treeCorrect = hasTreeCorrectValues(values);

        if (MEASURE_TIME) {
            end = get_time();
            correctValuesTime += end - begin;
        }

        if (!treeCorrect)
            return false;
        return true;
    }

}
