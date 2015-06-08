import java.util.Random;

public class IntervalTreeTester {
    final static int MAX_IDX = 100_000; // 100_000
    final static int NUM_QUERIES = 1_000_000; // 1_000_000

    final static int QUERY = 0;
    final static int UPDATE = 1;

    static int n;
    static int[] init; // Initial values
    static Query a[]; // Save queries
    static int ans1[]; // Save answers
    static int ans2[];

    static void solveTrees(IntervalTree tree) {
        //IntervalTree tree = new IntervalTree(init);
        //int countQueries = 0;
        for (int i = 0; i < n; i++) {
            if (a[i].type == QUERY){
                ans1[i] = tree.query(a[i].x1, a[i].x2);
//                if (ans1[i] != ans2[i]) {
//                    System.out.println("BAD answer on " + countQueries + "-th query with " + (i - countQueries) +
//                            " updates before that. Command was: type = " +a[i].type + ", startI = " + a[i].x1 +
//                            ", endI = " + a[i].x2);
//                    System.out.println("Expected answer was = " + ans2[i] + ", but got = " + ans1[i]);
//                    tree.printMaxesOfInternal(a[i].x1, a[i].x2);
//                    printMaxInit(a[i].x1, a[i].x2);
//                    return;
//                }
                //++countQueries;
            }
            else {
                ans1[i] = -1;
                tree.update(a[i].x1, a[i].x2, a[i].val);
            }
        }
    }

    static int dummy[];
    static void dummyUpdate(int x1, int x2, int val) {
        for (int x = x1; x <= x2; x++)
            dummy[x] += val;
    }

    static int dummyQuery(int x1, int x2) {
        int ret = -2100000000;
        for (int x = x1; x <= x2; x++)
            ret = Math.max(ret, dummy[x]);
        return ret;
    }

    static void solveDummy() {
        dummy = new int[MAX_IDX];
        for (int i = 0; i < MAX_IDX; i++)
            dummy[i] = init[i];
        for (int i = 0; i < n; i++) {
            if (a[i].type == QUERY)
                ans2[i] = dummyQuery(a[i].x1, a[i].x2);
            else {
                ans2[i] = -1;
                dummyUpdate(a[i].x1, a[i].x2, a[i].val);
            }
        }
    }

    public static void main(String[] args) {
        init = new int[MAX_IDX];
        a = new Query[NUM_QUERIES];
        ans1 = new int[NUM_QUERIES];
        ans2 = new int[NUM_QUERIES];

        Random rand = new Random();
        
        for (int i = 0; i < MAX_IDX; i++)
            init[i] = rand.nextInt(2000001) - 1000000;
            //init[i] = rand.nextInt(41) - 20;
        
        n = NUM_QUERIES; // Set number of queries
        for (int i = 0; i < n; i++) {
            a[i] = new Query();
            int idx1 = rand.nextInt(MAX_IDX);
            int idx2 = rand.nextInt(MAX_IDX);
            a[i].x1 = Math.min(idx1, idx2);
            a[i].x2 = Math.max(idx1, idx2);
            
            //a[i].val = rand.nextInt(11) - 5;
            a[i].val = rand.nextInt(20001) - 10000;
            //a[i].type = QUERY;
            a[i].type = rand.nextBoolean() ? QUERY : UPDATE;
        }
        
        
        
        long startTime;
        
        // Check by dummy
        startTime = System.currentTimeMillis();
        solveDummy();
        System.out.format("Dummy solution execution time: %.3fs\n",
            (System.currentTimeMillis() - startTime) / 1000.0);
        
        // Solve with interval trees
        startTime = System.currentTimeMillis();
        // extracted out to use printInternals
        IntervalTree tree = new IntervalTree(init);
        solveTrees(tree);
        System.out.format("Interval trees execution time: %.3fs\n",
            (System.currentTimeMillis() - startTime) / 1000.0);

        boolean correct = true;
        for (int i = 0; i < n; i++) {
            if (a[i].type == QUERY) {
                if (ans1[i] != ans2[i]) {
//                    System.out.println("BAD answer on " + i + "-th query! Command was: type = " + a[i].type +
//                            ", startI = " + a[i].x1 + ", endI = " + a[i].x2);
//                    System.out.println("Expected answer was = " + ans2[i] + ", but got = " + ans1[i]);
//                    tree.printMaxesOfInternal(a[i].x1, a[i].x2);
                    correct = false;
                    break;
                }
            }
        }
        System.out.format("Solution is %s.\n", correct ? "correct" : "incorect");
    }
    
    public static void printMaxInit(int startI, int endI){
        int max = Integer.MIN_VALUE;
        
        for (int i = startI; i <= endI; i++) {
            if(init[i] > max){
                max = init[i];
            }
        }
        
        System.out.println("maxInit = " + max);
    }
}

