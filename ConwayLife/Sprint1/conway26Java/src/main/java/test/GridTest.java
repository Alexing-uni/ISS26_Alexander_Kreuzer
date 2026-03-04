package main.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import main.java.conway.domain.Grid;
import main.java.conway.domain.IGrid;
import main.java.conway.domain.ICell;

public class GridTest {

    @Test
    public void testGridInitialization() {
        System.out.println("GridTest | testGridInitialization");
        
        IGrid grid = new Grid(5, 5);

        assertEquals("La cuadricula deberia tener 5 filas", 5, grid.getRows());
        assertEquals("La cuadricula deberia tener 5 columnas", 5, grid.getCols());

        // Comprobamos que todas las celdas se inicializan correctamente y están muertas
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                ICell cell = grid.getCell(r, c);
                
                assertNotNull("La celda en (" + r + "," + c + ") no deberia ser nula", cell);
                assertFalse("La celda deberia estar muerta inicialmente", cell.getState());
            }
        }
    }
}