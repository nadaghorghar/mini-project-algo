package engine;

import models.Root;
import models.Scheme;
import models.ValidationResult;
import structures.AVLTree;
import structures.HashTableSchemes;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Moteur morphologique pour la génération et validation de mots arabes
 * Avec déduction intelligente des schèmes morphologiques basée sur le type de racine
 *
 * @author Étudiant
 * @version 5.0
 */
public class MorphologyEngine {

    /**
     * Génère tous les mots dérivés possibles pour une racine donnée
     * en utilisant automatiquement tous les schèmes appropriés à son type
     */
    public Map<String, String> generateAll(Root root) {
        return root.getAvailableSchemes();
    }

    /**
     * Génère un mot dérivé à partir d'une racine et d'un NOM de schème
     * Le pattern est automatiquement récupéré selon le type de racine
     */
    public String generate(Root root, String schemeName) {
        String pattern = root.getSchemePattern(schemeName);
        if (pattern == null) {
            return "[ERREUR: Schème non disponible pour ce type de racine]";
        }

        return generateFromPattern(root, pattern);
    }

    /**
     * Génère un mot dérivé à partir d'une racine et d'un OBJET Scheme (pour compatibilité)
     * Cette méthode maintient la compatibilité avec l'ancien code
     */
    public String generate(Root root, Scheme scheme) {
        return generateFromPattern(root, scheme.getPattern());
    }

    /**
     * Génère un mot dérivé à partir d'une racine et d'un PATTERN
     * Support multiple notations: C1/C2/C3, ف1/ف2/ف3, ou 1/2/3
     */
    private String generateFromPattern(Root root, String pattern) {
        String racine = root.getValue();

        if (racine == null || racine.length() != 3) {
            return "[ERREUR: racine invalide]";
        }

        char lettre1 = racine.charAt(0);
        char lettre2 = racine.charAt(1);
        char lettre3 = racine.charAt(2);

        String result = pattern;

        // Support de: ف1/ف2/ف3 (notation traditionnelle arabe)
        result = result.replace("ف1", String.valueOf(lettre1));
        result = result.replace("ف2", String.valueOf(lettre2));
        result = result.replace("ف3", String.valueOf(lettre3));

        // Support de: C1/C2/C3 (notation claire)
        result = result.replace("C1", String.valueOf(lettre1));
        result = result.replace("C2", String.valueOf(lettre2));
        result = result.replace("C3", String.valueOf(lettre3));

        // Support des chiffres seuls
        result = result.replace("1", String.valueOf(lettre1));
        result = result.replace("2", String.valueOf(lettre2));
        result = result.replace("3", String.valueOf(lettre3));

        // Supprimer les espaces et symboles +
        result = result.replace(" ", "").replace("+", "");

        return result;
    }

    /**
     * Valide si un mot appartient à une racine donnée
     * Utilise automatiquement les schèmes appropriés au type de racine
     */
    public ValidationResult validate(String word, Root root, HashTableSchemes schemes) {
        // MÉTHODE 1: Tester avec les schèmes disponibles pour ce type de racine
        Map<String, String> availableSchemes = root.getAvailableSchemes();

        for (Map.Entry<String, String> entry : availableSchemes.entrySet()) {
            String generated = generateFromPattern(root, entry.getValue());
            if (generated.equals(word)) {
                Scheme scheme = new Scheme(entry.getKey() + " (déduit du type: " + root.getType() + ")", entry.getValue());
                return new ValidationResult(true, root, scheme);
            }
        }

        // MÉTHODE 2: Tester avec les schèmes généraux (table de hachage)
        List<Scheme> allSchemes = schemes.getAllSchemes();
        for (Scheme scheme : allSchemes) {
            String generated = generateFromPattern(root, scheme.getPattern());
            if (generated.equals(word)) {
                return new ValidationResult(true, root, scheme);
            }
        }

        // MÉTHODE 3: Déduction intelligente si aucune correspondance exacte
        if (containsRootLettersInOrder(word, root)) {
            String detectedPattern = extractPattern(word, root);
            String deducedName = deduceSchemeNameFromPattern(detectedPattern, word, root.getType());
            Scheme deducedScheme = new Scheme(deducedName, detectedPattern);
            return new ValidationResult(true, root, deducedScheme);
        }

        return new ValidationResult(false, null, null);
    }

