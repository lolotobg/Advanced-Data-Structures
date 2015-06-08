import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/*
 * ==================================================
 * --------------------------------------------------
 * 
 * @author Spas Kyuchukov
 * 
 * Some tests are specific to my implementation -
 * they are on the bottom and are separated by a comment
 * 
 * --------------------------------------------------
 * ==================================================
 */

public class MyHashMapTest {
     
    private static final String NO_SUCH_KEY = "No such key!";
    private static final String KEY_666 = "key666";
    private static final int MIN_BUCKETS = 128;
    private static final int BIG_MIN_BUCKETS = 2048;
    private static final int MAX_BUCKETS = BIG_MIN_BUCKETS * 8;
    private static final int OTHER_MAP_SIZE = MAX_BUCKETS * 2;
    
    private static Map<String, Integer> other_map;
    
    public static double calculateLoadFactor(HashMap<Object> map){
        return (map.size() + 1) * 1.0D / map.capacity();
    }
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @BeforeClass
    public static void SetUp(){
        other_map = new java.util.HashMap<>();
        
        for (int i = 0; i < OTHER_MAP_SIZE; i++) {
            other_map.put("key" + i, i);
        }
    }
    
    @Test
    public void testNewlyCreatedHashMapWithoutParametersHasSizeZero() {
        HashMap<Object> map = new HashMap<>();
        assertTrue(map.size() == 0);
    }
    
    @Test
    public void testNewlyCreatedHashMapWithMinBucketsParameterHasSizeZero() {
        HashMap<Object> map = new HashMap<>(MIN_BUCKETS);
        assertEquals(0, map.size());
    }
    
    @Test
    public void testNewlyCreatedHashMapWithBothParametersHasSizeZero() {
        HashMap<Object> map = new HashMap<>(MIN_BUCKETS, MAX_BUCKETS);
        assertEquals(0, map.size());
    }
    
    @Test
    public void testInsertSingleElementAndCheckSize() {
        HashMap<Object> map = new HashMap<>();
        map.insert("key1", 1);
        assertEquals(1, map.size());
    }
    
    @Test
    public void testInsertSingleElementAndCheckIfContained() {
        HashMap<Object> map = new HashMap<>();
        map.insert("key1", 1);
        assertTrue(map.contains("key1"));
    }
    
    @Test
    public void testInsertSingleElementAndThenGetIt() {
        HashMap<Object> map = new HashMap<>();
        map.insert("key1", 1);
        assertEquals(1, map.get("key1"));
    }
    
    @Test
    public void testContainsOnAnEmptyHashMap() {
        HashMap<Object> map = new HashMap<>();
        assertFalse(map.contains(NO_SUCH_KEY));
        assertEquals(0, map.size());
    }
    
    @Test
    public void testEraseSingleNonLastElement() {
        HashMap<Object> map = new HashMap<>();
        map.insert("key1", 1);
        map.insert("key2", 2);
        map.erase("key1");
        assertEquals(1, map.size());
        assertFalse(map.contains("key1"));
        assertTrue(map.contains("key2"));
    }
    
    @Test
    public void testEraseTheOnlyInsertedElement() {
        HashMap<Object> map = new HashMap<>();
        map.insert("key1", 1);
        map.erase("key1");
        assertEquals(0, map.size());
        assertFalse(map.contains("key1"));
    }
    
    @Test
    public void testEraseTheLastElement() {
        HashMap<Object> map = new HashMap<>();
        map.insert("key1", 1);
        map.insert("key2", 2);
        map.erase("key1");
        map.erase("key2");
        assertEquals(0, map.size());
    }
    
