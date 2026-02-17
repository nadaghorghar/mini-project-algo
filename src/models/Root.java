package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant une racine arabe trilitère.
 * Contient les lettres de la racine, son type morphologique,
 * et la liste des mots dérivés validés.
 *
 * @author Étudiant 1
 * @version 2.0
 */
public class Root {

    // ========== CONSTANTES - Types de racines ==========
    public static final String REGULIERE = "REGULIERE";    // Racine régulière (كتب)
    public static final String FAIBLE = "FAIBLE";          // Racine faible (قال, باع)
    public static final String HAMZA = "HAMZA";            // Contient hamza (أخذ, سأل)
    public static final String ASSIMILEE = "ASSIMILEE";    // Première lettre واو (وعد)
    public static final String CREUSE = "CREUSE";          // Lettre médiane واو/ياء (قال)
    public static final String DEFECTIVE = "DEFECTIVE";    // Dernière lettre واو/ياء (رمى)

    // ========== ATTRIBUTS ==========

    /**
     * Les lettres de la racine arabe (ex: "كتب")
     */
    private String letters;

    /**
     * Type morphologique de la racine
     */
    private String type;

    /**
     * Liste des mots dérivés validés pour cette racine
     */
    private ArrayList<String> validatedDerivatives;

    /**
     * Fréquence d'apparition (optionnel)
     */
    private int frequency;

    /**
     * Map des schèmes disponibles pour ce type de racine
     */
    private Map<String, String> availableSchemes;

    // ========== CONSTRUCTEURS ==========

    /**
     * Constructeur avec lettres seulement.
     * Le type est détecté automatiquement.
     *
     * @param letters Les lettres de la racine
     */
    public Root(String letters) {
        this.letters = letters;
        this.validatedDerivatives = new ArrayList<>();
        this.frequency = 0;
        this.availableSchemes = new HashMap<>();
        detectType();
        initializeAvailableSchemes();
    }

    /**
     * Constructeur complet avec type spécifié.
     *
     * @param letters Les lettres de la racine
     * @param type Le type morphologique (REGULIERE, FAIBLE, etc.)
     */
    public Root(String letters, String type) {
        this.letters = letters;
        this.type = type;
        this.validatedDerivatives = new ArrayList<>();
        this.frequency = 0;
        this.availableSchemes = new HashMap<>();
        initializeAvailableSchemes();
    }

    // ========== INITIALISATION DES SCHÈMES PAR TYPE ==========

