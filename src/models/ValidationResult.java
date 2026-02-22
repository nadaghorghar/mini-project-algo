package models;
//validation d'un mot arabe

public class ValidationResult {
    private boolean isValid;
    private Root root;
    private Scheme scheme;

    public ValidationResult(boolean isValid, Root root, Scheme scheme) {
        this.isValid = isValid;
        this.root = root;
        this.scheme = scheme;
    }

    public boolean isValid() {
        return isValid;
    }

    public Root getRoot() {
        return root;
    }

    public Scheme getScheme() {
        return scheme;
    }

    @Override
    public String toString() {
        if (isValid) {
            return "Validation: OUI - Racine: " + root.getValue() +
                    ", Schème: " + scheme.getName();
        } else {
            return "Validation: NON";
        }
    }
}

