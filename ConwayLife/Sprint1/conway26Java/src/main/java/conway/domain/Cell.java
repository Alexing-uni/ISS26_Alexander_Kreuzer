package main.java.conway.domain;

public class Cell {
    private boolean state;
    private final int x;
    private final int y;

    public Cell(int x, int y, boolean state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}