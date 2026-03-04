package main.java.conway;

import main.java.conway.devices.MockInputDev;
import main.java.conway.devices.MockOutdev;
import main.java.conway.domain.IOutDev;
import main.java.conway.domain.Life;
import main.java.conway.domain.LifeController;
import main.java.conway.domain.LifeInterface;

public class MainConwayLifeJava {

    public void configureTheSystemWitMockOutdev() {
        System.out.println("MainConway | configureTheSystemWitMockOutdev");
        
        // 1. Crear el dispositivo de salida (Pantalla Mock)
        IOutDev outputDevice = new MockOutdev();
        
        // 2. Crear las reglas del juego (Tablero 5x5 por defecto)
        LifeInterface gameRules = Life.CreateGameRules();
        
        // Configuramos un Blinker (Oscilador) inicial
        gameRules.setCell(2, 1, true);
        gameRules.setCell(2, 2, true);
        gameRules.setCell(2, 3, true);
        
        // 3. Crear el Controlador y conectarlo
        LifeController controller = new LifeController(gameRules, outputDevice);
        
        // 4. Crear el dispositivo de entrada y darle el control
        MockInputDev inputDevice = new MockInputDev(controller);
        
        // 5. Iniciar simulación de usuario
        inputDevice.simulateUserInteraction();
    }

    public void configureTheSystemWithSwing() {
        // Reservado para el Sprint 2
    }
    
    public void configureTheSystemWithHtmlWs(boolean pageexternal) {
        // Reservado para el Sprint 3
    }
    
    public static void main(String[] args) {
        System.out.println("MainConway | STARTS");
        MainConwayLifeJava app = new MainConwayLifeJava();
        app.configureTheSystemWitMockOutdev();
        System.out.println("MainConway | ENDS");
    }
}