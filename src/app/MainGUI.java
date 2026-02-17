package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.application.Platform;
import engine.MorphologyEngine;
import io.FileLoader;
import models.Root;
import models.Node;
import models.Scheme;
import models.ValidationResult;
import structures.AVLTree;
import structures.HashTableSchemes;

import java.util.List;
import java.util.Optional;
import java.util.Map;

public class MainGUI extends Application {

    private AVLTree tree;
    private HashTableSchemes schemes;
    private MorphologyEngine engine;
    private TextArea outputArea;
    private Label racinesStatLabel;
    private Label schemesStatLabel;

    private static final String PRIMARY_COLOR = "#667eea";
    private static final String SECONDARY_COLOR = "#764ba2";
    private static final String BACKGROUND_COLOR = "#f7fafc";
    private static final String CARD_COLOR = "#ffffff";

    // Dans le constructeur ou l'initialisation
    public void initializeBasicSchemes() {
        schemes.insert(new Scheme("فاعل", "C1+ا+C2+C3"));
        schemes.insert(new Scheme("مفعول", "م+C1+C2+و+C3"));
        schemes.insert(new Scheme("افتعل", "ا+C1+ت+C2+C3"));
        schemes.insert(new Scheme("تفعيل", "ت+C1+C2+ي+C3"));
        // Ajouter d'autres si besoin
    }
    @Override
    public void start(Stage primaryStage) {
        // Initialisation des structures
        tree = new AVLTree();
        schemes = new HashTableSchemes(50); // Augmenter la capacité à 50 ou plus
        engine = new MorphologyEngine();

        // Chargement des données depuis les fichiers
        FileLoader.loadRoots("data/racines.txt", tree);
        initializeBasicSchemes();

        primaryStage.setTitle("Moteur Morphologique Arabe");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        VBox header = createModernHeader();
        mainLayout.setTop(header);

        VBox centerArea = createCenterArea();
        mainLayout.setCenter(centerArea);

        Scene scene = new Scene(mainLayout, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();

        displayWelcome();
    }

    // ==================== PARTIE EN-TETE ====================

    private VBox createModernHeader() {
        VBox header = new VBox();
        header.setPrefHeight(130);
        header.setStyle(String.format(
                "-fx-background-color: linear-gradient(to right, %s 0%%, %s 100%%);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);",
                PRIMARY_COLOR, SECONDARY_COLOR
        ));

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(3, 10, 0, 10));

        Button closeBtn = new Button("✖");
        closeBtn.setStyle(
                "-fx-background-color: red;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> Platform.exit());
        topBar.getChildren().add(closeBtn);

        VBox content = new VBox(4);
        content.setPadding(new Insets(8, 10, 10, 10));
        content.setAlignment(Pos.CENTER);

        Label titleAr = new Label("محرك التصريف الصرفي العربي");
        titleAr.setFont(Font.font("Traditional Arabic", FontWeight.BOLD, 22));
        titleAr.setTextFill(Color.WHITE);

        Label titleFr = new Label("Moteur Morphologique Arabe");
        titleFr.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        titleFr.setTextFill(Color.rgb(255, 255, 255, 0.9));

        HBox statsBox = createStatsBox();
        statsBox.setScaleX(0.9);
        statsBox.setScaleY(0.9);

        content.getChildren().addAll(titleAr, titleFr, statsBox);
        header.getChildren().addAll(topBar, content);

        return header;
    }

    private HBox createStatsBox() {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10, 0, 0, 0));

        racinesStatLabel = createStatLabel("Racines: " + tree.size());
        schemesStatLabel = createStatLabel("Schèmes: " + schemes.size());

