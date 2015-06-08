
/*
 * Persistent vector using 32-way tree and path copying
 * 
 * No tail optimisation but
 *  doesn't have a fake root node (1 level of indirection less)
 *  i.e. the tree with WIDTH elements has height of 1
 *  and an empty tree doesn't have any nodes (root==null)
 * 
 * Warning! If the objects of type T are not immutable themselves the vector can
 * not guarantee that, too!
 * 
 * Spas Kyuchukov
 * June 2015
 */

public class PersistentVector<T> {
    
    public static final int BITS = 5,
            WIDTH = 1 << BITS, // 2^5 = 32
            MASK = WIDTH - 1; // 31, or 0x1f

    private int size;
    private Object[] root;
    
    // BITS * (height of tree - 1)
    private int shift;

    public PersistentVector() {
        size = 0;
        //root = new Object[WIDTH];
        root = null;
        shift = 0; 
    }
    
    private PersistentVector(int size, Object[] root, int shift) {
        this.size = size;
        this.root = root;
        this.shift = shift;
//        this.shift = BITS * (calculateHeight(size) - 1);
//        if (this.shift < 0){
//            this.shift = 0;
//        }
    }
    
    public int size() {
        return this.size;
    }

    /**
     * Returns the value of the element at position {@code index}.
     * 
     * Warning! If the objects of type T are not immutable themselves the vector can not be too!
     */
    public T get(int index) {
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Error! Index " + index +
                "is not inside [0, " + size + ")!");
        }

        Object[] node = this.root;

        // branching
        for (int level = this.shift; level > 0; level -= BITS) {
            int currIndex = (index >>> level) & MASK;
            node = (Object[]) node[currIndex];
        }

        // index % MASK
        return (T)(node[index & MASK]);
    }

    /**
     * Returns a new vector with the element at position {@code index} replaced
     * by {@code value}.
     */
    public PersistentVector<T> update(int index, T value) {
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException("Error! Index " + index +
                "is not inside [0, " + size + ")!");
        }
        
        Object[] newRoot = new Object[WIDTH];
        Object[] currNode = newRoot;
        
        System.arraycopy(this.root, 0, newRoot, 0, WIDTH);

        // branching
        for (int level = this.shift; level > 0; level -= BITS) {
            
            int currIndex = (index >>> level) & MASK;
            
            Object[] nextNode = new Object[WIDTH];
            System.arraycopy((Object[]) currNode[currIndex], 0, nextNode, 0, WIDTH);
            
            currNode[currIndex] = nextNode;
            currNode = nextNode;
        }

        // index % WIDTH
        currNode[index & MASK] = value;
        
        return new PersistentVector<T>(this.size, newRoot, this.shift);
    }

    /**
     * Returns a new vector with {@code value} appended at the end.
     */
    public PersistentVector<T> append(T value) {
        
        int newSize = size + 1;
        Object[] newRoot = new Object[WIDTH];
        int newShift = this.shift;
        
        if (size > 0) {
            System.arraycopy(root, 0, newRoot, 0, WIDTH);
            
            // we need to add a new level to the tree
            if(isPowerOfBranchingFactor(this.size)){
                Object[] realNewRoot = new Object[WIDTH];
                realNewRoot[0] = newRoot;
                newRoot = realNewRoot;
                
                newShift += BITS;
            }
        }
        
        Object[] currNode = newRoot;
        
        // branching - index of new element is this.size
        for (int level = newShift; level > 0; level -= BITS) {
            
            int currIndex = (this.size >>> level) & MASK;
            
            Object[] nextNode = new Object[WIDTH];
            if(currNode[currIndex] != null){
                System.arraycopy((Object[]) currNode[currIndex], 0, nextNode, 0, WIDTH);
            }
            
            currNode[currIndex] = nextNode;
            currNode = nextNode;
        }

        // index % WIDTH
        currNode[this.size & MASK] = value;
        
        return new PersistentVector<T>(newSize, newRoot, newShift);
    }

    /**
     * Returns a new vector that's the same as this one but without the last
     * element.
     */
    public PersistentVector<T> pop() {
        if(this.size == 0){
            throw new IllegalStateException("Error! You cannot pop() form an already empty vector!");
        }
        
        // remove root, can be handled by the recursive algorithm (and it is)
        // but this way it is pretty straightforward
        if(size == 1){
            return new PersistentVector<T>();
        }
        
        int newSize = size - 1;

        // we need to remove a level from the tree
        if(isPowerOfBranchingFactor(newSize)){
            // only the leftmost branch of root has elements
            // (we are removing the only element in the second branch)
            Object[] newRoot = (Object[])this.root[0];
            return new PersistentVector<T>(newSize, newRoot, this.shift - BITS);
        }
        
        // index of element to remove is newSize
        Object[] newRoot = getNodeAfterPopRecursive(newSize, this.root, this.shift);
        
        return new PersistentVector<T>(newSize, newRoot, this.shift);
    }
    
    private Object[] getNodeAfterPopRecursive(int totalIndex, Object[] currNode, int currShift) {
        
        Object[] copiedNode = new Object[WIDTH];
        System.arraycopy(currNode, 0, copiedNode, 0, WIDTH);

        // we are in a leaf
        if (currShift <= 0) {
            // if we are deleting the only element
            if ((totalIndex & MASK) == 0) {
                return null;
            } else {
                copiedNode[totalIndex & MASK] = null;
                return copiedNode;
            }
        }

        int currIndex = (totalIndex >>> currShift) & MASK;
        copiedNode[currIndex] = getNodeAfterPopRecursive(totalIndex, (Object[])copiedNode[currIndex], currShift - BITS);

        // currNode should be deleted as we are deleting it's only element(link)
        if(currIndex == 0 && copiedNode[currIndex] == null){
            return null;
        } //else
        return copiedNode;
    }
    
    // check if x is non-zero power of WIDTH i.e.
    //    x = WIDTH^y for some integer y > 0
    private static boolean isPowerOfBranchingFactor(int x) {
        if (x <= 0){
            return false;
        }
        // while (x > WIDTH) x/=WIDTH;
        while(x > WIDTH){
            if((x & MASK) != 0){
                return false;
            }
            x = x >>> BITS;
        }
        // x % WIDTH
        return (x & MASK) == 0;
    }
    
    public int calculateHeight(int size) {
        // Commented part could be right, but could also not be :D
        // Better use normal functions than debugging your miserable attempts at int arithmetic 
//        int h = 0;
//        // while (size > 0) size/=WIDTH;
//        while(size > 0){
//            size = size >>> BITS;
//            ++h;
//        }
//        return h;
        if(size == 0){
            return 0;
        }
        return (int)Math.ceil(Math.log(size)/Math.log(32));
    }
    
    // We assume the leftmost path length is equal to the height of the tree
    // (i.e. we are correctly building perfect (full) binary tree).
    // should be equal to calculateHeight(this.size)
    public int getLeftPathLength() {
        int hegiht = 0;
        Object[] currNode = this.root;
        while(currNode != null){
            ++hegiht;
            if(currNode[0] instanceof Integer){
                break;
            }
            currNode = (Object[]) currNode[0];
        }
        return hegiht;
    }
}