/**
 * @author Spas Kyuchukov
 */

public class Test {

    public static void main(String[] args) {
        
        TreapImplTest treap = new TreapImplTest();
        long begin = System.nanoTime();
        for (int i = 0; i < 40_000_000; i++) {
            treap.insert(i);
        }
        long end = System.nanoTime();
        System.out.println("Time: " + (end - begin));
        /*treap.insert(5, 0.32f);
        treap.insert(2, 0.43f);
        treap.insert(3, 0.58f);
        treap.printTreapInOrder();
        treap.remove(5);
        treap.printTreapInOrder();
        treap.remove(2);
        treap.printTreapInOrder();
        treap.remove(3);
        treap.printTreapInOrder();
        treap.remove(4);
        treap.printTreapInOrder();
        * */
        /*treap.insert(4);
        treap.insert(6);
        treap.insert(8);
        treap.insert(10);
        treap.insert(18);
        treap.insert(14);
        ((TreapImpl)treap).printTreapInOrder();
        
        treap.remove(14);
        ((TreapImpl)treap).printTreapInOrder();
        treap.remove(14);
        ((TreapImpl)treap).printTreapInOrder();
        treap.remove(3);
        ((TreapImpl)treap).printTreapInOrder();
        treap.remove(11);
        ((TreapImpl)treap).printTreapInOrder();
        System.out.println("treap.containsKey(6): " + treap.containsKey(6));
*/
        /*
        Treap treap2 = new TreapImpl();
        ((TreapImpl)treap2).insert(-1, 0.92f);
        ((TreapImpl)treap2).printTreapInOrder();
        ((TreapImpl)treap2).insert(2, 0.43f);
        ((TreapImpl)treap2).printTreapInOrder();
        ((TreapImpl)treap2).insert(3, 0.058f);
        ((TreapImpl)treap2).printTreapInOrder();
        ((TreapImpl)treap2).insert(4, 0.5f);
        ((TreapImpl)treap2).printTreapInOrder();
        ((TreapImpl)treap2).insert(6, 0.042f);
        ((TreapImpl)treap2).printTreapInOrder();
        */
    }
}
