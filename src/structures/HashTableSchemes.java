package structures;

import models.Node;
import models.Scheme;

import java.util.ArrayList;
import java.util.List;

public class HashTableSchemes {

    private Scheme[] table;
    private int size;

    public HashTableSchemes(int capacity) {
        table = new Scheme[capacity];
        size = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    public void insert(Scheme scheme) {
        int index = hash(scheme.getName());

        while (table[index] != null) {
            index = (index + 1) % table.length;
        }

        table[index] = scheme;
        size++;
    }

    public Scheme search(String name) {
        int index = hash(name);

        while (table[index] != null) {
            if (table[index].getName().equals(name)) {
                return table[index];
            }
            index = (index + 1) % table.length;
        }

        return null;
    }

    public void displayAll() {
        System.out.println("=== Schèmes stockés ===");
        for (Scheme s : table) {
            if (s != null)
                System.out.println(s);
        }
    }
    public List<Scheme> getAllSchemes() {
        List<Scheme> result = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                result.add(table[i]);
            }
        }
        return result;
    }
    public int size() {
        int count = 0;
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) count++;
        }
        return count;
    }
}
