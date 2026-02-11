package engine;

import models.Root;
import models.Scheme;
import models.ValidationResult;
import structures.AVLTree;
import structures.HashTableSchemes;
import java.util.List;
import java.util.ArrayList;

/**
 * Moteur morphologique pour la génération et validation de mots arabes
 *
 * @author Étudiant
 * @version 2.0
 */
public class MorphologyEngine {

    /**
     * Génère un mot dérivé à partir d'une racine et d'un schème
     * Gère les patterns avec ف1, ف2, ف3 ET les chiffres seuls 1, 2, 3
     *
     * @param root La racine trilitère
     * @param scheme Le schème morphologique
     * @return Le mot dérivé généré
     */
    public String generate(Root root, Scheme scheme) {
        String racine = root.getValue();
        String pattern = scheme.getPattern();

        // Vérifier que la racine a bien 3 lettres
        if (racine == null || racine.length() != 3) {
            return "[ERREUR: racine invalide]";
        }

        // Extraire les 3 lettres de la racine
        char lettre1 = racine.charAt(0);
        char lettre2 = racine.charAt(1);
        char lettre3 = racine.charAt(2);

        String result = pattern;

        // ÉTAPE 1: Remplacer ف1, ف2, ف3
        result = result.replace("ف1", String.valueOf(lettre1));
        result = result.replace("ف2", String.valueOf(lettre2));
        result = result.replace("ف3", String.valueOf(lettre3));

        // ÉTAPE 2: Remplacer les chiffres seuls 1, 2, 3 (pour les patterns avec espaces)
        // Important: faire APRÈS l'étape 1 pour éviter les conflits
        result = result.replace("1", String.valueOf(lettre1));
        result = result.replace("2", String.valueOf(lettre2));
        result = result.replace("3", String.valueOf(lettre3));

        // ÉTAPE 3: Supprimer tous les espaces
        result = result.replace(" ", "");

        return result;
    }

    /**
     * Valide si un mot appartient à une racine donnée
     * MÉTHODE HYBRIDE:
     * 1. Teste d'abord avec les schèmes connus (rapide et précis)
     * 2. Si échec, utilise l'extraction intelligente des lettres racines
     *
     * @param word Le mot à valider
     * @param root La racine à vérifier
     * @param schemes Table de hachage des schèmes
     * @return Résultat de validation avec le schème utilisé
     */
    public ValidationResult validate(String word, Root root, HashTableSchemes schemes) {
        // MÉTHODE 1: Tester avec les schèmes connus
        List<Scheme> allSchemes = schemes.getAllSchemes();

        for (Scheme scheme : allSchemes) {
            String generated = generate(root, scheme);
            if (generated.equals(word)) {
                return new ValidationResult(true, root, scheme);
            }
        }

        // MÉTHODE 2: Extraction intelligente des lettres racines
        // Vérifier si les 3 lettres de la racine apparaissent dans l'ordre dans le mot
        if (containsRootLettersInOrder(word, root)) {
            // Créer un schème générique détecté
            String detectedPattern = extractPattern(word, root);
            Scheme genericScheme = new Scheme("مكتشف - Schème détecté", detectedPattern);
            return new ValidationResult(true, root, genericScheme);
        }

        // Aucune correspondance trouvée
        return new ValidationResult(false, null, null);
    }

    /**
     * Vérifie si les 3 lettres de la racine apparaissent dans l'ordre dans le mot
     * Permet de valider un mot même si son schème n'est pas dans la base
     *
     * @param word Le mot à analyser
     * @param root La racine de référence
     * @return true si les lettres apparaissent dans l'ordre
     */
    private boolean containsRootLettersInOrder(String word, Root root) {
        String racine = root.getValue();
        if (racine == null || racine.length() != 3 || word == null || word.isEmpty()) {
            return false;
        }

        char l1 = racine.charAt(0);
        char l2 = racine.charAt(1);
        char l3 = racine.charAt(2);

        // Chercher la première lettre
        int pos1 = word.indexOf(l1);
        if (pos1 == -1) return false;

        // Chercher la deuxième lettre après la première
        int pos2 = word.indexOf(l2, pos1 + 1);
        if (pos2 == -1) return false;

        // Chercher la troisième lettre après la deuxième
        int pos3 = word.indexOf(l3, pos2 + 1);
        return pos3 != -1;
    }

