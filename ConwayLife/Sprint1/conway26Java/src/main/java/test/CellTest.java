package main.java.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import main.java.conway.domain.Cell;
import main.java.conway.domain.ICell;

public class CellTest {

    @Test
    public void testCellInitializationAlive() {
        System.out.println("CellTest | testCellInitializationAlive");
        
        // 1. Create an alive cell at position (2, 3)
        ICell cell = new Cell(2, 3, true);

        // 2. Check that coordinates and state were saved correctly
        assertEquals("The X coordinate should be 2", 2, cell.getX());
        assertEquals("The Y coordinate should be 3", 3, cell.getY());
        assertTrue("The cell should be alive (true)", cell.getState());
    }

    @Test
    public void testCellInitializationDead() {
        System.out.println("CellTest | testCellInitializationDead");
        
        // 1. Create a dead cell at position (0, 0)
        ICell cell = new Cell(0, 0, false);

        // 2. Check that it was initialized correctly
        assertEquals("The X coordinate should be 0", 0, cell.getX());
        assertEquals("The Y coordinate should be 0", 0, cell.getY());
        assertFalse("The cell should be dead (false)", cell.getState());
    }

    @Test
    public void testSetState() {
        System.out.println("CellTest | testSetState");
        
        // 1. Initially dead cell
        ICell cell = new Cell(1, 1, false);
        assertFalse("Initial state should be false", cell.getState());

        // 2. Revive it
        cell.setState(true);
        assertTrue("The cell should now be alive", cell.getState());

        // 3. Kill it again
        cell.setState(false);
        assertFalse("The cell should now be dead", cell.getState());
        
        // 4. Coordinates should not have changed
        assertEquals(1, cell.getX());
        assertEquals(1, cell.getY());
    }
}