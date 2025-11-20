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

public class MainController {

    @FXML private GridPane gridPane;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;
    @FXML private Button btnReset;

    private GameEngine engine;
    private Grid gridModel;
    private StackPane[][] cellPanes;

    private final int WIDTH = 8;
    private final int HEIGHT = 8;
    private final double CELL_SIZE = 64;

    private Player human;
    private Player ai;

    @FXML
    public void initialize() {
        setupGame();
        btnReset.setOnAction(e -> setupGame());
    }


    private void setupGame() {
        human = new Player(1, "Hráč 1", Color.web("#ff3b30"));
        ai    = new Player(2, "AI", Color.web("#007aff"));

        gridModel = new Grid(WIDTH, HEIGHT);
        engine = new GameEngine(gridModel, human, ai);

        buildGridUI();
        refreshGrid();
        updateStatus();
        infoLabel.setText("Hra začala.");
    }


    private void buildGridUI() {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        cellPanes = new StackPane[HEIGHT][WIDTH];

        for (int x = 0; x < WIDTH; x++)
            gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_SIZE));

        for (int y = 0; y < HEIGHT; y++)
            gridPane.getRowConstraints().add(new RowConstraints(CELL_SIZE));


        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                StackPane pane = new StackPane();
                pane.setPrefSize(CELL_SIZE, CELL_SIZE);
                pane.getStyleClass().add("cell");

                Circle atom = new Circle(CELL_SIZE * 0.18);
                atom.setVisible(false);

                Text count = new Text("");
                count.setFont(Font.font(14));
                count.setMouseTransparent(true);

                VBox box = new VBox(atom, count);
                box.setAlignment(Pos.CENTER);

                pane.getChildren().add(box);

                final int fx = x;
                final int fy = y;

                pane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY &&
                            engine.getCurrentPlayer().equals(human)) {

                        boolean moved = engine.makeMove(fx, fy);

                        if (!moved) {
                            infoLabel.setText("Neplatný tah.");
                        } else {
                            infoLabel.setText("Táhnul jsi.");
                            refreshGrid();
                            checkGameOver();

                            if (!engine.isGameOver()) {
                                engine.makeAiMove();
                                infoLabel.setText("AI táhla.");
                                refreshGrid();
                                checkGameOver();
                            }
                        }
                    }
                });

                gridPane.add(pane, x, y);
                cellPanes[y][x] = pane;
            }
        }
    }


    private void refreshGrid() {
        int lx = engine.getLastMoveX();
        int ly = engine.getLastMoveY();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                Cell c = gridModel.getCell(x, y);
                StackPane pane = cellPanes[y][x];
                VBox box = (VBox) pane.getChildren().get(0);
                Circle atom = (Circle) box.getChildren().get(0);
                Text count = (Text) box.getChildren().get(1);

                if (c.getOwner() == null || c.getAtomCount() == 0) {
                    atom.setVisible(false);
                    count.setText("");
                    pane.setStyle("-fx-background-color: #f9f9f9;");
                } else {
                    atom.setVisible(true);
                    count.setText(String.valueOf(c.getAtomCount()));
                    Color col = c.getOwner().getColor();
                    atom.setFill(col);

                    String bg = String.format(
                            "rgba(%d,%d,%d,0.18)",
                            (int)(col.getRed()*255),
                            (int)(col.getGreen()*255),
                            (int)(col.getBlue()*255)
                    );

                    pane.setStyle("-fx-background-color: " + bg + ";");
                }

                // GOLD
                if (x == lx && y == ly) {
                    pane.setStyle(pane.getStyle() + "-fx-border-color: gold; -fx-border-width: 3;");
                } else {
                    pane.setStyle(pane.getStyle() + "-fx-border-color: #333; -fx-border-width: 1;");
                }
            }
        }

        updateStatus();
    }


    private void updateStatus() {
        if (!engine.isGameOver()) {
            statusLabel.setText("Na tahu: " + engine.getCurrentPlayer().getName());
        }
    }


    private void checkGameOver() {
        if (engine.isGameOver()) {
            Player winner = engine.getWinner();
            if (winner != null)
                statusLabel.setText("Konec! Vítěz: " + winner.getName());
            else
                statusLabel.setText("Konec! Bez vítěze.");

            infoLabel.setText("Stiskni Reset pro novou hru.");
        }
    }
}
