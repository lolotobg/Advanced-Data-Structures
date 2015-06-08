
/**
 * @author Spas Kyuchukov
 */

public class Main {

    public static void main(String[] args) throws Exception {
        SkewHeap heap = new SkewHeap();
        heap.add(5);
        heap.add(8);
        heap.add(1);
        heap.add(11);
        heap.add(3);
        heap.add(2);

        System.out.println(heap.getHeapInfoInLevelOrder());
        System.out.println("removeMin(): " + heap.removeMin());
        System.out.println(heap.getHeapInfoInLevelOrder());
        System.out.println("removeMin(): " + heap.removeMin());
        System.out.println(heap.getHeapInfoInLevelOrder());
        System.out.println("removeMin(): " + heap.removeMin());
        System.out.println(heap.getHeapInfoInLevelOrder());
        System.out.println("removeMin(): " + heap.removeMin());
        System.out.println(heap.getHeapInfoInLevelOrder());
        System.out.println("removeMin(): " + heap.removeMin());
        System.out.println(heap.getHeapInfoInLevelOrder());
        System.out.println("removeMin(): " + heap.removeMin());
        System.out.println(heap.getHeapInfoInLevelOrder());
        //System.out.println("removeMin(): " + heap.removeMin());
 
    }
}
