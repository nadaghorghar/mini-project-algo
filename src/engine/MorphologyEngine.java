package engine;

import models.Root;
import models.Scheme;
import models.ValidationResult;
import structures.AVLTree;
import structures.HashTableSchemes;
import java.util.List;

public class MorphologyEngine {

    /**
     * Génère un mot dérivé à partir d'une racine et d'un schème
     * Gère les patterns avec ف1, ف2, ف3 ET les chiffres seuls 1, 2, 3
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
     */
    public ValidationResult validate(String word, Root root, HashTableSchemes schemes) {
        List<Scheme> allSchemes = schemes.getAllSchemes();

        for (Scheme scheme : allSchemes) {
            String generated = generate(root, scheme);
            if (generated.equals(word)) {
                return new ValidationResult(true, root, scheme);
            }
        }

        return new ValidationResult(false, null, null);
    }

    /**
     * Tente d'extraire la racine d'un mot en testant tous les schèmes
     * Cette méthode nécessite une implémentation plus complexe
     */
    public Root extractRoot(String word, HashTableSchemes schemes, AVLTree tree) {
        // TODO: Implémenter l'extraction inverse de racine
        // Pour chaque racine dans l'arbre, tester tous les schèmes
        // Si un schème + racine génère exactement ce mot, retourner la racine
        return null;
    }
}