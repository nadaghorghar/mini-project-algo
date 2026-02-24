package io;

import models.Root;
import models.Scheme;
import structures.AVLTree;
import structures.HashTableSchemes;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileLoader {


    public static void loadRoots(String filename, AVLTree tree) {
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println(" Fichier non trouvé: " + filename);
            System.err.println("   Le programme continuera avec les racines par défaut.");
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Ignorer les lignes vides et les commentaires
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Créer la racine avec détection automatique du type
                Root root = new Root(line);
                root.detectType();
                tree.insert(root);
                count++;
            }

            System.out.println(" " + count + " racines chargées depuis " + filename);

        } catch (IOException e) {
            System.err.println(" Erreur lors du chargement des racines: " + e.getMessage());
        }
    }


    public static void loadSchemes(String filename, HashTableSchemes schemes) {
        File file = new File(filename);

        if (!file.exists()) {
            System.err.println("  Fichier non trouvé: " + filename);
            System.err.println("   Le programme continuera avec les schèmes par défaut.");
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();


                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parser la ligne (format: nom|pattern)
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    schemes.insert(new Scheme(parts[0].trim(), parts[1].trim()));
                    count++;
                } else {
                    System.err.println(" Ligne invalide ignorée: " + line);
                }
            }

            System.out.println(" " + count + " schèmes chargés depuis " + filename);

        } catch (IOException e) {
            System.err.println(" Erreur lors du chargement des schèmes: " + e.getMessage());
        }
    }
}
