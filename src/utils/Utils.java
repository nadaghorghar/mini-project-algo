package utils;

import models.Root;

/**
 * Classe utilitaire contenant des fonctions auxiliaires pour:
 * - Vérification des types de racines arabes
 * - Manipulation des lettres arabes
 * - Détermination automatique du type de racine
 * 
 * @author Étudiant 1
 * @version 1.0
 */
public class Utils {
    
    // ========== CONSTANTES - LETTRES ARABES SPÉCIALES ==========
    
    private static final char ALIF = 'ا';
    private static final char WAW = 'و';
    private static final char YA = 'ي';
    
    private static final char HAMZA = 'ء';
    private static final char HAMZA_ALIF = 'أ';
    private static final char HAMZA_ALIF_MADDA = 'آ';
    private static final char HAMZA_WAW = 'ؤ';
    private static final char HAMZA_YA = 'ئ';
    private static final char HAMZA_ALIF_BELOW = 'إ';
    
    // ========== VÉRIFICATION DES TYPES DE RACINES ==========
    
    /**
     * Vérifie si une racine est régulière (sans lettres faibles ni hamza).
     */
    public static boolean isRegularRoot(String letters) {
        if (letters == null || letters.isEmpty()) {
            return false;
        }
        return !hasHamza(letters) && !isWeakRoot(letters);
    }
    
    /**
     * Vérifie si une racine contient hamza.
     */
    public static boolean hasHamza(String letters) {
        if (letters == null) return false;
        
        for (char c : letters.toCharArray()) {
            if (c == HAMZA || c == HAMZA_ALIF || c == HAMZA_ALIF_MADDA ||
                c == HAMZA_WAW || c == HAMZA_YA || c == HAMZA_ALIF_BELOW) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vérifie si une racine est faible (contient alif, waw ou ya).
     */
    public static boolean isWeakRoot(String letters) {
        if (letters == null) return false;
        
        for (char c : letters.toCharArray()) {
            if (c == ALIF || c == WAW || c == YA) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Vérifie si une racine est assimilée (première lettre = waw).
     */
    public static boolean isAssimilatedRoot(String letters) {
        if (letters == null || letters.isEmpty()) {
            return false;
        }
        return letters.charAt(0) == WAW;
    }
    
    /**
     * Vérifie si une racine est creuse (lettre médiane = waw ou ya).
     */
    public static boolean isHollowRoot(String letters) {
        if (letters == null || letters.length() < 3) {
            return false;
        }
        char middle = letters.charAt(1);
        return middle == ALIF || middle == WAW || middle == YA;
    }
    
    /**
     * Vérifie si une racine est défective (dernière lettre = waw ou ya).
     */
    public static boolean isDefectiveRoot(String letters) {
        if (letters == null || letters.isEmpty()) {
            return false;
        }
        char last = letters.charAt(letters.length() - 1);
        return last == WAW || last == YA;
    }
    
    // ========== DÉTERMINATION AUTOMATIQUE DU TYPE ==========
    
    /**
     * Détermine automatiquement le type morphologique d'une racine.
     */
    public static String determineRootType(String letters) {
        if (hasHamza(letters)) {
            return Root.HAMZA;
        }
        if (isAssimilatedRoot(letters)) {
            return Root.ASSIMILEE;
        }
        if (isHollowRoot(letters)) {
            return Root.CREUSE;
        }
        if (isDefectiveRoot(letters)) {
            return Root.DEFECTIVE;
        }
        if (isWeakRoot(letters)) {
            return Root.FAIBLE;
        }
        return Root.REGULIERE;
    }
    
    // ========== MANIPULATION DES LETTRES ARABES ==========
    
    /**
     * Extrait les lettres individuelles d'une racine.
     */
    public static char[] extractArabicLetters(String root) {
        if (root == null) return new char[0];
        return root.toCharArray();
    }
    
    /**
     * Vérifie si un caractère est une lettre arabe valide.
     */
    public static boolean isArabicLetter(char c) {
        return c >= '\u0600' && c <= '\u06FF';
    }
    
    /**
     * Vérifie si un caractère est une lettre faible (و, ي, ا).
     */
    public static boolean isWeakLetter(char c) {
        return c == ALIF || c == WAW || c == YA;
    }
    
    /**
     * Normalise un texte arabe en retirant les diacritiques.
     */
    public static String normalizeArabicText(String text) {
        if (text == null) return null;
        return text.replaceAll("[\u064B-\u065F]", "");
    }
    
    /**
     * Retire tous les espaces d'une chaîne arabe.
     */
    public static String removeSpaces(String text) {
        if (text == null) return null;
        return text.replace(" ", "");
    }
    
    // ========== COMPARAISON ET VALIDATION ==========
    
    /**
     * Compare deux chaînes arabes lexicographiquement.
     */
    public static int compareArabicStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }
    
    /**
     * Valide qu'une chaîne est une racine trilitère valide.
     */
    public static boolean isValidTrillateralRoot(String letters) {
        if (letters == null || letters.length() != 3) {
            return false;
        }
        
        for (char c : letters.toCharArray()) {
            if (!isArabicLetter(c)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Compte le nombre de lettres faibles dans une racine.
     */
    public static int countWeakLetters(String letters) {
        if (letters == null) return 0;
        
        int count = 0;
        for (char c : letters.toCharArray()) {
            if (isWeakLetter(c)) {
                count++;
            }
        }
        return count;
    }
    
    // ========== MÉTHODES D'AFFICHAGE UTILITAIRES ==========
    
    /**
     * Affiche les informations détaillées sur une racine.
     */
    public static void displayRootInfo(String letters) {
        System.out.println("=== Analyse de la racine: " + letters + " ===");
        System.out.println("Type: " + determineRootType(letters));
        System.out.println("Régulière: " + isRegularRoot(letters));
        System.out.println("Faible: " + isWeakRoot(letters));
        System.out.println("Hamza: " + hasHamza(letters));
        System.out.println("Valide: " + isValidTrillateralRoot(letters));
    }
}