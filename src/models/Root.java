package models;

import java.util.ArrayList;

/**
 * Classe représentant une racine arabe trilitère.
 * Contient les lettres de la racine, son type morphologique,
 * et la liste des mots dérivés validés.
 * 
 * @author Étudiant 1
 * @version 1.0
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
    
    // ========== CONSTRUCTEURS ==========
    
    /**
     * Constructeur avec lettres seulement.
     * Le type est défini automatiquement comme REGULIERE.
     * 
     * @param letters Les lettres de la racine
     */
    public Root(String letters) {
        this.letters = letters;
        this.type = REGULIERE;
        this.validatedDerivatives = new ArrayList<>();
        this.frequency = 0;
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
    }
    
    // ========== GETTERS ET SETTERS ==========
    
    public String getLetters() {
        return letters;
    }
    
    public void setLetters(String letters) {
        this.letters = letters;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
}