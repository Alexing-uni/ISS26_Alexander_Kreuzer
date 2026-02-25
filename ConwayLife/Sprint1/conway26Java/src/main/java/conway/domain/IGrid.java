package main.java.conway.domain;

public interface IGrid {
    int getRows();
    int getCols();
    ICell getCell(int r, int c);
}