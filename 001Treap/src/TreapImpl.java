/**
 * @author Spas Kyuchukov
 */

public class TreapImpl implements Treap {
    
    protected TreapNode root;
    // may overflow
    protected int size;
    
    public static class TreapNode {

        int key; // key provided by user
        float priority; // node's generated priority
        TreapNode left; // pointer for the left subtree
        TreapNode right; // pointer for the right subtree
        TreapNode parent; // pointer to the parent node

        TreapNode(int key) {
            this.key = key;
            priority = generator.nextFloat();
            left = null;
            right = null;
            this.parent = null;
        }
    };

    public TreapImpl() {
        root = null;
        size = 0;
    }
    
    @Override
    // If there is already another node with the same key the insertion is ignored
    public void insert(int key) {
        TreapNode newNode = new TreapNode(key);
        boolean inserted = insertBST(newNode);
        if(inserted){
            rotateUpHeapify(newNode);
            // size may overflow - we may throw exception here, or choose to use a longer type or an arbitary length one
            size++;
        }
    }

    @Override
    public void remove(int key) {
        TreapNode node = find(key);
        
        if(node != null){
            rotateDownToLeaf(node);
            
            if(node.parent != null){
                changeChild(node.parent, node, null);
            }
            
            if(root == node){
                root = null;
            }
            // Just to help the GC
            node.parent = null;
            
            size--;
        }
    }

    @Override
    public boolean containsKey(int key) {
        return find(key) != null;
    }
    
    public int size() {
        return this.size;
    }
    
    /*
     * @return true if the insertion is successfull and false if there is/
     * already an element with the same key and the insertion is ignored.
     */
    protected boolean insertBST(TreapNode node){
        TreapNode current = root;
        if(current == null){
            root = node;
        } else {
            while(true) {
                if(node.key == current.key){
                    return false;
                } else if(node.key < current.key){
                    if(current.left == null){
                        current.left = node;
                        node.parent = current;
                        break;
                    } else {
                        current = current.left;
                    }
                } else { // node.key > current.key
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
        
        return true;
    }
    
    // Return null if the node is not found
    protected TreapNode find(int key){
        TreapNode current = root;
        while (current != null) {
            if (current.key == key) {
                break;
            } else if (key < current.key) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return current;
    }
    
    // Rotates a newly inserted node up until the heap property is corrected
    protected void rotateUpHeapify(TreapNode node){
        TreapNode curr = node;
        
        // Stupid comparison of floats - it won't be a problem in this case
        while(curr.parent != null && curr.priority < curr.parent.priority){
            
            if(curr == curr.parent.left){
                rotateRight(curr);
            } else if(curr == curr.parent.right){
                rotateLeft(curr);
            } else {
                String errorMessage = "The child node is neither left, nor right child of it's parent!\n";
                errorMessage += "Child: key: " + curr.key + ", pri: " + curr.priority;
                errorMessage += ", parent.key: " + ((curr.parent != null) ? curr.parent.key : "null");
                errorMessage += ", left.key: " + ((curr.left != null) ? curr.left.key : "null");
                errorMessage += ", right.key: " + ((curr.right != null) ? curr.right.key : "null") + "\n";
                errorMessage += "Parent: key: " + curr.parent.key + ", pri: " + curr.parent.priority;
                errorMessage += ", parent.key: " + ((curr.parent.parent != null) ? curr.parent.parent.key : "null");
                errorMessage += ", left.key: " + ((curr.parent.left != null) ? curr.parent.left.key : "null");
                errorMessage += ", right.key: " + ((curr.parent.right != null) ? curr.parent.right.key : "null") + "\n";
                throw new RuntimeException(errorMessage);
            }
        }
    }
    
    // Rotates a node down until it is a leaf, so it can be safely removed
    protected void rotateDownToLeaf(TreapNode node){
        while(true){
            if(node.left != null && node.right != null){
                if(node.left.priority < node.right.priority){
                    rotateRight(node.left);
                } else {
                    rotateLeft(node.right);
                }
            } else if (node.left != null) {
                rotateRight(node.left);
            } else if (node.right != null) {
                rotateLeft(node.right);
            } else {
                break;
            }
        }
    }
    
    // bottom should have a parent!
    protected void rotateLeft(TreapNode bottom){
        TreapNode top = bottom.parent;
        TreapNode totalAncestor = top.parent;
        if(root == top){
            root = bottom;
        }
        
        top.parent = bottom;
        top.right = bottom.left;
        if (top.right != null){
            top.right.parent = top;
        }
        bottom.left = top;
        bottom.parent = totalAncestor;
        if(totalAncestor != null){
            changeChild(totalAncestor, top, bottom);
        }
    }
    
    // bottom should have a parent!
    protected void rotateRight(TreapNode bottom){
        TreapNode top = bottom.parent;
        TreapNode totalAncestor = top.parent;
        if(root == top){
            root = bottom;
        }
        
        top.parent = bottom;
        top.left = bottom.right;
        if (top.left != null){
            top.left.parent = top;
        }
        bottom.right = top;
        bottom.parent = totalAncestor;
        if(totalAncestor != null){
            changeChild(totalAncestor, top, bottom);
        }
    }
    
    protected void changeChild(TreapNode node, TreapNode currChild, TreapNode newChild){
        if(node.left == currChild){
            node.left = newChild;
        } else if(node.right == currChild){
            node.right = newChild;
        }
    }
}
