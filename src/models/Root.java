package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe représentant une racine arabe trilitère.
 * Contient les lettres de la racine, son type morphologique,
 * et la liste des mots dérivés validés.
 */
public class Root {


    public static final String REGULIERE = "REGULIERE";
    public static final String FAIBLE = "FAIBLE";
    public static final String HAMZA = "HAMZA";
    public static final String ASSIMILEE = "ASSIMILEE";
    public static final String CREUSE = "CREUSE";
    public static final String DEFECTIVE = "DEFECTIVE";


    private String letters;
    private String type;
    private ArrayList<String> validatedDerivatives;
    private Map<String, String> availableSchemes;


    public Root(String letters) {
        this.letters = letters;
        this.validatedDerivatives = new ArrayList<>();
        this.availableSchemes = new HashMap<>();
        detectType();
        initializeAvailableSchemes();
    }

    public Root(String letters, String type) {
        this.letters = letters;
        this.type = type;
        this.validatedDerivatives = new ArrayList<>();
        this.availableSchemes = new HashMap<>();
        initializeAvailableSchemes();
    }

    // ========== INITIALISATION DES SCHÈMES PAR TYPE ==========
    private void initializeAvailableSchemes() {
        availableSchemes.clear();

        availableSchemes.put("فاعل", "C1+ا+C2+C3");
        availableSchemes.put("مفعول", "م+C1+C2+و+C3");

        switch (type) {
            case REGULIERE:
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
                availableSchemes.put("فَعْل", "C1+C2+ء+C3");
                availableSchemes.put("فَعَل", "C1+C2+َ+ء+C3");
                availableSchemes.put("أَفْعَل", "أ+C1+C2+ء+C3");
                availableSchemes.put("تَفْعِيل", "ت+C1+C2+ي+ء+C3");
                availableSchemes.put("مُفَعِّل", "م+C1+C2+C2+ء+C3");
                break;

            case ASSIMILEE:
                availableSchemes.put("فَعْل", "و+C2+C3");
                availableSchemes.put("فَعَل", "و+C2+َ+C3");
                availableSchemes.put("فَعِل", "و+C2+ِ+C3");
                availableSchemes.put("فَعُل", "و+C2+ُ+C3");
                availableSchemes.put("أَفْعَل", "أ+و+C2+C3");
                availableSchemes.put("تَفْعِيل", "ت+و+C2+ي+C3");
                availableSchemes.put("مُفَعِّل", "م+و+C2+C2+C3");
                break;

            case CREUSE:
                availableSchemes.put("فَعْل", "C1+ا+C3");
                availableSchemes.put("فَعَل", "C1+ا+C3");
                availableSchemes.put("فَعِل", "C1+ا+C3");
                availableSchemes.put("فَعُل", "C1+ا+C3");
                availableSchemes.put("أَفْعَل", "أ+C1+ا+C3");
                availableSchemes.put("تَفْعِيل", "ت+C1+ي+C3");
                availableSchemes.put("مُفَعِّل", "م+C1+ي+C2+C3");
                break;

            case DEFECTIVE:
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

    public void detectType() {
        if (letters == null || letters.length() != 3) {
            this.type = REGULIERE;
            return;
        }

        char l1 = letters.charAt(0);
        char l2 = letters.charAt(1);
        char l3 = letters.charAt(2);

        boolean hasWaw = (l1 == 'و' || l2 == 'و' || l3 == 'و');
        boolean hasYa = (l1 == 'ي' || l2 == 'ي' || l3 == 'ي');
        boolean hasAlif = (l1 == 'ا' || l2 == 'ا' || l3 == 'ا');
        boolean hasHamza = (l1 == 'ء' || l2 == 'ء' || l3 == 'ء' ||
                l1 == 'أ' || l2 == 'أ' || l3 == 'أ' ||
                l1 == 'إ' || l2 == 'إ' || l3 == 'إ' ||
                l1 == 'ؤ' || l2 == 'ؤ' || l3 == 'ؤ' ||
                l1 == 'ئ' || l2 == 'ئ' || l3 == 'ئ');

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



    public Map<String, String> getAvailableSchemes() {
        return availableSchemes;
    }

    // ========== MÉTHODES DE GESTION DES SCHÈMES ==========
    public String getSchemePattern(String schemeName) {
        return availableSchemes.get(schemeName);
    }



    // gestion derivée
    public boolean addDerivative(String word) {
        if (hasDerivative(word)) {
            return false;
        }
        validatedDerivatives.add(word);
        return true;
    }

    public boolean hasDerivative(String word) {
        return validatedDerivatives.contains(word);
    }

    public int getDerivativesCount() {
        return validatedDerivatives.size();
    }

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




    public int compareTo(Root other) {
        return this.letters.compareTo(other.getLetters());
    }



    @Override
    public String toString() {
        return "Racine: " + letters + " (" + type + ") - " + getDerivativesCount() + " dérivés";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Root root = (Root) obj;
        return letters.equals(root.letters);
    }

    @Override
    public int hashCode() {
        return letters.hashCode();
    }

    public String getValue() {
        return letters;
    }
}