    /**
     * Initialise les schèmes disponibles selon le type de racine
     */
    private void initializeAvailableSchemes() {
        availableSchemes.clear();

        // Schèmes de base communs à tous les types
        availableSchemes.put("فاعل", "C1+ا+C2+C3");
        availableSchemes.put("مفعول", "م+C1+C2+و+C3");

        // Schèmes spécifiques selon le type de racine
        switch (type) {
            case REGULIERE:
                // Racine régulière (ex: كتب) - tous les schèmes standards
                availableSchemes.put("فَعْل", "C1+C2+C3");
                availableSchemes.put("فَعَل", "C1+C2+َ+C3");
                availableSchemes.put("فَعِل", "C1+C2+ِ+C3");
                availableSchemes.put("فَعُل", "C1+C2+ُ+C3");
                availableSchemes.put("أَفْعَل", "أ+C1+C2+C3");
                availableSchemes.put("تَفْعِيل", "ت+C1+C2+ي+C3");
                availableSchemes.put("مُفَعِّل", "م+C1+C2+C2+C3");
                availableSchemes.put("مَفْعَل", "م+C1+C2+C3");
                availableSchemes.put("فَعَّال", "C1+C2+C2+ا+C3");
                availableSchemes.put("فَعِيل", "C1+C2+ي+C3");
                availableSchemes.put("فَعُول", "C1+C2+و+C3");
                availableSchemes.put("اِفْتِعَال", "ا+C1+ت+C2+ا+C3");
                availableSchemes.put("اِنْفِعَال", "ا+ن+C1+ي+ا+C2+C3");
                break;

            case FAIBLE:
                // Racine faible - adaptations pour les lettres faibles
                availableSchemes.put("فَعْل", "C1+ا+C2+C3");
                availableSchemes.put("فَعَل", "C1+ا+C2+َ+C3");
                availableSchemes.put("فَعِل", "C1+ا+C2+ِ+C3");
                availableSchemes.put("فَعُل", "C1+ا+C2+ُ+C3");
                availableSchemes.put("أَفْعَل", "أ+C1+ا+C2+C3");
                availableSchemes.put("تَفْعِيل", "ت+C1+ا+C2+ي+C3");
                availableSchemes.put("مُفَعِّل", "م+C1+ا+C2+C2+C3");
                availableSchemes.put("مَفْعَل", "م+C1+ا+C2+C3");
                break;

            case HAMZA:
                // Racine avec hamza - gestion spéciale du hamza
                availableSchemes.put("فَعْل", "C1+C2+ء+C3");
                availableSchemes.put("فَعَل", "C1+C2+َ+ء+C3");
                availableSchemes.put("أَفْعَل", "أ+C1+C2+ء+C3");
                availableSchemes.put("تَفْعِيل", "ت+C1+C2+ي+ء+C3");
                availableSchemes.put("مُفَعِّل", "م+C1+C2+C2+ء+C3");
                break;

            case ASSIMILEE:
                // Première lettre واو - transformations spéciales
                availableSchemes.put("فَعْل", "و+C2+C3");
                availableSchemes.put("فَعَل", "و+C2+َ+C3");
                availableSchemes.put("فَعِل", "و+C2+ِ+C3");
                availableSchemes.put("فَعُل", "و+C2+ُ+C3");
                availableSchemes.put("أَفْعَل", "أ+و+C2+C3");
                availableSchemes.put("تَفْعِيل", "ت+و+C2+ي+C3");
                availableSchemes.put("مُفَعِّل", "م+و+C2+C2+C3");
                break;

            case CREUSE:
                // Lettre médiane واو/ياء - transformations spéciales
                availableSchemes.put("فَعْل", "C1+ا+C3");
                availableSchemes.put("فَعَل", "C1+ا+C3");
                availableSchemes.put("فَعِل", "C1+ا+C3");
                availableSchemes.put("فَعُل", "C1+ا+C3");
                availableSchemes.put("أَفْعَل", "أ+C1+ا+C3");
                availableSchemes.put("تَفْعِيل", "ت+C1+ي+C3");
                availableSchemes.put("مُفَعِّل", "م+C1+ي+C2+C3");
                break;

            case DEFECTIVE:
                // Dernière lettre واو/ياء - transformations spéciales
                availableSchemes.put("فَعْل", "C1+C2+ى");
                availableSchemes.put("فَعَل", "C1+C2+َ+ى");
                availableSchemes.put("فَعِل", "C1+C2+ِ+ى");
                availableSchemes.put("فَعُل", "C1+C2+ُ+و");
                availableSchemes.put("أَفْعَل", "أ+C1+C2+ى");
                availableSchemes.put("تَفْعِيل", "ت+C1+C2+ي+ة");
                availableSchemes.put("مُفَعِّل", "م+C1+C2+C2+ى");
                break;
        }
    }

    /**
     * Détecte automatiquement le type de la racine
     */
    public void detectType() {
        if (letters == null || letters.length() != 3) {
            this.type = REGULIERE;
            return;
        }

        char l1 = letters.charAt(0);
        char l2 = letters.charAt(1);
        char l3 = letters.charAt(2);

        // Lettres faibles
        boolean hasWaw = (l1 == 'و' || l2 == 'و' || l3 == 'و');
        boolean hasYa = (l1 == 'ي' || l2 == 'ي' || l3 == 'ي');
        boolean hasAlif = (l1 == 'ا' || l2 == 'ا' || l3 == 'ا');

        // Hamza
        boolean hasHamza = (l1 == 'ء' || l2 == 'ء' || l3 == 'ء' ||
                l1 == 'أ' || l2 == 'أ' || l3 == 'أ' ||
                l1 == 'إ' || l2 == 'إ' || l3 == 'إ' ||
                l1 == 'ؤ' || l2 == 'ؤ' || l3 == 'ؤ' ||
                l1 == 'ئ' || l2 == 'ئ' || l3 == 'ئ');

        // Classification
        if (hasHamza) {
            this.type = HAMZA;
        } else if (l1 == 'و') {
            this.type = ASSIMILEE;
        } else if (l2 == 'و' || l2 == 'ي' || l2 == 'ا') {
            this.type = CREUSE;
        } else if (l3 == 'و' || l3 == 'ي' || l3 == 'ى') {
            this.type = DEFECTIVE;
        } else if (hasWaw || hasYa || hasAlif) {
            this.type = FAIBLE;
        } else {
            this.type = REGULIERE;
        }
    }

