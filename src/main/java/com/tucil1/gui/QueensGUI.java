package com.tucil1.gui;

import com.tucil1.core.Board;
import com.tucil1.core.Parser;
import com.tucil1.core.Solver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;

public class QueensGUI extends Application {
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 800;
    private static final int CELL_SIZE = 50;
    private static final long UI_REFRESH_MS = 200;

    private Board board;
    private Solver solver;

    private Stage primaryStage;
    private GridPane gridPane;
    private Label statusLabel;
    private Label timeLabel;
    private Label caseLabel;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("N-Queens Logic Solver");

        BorderPane root = buildRootLayout();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BorderPane buildRootLayout() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setCenter(buildBoardArea());
        root.setBottom(buildBottomSection());

        return root;
    }

    private StackPane buildBoardArea() {
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return new StackPane(scrollPane);
    }

    private VBox buildBottomSection() {
        statusLabel = new Label("Status: Siap (Load file puzzle)");
        statusLabel.setTextFill(Color.DARKBLUE);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        timeLabel = new Label("Waktu: - ms");
        timeLabel.setFont(Font.font("Monospaced", 13));

        caseLabel = new Label("Kasus ditinjau: -");
        caseLabel.setFont(Font.font("Monospaced", 13));

        VBox bottom = new VBox(6);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        bottom.getChildren().addAll(statusLabel, timeLabel, caseLabel, buildActionBar());
        return bottom;
    }

    private HBox buildActionBar() {
        Button loadButton = new Button("Load");
        Button saveButton = new Button("Save");
        Button solveButton = new Button("Solve");
        Button exitButton = new Button("Exit");

        loadButton.setOnAction(e -> handleLoad());
        saveButton.setOnAction(e -> handleSave());
        solveButton.setOnAction(e -> handleSolve());
        exitButton.setOnAction(e -> Platform.exit());

        HBox actionBar = new HBox(8);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        actionBar.getChildren().addAll(loadButton, saveButton, solveButton, exitButton);
        return actionBar;
    }

    private void handleLoad() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih File Puzzle");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File file = chooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }

        try {
            board = Parser.readBoard(file.getAbsolutePath());
            if (board == null || board.getSize() <= 0) {
                showAlert("Error Loading File", "File puzzle kosong atau format tidak valid.");
                return;
            }
            solver = new Solver(board);

            statusLabel.setText("Status: File loaded (" + board.getSize() + "x" + board.getSize() + ")");
            statusLabel.setTextFill(Color.DARKBLUE);
            timeLabel.setText("Waktu: - ms");
            caseLabel.setText("Kasus ditinjau: 0");

            drawBoard();
        } catch (Exception e) {
            showAlert("Error Loading File", "Gagal membaca file:\n" + e.getMessage());
        }
    }

    private void handleSolve() {
        if (board == null) {
            showAlert("Peringatan", "Silakan Load File puzzle terlebih dahulu!");
            return;
        }

        statusLabel.setText("Status: Sedang mencari solusi... (Running)");
        statusLabel.setTextFill(Color.DARKBLUE);
        timeLabel.setText("Waktu: Running...");
        caseLabel.setText("Kasus ditinjau: 0");

        long startTime = System.currentTimeMillis();

        Thread solveThread = new Thread(() -> solver.solve());
        solveThread.setDaemon(true);

        LiveUpdateController liveUpdater = new LiveUpdateController(
            board,
            solver,
            solveThread,
            startTime,
            UI_REFRESH_MS,
            (elapsed, totalCase) -> {
                timeLabel.setText("Waktu: " + elapsed + " ms (running)");
                caseLabel.setText("Kasus ditinjau: " + totalCase);
            },
            this::drawBoard
        );

        solveThread.start();
        liveUpdater.start();

        Thread finalizer = new Thread(() -> {
            waitSolveThread(solveThread);
            liveUpdater.stop();
            showFinalSolveResult(startTime);
        });
        finalizer.setDaemon(true);
        finalizer.start();
    }

    private void waitSolveThread(Thread solveThread) {
        try {
            solveThread.join();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private void showFinalSolveResult(long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        Platform.runLater(() -> {
            timeLabel.setText("Waktu: " + duration + " ms");
            caseLabel.setText("Kasus ditinjau: " + solver.getTotalCase());

            if (solver.isFound()) {
                statusLabel.setText("Status: SOLUSI DITEMUKAN!");
                statusLabel.setTextFill(Color.GREEN);
                drawBoard();
            } else {
                statusLabel.setText("Status: TIDAK ADA SOLUSI.");
                statusLabel.setTextFill(Color.RED);
            }
        });
    }

    private void handleSave() {
        if (board == null || solver == null || !solver.isFound()) {
            showAlert("Error", "Belum ada solusi yang ditemukan untuk disimpan.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Simpan Solusi");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        chooser.setInitialFileName("solution.txt");

        File file = chooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(file)) {
            int n = board.getSize();
            boolean[][] queens = board.getQueens();

            for (int row = 0; row < n; row++) {
                for (int col = 0; col < n; col++) {
                    writer.print(queens[row][col] ? "#" : board.getRegion(row, col));
                    if (col < n - 1) {
                        writer.print(" ");
                    }
                }
                writer.println();
            }

            showInfo("Sukses", "Solusi berhasil disimpan ke:\n" + file.getName());
        } catch (Exception e) {
            showAlert("Error Saving", "Gagal menyimpan file:\n" + e.getMessage());
        }
    }

    private void drawBoard() {
        if (board == null) {
            return;
        }
        drawBoard(board.getQueens());
    }

    private void drawBoard(boolean[][] queenMap) {
        gridPane.getChildren().clear();
        if (board == null || queenMap == null) {
            return;
        }

        int n = board.getSize();

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                StackPane cell = new StackPane();

                Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
                background.setFill(getColorForRegion(board.getRegion(row, col)));
                background.setStroke(Color.rgb(0, 0, 0, 0.2));
                background.setStrokeWidth(0.2);

                Label label = new Label();
                if (queenMap[row][col]) {
                    label.setText("#");
                    label.setFont(Font.font("Arial", FontWeight.BOLD, 28));
                    label.setTextFill(Color.BLACK);
                } else {
                    label.setText(String.valueOf(board.getRegion(row, col)));
                    label.setFont(Font.font("Arial", 13));
                    label.setTextFill(Color.rgb(0, 0, 0, 0.35));
                }

                cell.getChildren().addAll(background, label);
                gridPane.add(cell, col, row);
            }
        }
    }

    private Color getColorForRegion(char region) {
        switch (Character.toUpperCase(region)) {
            case 'A': return Color.RED;
            case 'B': return Color.GREEN;
            case 'C': return Color.BLUE;
            case 'D': return Color.YELLOW;
            case 'E': return Color.ORANGE;
            case 'F': return Color.PURPLE;
            case 'G': return Color.CYAN;
            case 'H': return Color.PINK;
            case 'I': return Color.BROWN;
            case 'J': return Color.GRAY;
            case 'K': return Color.TEAL;
            case 'L': return Color.NAVY;
            case 'M': return Color.MAROON;
            case 'N': return Color.OLIVE;
            case 'O': return Color.SALMON;
            case 'P': return Color.SKYBLUE;
            case 'Q': return Color.VIOLET;
            case 'R': return Color.LIMEGREEN;
            case 'S': return Color.SANDYBROWN;
            case 'T': return Color.TURQUOISE;
            case 'U': return Color.PLUM;
            case 'V': return Color.DARKSEAGREEN;
            case 'W': return Color.LIGHTCORAL;
            case 'X': return Color.STEELBLUE;
            case 'Y': return Color.CHOCOLATE;
            case 'Z': return Color.INDIGO;
            default: return Color.WHITESMOKE;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
