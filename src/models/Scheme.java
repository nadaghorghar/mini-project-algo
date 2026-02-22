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


    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


    @Override
    public String toString() {
        String arabicPattern = convertToArabicNotation(pattern);
        return String.format("%s  ←  %s", name, arabicPattern);
    }

    //Convertit un pattern au format clair pour l'affichage arabe

    private String convertToArabicNotation(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return "";
        }

        String result = pattern;
        result = result.replace("C1", "ح1");
        result = result.replace("C2", "ح2");
        result = result.replace("C3", "ح3");


        return result;
    }



}