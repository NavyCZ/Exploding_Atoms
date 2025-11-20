package com.brovdij.exploding_atoms.data;

public class Grid {
    private final int width, height;
    private final Cell[][] cells;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        cells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Cell getCell(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return null;
        return cells[y][x];
    }

    public Cell[][] getCells() {
        return cells;
    }
}
