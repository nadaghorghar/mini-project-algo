package structures;

import models.Scheme;

import java.util.ArrayList;
import java.util.List;

public class HashTableSchemes {

    private Scheme[] table;
    private boolean[] deleted;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    public HashTableSchemes(int capacity) {
        this.capacity = capacity;
        table = new Scheme[capacity];
        deleted = new boolean[capacity];
        size = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    private void resize() {
        int newCapacity = capacity * 2;
        Scheme[] oldTable = table;
        boolean[] oldDeleted = deleted;

        table = new Scheme[newCapacity];
        deleted = new boolean[newCapacity];
        capacity = newCapacity;
        size = 0;

        // Rehacher tous les anciens elements
        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null && !oldDeleted[i]) {
                insert(oldTable[i]);
            }
        }
    }

    public void insert(Scheme scheme) {
        if (scheme == null) return;

        // Verifier si le scheme existe deja
        if (search(scheme.getName()) != null) {
            System.out.println("Scheme deja existant: " + scheme.getName());
            return;
        }

        // Verifier si besoin de redimensionner
        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hash(scheme.getName());
        int originalIndex = index;

        while (table[index] != null && !deleted[index]) {
            index = (index + 1) % capacity;
            if (index == originalIndex) {
                // Table pleine, on redimensionne et on reessaye
                resize();
                insert(scheme);
                return;
            }
        }

        table[index] = scheme;
        deleted[index] = false;
        size++;
    }

    public Scheme search(String name) {
        if (name == null) return null;

        int index = hash(name);
        int originalIndex = index;

        while (table[index] != null || deleted[index]) {
            if (table[index] != null && !deleted[index] && table[index].getName().equals(name)) {
                return table[index];
            }
            index = (index + 1) % capacity;

            if (index == originalIndex) {
                break;
            }
        }

        return null;
    }

    public boolean modify(String name, String newPattern) {
        if (name == null || newPattern == null) return false;

        Scheme scheme = search(name);
        if (scheme != null) {
            scheme.setPattern(newPattern);
            return true;
        }
        return false;
    }

    public boolean delete(String name) {
        if (name == null) return false;

        int index = hash(name);
        int originalIndex = index;

        while (table[index] != null || deleted[index]) {
            if (table[index] != null && !deleted[index] && table[index].getName().equals(name)) {
                deleted[index] = true;
                table[index] = null;
                size--;
                return true;
            }
            index = (index + 1) % capacity;

            if (index == originalIndex) {
                break;
            }
        }

        return false;
    }

    public void displayAll() {
        System.out.println("=== Schemes stockes ===");
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && !deleted[i]) {
                System.out.println(table[i]);
            }
        }
        System.out.println("Total: " + size + " schemes");
    }

    public List<Scheme> getAllSchemes() {
        List<Scheme> result = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && !deleted[i]) {
                result.add(table[i]);
            }
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public double loadFactor() {
        return (double) size / capacity;
    }
}