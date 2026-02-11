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

/**
 * Interface graphique améliorée et responsive pour le moteur morphologique arabe
 * Version optimisée pour tous types d'écrans
 */
public class MainGUI extends Application {

    private AVLTree tree;
    private HashTableSchemes schemes;
    private MorphologyEngine engine;
    private TextArea outputArea;

    // Constantes pour le design responsive
    private static final String PRIMARY_COLOR = "#667eea";
    private static final String SECONDARY_COLOR = "#764ba2";
    private static final String ACCENT_COLOR = "#48bb78";
    private static final String BACKGROUND_COLOR = "#f7fafc";
    private static final String CARD_COLOR = "#ffffff";

    // Tailles de police adaptatives
    private static final double TITLE_SIZE = 32;
    private static final double SUBTITLE_SIZE = 18;
    private static final double BUTTON_SIZE = 13;
    private static final double TEXT_SIZE = 14;

    @Override
    public void start(Stage primaryStage) {
        // Initialisation des structures
        tree = new AVLTree();
        schemes = new HashTableSchemes(20);
        engine = new MorphologyEngine();

        // Chargement des données
        FileLoader.loadRoots("data/racines.txt", tree);
        FileLoader.loadSchemes("data/schemes.txt", schemes);

        // Configuration de la fenêtre principale
        primaryStage.setTitle("محرك التصريف العربي - Moteur Morphologique Arabe");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Layout principal avec BorderPane pour une meilleure organisation
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // En-tête (titre)
        VBox header = createModernHeader();
        mainLayout.setTop(header);

        // Zone centrale avec boutons en haut et résultats en bas
        VBox centerArea = createCenterArea();
        mainLayout.setCenter(centerArea);

        // Créer la scène responsive
        Scene scene = new Scene(mainLayout, 1100, 750);

        // Ajuster dynamiquement la taille en fonction de l'écran
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            adjustLayoutForWidth(newVal.doubleValue(), centerArea);
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        // Message de bienvenue
        displayWelcome();
    }

    /**
     * Crée un en-tête moderne avec titre bilingue
     */
    private VBox createModernHeader() {

        VBox header = new VBox();
        header.setStyle(String.format(
                "-fx-background-color: linear-gradient(to right, %s 0%%, %s 100%%);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);",
                PRIMARY_COLOR, SECONDARY_COLOR
        ));

        // ============================
        // Barre du haut avec bouton ❌
        // ============================
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(8, 15, 0, 15));

        Button closeBtn = new Button("✖");
        closeBtn.setStyle(
                "-fx-background-color: red;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        // Effet hover
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
                "-fx-background-color: darkred;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));

        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
                "-fx-background-color: red;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        ));

        // Action fermeture
        closeBtn.setOnAction(e -> Platform.exit());

        topBar.getChildren().add(closeBtn);

        // ============================
        // Contenu principal du header
        // ============================
        VBox content = new VBox(8);
        content.setPadding(new Insets(20, 20, 25, 20));
        content.setAlignment(Pos.CENTER);

        // Titre arabe
        Label titleAr = new Label("محرك التصريف الصرفي العربي");
        titleAr.setFont(Font.font("Traditional Arabic", FontWeight.BOLD, TITLE_SIZE));
        titleAr.setTextFill(Color.WHITE);
        titleAr.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);");

