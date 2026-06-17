package test;

import domain.Hold;

/*
 * TestHold - test di unita' del POJO Hold (stato della stiva) - SPRINT 3.
 * Copre la logica delle tre risposte alla loadrequest (reserved/retrylater/reject,
 * cfr. TP2/TP3), slot5 come area di marcatura, le posizioni IOPort/HOME, il DISENGAGE
 * da timeout (TP4) e -- nuovo nello Sprint 3 -- lo stato della stiva per il display.
 *
 * Esecuzione (nessuna dipendenza esterna):
 *   cd TemaFinale26/Sprint3/cargoservice26
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
        check("T4 posizione slot5 = (2,5)",                hold.slotX("slot5") == 2 && hold.slotY("slot5") == 5);

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

        // T8 - SPRINT1: DISENGAGE da timeout 30s (TP4) -> lo slot riservato torna libero
        Hold h2 = new Hold();
        String r = h2.reserveFreeSlot();                          // engaged: riserva slot1
        check("T8 engaged: slot riservato occupato",       h2.isOccupied(r));
        h2.freeSlot(r);                                           // timeout -> disengage
        check("T8 disengage: slot liberato",               !h2.isOccupied(r));
        check("T8 disengage: stiva di nuovo non piena",    !h2.isFull());
        check("T8 disengage: lo slot e' di nuovo riservabile", h2.reserveFreeSlot().equals(r));

        // T9 - SPRINT3: stato della stiva per il display (requisito "current state of the hold")
        Hold h3 = new Hold();
        check("T9 stato iniziale descritto: tutti liberi",
              h3.stateDescription().equals("hold: slot1=libero slot2=libero slot3=libero slot4=libero"));
        h3.reserveFreeSlot();
        check("T9 dopo una riserva: slot1=PIENO",
              h3.stateDescription().equals("hold: slot1=PIENO slot2=libero slot3=libero slot4=libero"));

        // T10 - SPRINT3: stato come TERMINE Prolog (reply holdinfo del getholdstate)
        check("T10 stateTerm e' un termine valido (niente spazi)",
              !h3.stateTerm().contains(" ") && h3.stateTerm().startsWith("holdstate("));
        check("T10 stateTerm riflette la riserva: slot1(pieno)",
              h3.stateTerm().equals("holdstate(slot1(pieno),slot2(libero),slot3(libero),slot4(libero))"));

        // T11 - SPRINT3: il barcode del marker viene memorizzato nello slot e mostrato sul display
        Hold h4 = new Hold();
        String r4 = h4.reserveFreeSlot();                  // slot1
        h4.confirmStored(r4, "bc7");
        check("T11 barcode memorizzato nello slot",        h4.barcodeOf(r4).equals("bc7"));
        check("T11 display mostra il barcode",
              h4.stateDescription().equals("hold: slot1=PIENO(bc7) slot2=libero slot3=libero slot4=libero"));

        // T12 - SPRINT3: posizioni degli slot dalla mappa del committente (tf25map)
        Hold h5 = new Hold();
        check("T12 slot3 = (3,1) (mappa del committente)", h5.slotX("slot3") == 3 && h5.slotY("slot3") == 1);
        check("T12 slot4 = (3,4) (mappa del committente)", h5.slotX("slot4") == 3 && h5.slotY("slot4") == 4);
        check("T12 slot5 = (2,5) area di marcatura",        h5.slotX("slot5") == 2 && h5.slotY("slot5") == 5);

        System.out.println("----------------------------------------");
        System.out.println("TestHold (Sprint3): " + passed + " PASS, " + failed + " FAIL");
        System.exit(failed == 0 ? 0 : 1);
    }
}
