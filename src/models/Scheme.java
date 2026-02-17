package models;

public class Scheme {

    private String name;
    private String pattern;

    public Scheme(String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * Permet de modifier le pattern d'un schème
     * @param pattern Le nouveau pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Version améliorée de toString() pour un affichage plus clair
     * Utilise ح1، ح2، ح3 pour les lettres de la racine
     */
    @Override
    public String toString() {
        String arabicPattern = convertToArabicNotation(pattern);
        return String.format("%s  ←  %s", name, arabicPattern);
    }

    /**
     * Convertit un pattern au format clair pour l'affichage arabe
     * Exemple: "C1+ا+C2+C3" → "ح1 + ا + ح2 + ح3"
     */
    private String convertToArabicNotation(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return "";
        }

        String result = pattern;

        // Remplacer les notations latines par des notations arabes
        result = result.replace("C1", "ح1");
        result = result.replace("C2", "ح2");
        result = result.replace("C3", "ح3");
        result = result.replace("ف1", "ح1");
        result = result.replace("ف2", "ح2");
        result = result.replace("ف3", "ح3");
        result = result.replace("1", "ح1");
        result = result.replace("2", "ح2");
        result = result.replace("3", "ح3");

        // Remplacer les + par des espaces ou des tirets pour plus de clarté
        result = result.replace("+", " + ");

        return result;
    }

    /**
     * Version pour affichage compact (sans espaces)
     */
    public String toCompactString() {
        String arabicPattern = pattern
                .replace("C1", "ح1")
                .replace("C2", "ح2")
                .replace("C3", "ح3")
                .replace("+", "");
        return name + " ← " + arabicPattern;
    }
}