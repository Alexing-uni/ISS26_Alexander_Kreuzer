package domain;

/*
 * Hold - stato del dominio (POJO) della stiva del cargoservice (TemaFinale26).
 *
 * Posizioni degli slot CONFERMATE sulla mappa del committente (tf25map del DDR,
 * X=riga Y=colonna; IOPort magenta in (4,0) lo conferma):
 *     "00000001@00110001@00001001@00110001@00000001@11111111"
 *     HOME=(0,0)   IOPort=(4,0)
 *     slot1=(1,1)  slot2=(1,4)  slot3=(3,1)  slot4=(3,4)    [slot di carico]
 *     slot5=(2,5)                                            [area di marcatura]
 *   Tutte celle LIBERE/raggiungibili (verificato col pianificatore A* del robot).
 *
 * Punto aperto D1 (Project) RISOLTO: le posizioni provvisorie (3,2) e (5,3) cadevano
 * su ostacoli/muro (A* -> piano vuoto, robot non vi arriva); corrette sulle posizioni
 * reali del layout del committente qui sopra.
 */
public class Hold {

    private static class Slot {
        final String name; final int x, y; final boolean marking; boolean occupied;
        String barcode = "";   // SPRINT3: il barcode assegnato dal marker al container stoccato
        Slot(String name, int x, int y, boolean marking) {
            this.name = name; this.x = x; this.y = y; this.marking = marking;
        }
    }

    private final Slot[] slots = new Slot[] {
        new Slot("slot1", 1, 1, false),  // mappa del committente
        new Slot("slot2", 1, 4, false),  // mappa del committente
        new Slot("slot3", 3, 1, false),  // mappa del committente (era (4,3): posizione sbagliata)
        new Slot("slot4", 3, 4, false),  // mappa del committente (era (4,2): posizione sbagliata)
        new Slot("slot5", 2, 5, true)    // mappa del committente - area di marcatura (era (3,4))
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

    /** conferma lo stoccaggio: lo slot resta occupato e MEMORIZZA il barcode del marker */
    public void confirmStored(String name, String barcode) {
        Slot s = get(name);
        if (s != null) s.barcode = (barcode == null ? "" : barcode);
    }
    public String barcodeOf(String name) { Slot s = get(name); return s != null ? s.barcode : ""; }
    public void freeSlot(String name)       { Slot s = get(name); if (s != null) s.occupied = false; }
    public boolean isOccupied(String name)   { Slot s = get(name); return s != null && s.occupied; }

    /** stato corrente della stiva, per il display (requisito: "show the current state of the hold") */
    public String stateDescription() {
        StringBuilder sb = new StringBuilder("hold:");
        for (Slot s : slots) if (!s.marking) {
            sb.append(" ").append(s.name).append(s.occupied ? "=PIENO" : "=libero");
            if (s.occupied && !s.barcode.isEmpty()) sb.append("(").append(s.barcode).append(")");  // barcode del marker
        }
        return sb.toString();
    }

    /** stato della stiva come TERMINE Prolog (per la reply holdinfo: niente spazi/'=') */
    public String stateTerm() {
        StringBuilder sb = new StringBuilder("holdstate(");
        boolean first = true;
        for (Slot s : slots) if (!s.marking) {
            if (!first) sb.append(",");
            sb.append(s.name).append("(").append(s.occupied ? "pieno" : "libero").append(")");
            first = false;
        }
        return sb.append(")").toString();
    }

    // posizioni dalla mappa del DDR
    public int slotX(String name) { Slot s = get(name); return s != null ? s.x : 0; }
    public int slotY(String name) { Slot s = get(name); return s != null ? s.y : 0; }
    public int ioportX() { return 4; }   // IOPort (Robotsmart26Cmds.moveInPort = moverobot(4,0))
    public int ioportY() { return 0; }
    public int homeX()   { return 0; }   // HOME   (Robotsmart26Cmds.moveHome  = moverobot(0,0))
    public int homeY()   { return 0; }
}
