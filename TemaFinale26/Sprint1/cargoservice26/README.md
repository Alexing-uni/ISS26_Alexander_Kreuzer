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
  src/it/unibo/cargoservice/          Cargoservice.kt   (codice del modello, pattern del generatore QAK)
  src/it/unibo/iosensor/              Iosensor.kt       (sensor simulato: emit containerInPlace)
  src/it/unibo/pushbutton/            Pushbutton.kt     (pushbutton simulato: loadrequest)
  src/it/unibo/ctxcargo/              MainCtxcargo.kt   (main del context, porta 8030)
  cargoservice26.pl  sysRules.pl      descrizione del sistema (contexts, qactor, msg)
  utils/domain/Hold.java              stato della stiva (POJO) + posizioni dalla mappa
  utils/devices/DisplaySim.java       display simulato (output a video)
  utils/devices/LedSim.java           LED simulato (indicatore di stato)
  utils/test/TestHold.java            test di unita' (ESEGUITO: 20 PASS, 0 FAIL, incluso il disengage)
  build.gradle  settings.gradle       build (come robotsmart26usage)
  gradlew  gradlew.bat  gradle/       Gradle wrapper 8.6 (del corso) + gradle.properties
```

> Il codice in `src/it/unibo/` e' scritto seguendo **esattamente il pattern del codice generato**
> dal plugin QAK nei progetti del corso (`sistemasqak`, `firefly`, `robotsmart26usage`); da
> rigenerare/validare col plugin nell'IDE a partire da `src/cargoservice26.qak`.

## Test eseguito (POJO)
```
cd TemaFinale26/Sprint1/cargoservice26
javac -d build/testclasses utils/domain/Hold.java utils/test/TestHold.java
java  -cp build/testclasses test.TestHold        =>  TestHold (Sprint1): 20 PASS, 0 FAIL
```
Il test T8 verifica la logica del **disengage da timeout** (TP4): lo slot riservato torna
libero e di nuovo riservabile.

## Come eseguire
1. Infrastruttura: `docker compose -f ../../../robotsmart26/yamls/unibobasic26.yaml up`
   (VirtualRobot + GUI + mosquitto).
2. `cd ../../../robotsmart26 ; ./gradlew run` (servizio del docente, porta 8020).
3. In questa cartella: `./gradlew run`. Demo: il `pushbutton` invia la `loadrequest`
   (→ `reserved(slot1)`, LED on), l'`iosensor` notifica `containerInPlace` dopo 5s →
   trasporto IOPort → slot5 → slot riservato → HOME. Per provare il **TIMEOUT/disengage**:
   portare il delay dell'`iosensor` a > 30s.

> NB (pattern del corso): il `cargoservice` fa `subscribe "cargoservice26in_out"` per percepire
> gli eventi emessi via MQTT (come `robotsmart26tf26.qak` con `robotsmart26in_out`).
