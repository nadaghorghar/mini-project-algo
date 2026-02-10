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

    @Override
    public String toString() {
        return "Scheme[" + name + " => " + pattern + "]";
    }
}
