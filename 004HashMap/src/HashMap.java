import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Simple HashMap implementation in Java
 * @author: implementation by Spas Kyuchukov
 * @keywords: Data Structures, Map, Hashing
 * @modified:
 * 
 * Implement an unordered map with String keys.
 * Use load factor ((number of elements + 1) / number of buckets) of at least 0.25.
 *
 * (optional): Make it also work for integers (int) as keys.
 * (optional): Make it also work for arbitrary objects as keys.
 */
public class HashMap<V> { 
    
    /**
    * <key, value> tuple which presumes unique keys
    * double hashing sucked for me - too slow
    */
    private class Record{
        private final String key;
        private V value;
        private Record next;
        
        private Record(String key, V value){
            this.key = key;
            this.value = value;
            this.next = null;
        }
        
        public int getSize(){
            int size = 1;
            Record r = next;
            while(r != null){
                ++size;
                r=r.next;
            }
            return size;
        }

        @Override
        public int hashCode() {
            return hashString(key);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && this.getClass() == obj.getClass()) {
                Record otherRec = (Record) obj;
                return key.compareTo(otherRec.key) == 0;
            }
            return false;
        }
    }

    // Remnants from the double hashing - abstraction if we need another action
    private boolean equalKey(Record rec, String otherKey){
        return rec.key.compareTo(otherKey) == 0;
    }
    
    private static final int HASH_BASE = 257;
    private static final int DEFAULT_MIN_CAPACITY = 4;
    private static final int TWO_TO_THE_28 = 1<<28;
    private static final int DEFAULT_MAX_CAPACITY = TWO_TO_THE_28;
    private static final double MIN_LOAD_FACTOR = 0.25;
    // MAX_LOAD_FACTOR=0.6 is OK, but 0.75 is the dafault LF for the java.util.HashMap - better comparison
    private static final double MAX_LOAD_FACTOR = 0.75;
    private static final double RESIZE_FACTOR = 2;
    
    static {
       if (MIN_LOAD_FACTOR * RESIZE_FACTOR >= MAX_LOAD_FACTOR){
           throw new AssertionError("\nInvariant failed: 'MIN_LOAD_FACTOR * RESIZE_FACTOR < MAX_LOAD_FACTOR'"
                   + " && 'MAX_LOAD_FACTOR / RESIZE_FACTOR > MIN_LOAD_FACTOR'\n");
       }
    }
    
    private int size;
    private int capacity;
    private int minCapacity;
    private int maxCapacity;
    // ArrayList is pretty much auto growing array like std::vector from C++
    // O(1) get() and amortized O(1) set() operations (random access)
    private ArrayList<Record> data;
    
    // Just some statistics
    public int rehashTrueCount = 0;
    public int rehashAllCount = 0;
    
    /**
     * Constructs an empty HashMap.
     * Expected complexity: O(1)
     */
    HashMap() {
        this(DEFAULT_MIN_CAPACITY, DEFAULT_MAX_CAPACITY);
    }
    
    /**
     * Constructs an empty HashMap with minimum minBuckets buckets.
     * This lower bound on the number of buckets should be maintained
     * at all times!
     * Expected complexity: O(minBuckets)
     */
    HashMap(int minBuckets) {
        this(minBuckets, DEFAULT_MAX_CAPACITY);
    }

    /**
     * Constructs an empty HashMap with minimum minBuckets buckets and
     * maximum maxBuckets buckets. These lower and upper bounds on the
     * number of buckets should be maintained at all times!
     * Expected complexity: O(minBuckets)
     */
    HashMap(int minBuckets, int maxBuckets) {
        if(minBuckets < 1 || minBuckets > maxBuckets || maxBuckets > TWO_TO_THE_28){
            throw new IllegalArgumentException("\nError! Creating a HashMap,"
                    + " the following should be true: 1 <= minBuckets <= maxBuckets <= 2^28\n");
        }
        size = 0;
        minCapacity = minBuckets;
        maxCapacity = maxBuckets;
        capacity = minCapacity;
        data = new ArrayList<Record>(capacity);
        for (int bucketI = 0; bucketI < capacity; bucketI++) {
            data.add(null);
        }
    }
    
    /**
     * Resizes the HashMap so it has at least numBuckets slots (buckets).
     * Re-hashes the current values if needed.
     * Expected time complexity: O(N + numBuckets)
     */
    public void resize(int numBuckets) {
        // Otherwise we are already OK
        if(capacity != maxCapacity && numBuckets > capacity){
            int newCapacity = Math.max((int)(capacity * RESIZE_FACTOR), numBuckets);
            newCapacity = Math.min(newCapacity, maxCapacity);
            expandCapacity(newCapacity);
        }
    }
    
    /**
     * Removes all existing values from the HashMap and leaves it empty.
     * Expected complexity: O(N + B)
     */
    public void clear() {
        for (int bucketI = 0; bucketI < capacity; bucketI++) {
            data.set(bucketI, null);
        }
        size = 0;
        if(minCapacity == DEFAULT_MIN_CAPACITY){
            /* This will not preserve the minBuckets - so we do it only if user has not supplied
             * a value different than the default minBuckets one (DEFAULT_MIN_CAPACITY)
             */
            shrinkCapacity(DEFAULT_MIN_CAPACITY);
            data.trimToSize();
        }
    }
    
    /**
     * Returns the number of elements in the HashMap.
     * Expected complexity: O(1)
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns the number of allocated buckets in the HashMap.
     * Expected complexity: O(1)
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Checks if a value is already associated with a certain key.
     * Expected complexity: O(H + 1), where O(H) is needed to hash the key.
     */
    public boolean contains(String key) {
        if(key == null){
            throw new IllegalArgumentException("\nError! The key cannot be null!\n");
        }
        return getVal(key) != null;
    }
    
    /**
     * Inserts a new (key, value) pair or replaces the current value with
     * another one, if the key is already present.
     * Expected complexity: O(H + 1), where O(H) is needed to hash the key.
     */
    public void insert(String key, V value) {
        if(key == null || value == null){
            throw new IllegalArgumentException("\nError! The key and value cannot be null!\n");
        }
        
        Record newRecord = new Record(key, value);
        int bucketI = hashString(newRecord.key);
        insertInBucket(bucketI, newRecord);
    }
    
    /**
     * Removes a key and the associated with it value from the HashMap.
     * Expected complexity: O(H + 1), where O(H) is needed to hash the key.
     */
    public void erase(String key) {
        if(key == null){
            throw new IllegalArgumentException("\nError! The key cannot be null!\n");
        }
        int bucketI = hashString(key);
        removeFromBucket(bucketI, key);
    }
    
    /**
     * Returns a reference to the value, associated with a certain key.
     * If the key is not present in the HashMap, throw an error or fail
     * by assertion.
     * Expected complexity: O(H + 1), where O(H) is needed to hash the key.
     */
    public V get(String key) {
        if(key == null){
            throw new IllegalArgumentException("\nError! The key cannot be null!\n");
        }
        
        V value = getVal(key);
        if(value == null){
            throw new NoSuchElementException("\nError! There is no record in the HashMap"
                    + " associated with key = '" + key + "'!\nPlease use the contains(String key)"
                    + " method to check for existance before calling get(String key)");
        }
        return value;
    }
    
    public double loadFactor(){
        return loadFactor(capacity, size);
    }
    
    // Returns the average non-empty(!) bucket size
    public double getAverageBucketSize(){
        int numberOfNonEmptyBuckets = 0;
        for (Record r : data) {
            if(r != null){
                ++numberOfNonEmptyBuckets;
            }
        }
        return size * 1.0D / (numberOfNonEmptyBuckets != 0 ? numberOfNonEmptyBuckets : 1);
    }
    
    public int getMaxBucketSize(){
        int maxSize = 0;
        for (Record r : data) {
            int sizeB = (r==null) ? 0 : r.getSize();
            if(sizeB > maxSize){
                maxSize = sizeB;
            }
        }
        return maxSize;
    }
    
    public void printStatistics(){
        int maxBucketSize = getMaxBucketSize();
        System.out.println("Size = " + size + "; Capacity = " + capacity + "; Avg non-empty bucket size = " +
                getAverageBucketSize() + "; Max bucket size = " + maxBucketSize);
        int[] sizes = new int[maxBucketSize + 1];
        for (int i = 0; i <= maxBucketSize; i++) {
            sizes[i] = 0;
        }
        for (Record r : data) {
            int sizeB = (r==null) ? 0 : r.getSize();
            sizes[sizeB]++;
        }
        System.out.println("Sizes:");
        for (int i = 0; i <= maxBucketSize; i++) {
            if(sizes[i] > 0){
                System.out.println("    With size " + i + " : " + sizes[i]);
            }
        }
    }

    // returns the load factor calculated with size and capacity
    private double loadFactor(int capacity, int size){
        return (size + 1) * 1.0D / capacity;
    }
    
    private void rehashRecords(int oldCapacity){
        rehashAllCount+=this.size();
        
        for (int bucketI = 0; bucketI < oldCapacity; bucketI++) {
            Record bucket = data.get(bucketI);
            Record prev = null;
            Record next = null;
            while(bucket != null){
                // needed because we null-out the bucket.next in some cases
                next = bucket.next;
                int newHash = hashString(bucket.key);
                // We need to rehash - the new hash puts this record in another bucket
                if (newHash != bucketI){
                    ++rehashTrueCount;
                    // remove the current record from this bucket
                    if(prev != null){
                        prev.next = bucket.next;
                    } else {
                        data.set(bucketI, bucket.next);
                    }
                    // null-out bucket.next before insertion in another bucket
                    bucket.next = null;
                    // and insert it in it's new bucket
                    fastUnsecureInsertInBucket(newHash, bucket);
                    // when we execute 'prev = bucket' we will keep the current prev,
                    // because we need to skip the removed 'bucket'
                    bucket = prev;
                }
                prev = bucket;
                bucket = next;
            }
        }
    }
    
    private int hashString(String key){
        int h = key.hashCode();
        // from java.util.HashMap
        h ^= (h >>> 20) ^ (h >>> 12);
        h = h ^ (h >>> 7) ^ (h >>> 4);
        return Math.abs(h) % capacity;
    } 
    
    private V getFromBucket(int bucketI, String key){
        V value = null;
        Record record = data.get(bucketI);
        
        while(record != null){
            if (equalKey(record, key)){
                value = record.value;
                break;
            }
            record = record.next;
        }

        return value;
    }
    
    private V getVal(String key){
        int bucketI = hashString(key);
        V value = getFromBucket(bucketI, key);
        return value;
    }
    
    private void removeFromBucket(int bucketI, String key){
        Record record = data.get(bucketI);
        Record prev = null;
        
        while(record != null){
            if(equalKey(record, key)){
                if(prev != null){
                    prev.next = record.next;
                } else {
                    // removing the first element of the bucket
                    data.set(bucketI, record.next);
                }
                // just to help the GC
                record.next = null;
                
                decrementSizeWithOne();
                return;
            }
            
            prev = record;
            record = record.next;
        }
    }
    
    private void decrementSizeWithOne(){
        --size;
        if(loadFactor() < MIN_LOAD_FACTOR && capacity != minCapacity){
            int newCapacity = (int)(capacity / RESIZE_FACTOR);
            while(loadFactor(newCapacity, this.size) > MAX_LOAD_FACTOR){
                ++newCapacity;
            }
            if(newCapacity > capacity){
                // Let's hope this doesn't happen :D
                throw new RuntimeException("Error calculating new capacity. Try to change HashMap parameters.");
            }
            if(newCapacity < minCapacity){
                newCapacity = minCapacity;
            }
            shrinkCapacity(newCapacity);
        }
    }
    
    /**
     * 
     * @param bucketI the index of the bucket in which to insert record to.
     * @param record the new record to add.
     * @return true if the insertion replaced the value inside an existing element
     * with matching key or false if it was a clean new insertion.
     */
    private void insertInBucket(int bucketI, Record record){
        Record bucket = data.get(bucketI);
        if(bucket == null){
            data.set(bucketI, record);
            incrementSizeWithOne();
            return;
        }
        
        Record prev = null;
        while(bucket != null){
            if (bucket.equals(record)){
                /* If we already have a record with this key in the bucket 
                 * we only change it's value to the new one as per specification */
                bucket.value = record.value;
                return;
            }
            
            prev = bucket;
            bucket = bucket.next;
        }

        prev.next = record;
        incrementSizeWithOne();
    }
    
    private void incrementSizeWithOne(){
        ++size;
        if(loadFactor() > MAX_LOAD_FACTOR && capacity != maxCapacity){
            int newCapacity = (int)(capacity * RESIZE_FACTOR);
            while(loadFactor(newCapacity, this.size) < MIN_LOAD_FACTOR){
                --newCapacity;
            }
            if(newCapacity < capacity){
                // Let's hope this doesn't happen :D
                throw new RuntimeException("Error calculating new capacity. Try to change HashMap parameters.");
            }
            if(newCapacity > maxCapacity){
                newCapacity = maxCapacity;
            }
            expandCapacity(newCapacity);
        }
    }
    
    /**
     * Assumes the record is unique - there is no other record with the same key
     */
    private void fastUnsecureInsertInBucket(int bucketI, Record record){
        Record bucket = data.get(bucketI);
        
        // prepend
        if(bucket != null){
            record.next = bucket;
        }
        data.set(bucketI, record);
    }
    
    private void expandCapacity(int newCapacity){
        // Otherwise we are already OK
        if(newCapacity > capacity){
            data.ensureCapacity(newCapacity);
            int oldCapacity = capacity;
            capacity = newCapacity;
            for (int bucketI = oldCapacity; bucketI < newCapacity; bucketI++) {
                data.add(null);
            }
            rehashRecords(oldCapacity);
        }
    }
    
    private void shrinkCapacity(int newCapacity){
        // Otherwise we are already OK
        if(newCapacity < capacity){
            int oldCapacity = capacity;
            capacity = newCapacity;
            if(size > 0){
                rehashRecords(oldCapacity);
            }
            data.subList(newCapacity, oldCapacity).clear();
        }
    }
}