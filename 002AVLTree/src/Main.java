
import static AuxiliaryFunctions.get_time;
import static AuxiliaryFunctions.print;
import static TestAvlTree.MEASURE_TIME;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class Main {

    private static double avlTime;
    //private static Random rand = new Random(123342);
    private static Random rand = new Random();

    private static boolean executeTest(String testName, int numbers[], int size, int deletions[], int deletionsSize,
            boolean checkTree) {
        print("Executing test: '%s'", testName);
        double begin, end;
        avlTime = 0;
        double rbTime = 0, checkerTime = 0;
        begin = get_time();
        //List<Integer> values = new ArrayList<Integer>();
        TreeMap<Integer, Integer> values = new TreeMap();
        end = get_time();
        rbTime += (end - begin);
        begin = end;
        TestAvlTree<Integer> myTree = new TestAvlTree<Integer>();
        end = get_time();
        avlTime += (end - begin);
        begin = end;
    	if (checkTree && !myTree.checkTree(values))
    	{
    		return false;
    	}
        end = get_time();
        checkerTime += (end - begin);
        int operationIndex = 0;
        int deletionIndex = 0;
        int listIdx;
        for (int i = 0; i < size; i++) {
            operationIndex++;
            if (deletionIndex < deletionsSize && deletions[deletionIndex * 2] == operationIndex) {
                i--;
                if (deletions[deletionIndex * 2 + 1] < size) {
                    begin = get_time();
                    //if ((listIdx = values.indexOf(numbers[deletions[deletionIndex * 2 + 1]])) != -1) {
                        values.remove(numbers[deletions[deletionIndex * 2 + 1]]);
                    //}
                    end = get_time();
                    rbTime += (end - begin);
                    begin = end;
                    myTree.deleteNode(numbers[deletions[deletionIndex * 2 + 1]]);
                    end = get_time();
                    avlTime += (end - begin);
                } else {
                    begin = get_time();
                    // -1 should be non-existing value
                    myTree.deleteNode(-1);
                    end = get_time();
                    avlTime += (end - begin);
                }
            } else {
                begin = get_time();
                values.put(numbers[i], numbers[i]);
                //values.add(numbers[i]);
                end = get_time();
                rbTime += (end - begin);
                begin = end;
                myTree.insertNode(numbers[i]);
                end = get_time();
                avlTime += (end - begin);
            }
            begin = get_time();
            if (checkTree && !myTree.checkTree(values)) {
                end = get_time();
                checkerTime += (end - begin);
                print("\nTimes taken: List: %.5f AVL time %.5f Checker time  %.5f", rbTime, avlTime, checkerTime);
                if (MEASURE_TIME) {
                    myTree.printTimes();
                }
                print("\nTest failed\n\n");
                return false;
            }
            end = get_time();
            checkerTime += (end - begin);
        }
        print("\nTimes taken: List: %.5f AVL time %.5f Checker time  %.5f", rbTime, avlTime, checkerTime);
        if (MEASURE_TIME) {
            myTree.printTimes();
        }
    	if (checkTree)
    	{
    		print("\nTest passed");
    	}
    	print("\n\n");
        return true;
    }

    private static boolean randomTest(int numberCount, int deletionsCount, boolean executeCheck)
    {
        int [] numbers = new int[numberCount];
        int [] deletions = new int[deletionsCount * 2];
        for (int i = 0; i < numberCount; i++)
        {
            numbers[i] = rand.nextInt();
        }
        for (int i = 0; i < deletionsCount; i++)
        {
            deletions[i * 2] = 5 + rand.nextInt() % (numberCount - 5);
            deletions[i * 2 + 1] = rand.nextInt() % (i + 1);
        }
        String testName = "Random test with " + numberCount + " numbers and "+ deletionsCount + " deletions";
        boolean result = executeTest(testName, numbers, numberCount, deletions, deletionsCount, executeCheck);
        return result;
    }

    private static int testResultPrinter(String testLabel, boolean testSuccessful, int successfulPoints) {
        return testResultPrinter(testLabel, testSuccessful, successfulPoints, 0);
    }

    private static int testResultPrinter(String testLabel, boolean testSuccessful, int successfulPoints,
            int failurePoints) {
        if (testSuccessful) {
            print("----- '%s' test PASSED-----\n", testLabel);
            if (successfulPoints != 0) {
                print("----- this adds %d points to your final score----\n", successfulPoints);
            }
            print("\n\n");
            return successfulPoints;
        } else {
            print("----- '%s' test FAILED----\n", testLabel);
            if (failurePoints != 0) {
                print("----- this subtracts %d points from your final score----\n", failurePoints);
            }
            print("\n\n");
            return -failurePoints;
        }
    }

    private static int testInsertSmall() {
        boolean successfulTest = true;
        int smallTest1[] = { 5, 3, 7, 2, 11, 18 };
        int emptyDeletions[] = { 3, 50 };
        int smallTest2[] = { 134, 41, 876, 124, 23, 47, 13, 55, 7, 101, 68, 45, 75, 61, 31, 34, 62, 90, 17, 42 };
        successfulTest = successfulTest
                && executeTest("First small test", smallTest1, smallTest1.length, emptyDeletions, 0, true);
        successfulTest = successfulTest
                && executeTest("Second small test", smallTest2, smallTest2.length, emptyDeletions, 1, true);
        return testResultPrinter("Small insert", successfulTest, 1);
    }

    private static int testDeleteSmall() {
        boolean successfulTest = true;
        int smallTest2[] = { 134, 41, 876, 124, 23, 47, 13, 55, 7, 101, 68, 45, 75, 61, 31, 34, 62, 90, 17, 42 };
        int deletions1[] = { 5, 2, 14, 5, 15, 6 };
        successfulTest = successfulTest
                && executeTest("First small test with deletions", smallTest2, smallTest2.length, deletions1,
                        (deletions1.length) / 2, true);
        successfulTest = successfulTest && randomTest(22, 4, true);
        successfulTest = successfulTest && randomTest(22, 4, true);
        return testResultPrinter("Small delete", successfulTest, 1);
    }

    private static int testInsertBig() {
        boolean successfulTest = true;
        successfulTest = successfulTest && randomTest(91, 0, true);
        successfulTest = successfulTest && randomTest(265, 0, true);
        successfulTest = successfulTest && randomTest(310, 0, true);
        successfulTest = successfulTest && randomTest(2654, 0, true);
        successfulTest = successfulTest && randomTest(4213, 0, true);
        successfulTest = successfulTest && randomTest(12000, 0, true);
        return testResultPrinter("Big insert", successfulTest, 1);
    }

    private static int testDeleteBig() {
        boolean successfulTest = true;
        int smallTest2[] = { 134, 41, 876, 124, 23, 47, 13, 55, 7, 101, 68, 45, 75, 61, 31, 34, 62, 90, 17, 42 };
        int deletions2[] = { 7, 5, 8, 4, 9, 3, 10, 2, 11, 1, 12, 0 };
        successfulTest = successfulTest
                && executeTest("Testing deleting all numbers", smallTest2, smallTest2.length, deletions2,
                        (deletions2.length) / 2, true);
        successfulTest = successfulTest && randomTest(91, 21, true);
        successfulTest = successfulTest && randomTest(265, 42, true);
        successfulTest = successfulTest && randomTest(310, 63, true);
        successfulTest = successfulTest && randomTest(2654, 403, true);
        successfulTest = successfulTest && randomTest(4213, 702, true);
        successfulTest = successfulTest && randomTest(12000, 2000, true);
        return testResultPrinter("Big delete", successfulTest, 1);
    }

    private static int testComplexity() {
        randomTest(1200000, 800000, false);
        double avlTime1 = avlTime;
        randomTest(12000000, 8000000, false);
        double avlTime2 = avlTime;
        boolean complexityCorrect = (avlTime2 < 20.0 * Math.log(10.0) * avlTime1);
        return testResultPrinter("Asymptotic complexity", complexityCorrect, 0, 2);
    }

    public static void main(String[] args) {
        int result = 0;
        result += testInsertSmall();
        result += testInsertBig();
        result += testDeleteSmall();
        result += testDeleteBig();
        result += testComplexity();
        print("\n\nPreliminary result: %d points from this homework\n", Math.max(result, 0));
    	print("For the user: %s with moodle name: %s and fn: %s\n\n", TestAvlTree.NAME, TestAvlTree.MOODLE_NAME, TestAvlTree.FACULTY_NUMBER);

    }

}
