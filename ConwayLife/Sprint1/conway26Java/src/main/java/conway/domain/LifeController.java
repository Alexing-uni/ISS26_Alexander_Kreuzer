package main.java.conway.domain;

public class LifeController implements GameController {
    private final LifeInterface life;
    private final IOutDev outDev;
    private boolean isPlaying = false;

    public LifeController(LifeInterface life, IOutDev outDev) {
        this.life = life;
        this.outDev = outDev;
    }

    // Un método para simular el juego corriendo
    public void play() {
        isPlaying = true;
        outDev.display("Game Started...");
        
        while (isPlaying) {
            outDev.display("\nGeneración Actual:");
            outDev.display(life.gridRep());
            
            life.nextGeneration();
            
            try {
                Thread.sleep(1000); // Pausa 1 segundo entre generaciones
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void start() {
        if (!isPlaying) {
            new Thread(this::play).start();
        }
    }

    @Override
    public void stop() {
        isPlaying = false;
        outDev.display("Game Stopped.");
    }

    @Override
    public void clear() {
        if (!isPlaying) {
            life.clear();
            outDev.display("Grid Cleared.");
            outDev.display(life.gridRep());
        } else {
            outDev.display("Cannot clear while playing. Stop the game first.");
        }
    }
}