        // Titre français
        Label titleFr = new Label("Moteur Morphologique Arabe");
        titleFr.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, SUBTITLE_SIZE));
        titleFr.setTextFill(Color.rgb(255, 255, 255, 0.95));

        // Badge stats
        HBox statsBox = createStatsBox();

        content.getChildren().addAll(titleAr, titleFr, statsBox);

        // ============================
        // Ajouter tout dans le header
        // ============================
        header.getChildren().addAll(topBar, content);

        return header;
    }


    /**
     * Crée un badge avec les statistiques
     */
    private HBox createStatsBox() {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10, 0, 0, 0));

        Label racinesLabel = createStatLabel("📚 Racines: " + tree.size());
        Label schemesLabel = createStatLabel("📐 Schèmes: " + schemes.size());

        statsBox.getChildren().addAll(racinesLabel, schemesLabel);
        return statsBox;
    }

    /**
     * Crée un label de statistique stylisé
     */
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

    /**
     * Crée la zone centrale avec boutons horizontaux et zone de résultats
     */
    private VBox createCenterArea() {
        VBox centerArea = new VBox(20);
        centerArea.setPadding(new Insets(20));

        // Section des boutons - disposés horizontalement avec wrap
        FlowPane buttonsSection = createButtonsSection();

        // Section des résultats
        VBox resultsSection = createResultsSection();
        VBox.setVgrow(resultsSection, Priority.ALWAYS);

        centerArea.getChildren().addAll(buttonsSection, resultsSection);
        return centerArea;
    }

    /**
     * Crée la section des boutons avec disposition flexible
     */
    private FlowPane createButtonsSection() {
        FlowPane buttonsPane = new FlowPane();
        buttonsPane.setHgap(10);
        buttonsPane.setVgap(10);
        buttonsPane.setAlignment(Pos.CENTER);
        buttonsPane.setPadding(new Insets(15));
        buttonsPane.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );

        // Boutons d'actions principales
        Button btn1 = createActionButton("🔤 Générer", "Générer mot", () -> showGenerateDialog());
        Button btn2 = createActionButton("✅ Valider", "Valider mot", () -> showValidateDialog());
        Button btn3 = createActionButton("📚 Famille", "Famille morphologique", () -> showFamilyDialog());
        Button btn4 = createActionButton("📝 Dérivés", "Afficher dérivés", () -> showDerivativesDialog());
        Button btn5 = createActionButton("🔍 Analyser", "Décomposer mot", () -> showDecomposeDialog());
        Button btn6 = createActionButton("➕ Racine", "Ajouter racine", () -> showAddRootDialog());
        Button btn7 = createActionButton("➕ Schème", "Ajouter schème", () -> showAddSchemeDialog());
        Button btn8 = createActionButton("✏️ Modifier", "Modifier schème", () -> showModifySchemeDialog());
        Button btn9 = createActionButton("🗑️ Supprimer", "Supprimer schème", () -> showDeleteSchemeDialog());
        Button btn10 = createActionButton("📖 Racines", "Voir toutes racines", () -> showAllRoots());
        Button btn11 = createActionButton("📐 Schèmes", "Voir tous schèmes", () -> showAllSchemes());

        buttonsPane.getChildren().addAll(
                btn1, btn2, btn3, btn4, btn5, btn6,
                btn7, btn8, btn9, btn10, btn11
        );

        return buttonsPane;
    }

    /**
     * Crée un bouton d'action moderne et responsive
     */
    private Button createActionButton(String text, String tooltip, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(120);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, BUTTON_SIZE));
        btn.setTooltip(new Tooltip(tooltip));

        btn.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 5, 0, 0, 2);"
        );

        // Effets de survol
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: " + SECONDARY_COLOR + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(118, 75, 162, 0.4), 8, 0, 0, 3);" +
                            "-fx-scale-x: 1.05;" +
                            "-fx-scale-y: 1.05;"
            );
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: " + PRIMARY_COLOR + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(102, 126, 234, 0.3), 5, 0, 0, 2);"
            );
        });

        btn.setOnAction(e -> action.run());
        return btn;
    }

    /**
     * Crée la section des résultats
     */
    private VBox createResultsSection() {
        VBox resultsBox = new VBox(12);

        // Titre de la section
        Label resultsTitle = new Label("📄 النتائج - Résultats");
        resultsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        resultsTitle.setTextFill(Color.web(PRIMARY_COLOR));

        // Zone de texte pour les résultats
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Segoe UI", TEXT_SIZE));
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

    /**
     * Ajuste le layout en fonction de la largeur de la fenêtre
     */
    private void adjustLayoutForWidth(double width, VBox centerArea) {
        // Logique responsive - peut être étendue selon les besoins
        if (width < 900) {
            centerArea.setSpacing(15);
        } else {
            centerArea.setSpacing(20);
        }
    }

    /**
     * Affiche le message de bienvenue
     */
    private void displayWelcome() {
        outputArea.setText(
                "═══════════════════════════════════════════════════════════\n" +
                        "       مرحباً بك في محرك التصريف الصرفي العربي\n" +
                        "    Bienvenue dans le Moteur Morphologique Arabe\n" +
                        "═══════════════════════════════════════════════════════════\n\n" +
                        "📊 Statistiques du système:\n" +
                        "   • Racines chargées: " + tree.size() + "\n" +
                        "   • Schèmes chargés: " + schemes.size() + "\n\n" +
                        "🎯 Fonctionnalités disponibles:\n" +
                        "   ✓ Génération de mots dérivés\n" +
                        "   ✓ Validation morphologique\n" +
                        "   ✓ Analyse et décomposition de mots\n" +
                        "   ✓ Gestion des racines et schèmes\n" +
                        "   ✓ Consultation des familles morphologiques\n\n" +
                        "💡 Pour commencer:\n" +
                        "   Cliquez sur un bouton ci-dessus pour effectuer une action\n" +
                        "   انقر على أحد الأزرار أعلاه لتنفيذ إجراء\n\n" +
                        "═══════════════════════════════════════════════════════════\n"
        );
    }

    // ==================== DIALOGUES ====================

    private void showGenerateDialog() {
        Dialog<ButtonType> dialog = createStyledDialog("🔤 توليد كلمة - Générer mot");
        dialog.setHeaderText("أدخل الجذر والوزن\nEntrez la racine et le schème");

        GridPane grid = createDialogGrid();

        TextField rootField = createStyledTextField("الجذر (ex: كتب)");
        TextField schemeField = createStyledTextField("الوزن (ex: فاعل)");

        grid.add(new Label("الجذر - Racine:"), 0, 0);
        grid.add(rootField, 1, 0);
        grid.add(new Label("الوزن - Schème:"), 0, 1);
        grid.add(schemeField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String rootStr = rootField.getText().trim();
                String schemeStr = schemeField.getText().trim();

                Node node = tree.search(rootStr);
                if (node == null) {
                    showError("الجذر غير موجود\nRacine non trouvée: " + rootStr);
                    return;
                }

                Scheme scheme = schemes.search(schemeStr);
                if (scheme == null) {
                    showError("الوزن غير موجود\nSchème non trouvé: " + schemeStr);
                    return;
                }

                String result = engine.generate(node.getRoot(), scheme);
                node.getRoot().addDerivative(result);

                outputArea.setText(
                        "✅ نتيجة التوليد - Résultat de génération\n\n" +
                                "═══════════════════════════════════════\n" +
                                "الجذر - Racine: " + rootStr + "\n" +
                                "الوزن - Schème: " + schemeStr + " (" + scheme.getPattern() + ")\n" +
                                "═══════════════════════════════════════\n\n" +
                                "🎯 الكلمة المولدة - Mot généré:\n\n" +
                                "      " + result + "\n\n" +
                                "═══════════════════════════════════════\n" +
                                "✓ تم إضافته إلى المشتقات المصادق عليها\n" +
                                "✓ Ajouté aux dérivés validés\n"
                );
            }
        });
    }

    private void showValidateDialog() {
        Dialog<ButtonType> dialog = createStyledDialog("✅ التحقق - Validation");
        dialog.setHeaderText("التحقق من انتماء كلمة إلى جذر\nVérifier l'appartenance d'un mot");

        GridPane grid = createDialogGrid();

        TextField wordField = createStyledTextField("الكلمة (ex: كاتب)");
        TextField rootField = createStyledTextField("الجذر (ex: كتب)");

        grid.add(new Label("الكلمة - Mot:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(new Label("الجذر - Racine:"), 0, 1);
        grid.add(rootField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String word = wordField.getText().trim();
                String rootStr = rootField.getText().trim();

                Node node = tree.search(rootStr);
                if (node == null) {
                    showError("الجذر غير موجود\nRacine non trouvée");
                    return;
                }

                ValidationResult result = engine.validate(word, node.getRoot(), schemes);

                if (result.isValid()) {
                    node.getRoot().addDerivative(word);
                    outputArea.setText(
                            "✅ نعم - OUI\n\n" +
                                    "═══════════════════════════════════════\n" +
                                    "الكلمة '" + word + "' تنتمي إلى الجذر '" + rootStr + "'\n" +
                                    "Le mot '" + word + "' appartient à la racine '" + rootStr + "'\n" +
                                    "═══════════════════════════════════════\n\n" +
                                    "الوزن المستخدم - Schème utilisé:\n" +
                                    "  • " + result.getScheme().getName() + "\n" +
                                    "  • Pattern: " + result.getScheme().getPattern() + "\n\n" +
                                    "✓ تم إضافته إلى المشتقات\n" +
                                    "✓ Ajouté aux dérivés validés\n"
                    );
                } else {
                    outputArea.setText(
                            "❌ لا - NON\n\n" +
                                    "═══════════════════════════════════════\n" +
                                    "الكلمة '" + word + "' لا تنتمي إلى الجذر '" + rootStr + "'\n" +
                                    "Le mot '" + word + "' n'appartient pas à la racine '" + rootStr + "'\n" +
                                    "═══════════════════════════════════════\n"
                    );
                }
            }
        });
    }

    private void showFamilyDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "📚 العائلة الصرفية - Famille morphologique",
                "أدخل الجذر\nEntrez la racine",
                "الجذر - Racine:"
        );

        dialog.showAndWait().ifPresent(rootStr -> {
            Node node = tree.search(rootStr);
            if (node == null) {
                showError("الجذر غير موجود\nRacine non trouvée");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════\n");
            sb.append("  العائلة الصرفية - Famille morphologique\n");
            sb.append("═══════════════════════════════════════\n");
            sb.append("الجذر - Racine: ").append(rootStr).append("\n");
            sb.append("═══════════════════════════════════════\n\n");

            List<Scheme> allSchemes = schemes.getAllSchemes();
            int count = 0;
            for (Scheme scheme : allSchemes) {
                String derived = engine.generate(node.getRoot(), scheme);
                sb.append(String.format("%-20s → %s\n", scheme.getName(), derived));
                node.getRoot().addDerivative(derived);
                count++;
            }

            sb.append("\n═══════════════════════════════════════\n");
            sb.append("✓ Total: ").append(count).append(" mots générés\n");
            sb.append("✓ جميع المشتقات تمت إضافتها\n");
            sb.append("✓ Tous les dérivés ont été ajoutés\n");

            outputArea.setText(sb.toString());
        });
    }

    private void showDerivativesDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "📝 المشتقات - Dérivés",
                "عرض المشتقات المصادق عليها\nAfficher les dérivés validés",
                "الجذر - Racine:"
        );

        dialog.showAndWait().ifPresent(rootStr -> {
            Node node = tree.search(rootStr);
            if (node == null) {
                showError("الجذر غير موجود\nRacine non trouvée");
                return;
            }

            List<String> derivatives = node.getRoot().getValidatedDerivatives();
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════\n");
            sb.append("  المشتقات المصادق عليها - Dérivés validés\n");
            sb.append("═══════════════════════════════════════\n");
            sb.append("الجذر - Racine: ").append(rootStr).append("\n");
            sb.append("═══════════════════════════════════════\n\n");

            if (derivatives.isEmpty()) {
                sb.append("(لا توجد مشتقات)\n(Aucun dérivé)\n");
            } else {
                sb.append("العدد - Total: ").append(derivatives.size()).append("\n\n");
                for (int i = 0; i < derivatives.size(); i++) {
                    sb.append(String.format("%3d. %s\n", i + 1, derivatives.get(i)));
                }
            }

            outputArea.setText(sb.toString());
        });
    }

    private void showAddRootDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "➕ إضافة جذر - Ajouter racine",
                "أدخل الجذر الجديد (3 أحرف)\nEntrez la nouvelle racine (3 lettres)",
                "الجذر - Racine:"
        );

        dialog.showAndWait().ifPresent(rootStr -> {
            if (rootStr.length() != 3) {
                showError("يجب أن يحتوي الجذر على 3 أحرف بالضبط\nLa racine doit contenir exactement 3 lettres");
                return;
            }

            if (tree.search(rootStr) != null) {
                showError("هذا الجذر موجود بالفعل\nCette racine existe déjà");
                return;
            }

            Root newRoot = new Root(rootStr);
            newRoot.detectType();
            tree.insert(newRoot);

            outputArea.setText(
                    "✅ تمت الإضافة بنجاح - Ajouté avec succès\n\n" +
                            "═══════════════════════════════════════\n" +
                            "الجذر الجديد - Nouvelle racine: " + rootStr + "\n" +
                            "النوع - Type: " + newRoot.getType() + "\n" +
                            "═══════════════════════════════════════\n\n" +
                            "📊 Statistiques mises à jour:\n" +
                            "   Total racines: " + tree.size() + "\n"
            );
        });
    }

    private void showAddSchemeDialog() {
        Dialog<ButtonType> dialog = createStyledDialog("➕ إضافة وزن - Ajouter schème");
        dialog.setHeaderText("أدخل الوزن الجديد\nEntrez le nouveau schème");

        GridPane grid = createDialogGrid();

        TextField nameField = createStyledTextField("الاسم (ex: فاعل)");
        TextField patternField = createStyledTextField("النمط (ex: ف1ا ف2 ف3)");

        grid.add(new Label("الاسم - Nom:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("النمط - Pattern:"), 0, 1);
        grid.add(patternField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();
                String pattern = patternField.getText().trim();

                if (name.isEmpty() || pattern.isEmpty()) {
                    showError("الرجاء ملء جميع الحقول\nVeuillez remplir tous les champs");
                    return;
                }

                schemes.insert(new Scheme(name, pattern));
                outputArea.setText(
                        "✅ تمت الإضافة بنجاح - Ajouté avec succès\n\n" +
                                "═══════════════════════════════════════\n" +
                                "الوزن - Schème: " + name + "\n" +
                                "النمط - Pattern: " + pattern + "\n" +
                                "═══════════════════════════════════════\n\n" +
                                "📊 Total schèmes: " + schemes.size() + "\n"
                );
            }
        });
    }

    private void showModifySchemeDialog() {
        TextInputDialog searchDialog = createStyledInputDialog(
                "✏️ تعديل وزن - Modifier schème",
                "أدخل اسم الوزن المراد تعديله\nEntrez le nom du schème à modifier",
                "الاسم - Nom:"
        );

        Optional<String> result = searchDialog.showAndWait();

        result.ifPresent(schemeName -> {
            Scheme scheme = schemes.search(schemeName);

            if (scheme == null) {
                showError("الوزن غير موجود\nSchème non trouvé: " + schemeName);
                return;
            }

            Dialog<ButtonType> modifyDialog = createStyledDialog("✏️ تعديل وزن - Modifier schème");
            modifyDialog.setHeaderText("تعديل النمط\nModifier le pattern");

            GridPane grid = createDialogGrid();

            Label currentLabel = new Label("النمط الحالي - Pattern actuel:");
            Label currentValue = new Label(scheme.getPattern());
            currentValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            currentValue.setStyle("-fx-text-fill: " + PRIMARY_COLOR + ";");

            TextField newPatternField = createStyledTextField(scheme.getPattern());

            grid.add(new Label("الاسم - Nom:"), 0, 0);
            grid.add(new Label(schemeName), 1, 0);
            grid.add(currentLabel, 0, 1);
            grid.add(currentValue, 1, 1);
            grid.add(new Label("النمط الجديد - Nouveau:"), 0, 2);
            grid.add(newPatternField, 1, 2);

            modifyDialog.getDialogPane().setContent(grid);
            modifyDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            modifyDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String newPattern = newPatternField.getText().trim();

                    if (newPattern.isEmpty()) {
                        showError("النمط لا يمكن أن يكون فارغاً\nLe pattern ne peut pas être vide");
                        return;
                    }

                    String oldPattern = scheme.getPattern();
                    boolean success = schemes.modify(schemeName, newPattern);

                    if (success) {
                        outputArea.setText(
                                "✅ تم التعديل بنجاح - Modifié avec succès\n\n" +
                                        "═══════════════════════════════════════\n" +
                                        "الوزن - Schème: " + schemeName + "\n" +
                                        "النمط القديم - Ancien: " + oldPattern + "\n" +
                                        "النمط الجديد - Nouveau: " + newPattern + "\n" +
                                        "═══════════════════════════════════════\n"
                        );
                    } else {
                        showError("فشل التعديل\nÉchec de la modification");
                    }
                }
            });
        });
    }

    private void showDeleteSchemeDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "🗑️ حذف وزن - Supprimer schème",
                "أدخل اسم الوزن المراد حذفه\nEntrez le nom du schème à supprimer",
                "الاسم - Nom:"
        );

        dialog.showAndWait().ifPresent(schemeName -> {
            Scheme scheme = schemes.search(schemeName);

            if (scheme == null) {
                showError("الوزن غير موجود\nSchème non trouvé: " + schemeName);
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("تأكيد الحذف - Confirmer");
            confirmAlert.setHeaderText("هل أنت متأكد؟\nÊtes-vous sûr ?");
            confirmAlert.setContentText(
                    "الوزن - Schème: " + schemeName + "\n" +
                            "النمط - Pattern: " + scheme.getPattern()
            );

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                boolean success = schemes.delete(schemeName);

                if (success) {
                    outputArea.setText(
                            "✅ تم الحذف بنجاح - Supprimé avec succès\n\n" +
                                    "═══════════════════════════════════════\n" +
                                    "الوزن المحذوف - Supprimé: " + schemeName + "\n" +
                                    "النمط - Pattern: " + scheme.getPattern() + "\n" +
                                    "═══════════════════════════════════════\n\n" +
                                    "📊 Schèmes restants: " + schemes.size() + "\n"
                    );
                } else {
                    showError("فشل الحذف\nÉchec de la suppression");
                }
            }
        });
    }

    private void showAllRoots() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("     جميع الجذور - Toutes les racines\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append(tree.getInOrderString());
        sb.append("\n═══════════════════════════════════════\n");
        sb.append("📊 Total: ").append(tree.size()).append(" racines\n");
        outputArea.setText(sb.toString());
    }

    private void showAllSchemes() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("     جميع الأوزان - Tous les schèmes\n");
        sb.append("═══════════════════════════════════════\n\n");

        List<Scheme> allSchemes = schemes.getAllSchemes();

        if (allSchemes.isEmpty()) {
            sb.append("(لا توجد أوزان)\n(Aucun schème)\n");
        } else {
            for (int i = 0; i < allSchemes.size(); i++) {
                Scheme scheme = allSchemes.get(i);
                sb.append(String.format("%3d. %s\n", i + 1, scheme.toString()));
            }
            sb.append("\n═══════════════════════════════════════\n");
            sb.append("📊 Total: ").append(schemes.size()).append(" schèmes\n");
        }

        outputArea.setText(sb.toString());
    }

    private void showDecomposeDialog() {
        TextInputDialog dialog = createStyledInputDialog(
                "🔍 تحليل كلمة - Décomposer un mot",
                "أدخل الكلمة للتحليل\nEntrez le mot à décomposer",
                "الكلمة - Mot:"
        );

        dialog.showAndWait().ifPresent(word -> {
            if (word.trim().isEmpty()) {
                showError("الرجاء إدخال كلمة\nVeuillez entrer un mot");
                return;
            }

            ValidationResult result = engine.decomposeWord(word, tree, schemes);

            if (result.isValid()) {
                Root foundRoot = result.getRoot();
                Scheme foundScheme = result.getScheme();

                Node node = tree.search(foundRoot.getLetters());
                if (node != null) {
                    node.getRoot().addDerivative(word);
                }

                outputArea.setText(
                        "✅ تحليل ناجح - Décomposition réussie\n\n" +
                                "═══════════════════════════════════════\n" +
                                "الكلمة المحللة - Mot analysé: " + word + "\n" +
                                "═══════════════════════════════════════\n\n" +
                                "📌 النتائج - Résultats:\n\n" +
                                "🔹 الجذر المستخرج - Racine identifiée:\n" +
                                "   • " + foundRoot.getLetters() + "\n" +
                                "   • Type: " + foundRoot.getType() + "\n\n" +
                                "🔹 الوزن المستخدم - Schème utilisé:\n" +
                                "   • Nom: " + foundScheme.getName() + "\n" +
                                "   • Pattern: " + foundScheme.getPattern() + "\n\n" +
                                "═══════════════════════════════════════\n" +
                                "✓ تم إضافته إلى المشتقات المصادق عليها\n" +
                                "✓ Ajouté aux dérivés validés\n"
                );
            } else {
                outputArea.setText(
                        "❌ فشل التحليل - Décomposition échouée\n\n" +
                                "═══════════════════════════════════════\n" +
                                "الكلمة - Mot: " + word + "\n" +
                                "═══════════════════════════════════════\n\n" +
                                "لم يتم العثور على جذر مطابق\n" +
                                "Aucune racine correspondante trouvée\n\n" +
                                "🔍 الأسباب المحتملة - Causes possibles:\n" +
                                "   • الكلمة غير مشتقة من أي جذر موجود\n" +
                                "   • Le mot n'est dérivé d'aucune racine existante\n\n" +
                                "   • الوزن المستخدم غير موجود في النظام\n" +
                                "   • Le schème utilisé n'est pas dans le système\n\n" +
                                "   • خطأ في صياغة الكلمة\n" +
                                "   • Erreur dans la forme du mot\n"
                );
            }
        });
    }

    // ==================== UTILITAIRES ====================

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
        alert.setTitle("خطأ - Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}