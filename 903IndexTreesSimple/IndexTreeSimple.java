
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: Spas Kyuchukov
 *
 * Also solution to problem 1028. Stars of acm.timus.ru judge system
 * http://acm.timus.ru/problem.aspx?space=1&num=1028
 */

public class IndexTreeSimple {

    private int size;
    private int halfSize;
    private int[] tree;

    // all 0's, size up to 2^30
    public IndexTreeSimple(int size) {
        this.size = getPowerOfTwoNotLessThan(2 * size);
        tree = new int[this.size];
        for (int i = 0; i < size; i++) {
            tree[i] = 0;
        }
        buildIndexTree();
        this.halfSize = this.size / 2;
    }

    // size up to 2^30
    public IndexTreeSimple(int[] array) {
        this.size = getPowerOfTwoNotLessThan(2 * array.length);
        tree = new int[this.size];
        copyArray(array);
        buildIndexTree();
        this.halfSize = this.size / 2;
    }

    public void update(int index, int diff) {
        index += halfSize;
        while (index > 0) {
            tree[index] += diff;
            index = index >>> 1;
            //index /= 2;
        }
    }

    public int query(int index1, int index2) {
        index1 += halfSize;
        index2 += halfSize;
        if (index1 == index2) {
            return tree[index1];
        }

        int ret = 0;
        ret += tree[index1];
        //boolean flag1 = ((index1 % 2) == 0);
        boolean flag1 = ((index1 & 1) == 0);
        //index1 /= 2;
        index1 = index1 >>> 1;
        ret += tree[index2];
        boolean flag2 = ((index2 & 1) == 1);
        //boolean flag2 = ((index2 % 2) != 0);
        //index2 /= 2;
        index2 = index2 >>> 1;

        while (index1 != index2) {
            if (flag1) {
                ret += tree[(index1 * 2) + 1];
            }
            if (flag2) {
                ret += tree[(index2 * 2)];
            }
            //flag1 = ((index1 % 2) == 0);
            flag1 = ((index1 & 1) == 0);
            //index1 /= 2;
            index1 = index1 >>> 1;
            //flag2 = ((index2 % 2) != 0);
            flag2 = ((index2 & 1) == 1);
            //index2 /= 2;
            index2 = index2 >>> 1;
        }

        return ret;
    }

    private void copyArray(int[] array) {
        for (int i = array.length; i < 2 * array.length; i++) {
            tree[i] = array[i - array.length];
        }
        // padding 0's
        for (int i = 0; i < array.length; i++) {
            tree[i] = 0;
        }
    }

    private void buildIndexTree() {
        for (int i = size - 1; i > 2; i -= 2) {
            tree[i / 2] = tree[i] + tree[i - 1];
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
//        IndexTreeSimple tree = new IndexTreeSimple(32001);
//        int[] levels = new int[numberOfPoints];
//        for (int i = 0; i < numberOfPoints; i++) {
//            levels[i] = 0;
//        }
//        
//        for (int i = 0; i < numberOfPoints; i++) {
//            line = bi.readLine();
//            line = line.substring(0, line.indexOf(' '));
//            int x = Integer.parseInt(line);
//            int currLevel = tree.query(0, x);
//            ++levels[currLevel];
//            tree.update(x, 1);
//        }
//
//        for (int i = 0; i < numberOfPoints; i++) {
//            System.out.println(levels[i]);
//        }
//    }

// This is solution to http://acm.timus.ru/problem.aspx?space=1&num=1028
public static void main(String[] args) {
        int size = 0;
        Scanner in = new Scanner(System.in);
        size = in.nextInt();
        
        IndexTreeSimple tree = new IndexTreeSimple(size);
        int[] sizes = new int[size];
        for (int i = 0; i < size; i++) {
            sizes[i] = 0;
        }
        
        for (int i = 0; i < size; i++) {
            int x = in.nextInt();
            int y = in.nextInt();
            int currSize = tree.query(0, x);
            ++sizes[currSize];
            tree.update(x, 1);
        }
        
        for (int i = 0; i < size; i++) {
            System.out.println(sizes[i]);
        }
    }
}
