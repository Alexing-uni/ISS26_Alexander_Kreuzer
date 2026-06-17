# cargoservice26 — modello QAK (Sprint 3, sistema finale)

Posizione: `TemaFinale26/Sprint3/cargoservice26/`.

Automazione del carico di container nella stiva con un **Differential Drive Robot** (cargorobot =
`robotsmart`), modellata come sistema distribuito di **attori QAK** + boundary realizzati come POJO.

## Cosa fa (requisiti del committente)
- **IOPort = web-gui interattiva** (`webgui/ioport.html`), **servita e aperta** dall'applicazione
  (`devices.WebIoPort`, server HTTP del JDK su :8095): **pushbutton** (evento `loadrequest`),
  **sensor** giocabile (slider → `distance(D)`), **display** (risposta + stato della stiva).
- **risposte**: `reserved(slot)` / `retrylater` / `reject` (stiva piena); **LED** lampeggia mentre engaged.
- **sensor (sonar)**: container presente se `D<DFREE/2` per ~3s; se non arriva entro 30s → `disengage`.
- **Out of service**: se `D>DFREE` per ≥3s (sospetto guasto) → richieste `retrylater`; rientro automatico.
- **trasporto** IOPort → **slot5 (marcatura)** → slot riservato → HOME; il **marker** (attore) assegna un
  **barcode** e **segnala** `markingDone(BARCODE)`; il barcode è **memorizzato** e mostrato sul display
  (`slot1=PIENO(bc1)`).
- **robustezza**: `moverobotfailed` → ritenta / libera lo slot; notifiche stale scartate.
- **canali MQTT separati**: pushbutton → `cargoservice26in_out`, sensor → `cargoservice26sonar`
  (ogni messaggio parsato da UN solo attore; il runtime tuProlog non è thread-safe).

## Struttura
```
cargoservice26/
  src/cargoservice26.qak              modello (FSM) completo del sistema
  src/it/unibo/cargoservice/          Cargoservice.kt   (FSM: evalRequest/waitContainer/marking/.../doneRequest)
  src/it/unibo/marker/                Marker.kt         (domark -> markingDone(bcN))
  src/it/unibo/sensormonitor/         Sensormonitor.kt  (dal dato grezzo distance(D) alle notifiche logiche)
  src/it/unibo/tester/                Tester.kt         (TP1 automatizzato; usa devices.IoPortSim)
  src/it/unibo/ctxcargo/              MainCtxcargo.kt (demo)  MainCtxcargotp1.kt (TP1)
  utils/domain/Hold.java              stato della stiva (slot, barcode, posizioni sulla mappa del DDR)
  utils/devices/                      WebIoPort.java DisplayMqtt.java LedSim.java IoPortSim.java
  utils/test/TestHold.java            test di unità del POJO (28 PASS)
  webgui/ioport.html                  web-gui dell'IOPort (pushbutton + sensor + display)
  cargoservice26.pl                   config DEMO interattiva (cargoservice + sensormonitor + marker)
  cargoservice26tp1.pl                config TEST automatizzato (aggiunge il tester)
  demo.ps1 (in ../)                   script che avvia tutto in ordine
```

## Prerequisiti
- **Docker Desktop** avviato (fornisce VirtualRobot/WEnv + GUI + broker **mosquitto**).
- JDK 17, il Gradle wrapper incluso.
- **mosquitto fantasma**: se sul PC gira un mosquitto NATIVO di Windows occupa `127.0.0.1:1883` al posto
  di quello Docker, e il pushbutton della web-gui (che parla col Docker via websocket :9001) non raggiunge
  il cargoservice. Fix (una volta, PowerShell admin): `Stop-Service mosquitto; Set-Service mosquitto -StartupType Disabled`.
  Alternativa senza admin: in `cargoservice26.pl` mettere l'IP di sito al posto di `127.0.0.1`.

---

## Come eseguire — TUTTE le possibilità

