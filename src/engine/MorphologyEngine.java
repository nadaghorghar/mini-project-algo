package engine;

import models.Root;
import models.Scheme;

public class MorphologyEngine {

    // Génération simple
    public String generate(Root root, Scheme scheme) {

        char[] letters = root.extractLetters();
        if (letters.length != 3) return null;

        String pattern = scheme.getPattern();

        // Remplacer ف1 ف2 ف3 par lettres
        String word = pattern
                .replace("ف1", String.valueOf(letters[0]))
                .replace("ف2", String.valueOf(letters[1]))
                .replace("ف3", String.valueOf(letters[2]));

        root.addDerivative(word);

        return word;
    }

    // Validation morphologique simple
    public boolean validate(Root root, String word) {
        return root.hasDerivative(word);
    }
}
