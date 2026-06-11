# TemaFinale26 — cargoservice (Progetto Finale)

Progetto finale del corso ISS 2025/2026 (Protobook, cap. 31).
Sistema **cargoservice** che automatizza il carico di container nella stiva di una
nave tramite un Differential Drive Robot (*cargorobot*), con IOPort (pushbutton +
display), un *sensor* (sonar) associato all'IOPort, marcatura in slot5 e LED indicatore.

Studente: **Alexander Kreuzer** (Erasmus) — sviluppo individuale.

## Organizzazione del sistema finale in Sprint (Protobook §32.1)

Ogni Sprint è la **costruzione di un sottosistema compiuto** del sistema finale ed è una
cartella a sé. Dentro ogni cartella: il **doc** (`SprintN_vX`, sezioni del
[templateToFill.html](https://anatali.github.io/issLab2026/_static/templateToFill.html)) e,
quando previsto, il **sottosistema di codice** (modello qak + POJO + build, eseguibile/testato).
Convenzione del corso (come `ConwayLife/SprintN/...`).

| Sprint | Sottosistema compiuto | Documento (`_vN`) | Codice |
|--------|-----------------------|-------------------|--------|
| **Sprint 0 (core)** | flusso nominale: `loadrequest` → riserva slot → IOPort → slot5 (marcatura) → slot riservato → HOME (container assunto presente) | [Sprint0_v1.html](Sprint0/userDocs/Sprint0_v1.html) · [pdf](Sprint0/userDocs/Sprint0_v1.pdf) | [cargoservice26/](Sprint0/cargoservice26) (qak + POJO testati) |
| **Sprint 1** | analisi del problema (natura pojo/service/attore, scelta DDR motivata, interazioni) + **sensor dell'IOPort** (`containerInPlace`) + **timeout 30s → disengage** | [Sprint1_v1.html](Sprint1/userDocs/Sprint1_v1.html) · [pdf](Sprint1/userDocs/Sprint1_v1.pdf) | [cargoservice26/](Sprint1/cargoservice26) (qak + POJO: 20 PASS) |
| **Sprint 2** | + **Out of service** (sensormonitor dal dato grezzo `distance(D)` stile PicoW; retrylater + display "Out of service"; rientro) + **display web-gui** (MQTT/websocket) | [Sprint2_v1.html](Sprint2/userDocs/Sprint2_v1.html) · [pdf](Sprint2/userDocs/Sprint2_v1.pdf) | [cargoservice26/](Sprint2/cargoservice26) (demo TP1+TP5+TP6) |
| Sprint 3 | rifiniture (marker/barcode come attore, tester TP1 automatizzato) | _in lavorazione_ | — |

## Struttura delle cartelle

```
TemaFinale26/
├── README.md                      questo file (organizzazione in Sprint)
├── Sprint0/                       SOTTOSISTEMA: core business
│   ├── cargoservice26/            codice (eseguibile/testato)
│   │   ├── src/cargoservice26.qak     modello (FSM) del cargoservice
│   │   ├── utils/domain/Hold.java     stato della stiva (POJO) + posizioni dalla mappa del DDR
│   │   ├── utils/devices/DisplaySim.java   display simulato
│   │   ├── utils/devices/LedSim.java       LED simulato (indicatore di stato)
│   │   ├── utils/test/TestHold.java        test di unità (ESEGUITO: 16 PASS, 0 FAIL)
│   │   └── build.gradle  settings.gradle   build (pattern di robotsmart26usage)
│   └── userDocs/
│       ├── Sprint0_v1.html / .pdf          doc del corso (8 sezioni del template)
│       └── css/  img/
└── Sprint1/                       SOTTOSISTEMA: core + sensor dell'IOPort + timeout 30s
    ├── cargoservice26/            codice evoluto (eseguibile/testato)
    │   ├── src/cargoservice26.qak     FSM + Event containerInPlace + whenTime 30s + disengage
    │   ├── utils/...                  Hold, DisplaySim, LedSim
    │   ├── utils/test/TestHold.java   test (ESEGUITO: 20 PASS, 0 FAIL, incluso disengage)
    │   └── build.gradle  settings.gradle
    └── userDocs/
        ├── Sprint1_v1.html / .pdf
        └── css/  img/
```

I POJO sono **compilati e testati** (Sprint 0: **16 PASS**; Sprint 1: **20 PASS**, con il disengage).
I documenti contengono **solo link** a parti di codice corrette sintatticamente ed
eseguite/testate, come richiesto.

## Come importarlo nel tuo computer

Il progetto vive nel repo GitHub. Sulla tua macchina:

```
# 1) prendere l'ultima versione (sei già nel repo locale)
git pull origin main

# 2) il sottosistema del core è in:
cd TemaFinale26/Sprint0/cargoservice26

# 3) compilare ed eseguire il test dei POJO (nessuna dipendenza esterna):
javac -d build/testclasses utils/domain/Hold.java utils/devices/DisplaySim.java utils/devices/LedSim.java utils/test/TestHold.java
java  -cp build/testclasses test.TestHold        # => TestHold: 16 PASS, 0 FAIL

# 4) generare il codice Kotlin dal modello qak (nell'IDE Eclipse col PLUGIN QAK):
#    aprire src/cargoservice26.qak  ->  genera context ctxcargo  ->  it.unibo.ctxcargo.MainCtxcargoKt
#    poi:  ./gradlew build
```

> Il passo 4 (generazione `.kt`) richiede l'**IDE Eclipse con il plugin QAK** del corso
> (lo stesso usato per `robotsmart26usage`); non è uno step da riga di comando.

## Esecuzione del sistema (dopo la generazione del .kt)

```
(dalla cartella TemaFinale26/Sprint0/cargoservice26/)
1) docker compose -f ../../../robotsmart26/yamls/unibobasic26.yaml up   (VR + GUI + mosquitto)
2) cd ../../../robotsmart26 ; ./gradlew run                            (servizio del docente, 8020)
3) (in Sprint0/cargoservice26)  ./gradlew run                          (cargoservice26, ctxcargo 8030)
4) inviare una loadrequest dal caller del pushbutton
```

Con mosquitto in Docker usare l'**IP reale** del PC (non `localhost`). I dispositivi
(sensor, display, LED, marker) sono simulati: dipendendo il cargoservice solo
dall'informazione scambiata, sono sostituibili con l'hardware reale senza modifiche.
