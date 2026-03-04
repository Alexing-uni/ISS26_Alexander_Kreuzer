package conway.domain;

import conway.devices.ConwayLifeGrid;
import main.java.conway.domain.*; // Clases de tu JAR

public class MainConwaySwing {
    public static void main(String[] args) {
        // 1. Creamos la lógica (20x20)
        Life life = new Life(20, 20); 
        
        // 2. INICIALIZACIÓN: Ponemos un patrón vivo
        life.setCell(5, 5, true);
        life.setCell(5, 6, true);
        life.setCell(5, 7, true);
        
        // 3. Creamos la GUI
        ConwayLifeGrid gui = new ConwayLifeGrid(20, 20);
        
        // 4. Creamos el controlador conectando lógica y vista
        LifeController controller = new LifeController(life, gui);
        
        // 5. Conectamos la GUI al controlador y le pasamos el objeto life
        gui.setController(controller, life);
        
        System.out.println("Sprint 2: Sistema iniciado con éxito.");
    }
}