package engine;

import models.Root;
import models.Scheme;
import models.ValidationResult;
import structures.AVLTree;
import structures.HashTableSchemes;
import java.util.List;
import java.util.Map;

/**
 * Moteur morphologique pour la génération et validation de mots arabes
 * Avec déduction intelligente des schèmes morphologiques basée sur le type de racine
 */
public class MorphologyEngine {
    public String generate(Root root, Scheme scheme) {
        return generateFromPattern(root, scheme.getPattern());
    }

    // ========== GÉNÉRATION À PARTIR D'UN PATTERN ==========
    // Remplace les marqueurs (C1, C2, C3) par les lettres réelles de la racine

    private String generateFromPattern(Root root, String pattern) {
        String racine = root.getValue();

        if (racine == null || racine.length() != 3) {
            return "[ERREUR: racine invalide]";
        }
        char lettre1 = racine.charAt(0);
        char lettre2 = racine.charAt(1);
        char lettre3 = racine.charAt(2);
        String result = pattern;
        result = result.replace("C1", String.valueOf(lettre1));
        result = result.replace("C2", String.valueOf(lettre2));
        result = result.replace("C3", String.valueOf(lettre3));
        result = result.replace(" ", "").replace("+", "");

        return result;
    }


    // Vérifier si un mot correspond à une racine

    public ValidationResult validate(String word, Root root, HashTableSchemes schemes) {
        Map<String, String> availableSchemes = root.getAvailableSchemes();

        for (Map.Entry<String, String> entry : availableSchemes.entrySet()) {
            String generated = generateFromPattern(root, entry.getValue());
            if (generated.equals(word)) {
                Scheme scheme = new Scheme(entry.getKey() + " (déduit du type: " + root.getType() + ")", entry.getValue());
                return new ValidationResult(true, root, scheme);
            }
        }

        List<Scheme> allSchemes = schemes.getAllSchemes();
        for (Scheme scheme : allSchemes) {
            String generated = generateFromPattern(root, scheme.getPattern());
            if (generated.equals(word)) {
                return new ValidationResult(true, root, scheme);
            }
        }

        if (containsRootLettersInOrder(word, root)) {
            String detectedPattern = extractPattern(word, root);
            String deducedName = deduceSchemeNameFromPattern(detectedPattern, word, root.getType());
            Scheme deducedScheme = new Scheme(deducedName, detectedPattern);
            return new ValidationResult(true, root, deducedScheme);
        }

        return new ValidationResult(false, null, null);
    }


    // Vérifie si les 3 lettres apparaissent dans l'ordre dans le mot
    private boolean containsRootLettersInOrder(String word, Root root) {
        String racine = root.getValue();
        if (racine == null || racine.length() != 3 || word == null || word.isEmpty()) {
            return false;
        }

        char l1 = racine.charAt(0);
        char l2 = racine.charAt(1);
        char l3 = racine.charAt(2);

        int pos1 = word.indexOf(l1);
        if (pos1 == -1) return false;

        int pos2 = word.indexOf(l2, pos1 + 1);
        if (pos2 == -1) return false;

        int pos3 = word.indexOf(l3, pos2 + 1);
        return pos3 != -1;
    }

    // Extrait le schème en remplaçant les lettres de la racine par des marqueurs
    private String extractPattern(String word, Root root) {
        String racine = root.getValue();
        char l1 = racine.charAt(0);
        char l2 = racine.charAt(1);
        char l3 = racine.charAt(2);

        int pos1 = word.indexOf(l1);
        int pos2 = word.indexOf(l2, pos1 + 1);
        int pos3 = word.indexOf(l3, pos2 + 1);

        String pattern = word;

        String marker1 = "⚊1⚊";
        String marker2 = "⚊2⚊";
        String marker3 = "⚊3⚊";

        if (pos3 >= 0 && pos3 < pattern.length()) {
            pattern = pattern.substring(0, pos3) + marker3 +
                    (pos3 + 1 < pattern.length() ? pattern.substring(pos3 + 1) : "");
        }
        if (pos2 >= 0 && pos2 < pattern.length()) {
            pattern = pattern.substring(0, pos2) + marker2 +
                    (pos2 + 1 < pattern.length() ? pattern.substring(pos2 + 1) : "");
        }
        if (pos1 >= 0 && pos1 < pattern.length()) {
            pattern = pattern.substring(0, pos1) + marker1 +
                    (pos1 + 1 < pattern.length() ? pattern.substring(pos1 + 1) : "");
        }

        pattern = pattern.replace(marker1, "C1");
        pattern = pattern.replace(marker2, "C2");
        pattern = pattern.replace(marker3, "C3");

        return pattern;
    }


