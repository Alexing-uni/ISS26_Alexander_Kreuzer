package main.java.conway.domain;

public class Grid {
    private final int rows;
    private final int cols;
    private Cell[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        
        // Inicializamos todas las células como muertas
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell(r, c, false);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Cell getCell(int r, int c) {
        return cells[r][c];
    }
}