    /**
     * Extrait le pattern approximatif en identifiant les positions des lettres racines
     * Remplace les lettres de la racine par ف1, ف2, ف3 dans le mot
     *
     * @param word Le mot analysé
     * @param root La racine identifiée
     * @return Le pattern détecté (approximatif)
     */
    private String extractPattern(String word, Root root) {
        String racine = root.getValue();
        char l1 = racine.charAt(0);
        char l2 = racine.charAt(1);
        char l3 = racine.charAt(2);

        // Trouver les positions des lettres racines
        int pos1 = word.indexOf(l1);
        int pos2 = word.indexOf(l2, pos1 + 1);
        int pos3 = word.indexOf(l3, pos2 + 1);

        // Construire le pattern en remplaçant les lettres racines
        // mais en CONSERVANT les lettres intermédiaires
        String pattern = word;

        // Créer des marqueurs temporaires uniques pour éviter les conflits
        String marker1 = "⚊1⚊";
        String marker2 = "⚊2⚊";
        String marker3 = "⚊3⚊";

        // Remplacer chaque lettre racine par son marqueur
        // Important: le faire dans l'ordre inverse pour ne pas décaler les positions
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

        // Remplacer les marqueurs par les symboles finaux
        pattern = pattern.replace(marker1, "ف1");
        pattern = pattern.replace(marker2, "ف2");
        pattern = pattern.replace(marker3, "ف3");

        return pattern;
    }

    /**
     * NOUVELLE MÉTHODE: Décompose un mot pour identifier sa racine et son schème
     *
     * Cette méthode implémente la "Décomposition du mot afin d'identifier:
     * - la racine correspondante (si elle existe),
     * - le schème utilisé pour sa construction."
     *
     * @param word Le mot à décomposer
     * @param tree L'arbre AVL contenant toutes les racines
     * @param schemes Table de hachage des schèmes
     * @return ValidationResult contenant la racine et le schème, ou null si non trouvé
     */
    public ValidationResult decomposeWord(String word, AVLTree tree, HashTableSchemes schemes) {
        if (word == null || word.isEmpty()) {
            return new ValidationResult(false, null, null);
        }

        // Récupérer toutes les racines de l'arbre
        List<Root> allRoots = tree.getAllRoots();
        List<Scheme> allSchemes = schemes.getAllSchemes();

        // Parcourir toutes les racines
        for (Root root : allRoots) {
            // Pour chaque racine, tester tous les schèmes
            for (Scheme scheme : allSchemes) {
                String generated = generate(root, scheme);

                // Si le mot généré correspond exactement au mot donné
                if (generated.equals(word)) {
                    return new ValidationResult(true, root, scheme);
                }
            }
        }

        // MÉTHODE ALTERNATIVE: Si aucune correspondance exacte,
        // essayer la détection intelligente
        for (Root root : allRoots) {
            if (containsRootLettersInOrder(word, root)) {
                String detectedPattern = extractPattern(word, root);
                Scheme genericScheme = new Scheme("Schème non disponible - غير متوفر", detectedPattern);
                return new ValidationResult(true, root, genericScheme);
            }
        }

        // Aucune correspondance trouvée
        return new ValidationResult(false, null, null);
    }

    /**
     * Alias pour la méthode decomposeWord pour plus de clarté
     * Extrait la racine d'un mot en testant tous les schèmes
     *
     * @param word Le mot à analyser
     * @param schemes Table de hachage des schèmes
     * @param tree Arbre AVL des racines
     * @return La racine trouvée, ou null
     */
    public Root extractRoot(String word, HashTableSchemes schemes, AVLTree tree) {
        ValidationResult result = decomposeWord(word, tree, schemes);
        return result.isValid() ? result.getRoot() : null;
    }

    /**
     * Trouve tous les schèmes possibles pour un mot donné à partir d'une racine
     * Utile pour l'analyse morphologique avancée
     *
     * @param word Le mot à analyser
     * @param root La racine de référence
     * @param schemes Table de hachage des schèmes
     * @return Liste des schèmes qui génèrent ce mot à partir de cette racine
     */
    public List<Scheme> findPossibleSchemes(String word, Root root, HashTableSchemes schemes) {
        List<Scheme> possibleSchemes = new ArrayList<>();
        List<Scheme> allSchemes = schemes.getAllSchemes();

        for (Scheme scheme : allSchemes) {
            String generated = generate(root, scheme);
            if (generated.equals(word)) {
                possibleSchemes.add(scheme);
            }
        }

        return possibleSchemes;
    }
}