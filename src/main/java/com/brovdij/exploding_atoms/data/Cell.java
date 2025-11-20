package com.brovdij.exploding_atoms.data;

import com.brovdij.exploding_atoms.data.Player;

public class Cell {
    private final int x, y;
    private int atomCount;
    private Player owner;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.atomCount = 0;
        this.owner = null;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getAtomCount() { return atomCount; }
    public Player getOwner() { return owner; }

    public void setOwner(Player owner) { this.owner = owner; }
    public void setAtomCount(int atomCount) { this.atomCount = atomCount; }

    public void addAtom(Player player) {
        this.atomCount++;
        this.owner = player;
    }

    public void reset() {
        this.atomCount = 0;
        this.owner = null;
    }

    public boolean isEmpty() {
        return atomCount == 0;
    }
}
