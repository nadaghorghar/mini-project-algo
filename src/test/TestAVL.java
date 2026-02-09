package test;

import models.Root;
import models.Node;
import structures.AVLTree;
import utils.Utils;

public class TestAVL {
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   TESTS ARBRE AVL - ÉTUDIANT 1");
        System.out.println("=================================\n");
        
        testRoot();
        testNode();
        testUtils();
        testAVLInsertion();
        testAVLSearch();
        testAVLBalance();
        testAVLDisplay();
        
        System.out.println("\n=================================");
        System.out.println("   TESTS TERMINÉS");
        System.out.println("=================================");
    }
    
    public static void testRoot() {
        System.out.println("\n--- TEST 1: Classe Root ---");
        
        Root r1 = new Root("كتب");
        System.out.println(r1);
        
        Root r2 = new Root("قال", "FAIBLE");
        System.out.println(r2);
        
        r1.addDerivative("مكتوب");
        r1.addDerivative("كاتب");
        System.out.println("Nombre de dérivés: " + r1.getDerivativesCount());
        
        System.out.println("Contient 'مكتوب': " + r1.hasDerivative("مكتوب"));
        
        r1.displayDerivatives();
        
        int cmp = r1.compareTo(r2);
        System.out.println("كتب vs قال: " + (cmp < 0 ? "كتب < قال" : "كتب > قال"));
        
        System.out.println("✓ Tests Root terminés");
    }
    
    public static void testNode() {
        System.out.println("\n--- TEST 2: Classe Node ---");
        
        Root r = new Root("كتب");
        Node node = new Node(r);
        System.out.println(node);
        System.out.println("Hauteur: " + node.getHeight());
        
        System.out.println("Est une feuille: " + node.isLeaf());
        
        node.setLeft(new Node(new Root("درس")));
        System.out.println("Est une feuille: " + node.isLeaf());
        
        System.out.println("✓ Tests Node terminés");
    }
    
    public static void testUtils() {
        System.out.println("\n--- TEST 3: Classe Utils ---");
        
        System.out.println("كتب est régulière: " + Utils.isRegularRoot("كتب"));
        System.out.println("قال est régulière: " + Utils.isRegularRoot("قال"));
        
        System.out.println("أخذ a hamza: " + Utils.hasHamza("أخذ"));
        
        System.out.println("قال est faible: " + Utils.isWeakRoot("قال"));
        
        System.out.println("وعد est assimilée: " + Utils.isAssimilatedRoot("وعد"));
        
        System.out.println("Type de كتب: " + Utils.determineRootType("كتب"));
        System.out.println("Type de قال: " + Utils.determineRootType("قال"));
        System.out.println("Type de وعد: " + Utils.determineRootType("وعد"));
        
        System.out.println("كتب est valide: " + Utils.isValidTrillateralRoot("كتب"));
        System.out.println("كت est valide: " + Utils.isValidTrillateralRoot("كت"));
        
        System.out.println("✓ Tests Utils terminés");
    }
    
    public static void testAVLInsertion() {
        System.out.println("\n--- TEST 4: Insertion AVL ---");
        
        AVLTree tree = new AVLTree();
        System.out.println("Arbre vide: " + tree.isEmpty());
        
        Root r1 = new Root("كتب", "REGULIERE");
        tree.insert(r1);
        System.out.println("Nombre de racines: " + tree.getCount());
        
        tree.insert(new Root("درس", "REGULIERE"));
        tree.insert(new Root("قال", "FAIBLE"));
        tree.insert(new Root("فعل", "REGULIERE"));
        tree.insert(new Root("جلس", "REGULIERE"));
        System.out.println("Nombre de racines: " + tree.getCount());
        
        tree.insert(new Root("كتب", "REGULIERE"));
        System.out.println("Nombre après doublon: " + tree.getCount());
        
        System.out.println("✓ Tests Insertion terminés");
    }
    
    public static void testAVLSearch() {
        System.out.println("\n--- TEST 5: Recherche AVL ---");
        
        AVLTree tree = new AVLTree();
        tree.insert(new Root("كتب"));
        tree.insert(new Root("درس"));
        tree.insert(new Root("قال"));
        
        Node found = tree.search("كتب");
        if (found != null) {
            System.out.println("Trouvé: " + found.getRoot().getLetters());
        }
        
        Node notFound = tree.search("شرب");
        System.out.println("شرب trouvé: " + (notFound != null));
        
        System.out.println("Contient كتب: " + tree.contains("كتب"));
        System.out.println("Contient شرب: " + tree.contains("شرب"));
        
        System.out.println("✓ Tests Recherche terminés");
    }
    
    public static void testAVLBalance() {
        System.out.println("\n--- TEST 6: Équilibrage AVL ---");
        
        AVLTree tree = new AVLTree();
        tree.insert(new Root("أ"));
        tree.insert(new Root("ب"));
        tree.insert(new Root("ت"));
        tree.insert(new Root("ث"));
        tree.insert(new Root("ج"));
        tree.insert(new Root("ح"));
        
        System.out.println("Hauteur de l'arbre: " + tree.getMaxHeight());
        System.out.println("Arbre équilibré: " + tree.isBalanced());
        
        System.out.println("✓ Tests Équilibrage terminés");
    }
    
    public static void testAVLDisplay() {
        System.out.println("\n--- TEST 7: Affichage AVL ---");
        
        AVLTree tree = new AVLTree();
        tree.insert(new Root("كتب"));
        tree.insert(new Root("درس"));
        tree.insert(new Root("قال"));
        tree.insert(new Root("فعل"));
        tree.insert(new Root("جلس"));
        
        tree.displayInOrder();
        System.out.println();
        
        tree.displayPreOrder();
        System.out.println();
        
        tree.displayTree();
        System.out.println();
        
        tree.displayStatistics();
        
        System.out.println("✓ Tests Affichage terminés");
    }
}
