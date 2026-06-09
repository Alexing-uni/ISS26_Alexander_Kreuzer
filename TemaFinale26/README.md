# TemaFinale26 — cargoservice (Progetto Finale)

Progetto finale del corso ISS 2025/2026 (Protobook, cap. 31).
Sistema **cargoservice** che automatizza il carico di container nella stiva di una
nave tramite un Differential Drive Robot (*cargorobot*), con IOPort (pushbutton +
display), un *sensor* (sonar) associato all'IOPort, marcatura in slot5 e LED indicatore.

Studente: **Alexander Kreuzer** (Erasmus) — sviluppo individuale.

## Organizzazione del sistema in Sprint (stile Scrum)

Ogni Sprint costruisce un **sottosistema compiuto** del sistema finale. Ogni documento
segue il template del corso (Requirements, Requirement analysis, Problem analysis, Test
plans, Project, Testing, Deployment, Maintenance) ed è versionato `_vN`.

| Sprint | Sottosistema | Documento | Modello qak |
|--------|--------------|-----------|-------------|
| **Sprint 0 (core)** | flusso nominale IOPort→slot5(marcatura)→slot riservato→HOME (container assunto presente) | [Sprint0_v1.html](userDocs/Sprint0_v1.html) · [pdf](userDocs/Sprint0_v1.pdf) | [cargoservice26.qak](cargoservice26/src/cargoservice26.qak) |
| **Sprint 1** | analisi del problema + sensor dell'IOPort + timeout 30s | [Sprint1_v1.html](userDocs/Sprint1_v1.html) · [pdf](userDocs/Sprint1_v1.pdf) | — |
| Sprint 2 | + Out of service (guasto sensor) + display web-gui | _da fare_ | — |
| Sprint 3 | rifiniture (marker/barcode, richieste concorrenti) | _da fare_ | — |

## Modello qak (codice)

```
cargoservice26/
  src/cargoservice26.qak              modello (FSM) del cargoservice — core (Sprint 0)
  utils/domain/Hold.java              stato della stiva (POJO) + posizioni dalla mappa del DDR
  utils/devices/DisplaySim.java       display simulato (output a video)
  utils/devices/LedSim.java           LED simulato (indicatore di stato)
  build.gradle  settings.gradle       build (pattern di robotsmart26usage)
```

Il cargoservice è un **QActor** client di `robotsmart` (`moverobot(X,Y)`); generazione del
codice `.kt` col plugin QAK nell'IDE. Contratto preso da `robotsmart26usage/src/robotsmart26tf26.qak`.
Dettagli e stato in [cargoservice26/README.md](cargoservice26/README.md).

## Esecuzione

```
1) docker compose -f ../robotsmart26/yamls/unibobasic26.yaml up   (VR + GUI + mosquitto)
2) cd ../robotsmart26 ; ./gradlew run                              (servizio del docente, 8020)
3) cd cargoservice26  ; ./gradlew run                              (cargoservice26, ctxcargo 8030)
4) inviare una loadrequest dal caller del pushbutton
```

Con mosquitto in Docker usare l'**IP reale** del PC (non `localhost`). I dispositivi
(sensor, display, LED, marker) sono simulati: dipendendo il cargoservice solo
dall'informazione scambiata, sono sostituibili con l'hardware reale senza modifiche.
