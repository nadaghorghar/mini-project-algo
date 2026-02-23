package app;

import java.util.Scanner;
import java.util.List;
import engine.MorphologyEngine;
import io.FileLoader;
import models.Root;
import models.Node;
import models.Scheme;
import models.ValidationResult;
import structures.AVLTree;
import structures.HashTableSchemes;
import utils.Utils;

public class Main {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        HashTableSchemes schemes = new HashTableSchemes(20);
        MorphologyEngine engine = new MorphologyEngine();

        System.out.println("=== INITIALISATION DU MOTEUR MORPHOLOGIQUE ===");
        FileLoader.loadRoots("data/racines.txt", tree);
        FileLoader.loadSchemes("data/schemes.txt", schemes);
        System.out.println();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n╔════════════════════════════════════════════════╗");
            System.out.println("║         MOTEUR MORPHOLOGIQUE ARABE             ║");
            System.out.println("╚════════════════════════════════════════════════╝");
            System.out.println("│ 1. Générer un mot dérivé                       │");
            System.out.println("│ 2. Valider un mot (appartenance à racine)      │");
            System.out.println("│ 3. Générer toute la famille morphologique      │");
            System.out.println("│ 4. Afficher dérivés validés d'une racine       │");
            System.out.println("│ 5. Ajouter une nouvelle racine                 │");
            System.out.println("│ 6. Ajouter un nouveau schème                  │");
            System.out.println("│ 7. Afficher toutes les racines                 │");
            System.out.println("│ 8. Afficher tous les schèmes                   │");
            System.out.println("│ 0. Quitter                                     │");
            System.out.println("└────────────────────────────────────────────────┘");
            System.out.print("Choix: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println("Au revoir !");
                break;
            }

            switch (choice) {
                case 1:
                    System.out.print("Racine: ");
                    String r = sc.nextLine();
                    Node node = tree.search(r);
                    if (node == null) {
                        System.out.println("❌ Erreur: Racine '" + r + "' non trouvée!");
                        System.out.println("💡 Utilisez l'option 5 pour l'ajouter ou l'option 7 pour voir les racines disponibles.");
                        break;
                    }
                    Root root = node.getRoot();
                    System.out.print("Schème: ");
                    String s = sc.nextLine();
                    Scheme scheme = schemes.search(s);
                    if (scheme == null) {
                        System.out.println("❌ Erreur: Schème '" + s + "' non trouvé!");
                        System.out.println("💡 Utilisez l'option 8 pour voir les schèmes disponibles.");
                        break;
                    }
                    String result = engine.generate(root, scheme);
                    System.out.println("✅ Résultat: " + result);
                    root.addDerivative(result);
                    System.out.println("✓ Ajouté aux dérivés validés de la racine " + r);
                    break;

                case 2:
                    System.out.println("\n=== VALIDATION MORPHOLOGIQUE ===");
                    System.out.print("Mot à valider: ");
                    String word = sc.nextLine();
                    System.out.print("Racine supposée: ");
                    String rootStr = sc.nextLine();
                    Node nodeVal = tree.search(rootStr);
                    if (nodeVal == null) {
                        System.out.println("❌ Racine '" + rootStr + "' non trouvée");
                        break;
                    }
                    ValidationResult valResult = engine.validate(word, nodeVal.getRoot(), schemes);
                    if (valResult.isValid()) {
                        System.out.println("✅ OUI - Le mot '" + word + "' appartient à la racine '" + rootStr + "'");
                        System.out.println("   Schème utilisé: " + valResult.getScheme().getName() + " (" + valResult.getScheme().getPattern() + ")");
                        nodeVal.getRoot().addDerivative(word);
                        System.out.println("✓ Ajouté aux dérivés validés");
                    } else {
                        System.out.println("❌ NON - Le mot '" + word + "' n'appartient pas à la racine '" + rootStr + "'");
                    }
                    break;

                case 3:
                    System.out.println("\n=== FAMILLE MORPHOLOGIQUE ===");
                    System.out.print("Racine: ");
                    String rootFamily = sc.nextLine();
                    Node nodeFamily = tree.search(rootFamily);
                    if (nodeFamily == null) {
                        System.out.println("❌ Racine non trouvée");
                        break;
                    }
                    System.out.println("\n┌─────────────────────────────────────────┐");
                    System.out.println("│ Famille morphologique de: " + rootFamily + "          │");
                    System.out.println("├─────────────────────────────────────────┤");
                    List<Scheme> allSchemes = schemes.getAllSchemes();
                    for (Scheme sch : allSchemes) {
                        String derived = engine.generate(nodeFamily.getRoot(), sch);
                        System.out.printf("│ %-12s → %-20s │%n", sch.getName(), derived);
                        nodeFamily.getRoot().addDerivative(derived);
                    }
                    System.out.println("└─────────────────────────────────────────┘");
                    System.out.println("✓ Tous les dérivés ont été ajoutés à la liste validée");
                    break;

                case 4:
                    System.out.print("Racine: ");
                    String rootDer = sc.nextLine();
                    Node nodeDer = tree.search(rootDer);
                    if (nodeDer != null) {
                        nodeDer.getRoot().displayDerivatives();
                    } else {
                        System.out.println("❌ Racine non trouvée");
                    }
                    break;

                case 5:
                    System.out.print("Nouvelle racine (3 lettres arabes): ");
                    String newRoot = sc.nextLine().trim();

                    if (newRoot.length() != 3) {
                        System.out.println("❌ Une racine doit contenir exactement 3 lettres !");
                        break;
                    }

                    // ✅ Validation: uniquement des lettres arabes
                    boolean rootIsArabic = true;
                    for (char c : newRoot.toCharArray()) {
                        if (!Utils.isArabicLetter(c)) {
                            rootIsArabic = false;
                            break;
                        }
                    }
                    if (!rootIsArabic) {
                        System.out.println("❌ La racine doit contenir uniquement des lettres arabes !");
                        break;
                    }

                    if (tree.search(newRoot) != null) {
                        System.out.println("❌ Cette racine existe déjà !");
                        break;
                    }

                    tree.insert(new Root(newRoot));
                    System.out.println("✅ Racine '" + newRoot + "' ajoutée avec succès");
                    break;

                case 6:
                    System.out.print("Nom du schème (lettres arabes, ex: فاعل): ");
                    String schemeName = sc.nextLine().trim();

                    // ✅ Validation: uniquement des lettres arabes
                    boolean schemeIsArabic = true;
                    for (char c : schemeName.toCharArray()) {
                        if (!Utils.isArabicLetter(c)) {
                            schemeIsArabic = false;
                            break;
                        }
                    }
                    if (!schemeIsArabic) {
                        System.out.println("❌ Le nom du schème doit contenir uniquement des lettres arabes !");
                        break;
                    }

                    System.out.print("Pattern (ex: C1+ا+C2+C3): ");
                    String schemePattern = sc.nextLine().trim();
                    schemes.insert(new Scheme(schemeName, schemePattern));
                    System.out.println("✅ Schème ajouté avec succès");
                    break;

                case 7:
                    System.out.println("\n=== RACINES DISPONIBLES ===");
                    tree.displayInOrder();
                    break;

                case 8:
                    System.out.println("\n=== SCHÈMES DISPONIBLES ===");
                    schemes.displayAll();
                    break;

                default:
                    System.out.println("❌ Choix invalide !");
            }
        }

        sc.close();
    }
}
