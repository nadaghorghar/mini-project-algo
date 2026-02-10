package io;

import java.io.*;
import models.Root;
import models.Scheme;
import structures.AVLTree;
import structures.HashTableSchemes;

public class FileLoader {

    public static void loadSchemes(String file, HashTableSchemes table) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split(";");
                String name = parts[0];
                String pattern = parts[1];

                table.insert(new Scheme(name, pattern));
            }

        } catch (Exception e) {
            System.out.println("Erreur chargement schèmes: " + e.getMessage());
        }
    }

    public static void loadRoots(String file, AVLTree tree) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {
                tree.insert(new Root(line));
            }

        } catch (Exception e) {
            System.out.println("Erreur chargement racines: " + e.getMessage());
        }
    }
}