    // ========== GETTERS ET SETTERS ==========

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
        detectType();
        initializeAvailableSchemes();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        initializeAvailableSchemes();
    }

    public ArrayList<String> getValidatedDerivatives() {
        return validatedDerivatives;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Map<String, String> getAvailableSchemes() {
        return availableSchemes;
    }

    // ========== MÉTHODES DE GESTION DES SCHÈMES ==========

    /**
     * Récupère le pattern pour un nom de schème donné
     * @param schemeName Le nom du schème
     * @return Le pattern correspondant ou null si non trouvé
     */
    public String getSchemePattern(String schemeName) {
        return availableSchemes.get(schemeName);
    }

    /**
     * Récupère tous les noms de schèmes disponibles
     * @return Liste des noms de schèmes
     */
    public ArrayList<String> getAvailableSchemeNames() {
        return new ArrayList<>(availableSchemes.keySet());
    }

    /**
     * Récupère tous les patterns disponibles
     * @return Liste des patterns
     */
    public ArrayList<String> getAvailableSchemePatterns() {
        return new ArrayList<>(availableSchemes.values());
    }

    // ========== MÉTHODES DE GESTION DES DÉRIVÉS ==========

    /**
     * Ajoute un mot dérivé à la liste des dérivés validés.
     * Vérifie d'abord que le mot n'existe pas déjà.
     *
     * @param word Le mot dérivé à ajouter
     * @return true si ajouté, false si déjà présent
     */
    public boolean addDerivative(String word) {
        if (hasDerivative(word)) {
            return false;
        }
        validatedDerivatives.add(word);
        return true;
    }

    /**
     * Vérifie si un mot est dans les dérivés validés.
     *
     * @param word Le mot à vérifier
     * @return true si le mot est présent, false sinon
     */
    public boolean hasDerivative(String word) {
        return validatedDerivatives.contains(word);
    }

    /**
     * Obtient le nombre de dérivés validés.
     *
     * @return Le nombre de dérivés
     */
    public int getDerivativesCount() {
        return validatedDerivatives.size();
    }

    /**
     * Affiche tous les dérivés validés.
     */
    public void displayDerivatives() {
        System.out.println("Dérivés de " + letters + " (" + getDerivativesCount() + "):");
        if (validatedDerivatives.isEmpty()) {
            System.out.println("  (Aucun dérivé)");
        } else {
            for (String derivative : validatedDerivatives) {
                System.out.println("  - " + derivative);
            }
        }
    }

    /**
     * Supprime un dérivé de la liste.
     *
     * @param word Le mot à supprimer
     * @return true si supprimé, false si non trouvé
     */
    public boolean removeDerivative(String word) {
        return validatedDerivatives.remove(word);
    }

    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Extrait les 3 lettres racines individuellement.
     *
     * @return Tableau de 3 caractères
     */
    public char[] extractLetters() {
        if (letters == null || letters.length() != 3) {
            return new char[0];
        }
        return letters.toCharArray();
    }

    /**
     * Compare cette racine avec une autre (pour l'AVL).
     * Utilise la comparaison lexicographique Unicode des lettres.
     *
     * @param other L'autre racine à comparer
     * @return valeur négative si this < other,
     *         0 si égal,
     *         positive si this > other
     */
    public int compareTo(Root other) {
        return this.letters.compareTo(other.getLetters());
    }

    /**
     * Vérifie si cette racine est régulière.
     *
     * @return true si régulière, false sinon
     */
    public boolean isRegular() {
        return type.equals(REGULIERE);
    }

    /**
     * Retourne une représentation textuelle de la racine.
     *
     * @return Chaîne de caractères formatée
     */
    @Override
    public String toString() {
        return "Racine: " + letters + " (" + type + ") - " + getDerivativesCount() + " dérivés";
    }

    /**
     * Vérifie l'égalité entre deux racines.
     * Deux racines sont égales si elles ont les mêmes lettres.
     *
     * @param obj L'objet à comparer
     * @return true si égal, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Root root = (Root) obj;
        return letters.equals(root.letters);
    }

    /**
     * Calcule le code de hachage de la racine.
     *
     * @return Code de hachage basé sur les lettres
     */
    @Override
    public int hashCode() {
        return letters.hashCode();
    }

    public String getValue() {
        return letters;
    }
}