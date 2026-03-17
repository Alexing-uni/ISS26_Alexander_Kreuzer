package main.java.test;
<<<<<<< HEAD

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

=======
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
>>>>>>> upstream/main
import org.junit.Test;

import main.java.conway.domain.Cell;
import main.java.conway.domain.ICell;

public class CellTest {
<<<<<<< HEAD

    @Test
    public void testCellInitializationAlive() {
        System.out.println("CellTest | testCellInitializationAlive");
        
        // 1. Creamos una célula viva
        ICell cell = new Cell(true);

        // 2. Comprobamos su estado
        assertTrue("La celula deberia estar viva (true)", cell.getState());
    }

    @Test
    public void testCellInitializationDead() {
        System.out.println("CellTest | testCellInitializationDead");
        
        // 1. Creamos una célula muerta
        ICell cell = new Cell(false);

        // 2. Comprobamos su estado
        assertFalse("La celula deberia estar muerta (false)", cell.getState());
    }

    @Test
    public void testSetState() {
        System.out.println("CellTest | testSetState");
        
        ICell cell = new Cell(false);
        assertFalse("El estado inicial deberia ser false", cell.getState());

        cell.setState(true);
        assertTrue("La celula ahora deberia estar viva", cell.getState());

        cell.setState(false);
        assertFalse("La celula ahora deberia estar muerta", cell.getState());
    }
}
=======
private ICell c;

	@Before
	public void setup() {
		System.out.println("ConwayLifeTest | setup");	
	    c = new Cell();
	}
	@After
	public void down() {
		System.out.println("ConwayLifeTest | down");
	}
	
	@Test
	public void testCellAlive() {
		System.out.println("ConwayLifeTest | doing alive");
		c.setStatus(true);
		boolean r = c.isAlive();
		assertTrue(r);
		
	}
	
	@Test
	public void testCellDead() {
		System.out.println("ConwayLifeTest | doing dead");
		c.setStatus(false);
		boolean r = c.isAlive();
		assertTrue( !r);
	}	
}
>>>>>>> upstream/main
