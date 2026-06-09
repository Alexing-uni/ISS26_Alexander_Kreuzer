package test;

import domain.Hold;

/*
 * TestHold - test di unita' del POJO Hold (stato della stiva).
 * Copre la logica delle tre risposte alla loadrequest (vedi Sprint0_v1, Requirement analysis):
 *   reserved(SLOT) | retrylater | reject
 * e i Test Plans TP2 (stiva piena -> reject) e TP3 (IOPort occupato -> retrylater).
 *
 * Esecuzione (nessuna dipendenza esterna):
 *   cd TemaFinale26/cargoservice26
 *   javac -d build/testclasses utils/domain/Hold.java utils/test/TestHold.java
 *   java  -cp build/testclasses test.TestHold
 */
public class TestHold {

    static int passed = 0, failed = 0;

    static void check(String what, boolean cond) {
        if (cond) { passed++; System.out.println("PASS  " + what); }
        else      { failed++; System.out.println("FAIL  " + what); }
    }

    public static void main(String[] args) {
        Hold hold = new Hold();

        // T1 - stato iniziale: stiva vuota, IOPort libero
        check("T1 stato iniziale: stiva non piena",        !hold.isFull());
        check("T1 stato iniziale: IOPort libero",          !hold.ioportOccupied());
        check("T1 stato iniziale: slot1 libero",           !hold.isOccupied("slot1"));

        // T2 - riserva del primo slot libero (caso reserved(SLOT))
        String s = hold.reserveFreeSlot();
        check("T2 reserveFreeSlot -> slot1",               s.equals("slot1"));
        check("T2 slot1 risulta occupato",                 hold.isOccupied("slot1"));
        check("T2 posizione slot1 = (1,1)",                hold.slotX("slot1") == 1 && hold.slotY("slot1") == 1);

        // T3 - riempimento: dopo 4 riserve la stiva e' piena (caso reject, TP2)
        hold.reserveFreeSlot();   // slot2
        hold.reserveFreeSlot();   // slot3
        hold.reserveFreeSlot();   // slot4
        check("T3 dopo 4 riserve: stiva piena",            hold.isFull());
        check("T3 stiva piena: reserveFreeSlot -> \"\"",   hold.reserveFreeSlot().equals(""));

        // T4 - slot5 e' AREA DI MARCATURA: mai riservato come slot di carico
        check("T4 slot5 (marcatura) mai riservato",        !hold.isOccupied("slot5"));
        check("T4 posizione slot5 = (3,4)",                hold.slotX("slot5") == 3 && hold.slotY("slot5") == 4);

        // T5 - liberazione di uno slot (scarico/disengage) e nuova riserva
        hold.freeSlot("slot3");
        check("T5 dopo freeSlot(slot3): stiva non piena",  !hold.isFull());
        check("T5 nuova riserva -> slot3",                 hold.reserveFreeSlot().equals("slot3"));

        // T6 - IOPort occupato (caso retrylater, TP3)
        hold.setIoportOccupied(true);
        check("T6 IOPort occupato -> retrylater",          hold.ioportOccupied());
        hold.setIoportOccupied(false);
        check("T6 IOPort di nuovo libero",                 !hold.ioportOccupied());

        // T7 - posizioni-obiettivo dalla mappa (Robotsmart26Cmds)
        check("T7 IOPort = (4,0)",                         hold.ioportX() == 4 && hold.ioportY() == 0);
        check("T7 HOME   = (0,0)",                         hold.homeX()   == 0 && hold.homeY()   == 0);

        System.out.println("----------------------------------------");
        System.out.println("TestHold: " + passed + " PASS, " + failed + " FAIL");
        System.exit(failed == 0 ? 0 : 1);
    }
}
