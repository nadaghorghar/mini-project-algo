package structures;

import models.Scheme;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class HashTableSchemes {

    // ===== ATTRIBUTS =====
    private LinkedList<Scheme>[] table;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    // ===== CONSTRUCTEUR =====
    @SuppressWarnings("unchecked")
    public HashTableSchemes(int capacity) {
        this.capacity = capacity;
        table = new LinkedList[capacity];
        size = 0;
    }

    // ===== FONCTION DE HACHAGE SÉCURISÉE =====
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    // ===== REDIMENSIONNEMENT =====
    private void resize() {
        int newCapacity = capacity * 2;
        LinkedList<Scheme>[] oldTable = table;

        table = new LinkedList[newCapacity];
        capacity = newCapacity;
        size = 0;

        for (LinkedList<Scheme> bucket : oldTable) {
            if (bucket != null) {
                for (Scheme scheme : bucket) {
                    insert(scheme);
                }
            }
        }
    }

    // ===== INSERTION =====
    public void insert(Scheme scheme) {
        if (scheme == null) return;

        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hash(scheme.getName());

        if (table[index] == null) {
            table[index] = new LinkedList<>();
        }

        // Vérifier si existe déjà
        for (Scheme s : table[index]) {
            if (s.getName().equals(scheme.getName())) {
                System.out.println("Scheme deja existant: " + scheme.getName());
                return;
            }
        }

        table[index].add(scheme);
        size++;
    }

    // ===== RECHERCHE =====
    public Scheme search(String name) {
        if (name == null) return null;

        int index = hash(name);

        if (table[index] != null) {
            for (Scheme s : table[index]) {
                if (s.getName().equals(name)) {
                    return s;
                }
            }
        }

        return null;
    }

    // ===== MODIFICATION =====
    public boolean modify(String name, String newPattern) {
        Scheme scheme = search(name);
        if (scheme != null) {
            scheme.setPattern(newPattern);
            return true;
        }
        return false;
    }

    // ===== SUPPRESSION =====
    public boolean delete(String name) {
        if (name == null) return false;

        int index = hash(name);

        if (table[index] != null) {
            for (Scheme s : table[index]) {
                if (s.getName().equals(name)) {
                    table[index].remove(s);
                    size--;
                    return true;
                }
            }
        }

        return false;
    }

    // ===== AFFICHAGE =====
    public void displayAll() {
        System.out.println("=== Schemes stockes ===");
        for (LinkedList<Scheme> bucket : table) {
            if (bucket != null) {
                for (Scheme s : bucket) {
                    System.out.println(s);
                }
            }
        }
        System.out.println("Total: " + size + " schemes");
    }

    // ===== RÉCUPÉRATION LISTE =====
    public List<Scheme> getAllSchemes() {
        List<Scheme> result = new ArrayList<>();

        for (LinkedList<Scheme> bucket : table) {
            if (bucket != null) {
                result.addAll(bucket);
            }
        }

        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}