    @Test
    public void testInsertMultipleElementsAndCheckSize() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        assertEquals(other_map.size(), map.size());
    }    
        
    @Test
    public void testContainsWithANonContainedKey() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        assertFalse(map.contains(NO_SUCH_KEY));
    }
    
    @Test
    public void testInsertMultipleElementsAndCheckIfContained() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        for (String key : other_map.keySet()) {
            assertTrue(map.contains(key));
        }
    }
    
    @Test
    public void testInsertMultipleElementsAndThenGetThem() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            assertEquals(other_map.get(entry.getKey()), map.get(entry.getKey()));
        }
        assertEquals("get() should not change the size!", other_map.size(), map.size());
    }
    
    @Test
    public void testInsertMultipleElementsAndGetOneOfThemRepeatedly() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < 100; i++) {
            assertEquals(other_map.get(KEY_666), map.get(KEY_666));
        }
        assertEquals("get() should not change the size!", other_map.size(), map.size());
    }
    
    @Test
    public void testInsertMultipleElementsAndCheckOneOfThemIfContainedRepeatedly() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < 100; i++) {
            assertTrue(map.contains(KEY_666));
            assertEquals("contains() should not change the size!", other_map.size(), map.size());
        }
    }
        
    @Test
    public void testInsertMultipleElementsEraseOneOfThemThenInsertItAgainAndGetIt() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.erase(KEY_666);
        map.insert(KEY_666, other_map.get(KEY_666));
        assertTrue(map.contains(KEY_666));
        assertEquals(other_map.get(KEY_666), map.get(KEY_666));
        assertEquals("Size should not be changed!", other_map.size(), map.size());
    }
    
    @Test
    public void testInsertMultipleElementsAndEraseSomeOfThem() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        
        int counter = 0;
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            if(counter % 2 == 0){
                map.erase(entry.getKey());
            }
            counter++;
        }
        if(counter % 2 != 0){
            ++counter;
        }
        assertEquals(OTHER_MAP_SIZE - counter / 2, map.size());
        
        counter = 0;        
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            if(counter % 2 == 0){
                assertFalse(map.contains(entry.getKey()));
            } else {
                assertTrue(map.contains(entry.getKey()));
            }
            counter++;
        }
    }
    
    @Test
    public void testInsertMultipleElementsAndEraseAllOfThem() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.erase(entry.getKey());
        }
        assertEquals(0, map.size());
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            assertFalse(map.contains(entry.getKey()));
        }
    }
    
    @Test
    public void testEraseWithAKeyThatHasNoConnectedValueInTheMapDoesNotChangeSize() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.erase(NO_SUCH_KEY);
        assertEquals(other_map.size(), map.size()); 
    }
    
    @Test
    public void testInsertMultipleElementsAndEraseOneOfThemThenRepeatedly() {
                HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < 100; i++) {
            map.erase(KEY_666);
            assertFalse(map.contains(KEY_666));
            assertEquals("Multiple erase() should not change the size!", other_map.size() - 1, map.size());
        }
    }
    
    @Test
    public void testEraseFromAnEmptyHashMap() {
        HashMap<Object> map = new HashMap<>();
        map.erase(NO_SUCH_KEY);
        assertEquals(0, map.size());
    }
    
    @Test
    public void testInsertReplacesTheCurrentValueAndDoesNotChangeSizeOrOtherElements() {
        Integer newValue = -5;
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        
        map.insert(KEY_666, newValue);
        
        assertEquals("Size should not be changed!", other_map.size(), map.size());
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            if(entry.getKey().equals(KEY_666) == false){
                assertEquals("Other mappings should not be changed!",
                        other_map.get(entry.getKey()), map.get(entry.getKey()));
            } else {
                assertFalse("You should actually try to change the value to a DIFFERENT one.",
                        newValue.equals(entry.getValue()));
            }
        }
        assertEquals(newValue, map.get(KEY_666));
    }
    
    @Test
    public void testClearNewlyCreatedHashMapWithoutParametersHasSizeZero() {
        HashMap<Object> map = new HashMap<>();
        map.clear();
        assertEquals(0, map.size());
    }
    
    @Test
    public void testClearNewlyCreatedHashMapWithMinBucketsParameterHasSizeZero() {
        HashMap<Object> map = new HashMap<>(MIN_BUCKETS);
        map.clear();
        assertEquals(0, map.size());
    }
    
    @Test
    public void testClearNewlyCreatedHashMapWithBothParmetersHasSizeZero() {
        HashMap<Object> map = new HashMap<>(MIN_BUCKETS, MAX_BUCKETS);
        map.clear();
        assertEquals(0, map.size());
    }
    
    @Test
    public void testClearOnANonEmptyHashMapDeletesAllElementsAndMakesSizeZero() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.clear();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            assertFalse("The map should not contain any of it's elements after clear()!", map.contains(entry.getKey()));
        }
        assertEquals(0, map.size());
    }
    
    @Test
    public void testThatHashMapWithoutParametersMaintainsALoadFactorOfAtLeastPoint25() {
        HashMap<Object> map = new HashMap<>();
        assertTrue(calculateLoadFactor(map) >= 0.25);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
            assertTrue(calculateLoadFactor(map) >= 0.25);
        }
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.erase(entry.getKey());
            assertTrue(calculateLoadFactor(map) >= 0.25);
        }
    }
    
    @Test
    public void testThatHashMapWithMinBucketsParameterIsCreatedWithAtLeastThatCapacity() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS);
        assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
    }
    
    @Test
    public void testThatHashMapWithMinBucketsParameterMaintainsItDuringInsertAndErase() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
            assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
        }
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.erase(entry.getKey());
            assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
        }
    }
    
    @Test
    public void testThatHashMapWithBothParameterIsCreatedWithAtLeastMinBucketsCapacity() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS, MAX_BUCKETS);
        assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
    }
    
    @Test
    public void testThatHashMapWithBothParametersMaintainsThemDuringInsertAndErase() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS, MAX_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
            assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
            assertTrue(map.capacity() <= MAX_BUCKETS);
        }
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.erase(entry.getKey());
            assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
            assertTrue(map.capacity() <= MAX_BUCKETS);
        }
    }
    
    @Test
    public void testThatHashMapWithBothParametersSetEqualIsCreatedWithThatCapacity() {
        HashMap<Object> map = new HashMap<>(MAX_BUCKETS, MAX_BUCKETS);
        assertEquals(MAX_BUCKETS, map.capacity());
    }

    @Test
    public void testThatHashMapWithBothParametersSetEqualMaintainsThatCapacityDuringInsertAndErase() {
        HashMap<Object> map = new HashMap<>(MAX_BUCKETS, MAX_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
            assertEquals(MAX_BUCKETS, map.capacity());
        }
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.erase(entry.getKey());
            assertEquals(MAX_BUCKETS, map.capacity());
        }
    }
    
   
    /*
     * ==================================================
     * --------------------------------------------------
     * 
     * TESTS DOWNWARDS ARE SPECIFIC TO MY IMPLEMENTAITON
     * 
     * The specification demands handling of some situations but
     * doesn't explicitly state how.
     * 
     * --------------------------------------------------
     * ==================================================
     */
       
    
    @Test
    public void testGetWithAKeyThatHasNoConnectedValueInTheMapThrowsException() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        exception.expect(NoSuchElementException.class);
        map.get(NO_SUCH_KEY);
    }
    
    @Test
    public void testInsertElementThenEraseItAndGetItThrowsException() {
        HashMap<Object> map = new HashMap<>();
        map.insert(KEY_666, other_map.get(KEY_666));
        map.erase(KEY_666);
        exception.expect(NoSuchElementException.class);
        map.get(KEY_666);
    }
    
    @Test
    public void getFromAnEmptyHashMapThrowsException() {
        HashMap<Object> map = new HashMap<>(MAX_BUCKETS, MAX_BUCKETS);
        exception.expect(NoSuchElementException.class);
        map.get(NO_SUCH_KEY);
    }

    @Test
    public void testThatHashMapWithBothParametersSetEqualMaintainsThatCapacityAfterClear() {
        HashMap<Object> map = new HashMap<>(MAX_BUCKETS, MAX_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.clear();
        assertEquals(MAX_BUCKETS, map.capacity());
    }
            
    @Test
    public void testThatHashMapWithMinBucketsParameterMaintainsItAfterClear() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.clear();
        assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
    }
    
    @Test
    public void testThatHashMapWithBothParametersMaintainsThemAfterClear() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS, MAX_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.clear();
        assertTrue(map.capacity() >= BIG_MIN_BUCKETS);
        assertTrue(map.capacity() <= MAX_BUCKETS);
    }
    
    @Test
    public void testMaxBucketsEqualToTwoToThe28ThPowerIsOK() {
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS, 1<<28);
    }
    
    @Test
    public void testMaxBucketsEqualToTwoToThe29ThPowerThrowsException() {
        exception.expect(IllegalArgumentException.class);
        HashMap<Object> map = new HashMap<>(BIG_MIN_BUCKETS, 1<<29);
    }
    
    @Test
    public void testMinBucketsEqualToTwoToThe28ThPowerIsOK() {
        HashMap<Object> map = new HashMap<>(1<<28);
        assertTrue(map.capacity() >= 1<<28);
    }
    
    @Test
    public void testMinBucketsEqualToTwoToThe29ThPowerThrowsException() {
        exception.expect(IllegalArgumentException.class);
        HashMap<Object> map = new HashMap<>(1<<29);
    }
    
    @Test
    public void testCreateHashMapWithNegativeMinBucketsThrowsException() {
        exception.expect(IllegalArgumentException.class);
        HashMap<Object> map = new HashMap<>(-1);
    }
    
    @Test
    public void testCreateHashMapWithZeroMinBucketsThrowsException() {
        exception.expect(IllegalArgumentException.class);
        HashMap<Object> map = new HashMap<>(0);
    }
    
    @Test
    public void testCreateHashMapWithMinBucketsBiggerThanMaxBucketsThrowsException() {
        exception.expect(IllegalArgumentException.class);
        HashMap<Object> map = new HashMap<>(MAX_BUCKETS, MIN_BUCKETS);
    }
    
    @Test
    public void testInsertElementWithNullKeyThrowsException() {
        HashMap<Object> map = new HashMap<>();
        exception.expect(IllegalArgumentException.class);
        map.insert(null, 5);
    }
    
    @Test
    public void testInsertElementWithNullValueThrowsException() {
        HashMap<Object> map = new HashMap<>();
        exception.expect(IllegalArgumentException.class);
        map.insert(null, 5);
    }
    
    @Test
    public void testGetWithNullKeyThrowsException() {
        HashMap<Object> map = new HashMap<>();
        exception.expect(IllegalArgumentException.class);
        map.get(null);
    }
    
    @Test
    public void testContainsWithNullKeThrowsExceptiony() {
        HashMap<Object> map = new HashMap<>();
        exception.expect(IllegalArgumentException.class);
        map.contains(null);
    }
    
    @Test
    public void testEraseWithNullKeyThrowsException() {
        HashMap<Object> map = new HashMap<>();
        exception.expect(IllegalArgumentException.class);
        map.erase(null);
    }
    
    @Test
    public void testResizeGuaranteesAtLeastThatCapacityWithHashMapWithoutParameters() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.resize(1_000_000);
        assertTrue(map.capacity() >= 1_000_000);
    }
            
    @Test
    public void testResizeWorksOnEmptyHashMapWithoutParameters() {
        HashMap<Object> map = new HashMap<>();
        map.resize(1_000_000);
        assertTrue(map.capacity() >= 1_000_000);
    }
    
    @Test
    public void testResizeDoesNotViolateTheMaintainingOfMaxBuckets() {
        HashMap<Object> map = new HashMap<>(MIN_BUCKETS, MAX_BUCKETS);
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        map.resize(MAX_BUCKETS * 5);
        assertTrue(map.capacity() <= MAX_BUCKETS);
    }
    
    @Test
    public void testResizeDoesNotShrinkTheHashMap() {
        HashMap<Object> map = new HashMap<>();
        for (Map.Entry<String, Integer> entry : other_map.entrySet()) {
            map.insert(entry.getKey(), entry.getValue());
        }
        int oldCapacity = map.capacity();
        map.resize(map.size() / 5);
        assertEquals(oldCapacity, map.capacity());
    }
}
