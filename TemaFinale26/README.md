# TemaFinale26 — cargoservice (Progetto Finale)

Progetto finale del corso ISS 2025/2026 (Protobook, cap. 31).
Sistema **cargoservice** che automatizza il carico di container nella stiva di una
nave tramite un Differential Drive Robot (*cargorobot*), con IOPort (pushbutton +
display + sonar), marcatura in slot5 e LED.

Studente: **Alexander Kreuzer** (Erasmus) — sviluppo individuale.

## Consegna per fasi (stile Scrum)

I documenti vengono pubblicati **una fase alla volta**, man mano che vengono consegnati
al docente. I PDF sono forniti per consentire al docente di aggiungere note.

| Fase | Documento | HTML | PDF |
|------|-----------|------|-----|
| **Sprint 0 — Requisiti** | Analisi dei Requisiti | [html](userDocs/TemaFinale26_AnalisiRequisiti.html) | [pdf](userDocs/TemaFinale26_AnalisiRequisiti.pdf) |
| **Sprint 1 — Problema** | Analisi del Problema | [html](userDocs/TemaFinale26_AnalisiProblema.html) | [pdf](userDocs/TemaFinale26_AnalisiProblema.pdf) |
| **Project** | Architettura e Progetto | _in preparazione_ | _in preparazione_ |
| **Development** | codice `cargoservice26` | _da fare_ | — |
| **Prototypes** | demo + Test Plan automatizzato | _da fare_ | — |

## Struttura prevista (dalla fase Project)

```
TemaFinale26/
  userDocs/                      documenti (html + pdf) delle fasi
  src/cargoservice26.qak         modello qak (fase Development)
  utils/domain/Hold.java         stato della stiva (slot1..5)
  utils/devices/DisplaySim.java  display simulato
  utils/devices/LedSim.java      LED simulato
  build.gradle settings.gradle   build (modellato su robotsmart26usage)
```

## Esecuzione (dalla fase Project)

```
1) docker compose -f ../robotsmart26/yamls/unibobasic26.yaml up   (VR + GUI + mosquitto)
2) cd ../robotsmart26 ; ./gradlew run                              (servizio del docente, 8020)
3) ./gradlew run                                                  (cargoservice26)
4) cd ../Picow/mock ; python mockPicowRadar.py --broker <IP-reale>  (sonar simulato)
```

Il sonar fisico (PicoW) e' sostituito dal mock Python `mockPicowRadar.py`, che pubblica
lo stesso messaggio MQTT `sonardata(D)` del dispositivo reale.
