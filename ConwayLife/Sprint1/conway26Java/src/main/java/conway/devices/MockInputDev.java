package main.java.conway.devices;

import main.java.conway.domain.GameController;

public class MockInputDev {
    private final GameController controller;

    public MockInputDev(GameController controller) {
        this.controller = controller;
    }

    public void simulateUserInteraction() {
        System.out.println("MockInput: Simulando che l'utente preme 'Start'");
        controller.start();
        
        try {
            Thread.sleep(3500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\nMockInput: Simulando che l'utente preme 'Stop'");
        controller.stop();
        
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\nMockInput: Simulando che l'utente preme 'Clear'");
        controller.clear();
    }
}