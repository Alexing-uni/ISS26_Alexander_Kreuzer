package main.java.conway.domain;

public class Cell implements ICell {
    private boolean state;

    public Cell(boolean state) {
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
}