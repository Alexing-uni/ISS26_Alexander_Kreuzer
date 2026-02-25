package main.java.conway.domain;

import main.java.conway.domain.ICell;

@SuppressWarnings("unused")
public class Cell implements ICell {
    private boolean state;	
    private final int x;
    private final int y;

    public Cell(int x, int y, boolean state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}