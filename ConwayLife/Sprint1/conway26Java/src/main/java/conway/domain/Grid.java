package main.java.conway.domain;

public class Grid implements IGrid {
    private final int rows;
    private final int cols;
    private ICell[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new ICell[rows][cols];
        
        // Inicializamos todas las células como muertas
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell(false); 
            }
        }
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public ICell getCell(int r, int c) {
        return cells[r][c];
    }
}