package devices;

/*
 * LedSim - LED simulato. E' un INDICATORE DI STATO (non un attuatore):
 * lampeggia mentre il cargoservice e' 'engaged'. Nel core (Sprint 0) e' un log a video.
 */
public class LedSim {
    private boolean on = false;
    public void blink(boolean v) {
        on = v;
        System.out.println("[LED] " + (v ? "blink ON (engaged)" : "OFF"));
    }
    public boolean isOn() { return on; }
}
