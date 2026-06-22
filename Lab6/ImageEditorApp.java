package org.example.lab6_edytor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageEditorApp extends Application {

    private ImageView originalImageView;
    private ImageView modifiedImageView;
    private ComboBox<String> operationsComboBox;

    private Button btnExecute;
    private Button btnSave;
    private Button btnScale;
    private Button btnRotLeft;
    private Button btnRotRight;

    private Image currentOriginalImage;
    private boolean isModified = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Edytor Obrazów - Lab 6 (Wersja 4.0)");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox topBox = new HBox(20);
        topBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Edytor Obrazów JavaFX");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ImageView logoView = new ImageView();
        try {
            File logoFile = new File("logo.png");
            if (logoFile.exists()) {
                logoView.setImage(new Image(logoFile.toURI().toString()));
                logoView.setFitHeight(60);
                logoView.setPreserveRatio(true);
            }
        } catch (Exception e) {
            System.out.println("Brak logo.png w folderze projektu.");
        }
        topBox.getChildren().addAll(logoView, titleLabel);
        root.setTop(topBox);

        Label footerLabel = new Label("Autor: Jakub Miodek, Nr indeksu: 280081");
        footerLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        HBox bottomBox = new HBox(footerLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(15, 0, 5, 0));
        root.setBottom(bottomBox);

        // Interfejs
        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20, 0, 20, 0));

        HBox toolBox = new HBox(10);
        toolBox.setAlignment(Pos.CENTER);

        Button btnLoad = new Button("Wczytaj");
        btnSave = new Button("Zapisz");
        btnScale = new Button("Skaluj");
        btnRotLeft = new Button("⟲");
        btnRotRight = new Button("⟳");

        btnSave.setDisable(true);
        btnScale.setDisable(true);
        btnRotLeft.setDisable(true);
        btnRotRight.setDisable(true);

        operationsComboBox = new ComboBox<>();
        operationsComboBox.getItems().addAll("Brak (Test)", "Negatyw", "Progowanie", "Konturowanie");
        operationsComboBox.setValue(null);

        btnExecute = new Button("Wykonaj");
        btnExecute.setDisable(true);

        toolBox.getChildren().addAll(btnLoad, btnSave, new Separator(), btnScale, btnRotLeft, btnRotRight, new Separator(), operationsComboBox, btnExecute);

        HBox imagesBox = new HBox(20);
        imagesBox.setAlignment(Pos.CENTER);

        VBox originalBox = new VBox(5, new Label("Oryginał:"), originalImageView = new ImageView());
        originalBox.setAlignment(Pos.CENTER);
        originalImageView.setFitWidth(350);
        originalImageView.setFitHeight(350);
        originalImageView.setPreserveRatio(true);
        originalImageView.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        VBox modifiedBox = new VBox(5, new Label("Po zmianach:"), modifiedImageView = new ImageView());
        modifiedBox.setAlignment(Pos.CENTER);
        modifiedImageView.setFitWidth(350);
        modifiedImageView.setFitHeight(350);
        modifiedImageView.setPreserveRatio(true);
        modifiedImageView.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        imagesBox.getChildren().addAll(originalBox, modifiedBox);

        centerBox.getChildren().addAll(toolBox, imagesBox);
        root.setCenter(centerBox);

        //Akcje
        btnLoad.setOnAction(e -> loadFile(primaryStage));
        btnSave.setOnAction(e -> showSaveModal(primaryStage));
        btnScale.setOnAction(e -> showScaleModal(primaryStage));
        btnRotLeft.setOnAction(e -> rotateImage(-90));
        btnRotRight.setOnAction(e -> rotateImage(90));
        btnExecute.setOnAction(e -> executeOperation(primaryStage));

        Scene scene = new Scene(root, 900, 650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Logika plików

    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik obrazka");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki JPG", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String name = file.getName().toLowerCase();
            if (!name.endsWith(".jpg") && !name.endsWith(".jpeg")) {
                showToast(stage, "Niedozwolony format pliku");
                return;
            }

            try {
                currentOriginalImage = new Image(file.toURI().toString());
                originalImageView.setImage(currentOriginalImage);
                modifiedImageView.setImage(currentOriginalImage);
                isModified = false;

                //Odblokowanie narzędzi
                btnSave.setDisable(false);
                btnScale.setDisable(false);
                btnRotLeft.setDisable(false);
                btnRotRight.setDisable(false);
                btnExecute.setDisable(false);

                showToast(stage, "Pomyślnie załadowano plik");
            } catch (Exception ex) {
                showToast(stage, "Nie udało się załadować pliku");
            }
        }
    }

    //Skalowanie

    private void showScaleModal(Stage parentStage) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Skalowanie obrazu");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        Image currentMod = modifiedImageView.getImage();
        int curW = (int) currentMod.getWidth();
        int curH = (int) currentMod.getHeight();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        TextField widthField = new TextField(String.valueOf(curW));
        TextField heightField = new TextField(String.valueOf(curH));

        Label wError = new Label(); wError.setTextFill(Color.RED);
        Label hError = new Label(); hError.setTextFill(Color.RED);

        grid.add(new Label("Szerokość (px):"), 0, 0);
        grid.add(widthField, 1, 0);
        grid.add(wError, 1, 1);

        grid.add(new Label("Wysokość (px):"), 0, 2);
        grid.add(heightField, 1, 2);
        grid.add(hError, 1, 3);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        Button btnApply = new Button("Zmień rozmiar");
        Button btnCancel = new Button("Anuluj");
        Button btnRestore = new Button("Przywróć oryginał");

        btnCancel.setOnAction(e -> modalStage.close());

        btnRestore.setOnAction(e -> {
            modifiedImageView.setImage(currentOriginalImage);
            isModified = false;
            modalStage.close();
            showToast(parentStage, "Przywrócono oryginalne wymiary");
        });

        btnApply.setOnAction(e -> {
            wError.setText("");
            hError.setText("");
            boolean valid = true;

            if (widthField.getText().trim().isEmpty()) { wError.setText("Pole jest wymagane"); valid = false; }
            if (heightField.getText().trim().isEmpty()) { hError.setText("Pole jest wymagane"); valid = false; }

            if (!valid) return;

            try {
                int w = Integer.parseInt(widthField.getText().trim());
                int h = Integer.parseInt(heightField.getText().trim());

                if (w <= 0 || w > 3000) { wError.setText("Wartość spoza zakresu 1-3000"); valid = false; }
                if (h <= 0 || h > 3000) { hError.setText("Wartość spoza zakresu 1-3000"); valid = false; }

                if (valid) {
                    scaleImage(w, h);
                    modalStage.close();
                }
            } catch (NumberFormatException ex) {
                showToast(parentStage, "Wprowadź poprawne wartości liczbowe!");
            }
        });

        btnBox.getChildren().addAll(btnApply, btnRestore, btnCancel);
        vbox.getChildren().addAll(new Label("Zmień wymiary obrazu (Max 3000px)"), grid, btnBox);

        modalStage.setScene(new Scene(vbox, 450, 250));
        modalStage.show();
    }

    private void scaleImage(int newWidth, int newHeight) {
        try {
            Image src = modifiedImageView.getImage();
            Canvas canvas = new Canvas(newWidth, newHeight);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(src, 0, 0, newWidth, newHeight);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage scaled = canvas.snapshot(params, null);

            modifiedImageView.setImage(scaled);
            isModified = true;
        } catch (Exception e) {
            System.out.println("Błąd skalowania: " + e.getMessage());
        }
    }

    //Obrót

    private void rotateImage(int angle) {
        try {
            Image src = modifiedImageView.getImage();
            int w = (int) src.getWidth();
            int h = (int) src.getHeight();

            // Nowy obraz ma zamienione wymiary
            WritableImage rotated = new WritableImage(h, w);
            PixelReader pr = src.getPixelReader();
            PixelWriter pw = rotated.getPixelWriter();

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color color = pr.getColor(x, y);
                    if (angle == -90) {
                        // Obrót o 90 stopni w lewo
                        pw.setColor(y, w - 1 - x, color);
                    } else if (angle == 90) {
                        // Obrót o 90 stopni w prawo
                        pw.setColor(h - 1 - y, x, color);
                    }
                }
            }
            modifiedImageView.setImage(rotated);
            isModified = true;
        } catch (Exception e) {
            System.out.println("Błąd obrotu: " + e.getMessage());
        }
    }

    //Filtry

    private void executeOperation(Stage stage) {
        String operation = operationsComboBox.getValue();
        if (operation == null) {
            showToast(stage, "Nie wybrano operacji do wykonania");
            return;
        }

        switch (operation) {
            case "Negatyw":
                applyNegative(stage);
                break;
            case "Progowanie":
                showThresholdModal(stage);
                break;
            case "Konturowanie":
                applyContour(stage);
                break;
            case "Brak (Test)":
                isModified = true;
                showToast(stage, "Wykonano operację testową");
                break;
        }
    }

    private void applyNegative(Stage stage) {
        try {
            Image src = modifiedImageView.getImage();
            int w = (int) src.getWidth();
            int h = (int) src.getHeight();
            WritableImage dest = new WritableImage(w, h);
            PixelReader pr = src.getPixelReader();
            PixelWriter pw = dest.getPixelWriter();

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    pw.setColor(x, y, pr.getColor(x, y).invert());
                }
            }
            modifiedImageView.setImage(dest);
            isModified = true;
            showToast(stage, "Negatyw został wygenerowany pomyślnie!");
        } catch (Exception e) {
            showToast(stage, "Nie udało się wykonać negatywu.");
        }
    }

    private void showThresholdModal(Stage parentStage) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Progowanie");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        TextField thresholdField = new TextField("128");
        inputBox.getChildren().addAll(new Label("Wartość progu (0-255):"), thresholdField);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);
        Button btnApply = new Button("Wykonaj progowanie");
        Button btnCancel = new Button("Anuluj");

        btnCancel.setOnAction(e -> modalStage.close());

        btnApply.setOnAction(e -> {
            try {
                int threshold = Integer.parseInt(thresholdField.getText().trim());
                if (threshold < 0 || threshold > 255) {
                    errorLabel.setText("Próg musi być w zakresie od 0 do 255!");
                    return;
                }
                applyThreshold(parentStage, threshold);
                modalStage.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Podaj poprawną liczbę całkowitą!");
            }
        });

        btnBox.getChildren().addAll(btnApply, btnCancel);
        vbox.getChildren().addAll(inputBox, errorLabel, btnBox);

        modalStage.setScene(new Scene(vbox, 350, 150));
        modalStage.show();
    }

    private void applyThreshold(Stage stage, int threshold) {
        try {
            Image src = modifiedImageView.getImage();
            int w = (int) src.getWidth();
            int h = (int) src.getHeight();
            WritableImage dest = new WritableImage(w, h);
            PixelReader pr = src.getPixelReader();
            PixelWriter pw = dest.getPixelWriter();

            double normalizedThreshold = threshold / 255.0;

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    Color c = pr.getColor(x, y);
                    //Obliczanie jasności
                    double brightness = (c.getRed() + c.getGreen() + c.getBlue()) / 3.0;
                    if (brightness >= normalizedThreshold) {
                        pw.setColor(x, y, Color.WHITE);
                    } else {
                        pw.setColor(x, y, Color.BLACK);
                    }
                }
            }
            modifiedImageView.setImage(dest);
            isModified = true;
            showToast(stage, "Progowanie zostało przeprowadzone pomyślnie!");
        } catch (Exception e) {
            showToast(stage, "Nie udało się wykonać progowania.");
        }
    }

    private void applyContour(Stage stage) {
        try {
            Image src = modifiedImageView.getImage();
            int w = (int) src.getWidth();
            int h = (int) src.getHeight();
            WritableImage dest = new WritableImage(w, h);
            PixelReader pr = src.getPixelReader();
            PixelWriter pw = dest.getPixelWriter();

            //detekcja krawędzi
            for (int y = 1; y < h - 1; y++) {
                for (int x = 1; x < w - 1; x++) {
                    double center = pr.getColor(x, y).getBrightness() * 8;
                    double surround = pr.getColor(x-1, y-1).getBrightness() + pr.getColor(x, y-1).getBrightness() + pr.getColor(x+1, y-1).getBrightness()
                            + pr.getColor(x-1, y).getBrightness()   +                                     + pr.getColor(x+1, y).getBrightness()
                            + pr.getColor(x-1, y+1).getBrightness() + pr.getColor(x, y+1).getBrightness() + pr.getColor(x+1, y+1).getBrightness();

                    double edgeVal = center - surround;
                    edgeVal = Math.max(0.0, Math.min(1.0, edgeVal)); // Normalizacja 0-1

                    pw.setColor(x, y, Color.color(edgeVal, edgeVal, edgeVal));
                }
            }
            modifiedImageView.setImage(dest);
            isModified = true;
            showToast(stage, "Konturowanie zostało przeprowadzone pomyślnie!");
        } catch (Exception e) {
            showToast(stage, "Nie udało się wykonać konturowania.");
        }
    }

    //Zapisywanie i powiadomienia

    private void showSaveModal(Stage parentStage) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(parentStage);
        modalStage.setTitle("Zapisz plik");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);

        if (!isModified) {
            Label warnLabel = new Label("Na pliku nie zostały wykonane żadne operacje!");
            warnLabel.setTextFill(Color.ORANGE);
            vbox.getChildren().add(warnLabel);
        }

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        TextField nameField = new TextField();
        nameField.setPromptText("nazwa_pliku");
        inputBox.getChildren().addAll(new Label("Nazwa pliku:"), nameField);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        Button btnSaveModal = new Button("Zapisz");
        Button btnCancel = new Button("Anuluj");

        btnCancel.setOnAction(e -> {
            nameField.clear();
            modalStage.close();
        });

        btnSaveModal.setOnAction(e -> {
            String fileName = nameField.getText().trim();
            if (fileName.length() < 3 || fileName.length() > 100) {
                errorLabel.setText("Wpisz co najmniej 3 znaki");
                return;
            }
            saveFileProcess(parentStage, modalStage, fileName);
        });

        buttonsBox.getChildren().addAll(btnSaveModal, btnCancel);
        vbox.getChildren().addAll(inputBox, errorLabel, buttonsBox);

        Scene scene = new Scene(vbox, 400, 200);
        modalStage.setScene(scene);
        modalStage.show();
    }

    private void saveFileProcess(Stage parentStage, Stage modalStage, String fileName) {
        String userHome = System.getProperty("user.home");
        File defaultDirectory = new File(userHome, "Pictures");
        if (!defaultDirectory.exists()) defaultDirectory = new File(userHome, "Obrazy");
        if (!defaultDirectory.exists()) defaultDirectory = new File(userHome);

        File fileToSave = new File(defaultDirectory, fileName + ".jpg");

        if (fileToSave.exists()) {
            showToast(parentStage, "Plik " + fileName + ".jpg już istnieje w systemie. Podaj inną nazwę pliku!");
            return;
        }

        try {
            Image imageToSave = modifiedImageView.getImage();
            ImageIO.write(SwingFXUtils.fromFXImage(imageToSave, null), "jpg", fileToSave);
            showToast(parentStage, "Zapisano obraz w pliku " + fileName + ".jpg");
            modalStage.close();
        } catch (IOException ex) {
            showToast(parentStage, "Nie udało się zapisać pliku " + fileName + ".jpg");
        }
    }

    private void showToast(Stage stage, String message) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        Label label = new Label(message);
        label.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px; " +
                "-fx-background-radius: 5px;");

        popup.getContent().add(label);

        popup.setOnShown(e -> {
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2);
            popup.setY(stage.getY() + stage.getHeight() - 100);
        });

        popup.show(stage);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), evt -> popup.hide()));
        timeline.play();
    }
}
