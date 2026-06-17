package domain;

/*
 * Hold - stato del dominio (POJO) della stiva del cargoservice (TemaFinale26).
 *
 * Le posizioni-obiettivo sono prese dai comandi del docente
 * (robotsmart26/utils/callers/Robotsmart26Cmds.java):
 *     IOPort = (4,0)   HOME = (0,0)
 *     posizioni note: (1,1) (1,4) (3,2) (5,3) (4,3) (3,4)
 *
 * VERIFICATO sulla mappa built-in del DDR (robotsmart26/tf25map.txt):
 *     "00000001@00110001@00001001@00110001@00000001@11111111"  (X=riga, Y=colonna)
 *   Delle 6 posizioni del docente, in questa mappa sono RAGGIUNGIBILI (cella libera)
 *   solo (1,1) (1,4) (4,3) (3,4); INVECE (3,2) e (5,3) cadono su un OSTACOLO / sul
 *   muro inferiore -> il pianificatore A* del robot restituisce piano vuoto ''
 *   (cella irraggiungibile). Percio' slot3 e slot4 (provvisori, come gia' annotato)
 *   sono stati portati su celle LIBERE:
 *     slot3 -> (4,3)  [e' la posizione (4,3) del docente, move43, non usata prima]
 *     slot4 -> (4,2)  [cella libera adiacente alla cassa (3,2); DERIVATA dalla mappa,
 *                      NON dai comandi del docente -> DA CONFERMARE col committente]
 *   slot1/slot2/slot5 restano sulle coordinate del docente (gia' raggiungibili).
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
        new Slot("slot1", 1, 1, false),  // docente, raggiungibile
        new Slot("slot2", 1, 4, false),  // docente, raggiungibile
        new Slot("slot3", 4, 3, false),  // docente move43 (era (3,2): ostacolo -> irraggiungibile)
        new Slot("slot4", 4, 2, false),  // DERIVATA dalla mappa (era (5,3): muro -> irraggiungibile) -- DA CONFERMARE
        new Slot("slot5", 3, 4, true)    // docente, area di marcatura (non e' uno slot di carico)
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