        statsBox.getChildren().addAll(racinesStatLabel, schemesStatLabel);
        return statsBox;
    }

    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(5, 15, 5, 15));
        label.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-background-radius: 15;"
        );
        return label;
    }

    private void updateStats() {
        Platform.runLater(() -> {
            racinesStatLabel.setText("Racines: " + tree.size());
            schemesStatLabel.setText("Schèmes: " + schemes.size());
        });
    }

    // ==================== PARTIE ZONE CENTRALE ====================

    private VBox createCenterArea() {
        VBox centerArea = new VBox(20);
        centerArea.setPadding(new Insets(20));

        FlowPane buttonsSection = createButtonsSection();
        VBox resultsSection = createResultsSection();
        VBox.setVgrow(resultsSection, Priority.ALWAYS);

        centerArea.getChildren().addAll(buttonsSection, resultsSection);
        return centerArea;
    }

    private FlowPane createButtonsSection() {
        FlowPane buttonsPane = new FlowPane();
        buttonsPane.setHgap(15);
        buttonsPane.setVgap(15);
        buttonsPane.setAlignment(Pos.CENTER);
        buttonsPane.setPadding(new Insets(15));
        buttonsPane.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );

        VBox btn1 = createActionCard("Generer mot", "Creer un mot a partir d'une racine", () -> showGenerateDialog());
        VBox btn2 = createActionCard("Valider mot", "Verifier l'appartenance d'un mot", () -> showValidateDialog());
        VBox btn3 = createActionCard("Famille", "Afficher tous les derives", () -> showFamilyDialog());
        VBox btn4 = createActionCard("Derives", "Mots deja valides", () -> showDerivativesDialog());
        VBox btn5 = createActionCard("Analyser mot", "Trouver racine et scheme", () -> showDecomposeDialog());
        VBox btn6 = createActionCard("Ajouter racine", "Nouvelle racine", () -> showAddRootDialog());
        VBox btn7 = createActionCard("Ajouter scheme", "Nouveau scheme", () -> showAddSchemeDialog());
        VBox btn8 = createActionCard("Modifier scheme", "Changer pattern", () -> showModifySchemeDialog());
        VBox btn9 = createActionCard("Supprimer scheme", "Retirer un scheme", () -> showDeleteSchemeDialog());
        VBox btn10 = createActionCard("Liste racines", "Voir toutes les racines", () -> showAllRoots());
        VBox btn11 = createActionCard("Liste schemes", "Voir tous les schemes", () -> showAllSchemes());

        buttonsPane.getChildren().addAll(
                btn1, btn2, btn3, btn4, btn5, btn6,
                btn7, btn8, btn9, btn10, btn11
        );

        return buttonsPane;
    }

    private VBox createActionCard(String title, String description, Runnable action) {
        VBox card = new VBox(2);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(140);
        card.setPrefHeight(85);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 5, 0, 0, 2);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.WHITE);

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        descLabel.setTextFill(Color.rgb(255, 255, 255, 0.9));
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setMaxWidth(130);

        card.getChildren().addAll(titleLabel, descLabel);

        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: " + SECONDARY_COLOR + ";" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(118, 75, 162, 0.5), 10, 0, 0, 4);" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: " + PRIMARY_COLOR + ";" +
                            "-fx-background-radius: 10;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 5, 0, 0, 2);"
            );
        });

        card.setOnMouseClicked(e -> action.run());

        return card;
    }

    private VBox createResultsSection() {
        VBox resultsBox = new VBox(12);

        Label resultsTitle = new Label("Resultats");
        resultsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        resultsTitle.setTextFill(Color.web(PRIMARY_COLOR));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Segoe UI", 14));
        outputArea.setStyle(
                "-fx-control-inner-background: " + CARD_COLOR + ";" +
                        "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );

        VBox.setVgrow(outputArea, Priority.ALWAYS);
        resultsBox.getChildren().addAll(resultsTitle, outputArea);

        return resultsBox;
    }

    private void displayResult(String text) {
        outputArea.setText(text);
        Platform.runLater(() -> outputArea.setScrollTop(Double.MAX_VALUE));
    }

    private void displayWelcome() {
        displayResult(
                "--------------------------------------------------\n" +
                        "Bienvenue dans le Moteur Morphologique Arabe\n" +
                        "--------------------------------------------------\n\n" +
                        "Statistiques du systeme:\n" +
                        "  • Racines chargees: " + tree.size() + "\n" +
                        "  • Schemes charges: " + schemes.size() + "\n\n" +
                        "Pour commencer, cliquez sur un bouton ci-dessus\n" +
                        "--------------------------------------------------\n"
        );
    }

    // ==================== METHODES D'AFFICHAGE SIMPLIFIE ====================

    private void showSimpleAnalysis(String word, ValidationResult result) {
        StringBuilder sb = new StringBuilder();

        sb.append("--------------------------------------------------\n");
        sb.append("Resultat d'analyse de mot\n");
        sb.append("--------------------------------------------------\n\n");

        sb.append("Mot: ").append(word).append("\n\n");

        if (result.isValid()) {
            Root root = result.getRoot();
            Scheme scheme = result.getScheme();

            sb.append("Racine: ").append(root.getLetters()).append("\n");
            sb.append("Type: ").append(root.getType()).append("\n\n");

            String schemeName = scheme.getName();
            String schemePattern = scheme.getPattern();

            // Nettoyer le nom du scheme pour enlever les informations techniques
            String cleanSchemeName = schemeName;
            if (schemeName.contains("⚠️") || schemeName.contains("[")) {
                cleanSchemeName = "non disponible";
            }

            boolean isAvailable = schemes.search(schemeName.replace(" (deduit)", "").replace(" ⚠️", "").replaceAll("\\[.*\\]", "").trim()) != null;

            if (isAvailable) {
                sb.append("Schema: ").append(cleanSchemeName).append("\n");
                sb.append("Pattern: ").append(convertPatternToArabic(schemePattern)).append("\n");
            } else {
                sb.append("Schema: ").append(cleanSchemeName).append("\n");
                sb.append("Pattern deduit: ").append(convertPatternToArabic(schemePattern)).append("\n");
            }
        } else {
            sb.append("Aucune analyse trouvee pour ce mot\n");
        }

        sb.append("\n--------------------------------------------------\n");

        displayResult(sb.toString());
    }

    private String convertPatternToArabic(String pattern) {
        if (pattern == null) return "";

        // Remplacer d'abord les patterns techniques
        String result = pattern
                .replace("C1", "ح1")
                .replace("C2", "ح2")
                .replace("C3", "ح3")
                .replace("ف1", "ح1")
                .replace("ف2", "ح2")
                .replace("ف3", "ح3")
                .replace("+", " + ");

        // Nettoyer les eventuels crochets residus
        result = result.replaceAll("\\[.*?\\]", "").trim();

        return result;
    }

    // ==================== DIALOGUE GENERATION DE MOT ====================

    private void showGenerateDialog() {
        Dialog<ButtonType> dialog = createStyledDialog("Generer un mot");
        dialog.setHeaderText("Entrez la racine et choisissez le scheme");

        GridPane grid = createDialogGrid();

        TextField rootField = createStyledTextField("Racine (ex: كتب)");
        ComboBox<String> schemeCombo = new ComboBox<>();
        schemeCombo.setPromptText("Choisir un scheme...");
        schemeCombo.setPrefWidth(300);

        // Charger tous les schemes de la table de hachage
        List<Scheme> allSchemes = schemes.getAllSchemes();
        for (Scheme s : allSchemes) {
            schemeCombo.getItems().add(s.getName());
        }

        Label typeLabel = new Label("");
        typeLabel.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");

        grid.add(new Label("Racine:"), 0, 0);
        grid.add(rootField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeLabel, 1, 1);
        grid.add(new Label("Scheme:"), 0, 2);
        grid.add(schemeCombo, 1, 2);

        rootField.textProperty().addListener((obs, oldVal, newVal) -> {
            String rootStr = newVal.trim();
            if (rootStr.length() == 3) {
                Node node = tree.search(rootStr);
                if (node != null) {
                    typeLabel.setText(node.getRoot().getType());
                } else {
                    typeLabel.setText("Racine non trouvee");
                }
            } else {
                typeLabel.setText("");
            }
        });

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String rootStr = rootField.getText().trim();
                String schemeName = schemeCombo.getValue();

                if (schemeName == null) {
                    showError("Veuillez selectionner un scheme");
                    return;
                }

                Node node = tree.search(rootStr);
                if (node == null) {
                    showError("Racine non trouvee: " + rootStr);
                    return;
                }

                Scheme selectedScheme = schemes.search(schemeName);
                if (selectedScheme == null) {
                    showError("Scheme non trouve: " + schemeName);
                    return;
                }

                String result = engine.generate(node.getRoot(), selectedScheme);
                node.getRoot().addDerivative(result);

                StringBuilder sb = new StringBuilder();
                sb.append("--------------------------------------------------\n");
                sb.append("Resultat de generation\n");
                sb.append("--------------------------------------------------\n\n");
                sb.append("Racine: ").append(rootStr).append("\n");
                sb.append("Type: ").append(node.getRoot().getType()).append("\n");
                sb.append("Scheme: ").append(schemeName).append("\n");
                sb.append("Pattern: ").append(convertPatternToArabic(selectedScheme.getPattern())).append("\n\n");
                sb.append("Mot genere: ").append(result).append("\n");
                sb.append("\n--------------------------------------------------\n");

                displayResult(sb.toString());
            }
        });
    }

    // ==================== DIALOGUE VALIDATION DE MOT ====================

    private void showValidateDialog() {
        Dialog<ButtonType> dialog = createStyledDialog("Validation de mot");
        dialog.setHeaderText("Verifier si un mot appartient a une racine");

        GridPane grid = createDialogGrid();

        TextField wordField = createStyledTextField("Mot (ex: كاتب)");
        TextField rootField = createStyledTextField("Racine (ex: كتب)");

        grid.add(new Label("Mot:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(new Label("Racine:"), 0, 1);
        grid.add(rootField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String word = wordField.getText().trim();
                String rootStr = rootField.getText().trim();

                Node node = tree.search(rootStr);
                if (node == null) {
                    showError("Racine non trouvee");
                    return;
                }

                ValidationResult result = engine.validate(word, node.getRoot(), schemes);

                if (result.isValid()) {
                    node.getRoot().addDerivative(word);
                    showSimpleAnalysis(word, result);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("--------------------------------------------------\n");
                    sb.append("Resultat de validation\n");
                    sb.append("--------------------------------------------------\n\n");
                    sb.append("Mot: ").append(word).append("\n");
                    sb.append("Racine: ").append(rootStr).append("\n\n");
                    sb.append("Resultat: NON - Le mot n'appartient pas a cette racine\n");
                    sb.append("\n--------------------------------------------------\n");
                    displayResult(sb.toString());
                }
            }
        });
    }

    // ==================== DIALOGUE FAMILLE MORPHOLOGIQUE ====================

    private void showFamilyDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "Famille morphologique",
                "Entrez la racine",
                "Racine:"
        );

        dialog.showAndWait().ifPresent(rootStr -> {
            Node node = tree.search(rootStr);
            if (node == null) {
                showError("Racine non trouvee");
                return;
            }

            Root root = node.getRoot();
            List<Scheme> allSchemes = schemes.getAllSchemes(); // TOUS les schemes

            StringBuilder sb = new StringBuilder();
            sb.append("--------------------------------------------------\n");
            sb.append("Famille morphologique de la racine: ").append(rootStr).append("\n");
            sb.append("Type: ").append(root.getType()).append("\n");
            sb.append("--------------------------------------------------\n\n");

            int count = 0;
            for (Scheme scheme : allSchemes) {
                String derived = engine.generate(root, scheme);
                sb.append(String.format("%-20s -> %s\n", scheme.getName(), derived));
                root.addDerivative(derived);
                count++;
            }

            sb.append("\nTotal: ").append(count).append(" mots generes\n");
            sb.append("--------------------------------------------------\n");

            displayResult(sb.toString());
        });
    }

    // ==================== DIALOGUE DERIVES VALIDES ====================

    private void showDerivativesDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "Derives valides",
                "Afficher les derives valides pour une racine",
                "Racine:"
        );

        dialog.showAndWait().ifPresent(rootStr -> {
            Node node = tree.search(rootStr);
            if (node == null) {
                showError("Racine non trouvee");
                return;
            }

            List<String> derivatives = node.getRoot().getValidatedDerivatives();
            StringBuilder sb = new StringBuilder();
            sb.append("--------------------------------------------------\n");
            sb.append("Derives valides pour la racine: ").append(rootStr).append("\n");
            sb.append("--------------------------------------------------\n\n");

            if (derivatives.isEmpty()) {
                sb.append("Aucun derive valide\n");
            } else {
                sb.append("Total: ").append(derivatives.size()).append("\n\n");
                for (int i = 0; i < derivatives.size(); i++) {
                    sb.append(String.format("%d. %s\n", i + 1, derivatives.get(i)));
                }
            }

            sb.append("\n--------------------------------------------------\n");
            displayResult(sb.toString());
        });
    }

    // ==================== DIALOGUE ANALYSE DE MOT ====================

    private void showDecomposeDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "Analyser un mot",
                "Entrez le mot a analyser",
                "Mot:"
        );

        dialog.showAndWait().ifPresent(word -> {
            if (word.trim().isEmpty()) {
                showError("Veuillez entrer un mot");
                return;
            }

            ValidationResult result = engine.decomposeWord(word, tree, schemes);

            showSimpleAnalysis(word, result);

            if (result.isValid()) {
                Node node = tree.search(result.getRoot().getLetters());
                if (node != null) {
                    node.getRoot().addDerivative(word);
                }
            }
        });
    }

    // ==================== DIALOGUE AJOUT DE RACINE ====================

    private void showAddRootDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "Ajouter une racine",
                "Entrez la nouvelle racine (3 lettres)",
                "Racine:"
        );

        dialog.showAndWait().ifPresent(rootStr -> {
            if (rootStr.length() != 3) {
                showError("La racine doit contenir exactement 3 lettres");
                return;
            }

            if (tree.search(rootStr) != null) {
                showError("Cette racine existe deja");
                return;
            }

            Root newRoot = new Root(rootStr);
            newRoot.detectType();
            tree.insert(newRoot);
            updateStats();

            StringBuilder sb = new StringBuilder();
            sb.append("--------------------------------------------------\n");
            sb.append("Racine ajoutee avec succes\n");
            sb.append("--------------------------------------------------\n\n");
            sb.append("Racine: ").append(rootStr).append("\n");
            sb.append("Type: ").append(newRoot.getType()).append("\n");
            sb.append("\nTotal racines: ").append(tree.size()).append("\n");
            sb.append("--------------------------------------------------\n");

            displayResult(sb.toString());
        });
    }

    // ==================== DIALOGUE AJOUT DE SCHEME ====================

    private void showAddSchemeDialog() {
        Dialog<ButtonType> dialog = createStyledDialog("Ajouter un scheme");
        dialog.setHeaderText("Entrez le nom du scheme (pattern genere automatiquement)");

        VBox content = new VBox(10);

        Label helpText = new Label(
                "Exemples de noms de schemes:\n" +
                        "  • فاعل\n" +
                        "  • مفعول\n" +
                        "  • تفعيل\n" +
                        "  • افتعل\n" +
                        "  • انفعل"
        );
        helpText.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-padding: 10;");
        helpText.setWrapText(true);

        GridPane grid = createDialogGrid();

        TextField nameField = createStyledTextField("Nom du scheme");

        Label patternPreview = new Label("");
        patternPreview.setStyle("-fx-text-fill: " + PRIMARY_COLOR + "; -fx-font-weight: bold;");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Pattern genere:"), 0, 1);
        grid.add(patternPreview, 1, 1);

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            String generatedPattern = generatePatternFromSchemeName(newVal.trim());
            if (generatedPattern != null && !generatedPattern.isEmpty()) {
                patternPreview.setText(convertPatternToArabic(generatedPattern));
            } else {
                patternPreview.setText("(pattern sera genere automatiquement)");
            }
        });

        content.getChildren().addAll(helpText, grid);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();

                if (name.isEmpty()) {
                    showError("Veuillez entrer le nom du scheme");
                    return;
                }

                if (schemes.search(name) != null) {
                    showError("Ce scheme existe deja");
                    return;
                }

                String generatedPattern = generatePatternFromSchemeName(name);

                if (generatedPattern == null || generatedPattern.isEmpty()) {
                    showError("Impossible de generer un pattern pour ce scheme");
                    return;
                }

                Scheme newScheme = new Scheme(name, generatedPattern);
                schemes.insert(newScheme);
                updateStats();

                StringBuilder sb = new StringBuilder();
                sb.append("--------------------------------------------------\n");
                sb.append("Scheme ajoute avec succes\n");
                sb.append("--------------------------------------------------\n\n");
                sb.append("Nom: ").append(name).append("\n");
                sb.append("Pattern genere: ").append(convertPatternToArabic(generatedPattern)).append("\n");
                sb.append("\nTotal schemes: ").append(schemes.size()).append("\n");
                sb.append("--------------------------------------------------\n");

                displayResult(sb.toString());
            }
        });
    }

    private String generatePatternFromSchemeName(String schemeName) {
        if (schemeName == null || schemeName.isEmpty()) {
            return null;
        }

        String name = schemeName.trim();

        // Enlever les diacritiques
        String normalized = name
                .replace("َ", "")
                .replace("ِ", "")
                .replace("ُ", "")
                .replace("ْ", "")
                .replace("ّ", "");

        // Remplacer les lettres racines
        String pattern = normalized
                .replace('ف', '1')
                .replace('ع', '2')
                .replace('ل', '3');

        // Convertir en notation C1, C2, C3
        pattern = pattern
                .replace("1", "C1")
                .replace("2", "C2")
                .replace("3", "C3");

        // Ajouter les separateurs
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            result.append(pattern.charAt(i));
            if (i < pattern.length() - 1) {
                // Verifier si le prochain caractere est un C (donc debut d'un C1/C2/C3)
                if (pattern.charAt(i + 1) == 'C') {
                    result.append("+");
                }
            }
        }

        return result.toString();
    }

    // ==================== DIALOGUE MODIFICATION DE SCHEME ====================

    private void showModifySchemeDialog() {
        TextInputDialog searchDialog = createStyledInputDialog(
                "Modifier un scheme",
                "Entrez le nom du scheme a modifier",
                "Nom:"
        );

        Optional<String> result = searchDialog.showAndWait();

        result.ifPresent(schemeName -> {
            Scheme scheme = schemes.search(schemeName);

            if (scheme == null) {
                showError("Scheme non trouve: " + schemeName);
                return;
            }

            Dialog<ButtonType> modifyDialog = createStyledDialog("Modifier un scheme");
            modifyDialog.setHeaderText("Modification du scheme - " + schemeName);

            GridPane grid = createDialogGrid();

            // Afficher le pattern actuel
            Label currentLabel = new Label("Pattern actuel:");
            Label currentValue = new Label(convertPatternToArabic(scheme.getPattern()));
            currentValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            currentValue.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

            // Champ pour saisir le nouveau pattern
            Label newPatternLabel = new Label("Nouveau pattern:");
            TextField newPatternField = createStyledTextField("Entrez le nouveau pattern");

            // Suggestion automatique (optionnelle)
            String suggestedPattern = generatePatternFromSchemeName(schemeName);

            // Preview du pattern en arabe
            Label previewLabel = new Label("");
            previewLabel.setStyle("-fx-text-fill: " + SECONDARY_COLOR + "; -fx-font-style: italic;");

            // Ajouter un ecouteur pour la preview
            newPatternField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.trim().isEmpty()) {
                    previewLabel.setText("Apercu: " + convertPatternToArabic(newVal));
                } else {
                    previewLabel.setText("");
                }
            });

            // Organiser dans la grille
            grid.add(new Label("Nom:"), 0, 0);
            grid.add(new Label(schemeName), 1, 0);
            grid.add(currentLabel, 0, 1);
            grid.add(currentValue, 1, 1);
            grid.add(newPatternLabel, 0, 2);
            grid.add(newPatternField, 1, 2);
            grid.add(previewLabel, 0, 3, 2, 1);

            // Suggestion si disponible
            if (suggestedPattern != null && !suggestedPattern.isEmpty()) {
                Label suggestionLabel = new Label("Suggestion: " + suggestedPattern);
                suggestionLabel.setStyle("-fx-text-fill: " + SECONDARY_COLOR + "; -fx-font-size: 11;");

                Button useSuggestionBtn = new Button("Utiliser cette suggestion");
                useSuggestionBtn.setStyle(
                        "-fx-background-color: " + SECONDARY_COLOR + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 11;" +
                                "-fx-padding: 5 10;" +
                                "-fx-background-radius: 5;" +
                                "-fx-cursor: hand;"
                );

                useSuggestionBtn.setOnAction(e -> {
                    newPatternField.setText(suggestedPattern);
                    previewLabel.setText("Apercu: " + convertPatternToArabic(suggestedPattern));
                });

                HBox suggestionBox = new HBox(10, new Label("   "), suggestionLabel, useSuggestionBtn);
                grid.add(suggestionBox, 0, 4, 2, 1);
            }

            modifyDialog.getDialogPane().setContent(grid);
            modifyDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            modifyDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String newPattern = newPatternField.getText().trim();

                    if (newPattern.isEmpty()) {
                        showError("Veuillez entrer un nouveau pattern");
                        return;
                    }

                    String oldPattern = scheme.getPattern();
                    boolean success = schemes.modify(schemeName, newPattern);
                    updateStats();

                    if (success) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("--------------------------------------------------\n");
                        sb.append("Scheme modifie avec succes\n");
                        sb.append("--------------------------------------------------\n\n");
                        sb.append("Nom: ").append(schemeName).append("\n");
                        sb.append("Ancien pattern: ").append(convertPatternToArabic(oldPattern)).append("\n");
                        sb.append("Nouveau pattern: ").append(convertPatternToArabic(newPattern)).append("\n");
                        sb.append("\n--------------------------------------------------\n");
                        displayResult(sb.toString());
                    } else {
                        showError("Echec de la modification");
                    }
                }
            });
        });
    }

    // ==================== DIALOGUE SUPPRESSION DE SCHEME ====================

    private void showDeleteSchemeDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "Supprimer un scheme",
                "Entrez le nom du scheme a supprimer",
                "Nom:"
        );

        dialog.showAndWait().ifPresent(schemeName -> {
            Scheme scheme = schemes.search(schemeName);

            if (scheme == null) {
                showError("Scheme non trouve: " + schemeName);
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Etes-vous sur ?");
            confirmAlert.setContentText(
                    "Scheme: " + schemeName + "\n" +
                            "Pattern: " + convertPatternToArabic(scheme.getPattern())
            );

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                boolean success = schemes.delete(schemeName);

                if (success) {
                    updateStats();
                    StringBuilder sb = new StringBuilder();
                    sb.append("--------------------------------------------------\n");
                    sb.append("Scheme supprime avec succes\n");
                    sb.append("--------------------------------------------------\n\n");
                    sb.append("Nom: ").append(schemeName).append("\n");
                    sb.append("Pattern: ").append(convertPatternToArabic(scheme.getPattern())).append("\n");
                    sb.append("\nSchemes restants: ").append(schemes.size()).append("\n");
                    sb.append("--------------------------------------------------\n");
                    displayResult(sb.toString());
                } else {
                    showError("Echec de la suppression");
                }
            }
        });
    }

    // ==================== AFFICHAGE LISTES ====================

    private void showAllRoots() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("Liste de toutes les racines\n");
        sb.append("--------------------------------------------------\n\n");

        List<Root> allRoots = tree.getAllRoots();

        if (allRoots.isEmpty()) {
            sb.append("Aucune racine\n");
        } else {
            for (int i = 0; i < allRoots.size(); i++) {
                Root root = allRoots.get(i);
                sb.append(String.format("%d. %s (%s) - %d derives\n",
                        i + 1, root.getLetters(), root.getType(), root.getDerivativesCount()));
            }
            sb.append("\nTotal: ").append(tree.size()).append(" racines\n");
        }

        sb.append("--------------------------------------------------\n");
        displayResult(sb.toString());
    }

    private void showAllSchemes() {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append("Liste de tous les schemes\n");
        sb.append("--------------------------------------------------\n\n");

        List<Scheme> allSchemes = schemes.getAllSchemes();

        if (allSchemes.isEmpty()) {
            sb.append("Aucun scheme\n");
        } else {
            for (int i = 0; i < allSchemes.size(); i++) {
                Scheme scheme = allSchemes.get(i);
                String arabicPattern = convertPatternToArabic(scheme.getPattern());
                sb.append(String.format("%d. %s -> %s\n", i + 1, scheme.getName(), arabicPattern));
            }
            sb.append("\nTotal: ").append(schemes.size()).append(" schemes\n");
        }

        sb.append("--------------------------------------------------\n");
        displayResult(sb.toString());
    }

    // ==================== METHODES UTILITAIRES ====================

    private Dialog<ButtonType> createStyledDialog(String title) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        return dialog;
    }

    private TextInputDialog createStyledInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog;
    }

    private GridPane createDialogGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        return grid;
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setFont(Font.font("Segoe UI", 15));
        field.setPrefWidth(300);
        return field;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}