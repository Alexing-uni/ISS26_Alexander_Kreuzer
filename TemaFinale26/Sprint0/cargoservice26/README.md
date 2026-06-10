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
  src/it/unibo/cargoservice/          Cargoservice.kt  (codice del modello, pattern del generatore QAK)
  src/it/unibo/ctxcargo/              MainCtxcargo.kt  (main del context, porta 8030)
  cargoservice26.pl  sysRules.pl      descrizione del sistema (contexts, qactor, msg)
  utils/domain/Hold.java              stato della stiva (POJO) + posizioni dalla mappa
  utils/devices/DisplaySim.java       display simulato (output a video)
  utils/devices/LedSim.java           LED simulato (indicatore di stato)
  utils/test/TestHold.java            test di unita' di Hold (ESEGUITO: 16 PASS, 0 FAIL)
  build.gradle  settings.gradle       build (come robotsmart26usage)
  gradlew  gradlew.bat  gradle/       Gradle wrapper 8.6 (del corso) + gradle.properties
```

> Il codice in `src/it/unibo/` e' scritto seguendo **esattamente il pattern del codice generato**
> dal plugin QAK nei progetti del corso (`sistemasqak`, `robotsmart26usage`); da
> rigenerare/validare col plugin nell'IDE a partire da `src/cargoservice26.qak`.
> `gradlew build` = BUILD SUCCESSFUL. Il core attende una `loadrequest`: il caller
> (pushbutton simulato) e' introdotto nello **Sprint 1**.

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

## Come eseguire
1. Infrastruttura: `docker compose -f ../../../robotsmart26/yamls/unibobasic26.yaml up`
   (VirtualRobot + GUI + mosquitto).
2. `cd ../../../robotsmart26 ; ./gradlew run` (servizio del docente, porta 8020).
3. In questa cartella: `./gradlew run` (context `ctxcargo`, porta 8030). Il servizio si avvia
   e **attende una `loadrequest`**; il caller (pushbutton simulato) e' nello **Sprint 1**,
   che esegue l'intero ciclo.

> Stato: i POJO sono **compilati e testati** (`TestHold`: 16 PASS, 0 FAIL); `gradlew build` =
> **BUILD SUCCESSFUL** (modello + codice). Il ciclo completo e' **eseguito nello Sprint 1**
> (vedi `../../Sprint1/cargoservice26/`).