### A) Script automatico (consigliato)
```
cd TemaFinale26/Sprint3
powershell -ExecutionPolicy Bypass -File demo.ps1
```
Avvia: Docker + listener websocket 9001 → (ri)crea la scena WebGL e la apre → robotsmart26 (8020) →
cargoservice26 (apre la web-gui :8095). Avvisa se rileva il mosquitto fantasma.

### B) A mano, passo-passo (demo interattiva)
```
# 1) infrastruttura Docker (VirtualRobot + GUI + mosquitto)
docker compose -f ../../robotsmart26/yamls/unibobasic26.yaml up -d
#    abilitare il websocket :9001 di mosquitto (una volta):
docker exec mosquitto sh -c "printf '\nlistener 1883\nprotocol mqtt\n\nlistener 9001\nprotocol websockets\n\nallow_anonymous true\n' >> /mosquitto/config/mosquitto.conf"
docker restart mosquitto

# 2) APRIRE la scena WebGL e TENERLA VISIBILE/IN PRIMO PIANO (esegue i movimenti del robot)
#    http://localhost:8090   (NB: una sola scheda; se il robot inizia a fallire gli step,
#    ricreare il container: docker compose ... up -d --force-recreate wenv)

# 3) robotsmart26 (servizio del docente, porta 8020) — attendere "at home. GUI on"
cd ../../robotsmart26 ;  ./gradlew run

# 4) cargoservice26: APRE da solo la web-gui su http://localhost:8095
cd ../TemaFinale26/Sprint3/cargoservice26 ;  ./gradlew run
```

