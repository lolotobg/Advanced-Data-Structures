/**
 * Interval Tree implementation in Java
 * @author: implementation by Spas Kyuchukov
 * @keywords: Data Structures, Dynamic RMQ, Interval update and query
 * 
 * Implement a dynamic RMQ with interval query and update. The update should
 * add a certain value to all cells in a given interval, while the query should
 * return the maximum value inside an interval.
 * 
 * All directions in the trees are according to presentation with the root at the bottom
 * and leaves at the top. So every node except the leaves have a left and a right parent.
 */

public class IntervalTree {
    
    private int[] indexTree;
    private int[] RMQTree;
    private int origSize;
    
    /**
     * Creates a new interval tree with initial values given in values.
     * Each update and query should allow indices [0, values.size() - 1].
     */
    public IntervalTree(int[] values) {
        int internalSize = getPowerOfTwoNotLessThan(2 * values.length);
        this.origSize = values.length;
        initializeArrays(internalSize, values.length);
        System.arraycopy(values, 0, indexTree, indexTree.length / 2, values.length);
        System.arraycopy(values, 0, RMQTree, RMQTree.length / 2, values.length);
        buildRMQTree();
    }
    
    // returns the real number of elements that are kept in the tree (the size of the original array)
    public int size(){
        return this.origSize;
    }

    /**
     * Adds the value 'add' to each element in the interval [left, right].
     */
    public void update(int left, int right, int add) {
        if(left < 0 || left > right || right >= origSize){
            throw new IllegalArgumentException("Error! When calling update() the following should be true:\n"
                    + " 0 <= left <= right < size()");
        }
        
        left += indexTree.length / 2;
        right += indexTree.length / 2;
        if (left == right) {
            indexTree[left] += add;
            RMQTree[left] += add;
            // Update up to the bottom, leftI==rightI
            propagateUpdateRMQ(left);
            return;
        }

        indexTree[left] += add;
        RMQTree[left] += add;
        boolean leftCameFromLeft = ((left % 2) == 0);
        left /= 2;

        indexTree[right] += add;
        RMQTree[right] += add;
        boolean rightCameFromRight = ((right % 2) != 0);
        right /= 2;

        while (left != right) {
            int leftParentOfLeft = (left * 2) + 1;
            int rightParentOfLeft = left * 2;
            if (leftCameFromLeft) {
                indexTree[leftParentOfLeft] += add;
                // If it is leaf (at the top) we also update RMQTree
                if(leftParentOfLeft >= indexTree.length / 2){
                    RMQTree[leftParentOfLeft] += add;
                }
                updateRMQ(rightParentOfLeft);
            }
            updateRMQ(leftParentOfLeft);
            
            int leftParentOfRight = (right * 2) + 1;
            int rightParentOfRight = right * 2;
            if (rightCameFromRight) {
                indexTree[rightParentOfRight] += add;
                // If it is leaf (at the top) we also update RMQTree
                if(rightParentOfRight >= indexTree.length / 2){
                    RMQTree[rightParentOfRight] += add;
                }
                updateRMQ(leftParentOfRight);
            }
            updateRMQ(rightParentOfRight);
            
            leftCameFromLeft = ((left % 2) == 0);
            left /= 2;
            rightCameFromRight = ((right % 2) != 0);
            right /= 2;
        }
        
        // Update the last different parents of left and right
        // rightParentOfRight
        updateRMQ((right * 2));
        // leftParentOfLeft
        updateRMQ(((left * 2) + 1));
        // Update up to the bottom, left==right
        propagateUpdateRMQ(left);
    }

    private void updateRMQ(int indexInternal) {
        if (indexInternal < indexTree.length / 2) {
            RMQTree[indexInternal] = Math.max(RMQTree[indexInternal * 2],RMQTree[(indexInternal * 2) + 1]) 
                    + indexTree[indexInternal];
        }
    }
    
    private void propagateUpdateRMQ(int indexInternal) {
        // If called with a leaf indexInternal
        if (indexInternal >= indexTree.length / 2) {
            indexInternal /= 2;
        }
        while (indexInternal > 0) {
            // Max from the 2 parents + update in the current node in indexTree
            RMQTree[indexInternal] = Math.max(RMQTree[indexInternal * 2], RMQTree[(indexInternal * 2) + 1])
                    + indexTree[indexInternal];
            indexInternal /= 2;
        }
    }
    
    /**
     * @return The maximum value in the interval [left, right].
     */
    public int query(int left, int right) {
        if(left < 0 || left > right || right >= origSize){
            throw new IllegalArgumentException("Error! When calling query() the following should be true:\n"
                    + " 0 <= left <= right < size()");
        }
        
        left += RMQTree.length / 2;
        right += RMQTree.length / 2;
        int maxVal;
        
        if (left == right) {
            maxVal = RMQTree[left];
            left /= 2;
            while (left > 0) {
                maxVal += indexTree[left];
                left /= 2;
            }
            return maxVal;
        }
        
        int leftMax = Integer.MIN_VALUE;
        int rightMax = Integer.MIN_VALUE;
        
        leftMax = Math.max(leftMax, RMQTree[left]);
        boolean leftCameFromLeft = ((left % 2) == 0);
        left /= 2;
        
        rightMax = Math.max(rightMax, RMQTree[right]);
        boolean rightCameFromRight = ((right % 2) != 0);
        right /= 2;


        while (left != right) {
            if (leftCameFromLeft) {
                // Max of current and the left parent of 'left'
                leftMax = Math.max(leftMax, RMQTree[(left * 2) + 1]);
            }
            if (rightCameFromRight) {
                // Max of current and the righ parent of 'righ'
                rightMax = Math.max(rightMax, RMQTree[right * 2]);
            }
            leftMax += indexTree[left];
            rightMax += indexTree[right];
            leftCameFromLeft = ((left % 2) == 0);
            left /= 2;
            rightCameFromRight = ((right % 2) != 0);
            right /= 2;
        }
        
        maxVal = Math.max(leftMax, rightMax);
        
        // left == right
        while(left > 0){
            // If there are more updates that are written down in the indexTree
            maxVal += indexTree[left];
            left /= 2;
        }

        return maxVal;
    }

    private void initializeArrays(int internalSize, int userDataSize) {
        indexTree = new int[internalSize];
        RMQTree = new int[internalSize];
        int midI = internalSize / 2 + userDataSize;
        for (int i = 0; i < midI; i++) {
            // 0's so the changes (updates) are initialised with 0
            indexTree[i] = 0;
            // way better than -2100000000 :D
            RMQTree[i] = Integer.MIN_VALUE;
        }
        for (int i = midI; i < internalSize; i++) {
            // Integer.MIN_VALUE in indexTree for padding so as to not mess up with the max value of the last elements
            // Just a precaution, it should not really matter if the rest of the code is correct
            indexTree[i] = Integer.MIN_VALUE;
            RMQTree[i] = Integer.MIN_VALUE;
        }
    }
    
    private void buildRMQTree() { 
        int beginI = RMQTree.length / 2 - 1;
        for (int i = beginI; i >= 0; --i) {
            RMQTree[i] = Math.max(RMQTree[2*i], RMQTree[2*i+1]);
        }
    }

    // A helper function to get the closest power of two that is >= floor
    private static int getPowerOfTwoNotLessThan(int floor) {
        // check if floor is > (2 to the 30-th)
        if (floor > (1 << 30)) {
            throw new IllegalArgumentException("\nError! floor should be no more than 2^30\n");
        }
        int candidate = 1;
        while (candidate < floor) {
            candidate = (candidate << 1);
        }
        return candidate;
    }
};