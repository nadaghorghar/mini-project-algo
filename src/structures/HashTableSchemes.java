package structures;

import models.Scheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Table de hachage pour stocker les schèmes morphologiques arabes.
 * Utilise le sondage linéaire pour gérer les collisions.
 *
 * Complexité des opérations:
 * - Insertion: O(1) en moyenne, O(n) dans le pire cas
 * - Recherche: O(1) en moyenne, O(n) dans le pire cas
 * - Suppression: O(1) en moyenne, O(n) dans le pire cas
 *
 * @author Étudiant
 * @version 2.0
 */
public class HashTableSchemes {

    private Scheme[] table;
    private boolean[] deleted; // Marque les cases supprimées
    private int size;

    public HashTableSchemes(int capacity) {
        table = new Scheme[capacity];
        deleted = new boolean[capacity];
        size = 0;
    }

    /**
     * Fonction de hachage simple
     * @param key La clé à hacher
     * @return L'index dans la table
     */
    private int hash(String key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    /**
     * Insère un nouveau schème dans la table
     * @param scheme Le schème à insérer
     * @return true si l'insertion a réussi, false sinon
     */
    public void insert(Scheme scheme) {
        if (scheme == null) return;

        // Vérifier si le schème existe déjà
        if (search(scheme.getName()) != null) {
            System.out.println("Schème déjà existant: " + scheme.getName());
            return;
        }

        int index = hash(scheme.getName());
        int originalIndex = index;

        // Chercher une case libre (null ou supprimée)
        while (table[index] != null && !deleted[index]) {
            index = (index + 1) % table.length;

            // Table pleine
            if (index == originalIndex) {
                System.out.println("Table de hachage pleine!");
                return;
            }
        }

        table[index] = scheme;
        deleted[index] = false;
        size++;
    }

    /**
     * Recherche un schème par son nom
     * @param name Le nom du schème
     * @return Le schème trouvé, ou null
     */
    public Scheme search(String name) {
        if (name == null) return null;

        int index = hash(name);
        int originalIndex = index;

        while (table[index] != null || deleted[index]) {
            if (table[index] != null && !deleted[index] && table[index].getName().equals(name)) {
                return table[index];
            }
            index = (index + 1) % table.length;

            // Retour au point de départ
            if (index == originalIndex) {
                break;
            }
        }

        return null;
    }

    /**
     * Modifie le pattern d'un schème existant
     * @param name Le nom du schème à modifier
     * @param newPattern Le nouveau pattern
     * @return true si la modification a réussi, false sinon
     */
    public boolean modify(String name, String newPattern) {
        if (name == null || newPattern == null) return false;

        Scheme scheme = search(name);
        if (scheme != null) {
            scheme.setPattern(newPattern);
            return true;
        }
        return false;
    }

    /**
     * Supprime un schème de la table
     * Utilise le marquage par suppression (lazy deletion)
     * @param name Le nom du schème à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public boolean delete(String name) {
        if (name == null) return false;

        int index = hash(name);
        int originalIndex = index;

        while (table[index] != null || deleted[index]) {
            if (table[index] != null && !deleted[index] && table[index].getName().equals(name)) {
                // Marquer comme supprimé
                deleted[index] = true;
                table[index] = null;
                size--;
                return true;
            }
            index = (index + 1) % table.length;

            // Retour au point de départ
            if (index == originalIndex) {
                break;
            }
        }

        return false;
    }

    /**
     * Affiche tous les schèmes stockés
     */
    public void displayAll() {
        System.out.println("=== Schèmes stockés ===");
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !deleted[i]) {
                System.out.println(table[i]);
            }
        }
        System.out.println("Total: " + size + " schèmes");
    }

    /**
     * Récupère tous les schèmes sous forme de liste
     * @return Liste de tous les schèmes
     */
    public List<Scheme> getAllSchemes() {
        List<Scheme> result = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null && !deleted[i]) {
                result.add(table[i]);
            }
        }
        return result;
    }

    /**
     * Retourne le nombre de schèmes stockés
     * @return Le nombre de schèmes
     */
    public int size() {
        return size;
    }

    /**
     * Vérifie si la table est vide
     * @return true si vide, false sinon
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Retourne la capacité totale de la table
     * @return La capacité
     */
    public int getCapacity() {
        return table.length;
    }

    /**
     * Calcule le facteur de charge de la table
     * @return Le facteur de charge (entre 0 et 1)
     */
    public double loadFactor() {
        return (double) size / table.length;
    }
}