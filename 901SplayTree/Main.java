
/**
 * @author Spas Kyuchukov
 */

public class Main {

    public static void main(String[] args) {
        SplayTree spt = new SplayTree();
        int perfectTreeWithHeight4[] = new int[]{
            9,
            4, 13,
            2, 6, 11, 15,
            1, 3, 5, 8, 10, 12, 14, 16,
            7
        };

        System.out.println(spt.getTreeInfoInLevelOrder());
        for (int i = 0; i < perfectTreeWithHeight4.length; i++) {
            spt.insertNode(perfectTreeWithHeight4[i]);
            System.out.println(spt.getTreeInfoInLevelOrder());
        }

        spt.deleteNode(3);
        System.out.println(spt.getTreeInfoInLevelOrder());

        spt.deleteNode(5);
        System.out.println(spt.getTreeInfoInLevelOrder());

        spt.deleteNode(8);
        System.out.println(spt.getTreeInfoInLevelOrder());

        spt.deleteNode(13);
        System.out.println(spt.getTreeInfoInLevelOrder());

        spt.deleteNode(9);
        System.out.println(spt.getTreeInfoInLevelOrder());

        spt.contains(9);
        System.out.println(spt.getTreeInfoInLevelOrder());
    }

}