### C) Usare la web-gui (interazione del committente)
Nella pagina `http://localhost:8095` (la apre l'app):
- premere **PREMI** → `reserved slotX` sul display;
- portare lo **slider** a ~20 (container presente) e lasciarlo → ~3s → il robot trasporta → `slotX=PIENO`;
- per **Out of service**: slider a ~95 per qualche secondo; per il rientro: tornare a ~45;
- ripetere per riempire tutta la stiva (slot1..slot4).
> Tenere la **scena (8090) visibile** mentre il robot si muove.

### D) Prova TP1 automatizzata (senza interazione umana)
```
cd TemaFinale26/Sprint3/cargoservice26 ;  ./gradlew runTp1
```
Il tester emette da solo pushbutton + sensor (via `IoPortSim`, canali separati) e interroga lo stato:
stampa `*** TP1 PASS ***` / `*** TP1 FAIL ***`.

### E) Test di unità del POJO (nessuna dipendenza esterna)
```
cd TemaFinale26/Sprint3/cargoservice26
javac -d build/testclasses utils/domain/Hold.java utils/test/TestHold.java
java  -cp build/testclasses test.TestHold        #  -> 28 PASS, 0 FAIL
```

### F) Solo build
```
cd TemaFinale26/Sprint3/cargoservice26 ;  ./gradlew build     #  -> BUILD SUCCESSFUL
```

---

## Requisiti del committente → realizzazione (Sprint 3)
| Requisito (tema del docente) | Dove / come |
|---|---|
| richiesta col **pushbutton** dell'IOPort | bottone della web-gui → EVENT `loadrequest` |
| **display**: risposta + stato della stiva | `display.show(reserved/retrylater/reject)` e `hold.stateDescription()` (web-gui via MQTT) |
| risposte **reserved / retrylater / reject** | `evalRequest` + `Hold.reserveFreeSlot()/isFull()` |
| **LED** lampeggia mentre engaged | `led.blink(true/false)` (`LedSim`) |
| **sensor (sonar)**, container entro ~30s o disengage | `sensormonitor` (D<DFREE/2 per ~3s → `containerInPlace`) + `waitContainer whenTime 30000 → disengage` |
| **Out of service** se D>DFREE per ≥3s | `sensormonitor` → `outofservice(on)`; stato `outOfService` → `retrylater`; rientro `outofservice(off)` |
| **trasporto** IOPort → slot5 → slot (cargorobot) | client di `robotsmart` (`moverobot`), stati `goToIOPort/carryToSlot5/carryToReserved/goHome` |
| **marker**: barcode + **segnala** il completamento | attore `marker` (`domark` → `markingDone(BARCODE)`); barcode memorizzato in `Hold` e mostrato sul display |
| robustezza ai fallimenti del robot | `retryIOPort/retrySlot5/retryReserved` (max 2) → `robotFailure` libera lo slot |

**Evoluzione rispetto al Project (Sprint 1) e alla revisione del docente (Sprint 2):**
- l'`iosensor` (QActor che filtrava il sonar) diventa il `sensormonitor`, alimentato dalla **web-gui
  giocabile** invece che dal mock Python hard-coded (richiesta del docente: *sensor con cui giocare,
  non hard-coded*);
- l'IOPort diventa **web-gui interattiva servita e aperta dall'app** (richiesta del docente: *bottone +
  la pagina la apre l'applicazione*);
- il punto aperto **D1** del Project (posizioni slot *da confermare sulla mappa*) è **risolto**:
  posizioni **confermate sulla mappa del committente** e verificate col pianificatore A* —
  `slot3=(3,1)` `slot4=(3,4)` (carico) e `slot5=(2,5)` (marcatura); le provvisorie `(3,2)/(5,3)`
  cadevano su ostacoli (per quello il robot non andava nel posto giusto).

## Demo di difesa (colloquio, ~10-15 min) — passo passo
1. **Prima della difesa**: avvia tutto (`demo.ps1` oppure i passi B). Verifica: robot **at home**,
   scena **8090 visibile**, web-gui aperta su **8095** ("connesso al broker" in verde), bodega vuota.
2. **Presenta l'architettura** (slide 6): sistema distribuito di attori; IOPort = boundary realizzato
   come web-gui; `robotsmart` = service esterno del docente.
3. **Carico nominale** (slide 5 + live): nella web-gui premi **PREMI** → display `reserved slot1`, LED on.
   Abbassa lo **slider** a ~20 → a ~3s il robot trasporta IOPort → slot5 (marcatura `bc1`) → slot1 → HOME
   → display `slot1=PIENO(bc1)`. Spiega: la risposta è **sul display**, il barcode lo assegna il **marker**.
4. **Riempi la stiva**: ripeti per slot2, slot3, slot4 → `slot1..slot4 = PIENO` (mostra le coordinate corrette).
5. **Out of service**: slider a ~95 per qualche secondo → display `Out of service`; premi PREMI → `retrylater`.
   Riporta lo slider a ~45 → `serviceworking`.
6. **Timeout**: premi PREMI (`reserved`), **non** toccare il sensore → dopo 30s `disengage` (slot liberato).
7. **(facoltativo) TP1 automatizzato**: in un'altra finestra `./gradlew runTp1` → `*** TP1 PASS ***`.
8. **Discussione "da ingegneri"**: perché web-gui + MQTT/websocket (trasparenza del dispositivo, il PicoW
   reale sostituisce la GUI), **canali separati** pushbutton/sensor, **ruolo dell'analisi** (prima il
   tipo del componente, poi la tecnologia), e il **finding** sulle coordinate (rigore del testing).
> Tieni la **scena (8090) visibile** durante i movimenti del robot.

---

## Note operative (affidabilità della scena WebGL)
- **Una sola** scheda della scena (8090); più schede = robot desincronizzato.
- Tenerla **in primo piano/visibile**: in background il WebGL rallenta e gli step possono fallire.
- Dopo molti riavvii il robot può degradarsi (`asynchstep failed`): **ricreare** il container
  (`docker compose ... up -d --force-recreate wenv`), non solo `restart`.

## Posizioni degli slot (mappa del committente)
**Confermate sulla mappa del committente** (`robotsmart26/tf25map.txt`, X=riga Y=colonna) e
verificate col pianificatore A* del robot:
`slot1=(1,1) slot2=(1,4) slot3=(3,1) slot4=(3,4)` (slot di carico) e `slot5=(2,5)` (area di marcatura).
Le provvisorie `slot3=(3,2)` e `slot4=(5,3)` cadevano su ostacoli (A* → piano vuoto, il robot non
arrivava nel posto giusto).
