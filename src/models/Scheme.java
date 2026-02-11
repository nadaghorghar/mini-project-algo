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

    @Override
    public String toString() {
        return "Scheme[" + name + " => " + pattern + "]";
    }
}