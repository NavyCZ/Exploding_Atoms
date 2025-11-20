package com.brovdij.exploding_atoms.logic;

import com.brovdij.exploding_atoms.data.Cell;
import com.brovdij.exploding_atoms.data.Grid;
import com.brovdij.exploding_atoms.data.Player;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class GameEngine {

    private final Grid grid;
    private final Player[] players;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;
    private int movesMade = 0;

    // Last move
    private int lastMoveX = -1;
    private int lastMoveY = -1;
    private final Random random = new Random();

    public GameEngine(Grid grid, Player... players) {
        this.grid = grid;
        this.players = players;
    }

    public Grid getGrid() { return grid; }
    public Player getCurrentPlayer() { return players[currentPlayerIndex]; }
    public boolean isGameOver() { return gameOver; }

    public int getLastMoveX() { return lastMoveX; }
    public int getLastMoveY() { return lastMoveY; }

    //hráč
    public boolean makeMove(int x, int y) {
        if (gameOver) return false;

        Cell cell = grid.getCell(x, y);
        if (cell == null) return false;

        Player current = getCurrentPlayer();

        if (cell.getOwner() == null || cell.getOwner().equals(current)) {
            lastMoveX = x;
            lastMoveY = y;

            cell.addAtom(current);
            processExplosions();
            movesMade++;
            evaluateGameOver();

            if (!gameOver)
                nextPlayer();

            return true;
        } else {
            return false;
        }
    }


    //BOT
    public boolean makeAiMove() {
        if (gameOver) return false;

        Player ai = getCurrentPlayer();


        List<int[]> valid = new ArrayList<>();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c.getOwner() == null || c.getOwner().equals(ai)) {
                    valid.add(new int[]{x, y});
                }
            }
        }

        if (valid.isEmpty()) return false;

        int[] selected = valid.get(random.nextInt(valid.size()));

        return makeMove(selected[0], selected[1]);
    }
    //______________
    //EXPLOOOOOSION!
    //______________
    private void processExplosions() {
        Queue<Cell> q = new ArrayDeque<>();

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c.getAtomCount() > threshold(x, y)) {
                    q.add(c);
                }
            }
        }

        while (!q.isEmpty()) {
            Cell c = q.poll();
            int x = c.getX();
            int y = c.getY();

            if (c.getAtomCount() <= threshold(x, y)) continue;

            Player owner = c.getOwner();
            c.reset();

            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

            for (int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                Cell n = grid.getCell(nx, ny);
                if (n == null) continue;

                n.addAtom(owner);

                if (n.getAtomCount() > threshold(nx, ny)) {
                    q.add(n);
                }
            }
        }
    }


    private int neighborCount(int x, int y) {
        int count = 0;
        if (grid.getCell(x+1, y) != null) count++;
        if (grid.getCell(x-1, y) != null) count++;
        if (grid.getCell(x, y+1) != null) count++;
        if (grid.getCell(x, y-1) != null) count++;
        return count;
    }

    private int threshold(int x, int y) {
        return neighborCount(x, y) - 1;
    }

    private void evaluateGameOver() {
        if (movesMade < 2) return;

        boolean[] has = new boolean[players.length];
        int totalOwners = 0;

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c.getOwner() != null) {
                    for (int i = 0; i < players.length; i++) {
                        if (players[i].equals(c.getOwner()) && !has[i]) {
                            has[i] = true;
                            totalOwners++;
                        }
                    }
                }
            }
        }

        if (totalOwners <= 1)
            gameOver = true;
    }


    public Player getWinner() {
        if (!gameOver) return null;

        for (Player p : players) {
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Cell c = grid.getCell(x, y);
                    if (c.getOwner() != null && c.getOwner().equals(p)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
}
