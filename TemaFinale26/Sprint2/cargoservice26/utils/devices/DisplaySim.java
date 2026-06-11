package devices;

/*
 * DisplaySim - display dell'IOPort simulato (output a video).
 * Nel core (Sprint 0) il display e' un semplice log; in un'iterazione successiva
 * sara' sostituito dalla web-gui (cfr. chiarimento D5 del committente), senza
 * modificare il cargoservice.
 */
public class DisplaySim {
    public void show(String what) {
        System.out.println("[DISPLAY] " + what);
    }
}
