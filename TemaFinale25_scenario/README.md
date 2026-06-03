# Scenario TemaFinale25 (cargoservice)

Ambiente di esecuzione **pronto all'uso** per il progetto finale. Quando arriva
l'enunciato, basta scrivere il `cargoservice26`: l'infrastruttura (robot virtuale,
GUI mappa, broker MQTT, sonar mock, robot "smart") e' gia' configurata e verificata.

---

## Prerequisiti (una volta)

| Cosa | Verifica |
|------|----------|
| **Docker Desktop** avviato (icona 🐳 "Engine running") | `docker ps` risponde senza errori |
| **Python 3** + `paho-mqtt` | `pip install paho-mqtt` |
| **JDK 17+** (per gradlew) | gia' usato nei progetti del corso |

Le immagini Docker (`virtualrobotdisi25:2.2`, `robotoutgui25:1.0`,
`eclipse-mosquitto:1.6.15`) sono gia' scaricate.

---

## Avvio rapido

### 1. Infrastruttura Docker (un comando)
In PowerShell:
```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\TemaFinale25_scenario
.\start-scenario.ps1
```
Avvia **VirtualRobot + GUI mappa + mosquitto** e stampa lo stato e gli URL.

Apri nel browser:
- **http://localhost:8090** — scena 3D del robot
- **http://localhost:8085** — GUI mappa mentale

### 2. Robot "smart" (terminale A)
```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26
.\gradlew run
```
Espone il servizio su **porta 8020**; comanda il VirtualRobot e pianifica i percorsi
(`moverobot(X,Y,STEPTIME)`).

### 3. Sonar mock = PicoW (terminale B)
```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\Picow\mock
python mockPicowRadar.py
```
Pubblica `sonardata(D)` sul topic MQTT `mqttdemotopic` (broker `localhost:1883`),
identico al PicoW reale.

### 4. cargoservice26 (terminale C) — *dopo aver ricevuto l'enunciato*
Il componente da realizzare. Comandera' il robot via `moverobot`, ascoltera' il
sonar, gestira' gli slot della stiva.

---

## Ordine di avvio (importante)

```
1. start-scenario.ps1   (Docker: VR + GUI + mosquitto)   <-- PRIMA
2. robotsmart26          (gradlew run, porta 8020)
3. mock PicoW            (python, sonar)
4. cargoservice26        (progetto finale)               <-- ULTIMO
```

---

## Checklist "e' tutto su?"

| Servizio | Come verificare | Atteso |
|----------|-----------------|--------|
| VirtualRobot | apri http://localhost:8090 | scena 3D |
| GUI mappa | apri http://localhost:8085 | griglia celle |
| mosquitto | `docker logs mosquitto` | "running" su 1883 |
| robotsmart26 | log `gradlew run` | "robotsmart ... working" |
| mock PicoW | output console | righe `pub: msg(sonardata,...)` |

---

## Riferimenti utili (dal docente, June3)

**Mappa della stiva** (`robotsmart26/tf25map.txtok`, 6x7):
```
0000001
0011001
0000101
0011001
0000001
1111111
```
`1` = occupato (parete / slot permanente), `0` = libero.

**Posizioni note** (da `robotsmart26/utils/callers/Robotsmart26Cmds.java`):
```
moverobot(4,0,335)   ->  IOPort  (porta di ingresso/uscita)
moverobot(1,1,335)   ->  slot
moverobot(1,4,335)   ->  slot
moverobot(3,2,335)   ->  slot
moverobot(5,3,335)   ->  slot
```

**Contratto del robot smart** (`robotsmart26.qak`):
```
Request  moverobot     : moverobot(TARGETX, TARGETY, STEPTIME)
Reply    moverobotdone : moverobotok(ARG)                    for moverobot
Reply    moverobotfailed: moverobotfailed(PLANDONE, PLANTODO) for moverobot
```

I caller `Robotsmart26CallerCoap / Mqtt / Tcp` mostrano come inviare questi comandi.

---

## Stop

```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\TemaFinale25_scenario
.\stop-scenario.ps1
```

---

## Troubleshooting

| Sintomo | Causa | Soluzione |
|---------|-------|-----------|
| `docker ps` -> 500 / errore | Docker Desktop non avviato | apri Docker Desktop, attendi "Engine running" |
| `port is already allocated` | container/processo gia' attivo su quella porta | `.\stop-scenario.ps1` oppure `docker ps` e ferma il colpevole |
| `network iss-network not found` | rete mancante | lo script la crea da solo; in manuale: `docker network create iss-network` |
| robotsmart26 `ConnectException` | VR non ancora pronto | attendi che 8090 risponda, poi `gradlew run` |
| mock: `impossibile connettersi` | mosquitto non su | verifica `docker ps` (mosquitto), riavvia con lo script |
| mock: `manca paho-mqtt` | libreria assente | `pip install paho-mqtt` |
