package com.brovdij.exploding_atoms.presentation;

import com.brovdij.exploding_atoms.data.Cell;
import com.brovdij.exploding_atoms.data.Grid;
import com.brovdij.exploding_atoms.data.Player;
import com.brovdij.exploding_atoms.logic.GameEngine;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainController {

    @FXML private GridPane gridPane;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;
    @FXML private Button btnReset;

    private GameEngine engine;
    private Grid gridModel;
    private StackPane[][] cellPanes;

    //Grid
    private final int WIDTH = 8;
    private final int HEIGHT = 8;
    private final double CELL_SIZE = 64;

    private Player human;
    private Player ai;
    private final Random random = new Random();

    // Last move
    private int lastX = -1;
    private int lastY = -1;

    @FXML
    public void initialize() {
        setupGame();
        btnReset.setOnAction(e -> setupGame());
    }

    private void setupGame() {
        human = new Player(1, "Hráč 1", Color.web("#ff3b30"));
        ai = new Player(2, "AI", Color.web("#007aff"));

        gridModel = new Grid(WIDTH, HEIGHT);
        engine = new GameEngine(gridModel, human, ai);

        buildGridUI();
        updateStatus();
    }

    private void buildGridUI() {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        cellPanes = new StackPane[HEIGHT][WIDTH];

        for (int x = 0; x < WIDTH; x++) gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));
        for (int y = 0; y < HEIGHT; y++) gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                StackPane cellPane = new StackPane();
                cellPane.getStyleClass().add("cell");
                cellPane.setPrefSize(CELL_SIZE, CELL_SIZE);
                cellPane.setMaxSize(CELL_SIZE, CELL_SIZE);

                Circle atomVisual = new Circle(CELL_SIZE * 0.18);
                atomVisual.setVisible(false);
                Text atomCount = new Text("");
                atomCount.setFont(Font.font(14));
                atomCount.setMouseTransparent(true);

                VBox vbox = new VBox(atomVisual, atomCount);
                vbox.setAlignment(Pos.CENTER);
                cellPane.getChildren().add(vbox);

                final int fx = x;
                final int fy = y;
                cellPane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY &&
                            engine.getCurrentPlayer().equals(human)) {

                        boolean moved = engine.makeMove(fx, fy);
                        if (!moved) {
                            infoLabel.setText("Neplatný tah (buňka patří jinému hráči).");
                        } else {
                            infoLabel.setText("Tah proveden.");
                            lastX = fx;
                            lastY = fy;
                            refreshGrid();
                            checkGameOver();
                            if (!engine.isGameOver()) aiMove();
                        }
                    }
                });

                gridPane.add(cellPane, x, y);
                cellPanes[y][x] = cellPane;
            }
        }
        refreshGrid();
    }

    private void aiMove() {
        List<int[]> validCells = new ArrayList<>();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell c = gridModel.getCell(x, y);
                if (c.getOwner() == null || c.getOwner().equals(ai)) {
                    validCells.add(new int[]{x, y});
                }
            }
        }

        if (!validCells.isEmpty()) {
            int[] move = validCells.get(random.nextInt(validCells.size()));
            engine.makeMove(move[0], move[1]);
            lastX = move[0];
            lastY = move[1];
            infoLabel.setText("AI provedla tah.");
            refreshGrid();
            checkGameOver();
        }
    }

    private void refreshGrid() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell c = gridModel.getCell(x, y);
                StackPane pane = cellPanes[y][x];
                VBox vbox = (VBox) pane.getChildren().get(0);
                Circle atomVisual = (Circle) vbox.getChildren().get(0);
                Text atomCount = (Text) vbox.getChildren().get(1);

                if (c.getOwner() == null || c.getAtomCount() == 0) {
                    atomVisual.setVisible(false);
                    atomCount.setText("");
                    pane.setStyle("-fx-background-color: #f9f9f9;");
                } else {
                    atomVisual.setVisible(true);
                    atomCount.setText(String.valueOf(c.getAtomCount()));
                    Color col = c.getOwner().getColor();
                    String bg = String.format("rgba(%d,%d,%d,0.18)",
                            (int)(col.getRed()*255),
                            (int)(col.getGreen()*255),
                            (int)(col.getBlue()*255));
                    pane.setStyle("-fx-background-color: " + bg + "; -fx-border-color: #333333; -fx-border-width: 1;");
                    atomVisual.setFill(c.getOwner().getColor());
                }

                //So Shiny
                if (x == lastX && y == lastY) {
                    pane.setStyle(pane.getStyle() + "; -fx-border-color: gold; -fx-border-width: 3;");
                }
            }
        }
        updateStatus();
    }

    private void updateStatus() {
        if (!engine.isGameOver()) {
            Player cur = engine.getCurrentPlayer();
            statusLabel.setText("Na tahu: " + cur.getName());
        }
    }

    private void checkGameOver() {
        if (engine.isGameOver()) {
            Player winner = engine.getWinner();
            if (winner != null) {
                statusLabel.setText("Konec hry! Vítěz: " + winner.getName());
            } else {
                statusLabel.setText("Konec hry! Žádný vítěz.");
            }
            infoLabel.setText("Stiskni Reset pro novou hru.");
        }
    }
}

