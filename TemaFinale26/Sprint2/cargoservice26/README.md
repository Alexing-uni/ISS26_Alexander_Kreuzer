# cargoservice26 — modello qak (Sprint 2)

Posizione: `TemaFinale26/Sprint2/cargoservice26/` (sottosistema dello Sprint 2).

Evoluzione dello **Sprint 1** (core + sensor + timeout 30s): aggiunge **Out of service**
e il **display come web-gui**.

- il sensor (sonar) dell'IOPort produce il **dato grezzo** `sonaralarm : distance(D)`
  nel **formato del PicoW reale** (`msg(sonaralarm,event,picow,none,distance(D),0)`,
  vedi `Picow/Sonar/sonarMqtt.py`): il dispositivo reale sostituisce il simulato
  **senza modifiche** al resto del sistema;
- il **sensormonitor** (nuovo attore) traduce il flusso di distanze in notifiche logiche:
  `D < DFREE/2` per ~3s → `containerInPlace`; `D > DFREE` per ≥3s → `outofservice(on)`;
  rientro sostenuto → `outofservice(off)` (DFREE=60: parametro di calibrazione);
- il cargoservice in **Out of service** risponde `retrylater` (requisito) e il display
  mostra `Out of service`; al rientro `serviceworking`;
- **display web-gui**: `DisplayMqtt` pubblica su MQTT (`cargoservice26display`);
  la pagina `webgui/display.html` (mqtt.js su websocket :9001) lo mostra nel browser.

## Struttura
```
cargoservice26/
  src/cargoservice26.qak              modello (FSM) + sensormonitor + iosensor/pushbutton simulati
  src/it/unibo/cargoservice/          Cargoservice.kt    (FSM: stati OOS + core Sprint1)
  src/it/unibo/sensormonitor/         Sensormonitor.kt   (dal dato grezzo alle notifiche logiche)
  src/it/unibo/iosensor/              Iosensor.kt        (sonar simulato stile PicoW, scenario demo)
  src/it/unibo/pushbutton/            Pushbutton.kt      (3 richieste nello scenario)
  src/it/unibo/ctxcargo/              MainCtxcargo.kt    (main del context, porta 8030)
  cargoservice26.pl  sysRules.pl      descrizione del sistema (contexts, qactor, msg)
  utils/domain/Hold.java              stato della stiva (POJO)
  utils/devices/DisplayMqtt.java      display web-gui (MQTT, fallback a video)
  utils/devices/LedSim.java           LED simulato (indicatore di stato)
  utils/test/TestHold.java            test di unita' (ESEGUITO: 20 PASS, 0 FAIL)
  webgui/display.html                 pagina del display (mqtt su websocket :9001)
  build.gradle ... gradlew            build (pattern robotsmart26usage, wrapper 8.6)
```

## Scenario della demo (TP1 + TP5 + TP6 in un'unica run)
```
t=1s   pushbutton(1) -> reserved(slot1), LED on (engaged)
t=5s   sonar: D=20 per ~3s -> containerInPlace -> trasporto IOPort->slot5->slot1->HOME
t=65s  sonar: D=90 per ~3s -> outofservice(on) -> display "Out of service"
t=75s  pushbutton(2) -> retrylater                                  (TP5)
t=85s  sonar: D=45 per ~3s -> outofservice(off) -> "serviceworking"
t=91s  pushbutton(3) -> reserved -> secondo ciclo completo          (TP6)
```
NB: le notifiche che arrivano **durante un trasporto** restano in coda (discardMsg Off)
e sono servite in ordine al ritorno in `available`: la sequenza logica e' preservata
anche se il trasporto dura piu' del previsto.

## Come eseguire
1. Infrastruttura: `docker compose -f ../../../robotsmart26/yamls/unibobasic26.yaml up`
   (VirtualRobot + GUI + mosquitto).
2. **Aprire la scena WebGL: `http://localhost:8090`** e tenerla **in primo piano**
   (la scena ESEGUE i movimenti: senza pagina aperta i moverobot non terminano;
   in background il browser puo' rallentare i frame e far fallire gli step).
3. `cd ../../../robotsmart26 ; ./gradlew run` (servizio del docente, porta 8020).
4. In questa cartella: `./gradlew run` (scenario automatico ~3 min).
5. Aprire `webgui/display.html` nel browser (display IOPort web-gui).

> Robustezza verificata: se un `moverobot` fallisce (es. sonar-safety del DDR),
> il cargoservice **libera lo slot e torna disponibile** (`moverobotfailed` →
> `robotFailure`); la richiesta si puo' rilanciare.
