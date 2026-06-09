# cargoservice26 — modello qak (Sprint 1)

Posizione: `TemaFinale26/Sprint1/cargoservice26/` (sottosistema dello Sprint 1).

Evoluzione del **core (Sprint 0)**: aggiunge il **sensor dell'IOPort** e il **timeout di 30s**.
Ricevuta una `loadrequest`, il cargoservice riserva uno slot (engaged, LED on) e **attende
il container nell'area del sensor**:
- se il sensor notifica la presenza (`containerInPlace`) → trasporto **IOPort → slot5
  (marcatura) → slot riservato → HOME** (come nel core);
- se scadono **30s** senza notifica → **disengage**: lo slot viene liberato e il LED spento.

## Cosa aggiunge rispetto allo Sprint 0
| Elemento | Costrutto qak | Fonte del pattern |
|----------|---------------|-------------------|
| notifica del sensor dell'IOPort | `Event containerInPlace : containerInPlace(WHERE)` + `whenEvent` | `firefly100.qak`, `griddisplay.qak` |
| attesa con scadenza (engaged) | `State waitContainer` + `Transition whenEvent ... whenTime 30000 ...` | `firefly100.qak` (whenTime+whenEvent insieme) |
| timeout → libera lo slot | `State disengage` → `hold.freeSlot(Slot)` | requisito (e.g. 30 secs) |
| sensor simulato | `QActor iosensor` che dopo un delay fa `emit containerInPlace` | `demoSendReceiveEmit.qak` (sender) |
| pushbutton simulato | `QActor pushbutton` che fa `request cargoservice -m loadrequest` | request/reply standard |

> Il sensor reale (sonar) rileva la presenza quando `D < DFREE/2` per ~3s. La notifica
> `containerInPlace` è la sua astrazione **logica**: il formato/trasporto fisico del sonar
> è dettaglio di **Progetto** (resta distinto dal `sonaralarm` di WEnv, estraneo all'app).

## Struttura
```
cargoservice26/
  src/cargoservice26.qak              modello (FSM) + iosensor e pushbutton simulati
  utils/domain/Hold.java              stato della stiva (POJO) + posizioni dalla mappa
  utils/devices/DisplaySim.java       display simulato (output a video)
  utils/devices/LedSim.java           LED simulato (indicatore di stato)
  utils/test/TestHold.java            test di unita' (ESEGUITO: 20 PASS, 0 FAIL, incluso il disengage)
  build.gradle  settings.gradle       build (come robotsmart26usage)
```

## Test eseguito (POJO)
```
cd TemaFinale26/Sprint1/cargoservice26
javac -d build/testclasses utils/domain/Hold.java utils/test/TestHold.java
java  -cp build/testclasses test.TestHold        =>  TestHold (Sprint1): 20 PASS, 0 FAIL
```
Il test T8 verifica la logica del **disengage da timeout** (TP4): lo slot riservato torna
libero e di nuovo riservabile.

## Come generare ed eseguire
1. Aprire il progetto nell'IDE col **plugin QAK** e generare il codice Kotlin da
   `src/cargoservice26.qak` (context `ctxcargo` → `it.unibo.ctxcargo.MainCtxcargoKt`).
2. Avviare l'infrastruttura: `docker compose -f ../../../robotsmart26/yamls/unibobasic26.yaml up`
   e `robotsmart26` (`gradlew run`, porta 8020).
3. `gradlew run` di questo progetto. Demo: il `pushbutton` invia la `loadrequest`, l'`iosensor`
   notifica dopo 5s → ciclo completo. Per il TIMEOUT: portare il delay dell'iosensor a > 30s.

> Stato: i POJO sono **compilati e testati** (`TestHold`: 20 PASS, 0 FAIL). Il **modello qak**
> e' scritto con sintassi allineata ai sorgenti del corso (`firefly100.qak`,
> `demoSendReceiveEmit.qak`, `robotsmart26tf26.qak`); generazione `.kt` (plugin QAK), build ed
> esecuzione end-to-end (con robotsmart) = passo successivo.