    // Trouve le nom du schème à partir du pattern détecté
    private String deduceSchemeNameFromPattern(String pattern, String word, String rootType) {
        String clean = pattern.replace("+", "").replace(" ", "");

        String typeInfo = " [" + rootType + "]";

        if (clean.matches("C1[اأ]C2C3")) {
            return "فاعل" + typeInfo;
        }
        if (clean.matches("مC1C2[وۏ]C3")) {
            return "مفعول" + typeInfo;
        }
        if (clean.equals("مC1C2C3")) {
            return "مَفْعَل" + typeInfo;
        }
        if (clean.matches("C1C2C2[اأ]C3")) {
            return "فَعَّال" + typeInfo;
        }
        if (clean.matches("تC1C2[يی]C3")) {
            return "تَفْعِيل" + typeInfo;
        }

        switch (rootType) {
            case "ASSIMILEE":
                if (clean.matches("وC2C3")) return "فَعْل (assimilée)" + typeInfo;
                if (clean.matches("تC1C2[يی]C3")) return "تَفْعِيل (assimilée)" + typeInfo;
                break;
            case "CREUSE":
                if (clean.matches("C1اC3")) return "فَعْل (creuse)" + typeInfo;
                if (clean.matches("تC1يC3")) return "تَفْعِيل (creuse)" + typeInfo;
                break;
            case "DEFECTIVE":
                if (clean.matches("C1C2ى")) return "فَعْل (défective)" + typeInfo;
                if (clean.matches("C1C2ي")) return "فَعِيل (défective)" + typeInfo;
                break;
            case "HAMZA":
                if (clean.matches("C1C2ءC3")) return "فَعْل (hamza)" + typeInfo;
                if (clean.matches("أC1C2ءC3")) return "أَفْعَل (hamza)" + typeInfo;
                break;
        }

        return " Schème non répertorié: " + pattern + typeInfo;
    }


    // Analyse complète d'un mot pour trouver sa racine et son schème
    public ValidationResult decomposeWord(String word, AVLTree tree, HashTableSchemes schemes) {
        if (word == null || word.isEmpty()) {
            return new ValidationResult(false, null, null);
        }

        List<Root> allRoots = tree.getAllRoots();

        for (Root root : allRoots) {
            Map<String, String> availableSchemes = root.getAvailableSchemes();

            for (Map.Entry<String, String> entry : availableSchemes.entrySet()) {
                String generated = generateFromPattern(root, entry.getValue());
                if (generated.equals(word)) {
                    Scheme scheme = new Scheme(entry.getKey() + " (déduit)", entry.getValue());
                    return new ValidationResult(true, root, scheme);
                }
            }

            for (Scheme scheme : schemes.getAllSchemes()) {
                String generated = generateFromPattern(root, scheme.getPattern());
                if (generated.equals(word)) {
                    return new ValidationResult(true, root, scheme);
                }
            }

            if (containsRootLettersInOrder(word, root)) {
                String detectedPattern = extractPattern(word, root);
                String deducedName = deduceSchemeNameFromPattern(detectedPattern, word, root.getType());
                Scheme deducedScheme = new Scheme(deducedName, detectedPattern);
                return new ValidationResult(true, root, deducedScheme);
            }
        }

        return new ValidationResult(false, null, null);
    }
}