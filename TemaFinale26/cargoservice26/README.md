# cargoservice26 — modello qak (Sprint 0, core)

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
  build.gradle  settings.gradle       build (come robotsmart26usage)
```

## Contratto del cargorobot
Il cargoservice e' **client di robotsmart**: usa `moverobot(X,Y,STEPTIME)` con risposta
`moverobotok` / `moverobotfailed` (vedi `robotsmart26usage/src/robotsmart26tf26.qak`).

## Come generare ed eseguire
1. Aprire il progetto nell'IDE col **plugin QAK** e generare il codice Kotlin da
   `src/cargoservice26.qak` (context `ctxcargo` → `it.unibo.ctxcargo.MainCtxcargoKt`).
2. Avviare l'infrastruttura: `docker compose -f ../../robotsmart26/yamls/unibobasic26.yaml up`
   e `robotsmart26` (`gradlew run`, porta 8020).
3. `gradlew run` di questo progetto; inviare una `loadrequest` (caller del pushbutton).

> Stato: il **modello** e' scritto (sintassi modellata su `robotsmart26tf26.qak`).
> Generazione `.kt`, build ed esecuzione/test sono il passo immediato successivo.
