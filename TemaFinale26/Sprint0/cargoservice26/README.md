# cargoservice26 — modello qak (Sprint 0, core)

Posizione: `TemaFinale26/Sprint0/cargoservice26/` (sottosistema dello Sprint 0).

Modello a **QActor** del `cargoservice` del TemaFinale26. Realizza il **core business**
dello Sprint 0: ricevuta una `loadrequest`, riserva uno slot e comanda il cargorobot
**IOPort → slot5 (marcatura) → slot riservato → HOME**. Il container e' assunto presente
all'IOPort (il *sensor* e' lo Sprint 1; *Out of service* e timeout sono Sprint successivi).

## Struttura (pattern di robotsmart26usage)
```
cargoservice26/
  src/cargoservice26.qak              modello (FSM) del cargoservice
  utils/domain/Hold.java              stato della stiva (POJO) + posizioni dalla mappa
  utils/devices/DisplaySim.java       display simulato (output a video)
  utils/devices/LedSim.java           LED simulato (indicatore di stato)
  utils/test/TestHold.java            test di unita' di Hold (ESEGUITO: 16 PASS, 0 FAIL)
  build.gradle  settings.gradle       build (come robotsmart26usage)
```

## Test eseguito (POJO)

```
cd TemaFinale26/Sprint0/cargoservice26
javac -d build/testclasses utils/domain/Hold.java utils/test/TestHold.java
java  -cp build/testclasses test.TestHold        =>  TestHold: 16 PASS, 0 FAIL
```

Il test copre la logica delle tre risposte alla `loadrequest` (`reserved/retrylater/reject`,
cfr. TP2/TP3), slot5 mai riservato come slot di carico, posizioni IOPort=(4,0) e HOME=(0,0).

## Contratto del cargorobot
Il cargoservice e' **client di robotsmart**: usa `moverobot(X,Y,STEPTIME)` con risposta
`moverobotok` / `moverobotfailed` (vedi `robotsmart26usage/src/robotsmart26tf26.qak`).

## Come generare ed eseguire
1. Aprire il progetto nell'IDE col **plugin QAK** e generare il codice Kotlin da
   `src/cargoservice26.qak` (context `ctxcargo` → `it.unibo.ctxcargo.MainCtxcargoKt`).
2. Avviare l'infrastruttura: `docker compose -f ../../../robotsmart26/yamls/unibobasic26.yaml up`
   e `robotsmart26` (`gradlew run`, porta 8020).
3. `gradlew run` di questo progetto; inviare una `loadrequest` (caller del pushbutton).

> Stato: i POJO sono **compilati e testati** (`TestHold`: 16 PASS, 0 FAIL). Il **modello qak**
> e' scritto con sintassi allineata ai sorgenti del corso (`robotsmart26tf26.qak`, `firefly`,
> `robotservice26`); generazione `.kt` (plugin QAK), build ed esecuzione di TP1 = passo successivo.
