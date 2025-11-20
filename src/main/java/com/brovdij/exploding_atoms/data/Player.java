package com.brovdij.exploding_atoms.data;

import javafx.scene.paint.Color;

public class Player {
    private final int id;
    private final String name;
    private final Color color;

    public Player(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Color getColor() { return color; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Player)) return false;
        Player p = (Player) obj;
        return p.id == this.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