    /**
     * Vérifie si les 3 lettres de la racine apparaissent dans l'ordre
     */
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

    /**
     * Extrait le pattern en remplaçant les lettres de la racine par C1, C2, C3
     */
    private String extractPattern(String word, Root root) {
        String racine = root.getValue();
        char l1 = racine.charAt(0);
        char l2 = racine.charAt(1);
        char l3 = racine.charAt(2);

        int pos1 = word.indexOf(l1);
        int pos2 = word.indexOf(l2, pos1 + 1);
        int pos3 = word.indexOf(l3, pos2 + 1);

        String pattern = word;

        // Marqueurs temporaires
        String marker1 = "⚊1⚊";
        String marker2 = "⚊2⚊";
        String marker3 = "⚊3⚊";

        // Remplacer dans l'ordre inverse
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

    /**
     * Déduit le nom du schème à partir du pattern détecté et du type de racine
     */
    private String deduceSchemeNameFromPattern(String pattern, String word, String rootType) {
        String clean = pattern.replace("+", "").replace(" ", "");

        // Ajouter le type de racine dans la déduction
        String typeInfo = " [" + rootType + "]";

        // Schèmes de base
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

        // Formes spéciales selon le type
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

        return "⚠️ Schème non répertorié: " + pattern + typeInfo;
    }

    /**
     * Décompose un mot pour identifier sa racine et son schème
     */
    public ValidationResult decomposeWord(String word, AVLTree tree, HashTableSchemes schemes) {
        if (word == null || word.isEmpty()) {
            return new ValidationResult(false, null, null);
        }

        List<Root> allRoots = tree.getAllRoots();

        // Parcourir toutes les racines
        for (Root root : allRoots) {
            // Tester avec les schèmes disponibles pour ce type de racine
            Map<String, String> availableSchemes = root.getAvailableSchemes();

            for (Map.Entry<String, String> entry : availableSchemes.entrySet()) {
                String generated = generateFromPattern(root, entry.getValue());
                if (generated.equals(word)) {
                    Scheme scheme = new Scheme(entry.getKey() + " (déduit)", entry.getValue());
                    return new ValidationResult(true, root, scheme);
                }
            }

            // Tester avec les schèmes généraux
            for (Scheme scheme : schemes.getAllSchemes()) {
                String generated = generateFromPattern(root, scheme.getPattern());
                if (generated.equals(word)) {
                    return new ValidationResult(true, root, scheme);
                }
            }

            // Déduction intelligente
            if (containsRootLettersInOrder(word, root)) {
                String detectedPattern = extractPattern(word, root);
                String deducedName = deduceSchemeNameFromPattern(detectedPattern, word, root.getType());
                Scheme deducedScheme = new Scheme(deducedName, detectedPattern);
                return new ValidationResult(true, root, deducedScheme);
            }
        }

        return new ValidationResult(false, null, null);
    }

    /**
     * Extrait la racine d'un mot
     */
    public Root extractRoot(String word, HashTableSchemes schemes, AVLTree tree) {
        ValidationResult result = decomposeWord(word, tree, schemes);
        return result.isValid() ? result.getRoot() : null;
    }

    /**
     * Trouve tous les schèmes possibles pour un mot donné
     */
    public List<Scheme> findPossibleSchemes(String word, Root root, HashTableSchemes schemes) {
        List<Scheme> possibleSchemes = new ArrayList<>();

        // Chercher dans les schèmes disponibles pour ce type
        Map<String, String> availableSchemes = root.getAvailableSchemes();
        for (Map.Entry<String, String> entry : availableSchemes.entrySet()) {
            String generated = generateFromPattern(root, entry.getValue());
            if (generated.equals(word)) {
                possibleSchemes.add(new Scheme(entry.getKey() + " (déduit)", entry.getValue()));
            }
        }

        // Chercher dans les schèmes généraux
        List<Scheme> allSchemes = schemes.getAllSchemes();
        for (Scheme scheme : allSchemes) {
            String generated = generateFromPattern(root, scheme.getPattern());
            if (generated.equals(word)) {
                possibleSchemes.add(scheme);
            }
        }

        return possibleSchemes;
    }
}