
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: Spas Kyuchukov
 */

public class IndexTreeSimpleReversed {

    private int[] tree;

    // all 0's, size up to 2^30
    public IndexTreeSimpleReversed (int size) {
        size = getPowerOfTwoNotLessThan(2 * size);
        initializeArray(size);
    }

    // size up to 2^30
    public IndexTreeSimpleReversed (int[] array) {
        this(2 * array.length);
        // NOT TESTED!
        System.arraycopy(array, 0, tree, array.length, array.length);
    }

    public int query(int index) {
        // index should be within the original indices
        index += tree.length / 2;
        int value = 0;
        while (index > 0) {
            value += tree[index];
            index /= 2;
        }
        return value;
    }

    public void update(int index1, int index2, int diff) {
        index1 += tree.length / 2;
        index2 += tree.length / 2;
        if (index1 == index2) {
            tree[index1] += diff;
            return;
        }

        tree[index1] += diff;
        boolean flag1 = ((index1 % 2) == 0);
        index1 /= 2;

        tree[index2] += diff;
        boolean flag2 = ((index2 % 2) != 0);
        index2 /= 2;

        while (index1 != index2) {
            if (flag1) {
                tree[(index1 * 2) + 1] += diff;
            }
            if (flag2) {
                tree[(index2 * 2)] += diff;
            }
            flag1 = ((index1 % 2) == 0);
            index1 /= 2;
            flag2 = ((index2 % 2) != 0);
            index2 /= 2;
        }
    }

//    private void copyArray(int[] array) {
//        System.arraycopy(array, 0, tree, array.length, array.length);
//        for (int i = array.length; i < 2 * array.length; i++) {
//            tree[i] = array[i - array.length];
//        }
//    }

    private void initializeArray(int size) {
        tree = new int[size];
        for (int i = 0; i < tree.length; i++) {
            tree[i] = 0;
        }
    }

    private static int getPowerOfTwoNotLessThan(int floor) {
        if (floor > Math.pow(2, 31)) {
            throw new IllegalArgumentException("\nError! floor should be no more than 2^31\n");
        }
        int candidate = 1;
        while (candidate < floor) {
            candidate = (candidate << 1);
        }
        return candidate;
    }

//    public static void main(String[] args) throws IOException {
//        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
//        String line = bi.readLine();
//        
//        int numberOfPoints = Integer.parseInt(line);
//        
//        IndexTreeSimpleReversed  tree = new IndexTreeSimpleReversed (numberOfPoints);
//        
//        // the number of updates is numberOfPoints + 1
//        for (int i = 0; i < numberOfPoints + 1; i++) {
//            line = bi.readLine();
//            int firstSpaceI = line.indexOf(' ');
//            int from = Integer.parseInt(line.substring(0, firstSpaceI));
//            int secondSpaceI = line.indexOf(' ', firstSpaceI + 1);
//            int to = Integer.parseInt(line.substring(firstSpaceI + 1, secondSpaceI));
//            int add = Integer.parseInt(line.substring(secondSpaceI + 1, line.length()));
//            tree.update(from - 1, to - 1, add);
//        }
//
//        for (int i = 0; i < numberOfPoints; i++) {
//            System.out.println(tree.query(i));
//        }
//    }
}
