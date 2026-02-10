package app;

import java.util.Scanner;

import engine.MorphologyEngine;
import models.Root;
import models.Scheme;
import structures.AVLTree;
import structures.HashTableSchemes;

public class Main {

    public static void main(String[] args) {

        AVLTree tree = new AVLTree();
        HashTableSchemes schemes = new HashTableSchemes(20);
        MorphologyEngine engine = new MorphologyEngine();

        // Ajouter schèmes initiaux
        schemes.insert(new Scheme("فاعل", "ف1ا ف2 ف3"));
        schemes.insert(new Scheme("مفعول", "مف1ف2و ف3"));

        // Ajouter racines test
        tree.insert(new Root("كتب"));

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n=== MENU ===");
            System.out.println("1. Générer mot dérivé");
            System.out.println("2. Afficher racines");
            System.out.println("3. Afficher schèmes");
            System.out.println("0. Quitter");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) break;

            if (choice == 1) {
                System.out.print("Racine: ");
                String r = sc.nextLine();

                Root root = tree.search(r).getRoot();

                System.out.print("Schème: ");
                String s = sc.nextLine();

                Scheme scheme = schemes.search(s);

                String result = engine.generate(root, scheme);

                System.out.println("Résultat: " + result);
            }

            if (choice == 2) tree.displayInOrder();

            if (choice == 3) schemes.displayAll();
        }

        sc.close();
    }
}
