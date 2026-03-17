package main.java.test;
<<<<<<< HEAD

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
=======
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import main.java.conway.domain.Grid;

public class GridTest {
	private static final int nRows=5;
	private static final int nCols=5;
	
private Grid grid;

	@Before
	public void setup() {
		System.out.println("GridTest | setup");	
		grid= new Grid(nRows,nCols);
	}
	@After
	public void down() {
		System.out.println("GridTest | down");
	}
	
	@Test
	public void testDims() {
		System.out.println("testDims ---------------------" );
		int nr = grid.getRowsNum();
		int nc = grid.getColsNum();
		assertTrue( nr==nRows && nc==nCols );
	}
	@Test
	public void testCGridCellValue() {
		System.out.println("testCGridCellValue ---------------------" );
		grid.setCellValue(0,0,true);
		assertTrue(   grid.getCellValue(0,0) );
		assertFalse(  grid.getCellValue(0,1) );
	}
	@Test
	public void testGridRep() {
		System.out.println("testGridRep ---------------------" );
 		System.out.println(""+grid);
		assertTrue( grid.toString().startsWith(". . . . ."));
	}
	@Test
	public void testPrintGrid() {
		System.out.println("testPrintGrid ---------------------" );
		grid.setCellValue(0,0,true);
		grid.setCellValue(0,1,true);
		grid.setCellValue(0,2,true);
		grid.setCellValue(0,3,true);
		grid.setCellValue(0,4,true);
		//grid.printGrid();
	}

}
>>>>>>> upstream/main
