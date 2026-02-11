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

public class MainGUI extends Application {

    private AVLTree tree;
    private HashTableSchemes schemes;
    private MorphologyEngine engine;

    private TextArea outputArea;

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

        // Layout principal
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Titre en haut
        VBox topBox = createHeader();
        mainLayout.setTop(topBox);

        // Menu à gauche
        VBox menuBox = createMenu();
        mainLayout.setLeft(menuBox);

        // Zone d'affichage au centre
        VBox centerBox = createCenterArea();
        mainLayout.setCenter(centerBox);

        // Créer la scène
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Message de bienvenue
        displayWelcome();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%); -fx-background-radius: 10;");

        Label titleAr = new Label("محرك التصريف الصرفي العربي");
        titleAr.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleAr.setTextFill(Color.WHITE);

        Label titleFr = new Label("Moteur Morphologique Arabe");
        titleFr.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        titleFr.setTextFill(Color.rgb(255, 255, 255, 0.9));

        header.getChildren().addAll(titleAr, titleFr);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(15));
        menu.setPrefWidth(280);
        menu.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        Label menuTitle = new Label("📋 القائمة - Menu");
        menuTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        menuTitle.setStyle("-fx-text-fill: #667eea;");

        Separator sep = new Separator();

        Button btn1 = createMenuButton("🔤 توليد كلمة\nGénérer mot dérivé", () -> showGenerateDialog());
        Button btn2 = createMenuButton("✅ التحقق من كلمة\nValider un mot", () -> showValidateDialog());
        Button btn3 = createMenuButton("📚 العائلة الصرفية\nFamille morphologique", () -> showFamilyDialog());
        Button btn4 = createMenuButton("📝 عرض المشتقات\nAfficher dérivés", () -> showDerivativesDialog());
        Button btn5 = createMenuButton("➕ إضافة جذر\nAjouter racine", () -> showAddRootDialog());
        Button btn6 = createMenuButton("➕ إضافة وزن\nAjouter schème", () -> showAddSchemeDialog());
        Button btn7 = createMenuButton("✏️ تعديل وزن\nModifier schème", () -> showModifySchemeDialog());
        Button btn8 = createMenuButton("🗑️ حذف وزن\nSupprimer schème", () -> showDeleteSchemeDialog());
        Button btn9 = createMenuButton("📖 عرض الجذور\nAfficher racines", () -> showAllRoots());
        Button btn10 = createMenuButton("📐 عرض الأوزان\nAfficher schèmes", () -> showAllSchemes());

        menu.getChildren().addAll(menuTitle, sep, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10);
        return menu;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(60);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #333; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #333; -fx-font-size: 13px; -fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnAction(e -> action.run());

        return btn;
    }

    private VBox createCenterArea() {
        VBox center = new VBox(10);
        center.setPadding(new Insets(15));

        Label outputTitle = new Label("📄 النتائج - Résultats");
        outputTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Arial", 14));
        outputArea.setStyle("-fx-control-inner-background: white; -fx-background-radius: 10;");
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        center.getChildren().addAll(outputTitle, outputArea);
        return center;
    }

    private void displayWelcome() {
        outputArea.setText("مرحباً بك في محرك التصريف الصرفي العربي\n" +
                "Bienvenue dans le Moteur Morphologique Arabe\n\n" +
                "════════════════════════════════════════\n\n" +
                "📊 Statistiques:\n" +
                "  • Racines chargées: " + tree.size() + "\n" +
                "  • Schèmes chargés: " + schemes.size() + "\n\n" +
                "💡 Utilisez le menu de gauche pour commencer\n" +
                "   استخدم القائمة على اليسار للبدء");
    }

    private void showGenerateDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("توليد كلمة - Générer mot");
        dialog.setHeaderText("أدخل الجذر والوزن\nEntrez la racine et le schème");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField rootField = new TextField();
        rootField.setPromptText("الجذر (ex: كتب)");
        rootField.setFont(Font.font("Arial", 16));

        TextField schemeField = new TextField();
        schemeField.setPromptText("الوزن (ex: فاعل)");
        schemeField.setFont(Font.font("Arial", 16));

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

                outputArea.setText("✅ نتيجة التوليد - Résultat de génération\n\n" +
                        "الجذر - Racine: " + rootStr + "\n" +
                        "الوزن - Schème: " + schemeStr + " (" + scheme.getPattern() + ")\n" +
                        "═══════════════════════════════\n" +
                        "الكلمة المولدة - Mot généré: " + result + "\n\n" +
                        "✓ تم إضافته إلى المشتقات المصادق عليها\n" +
                        "✓ Ajouté aux dérivés validés");
            }
        });
    }

    private void showValidateDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("التحقق - Validation");
        dialog.setHeaderText("التحقق من انتماء كلمة إلى جذر\nVérifier l'appartenance d'un mot");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField wordField = new TextField();
        wordField.setPromptText("الكلمة (ex: كاتب)");
        wordField.setFont(Font.font("Arial", 16));

        TextField rootField = new TextField();
        rootField.setPromptText("الجذر (ex: كتب)");
        rootField.setFont(Font.font("Arial", 16));

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
                    outputArea.setText("✅ نعم - OUI\n\n" +
                            "الكلمة '" + word + "' تنتمي إلى الجذر '" + rootStr + "'\n" +
                            "Le mot '" + word + "' appartient à la racine '" + rootStr + "'\n\n" +
                            "الوزن المستخدم - Schème utilisé:\n" +
                            "  " + result.getScheme().getName() + " (" + result.getScheme().getPattern() + ")\n\n" +
                            "✓ تم إضافته إلى المشتقات\n" +
                            "✓ Ajouté aux dérivés validés");
                } else {
                    outputArea.setText("❌ لا - NON\n\n" +
                            "الكلمة '" + word + "' لا تنتمي إلى الجذر '" + rootStr + "'\n" +
                            "Le mot '" + word + "' n'appartient pas à la racine '" + rootStr + "'");
                }
            }
        });
    }

    private void showFamilyDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("العائلة الصرفية - Famille morphologique");
        dialog.setHeaderText("أدخل الجذر\nEntrez la racine");
        dialog.setContentText("الجذر - Racine:");

        dialog.showAndWait().ifPresent(rootStr -> {
            Node node = tree.search(rootStr);
            if (node == null) {
                showError("الجذر غير موجود\nRacine non trouvée");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════\n");
            sb.append("العائلة الصرفية - Famille morphologique\n");
            sb.append("الجذر - Racine: ").append(rootStr).append("\n");
            sb.append("═══════════════════════════════════════\n\n");

            List<Scheme> allSchemes = schemes.getAllSchemes();
            for (Scheme scheme : allSchemes) {
                String derived = engine.generate(node.getRoot(), scheme);
                sb.append(String.format("%-15s → %s\n", scheme.getName(), derived));
                node.getRoot().addDerivative(derived);
            }

            sb.append("\n✓ جميع المشتقات تمت إضافتها\n");
            sb.append("✓ Tous les dérivés ont été ajoutés");

            outputArea.setText(sb.toString());
        });
    }

    private void showDerivativesDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("المشتقات - Dérivés");
        dialog.setHeaderText("عرض المشتقات المصادق عليها\nAfficher les dérivés validés");
        dialog.setContentText("الجذر - Racine:");

        dialog.showAndWait().ifPresent(rootStr -> {
            Node node = tree.search(rootStr);
            if (node == null) {
                showError("الجذر غير موجود\nRacine non trouvée");
                return;
            }

            List<String> derivatives = node.getRoot().getValidatedDerivatives();
            StringBuilder sb = new StringBuilder();
            sb.append("المشتقات المصادق عليها - Dérivés validés\n");
            sb.append("الجذر - Racine: ").append(rootStr).append("\n");
            sb.append("═══════════════════════════════════════\n\n");

            if (derivatives.isEmpty()) {
                sb.append("(لا توجد مشتقات)\n(Aucun dérivé)");
            } else {
                sb.append("العدد - Total: ").append(derivatives.size()).append("\n\n");
                for (String der : derivatives) {
                    sb.append("  • ").append(der).append("\n");
                }
            }

            outputArea.setText(sb.toString());
        });
    }

    private void showAddRootDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("إضافة جذر - Ajouter racine");
        dialog.setHeaderText("أدخل الجذر الجديد (3 أحرف)\nEntrez la nouvelle racine (3 lettres)");
        dialog.setContentText("الجذر - Racine:");

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

            outputArea.setText("✅ تمت الإضافة بنجاح - Ajouté avec succès\n\n" +
                    "الجذر الجديد - Nouvelle racine: " + rootStr + "\n" +
                    "النوع - Type: " + newRoot.getType());
        });
    }

    private void showAddSchemeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("إضافة وزن - Ajouter schème");
        dialog.setHeaderText("أدخل الوزن الجديد\nEntrez le nouveau schème");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("الاسم (ex: فاعل)");
        nameField.setFont(Font.font("Arial", 16));

        TextField patternField = new TextField();
        patternField.setPromptText("النمط (ex: ف1ا ف2 ف3)");
        patternField.setFont(Font.font("Arial", 16));

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
                outputArea.setText("✅ تمت الإضافة بنجاح - Ajouté avec succès\n\n" +
                        "الوزن - Schème: " + name + "\n" +
                        "النمط - Pattern: " + pattern);
            }
        });
    }

    /**
     * Dialogue pour modifier un schème existant
     */
    private void showModifySchemeDialog() {
        // Étape 1: Demander le nom du schème à modifier
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("تعديل وزن - Modifier schème");
        searchDialog.setHeaderText("أدخل اسم الوزن المراد تعديله\nEntrez le nom du schème à modifier");
        searchDialog.setContentText("الاسم - Nom:");

        Optional<String> result = searchDialog.showAndWait();

        result.ifPresent(schemeName -> {
            Scheme scheme = schemes.search(schemeName);

            if (scheme == null) {
                showError("الوزن غير موجود\nSchème non trouvé: " + schemeName);
                return;
            }

            // Étape 2: Afficher le pattern actuel et demander le nouveau
            Dialog<ButtonType> modifyDialog = new Dialog<>();
            modifyDialog.setTitle("تعديل وزن - Modifier schème");
            modifyDialog.setHeaderText("تعديل النمط\nModifier le pattern");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            Label currentLabel = new Label("النمط الحالي - Pattern actuel:");
            Label currentValue = new Label(scheme.getPattern());
            currentValue.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            currentValue.setStyle("-fx-text-fill: #667eea;");

            TextField newPatternField = new TextField(scheme.getPattern());
            newPatternField.setPromptText("النمط الجديد (ex: ف1ا ف2 ف3)");
            newPatternField.setFont(Font.font("Arial", 16));

            grid.add(new Label("الاسم - Nom:"), 0, 0);
            grid.add(new Label(schemeName), 1, 0);
            grid.add(currentLabel, 0, 1);
            grid.add(currentValue, 1, 1);
            grid.add(new Label("النمط الجديد - Nouveau pattern:"), 0, 2);
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
                        outputArea.setText("✅ تم التعديل بنجاح - Modifié avec succès\n\n" +
                                "الوزن - Schème: " + schemeName + "\n" +
                                "النمط القديم - Ancien pattern: " + oldPattern + "\n" +
                                "النمط الجديد - Nouveau pattern: " + newPattern);
                    } else {
                        showError("فشل التعديل\nÉchec de la modification");
                    }
                }
            });
        });
    }

    /**
     * Dialogue pour supprimer un schème
     */
    private void showDeleteSchemeDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("حذف وزن - Supprimer schème");
        dialog.setHeaderText("أدخل اسم الوزن المراد حذفه\nEntrez le nom du schème à supprimer");
        dialog.setContentText("الاسم - Nom:");

        dialog.showAndWait().ifPresent(schemeName -> {
            Scheme scheme = schemes.search(schemeName);

            if (scheme == null) {
                showError("الوزن غير موجود\nSchème non trouvé: " + schemeName);
                return;
            }

            // Confirmation avant suppression
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("تأكيد الحذف - Confirmer la suppression");
            confirmAlert.setHeaderText("هل أنت متأكد من حذف هذا الوزن؟\nÊtes-vous sûr de vouloir supprimer ce schème ?");
            confirmAlert.setContentText("الوزن - Schème: " + schemeName + "\n" +
                    "النمط - Pattern: " + scheme.getPattern());

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                boolean success = schemes.delete(schemeName);

                if (success) {
                    outputArea.setText("✅ تم الحذف بنجاح - Supprimé avec succès\n\n" +
                            "الوزن المحذوف - Schème supprimé: " + schemeName + "\n" +
                            "النمط - Pattern: " + scheme.getPattern() + "\n\n" +
                            "عدد الأوزان المتبقية - Schèmes restants: " + schemes.size());
                } else {
                    showError("فشل الحذف\nÉchec de la suppression");
                }
            }
        });
    }

    private void showAllRoots() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("جميع الجذور - Toutes les racines\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append(tree.getInOrderString());
        outputArea.setText(sb.toString());
    }

    private void showAllSchemes() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("جميع الأوزان - Tous les schèmes\n");
        sb.append("═══════════════════════════════════════\n\n");

        List<Scheme> allSchemes = schemes.getAllSchemes();

        if (allSchemes.isEmpty()) {
            sb.append("(لا توجد أوزان)\n(Aucun schème)");
        } else {
            for (Scheme scheme : allSchemes) {
                sb.append("• ").append(scheme.toString()).append("\n");
            }
            sb.append("\nالعدد الكلي - Total: ").append(schemes.size()).append(" schèmes");
        }

        outputArea.setText(sb.toString());
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