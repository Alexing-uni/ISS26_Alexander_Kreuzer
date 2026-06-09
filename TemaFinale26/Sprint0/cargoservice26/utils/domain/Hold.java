package domain;

/*
 * Hold - stato del dominio (POJO) della stiva del cargoservice (TemaFinale26).
 *
 * Le posizioni-obiettivo sono prese dai comandi del docente
 * (robotsmart26/utils/callers/Robotsmart26Cmds.java):
 *     IOPort = (4,0)   HOME = (0,0)
 *     posizioni note: (1,1) (1,4) (3,2) (5,3) (4,3) (3,4)
 * NB: la corrispondenza slotN <-> posizione e' PROVVISORIA, da confermare sulla
 *     mappa built-in del DDR (cfr. D1). Qui serve solo per eseguire il modello.
 */
public class Hold {

    private static class Slot {
        final String name; final int x, y; final boolean marking; boolean occupied;
        Slot(String name, int x, int y, boolean marking) {
            this.name = name; this.x = x; this.y = y; this.marking = marking;
        }
    }

    private final Slot[] slots = new Slot[] {
        new Slot("slot1", 1, 1, false),
        new Slot("slot2", 1, 4, false),
        new Slot("slot3", 3, 2, false),
        new Slot("slot4", 5, 3, false),
        new Slot("slot5", 3, 4, true)    // area di marcatura (non e' uno slot di carico)
    };
    private boolean ioOccupied = false;

    private Slot get(String name) {
        for (Slot s : slots) if (s.name.equals(name)) return s;
        return null;
    }

    public boolean ioportOccupied()            { return ioOccupied; }
    public void    setIoportOccupied(boolean v){ ioOccupied = v; }

    /** true se tutti gli slot di carico (slot1..slot4) sono occupati */
    public boolean isFull() {
        for (Slot s : slots) if (!s.marking && !s.occupied) return false;
        return true;
    }

    /** riserva (occupa) il primo slot di carico libero; "" se la stiva e' piena */
    public String reserveFreeSlot() {
        for (Slot s : slots) if (!s.marking && !s.occupied) { s.occupied = true; return s.name; }
        return "";
    }

    public void confirmStored(String name) { /* lo slot riservato resta occupato */ }
    public void freeSlot(String name)       { Slot s = get(name); if (s != null) s.occupied = false; }
    public boolean isOccupied(String name)   { Slot s = get(name); return s != null && s.occupied; }

    // posizioni dalla mappa del DDR
    public int slotX(String name) { Slot s = get(name); return s != null ? s.x : 0; }
    public int slotY(String name) { Slot s = get(name); return s != null ? s.y : 0; }
    public int ioportX() { return 4; }   // IOPort (Robotsmart26Cmds.moveInPort = moverobot(4,0))
    public int ioportY() { return 0; }
    public int homeX()   { return 0; }   // HOME   (Robotsmart26Cmds.moveHome  = moverobot(0,0))
    public int homeY()   { return 0; }
}
