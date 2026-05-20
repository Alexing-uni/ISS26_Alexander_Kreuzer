# Mock del PicoW Radar

Simulatore software del Raspberry PicoW (Radar con sonar HC-SR04) per il
sistema **DDRBoundary**, da eseguire sul PC al posto dell'hardware fisico.

Pubblica sul broker MQTT gli stessi messaggi del PicoW reale
(vedi `../sonarMqtt.py`):

```
topic   : mqttdemotopic
payload : msg(sonardata,event,picow,none,sonardata(D),0)
```

Simula un intruso che si avvicina e si allontana ciclicamente, cosi' da
collaudare la reazione del `boundaryworker` agli eventi
`intruderClose` / `intruderGone` senza hardware.

## Prerequisiti

```bash
# 1. broker MQTT (mosquitto) in esecuzione
mosquitto -p 1883 -v

# 2. libreria paho-mqtt
pip install paho-mqtt
```

## Uso

```bash
python mockPicowRadar.py
# oppure con parametri
python mockPicowRadar.py --broker 127.0.0.1 --port 1883 --dmin 30 --period 1
```

Per fermarlo: `Ctrl+C`.

## Esempio di output

```
Mock PicoW Radar connesso a 127.0.0.1:1883, topic 'mqttdemotopic', DMIN=30 cm
  D=100 cm  [via libera   ]   pub: msg(sonardata,event,picow,none,sonardata(100),0)
  D= 90 cm  [via libera   ]   pub: msg(sonardata,event,picow,none,sonardata(90),0)
  ...
  D= 20 cm  [INTRUSO VICINO   <-- cambio stato]   pub: msg(...sonardata(20)...)
  D=  8 cm  [INTRUSO VICINO]   pub: msg(...sonardata(8)...)
  ...
  D=120 cm  [via libera      <-- cambio stato]   pub: msg(...sonardata(120)...)
```

## Come si integra nel sistema

```
+-------------------+        MQTT          +-----------+
| mockPicowRadar.py | -------------------> | mosquitto |
| (questo script)   |   sonardata(D)       |  :1883    |
+-------------------+                      +-----+-----+
                                                 |
                                                 | subscribe topic
                                                 v
                                       +---------------------+
                                       |  boundaryworker     |
                                       |  (radaractor)       |
                                       |  D<DMIN -> intruderClose
                                       |  D>=DMIN-> intruderGone
                                       +---------------------+
```

Quando si avra' il PicoW fisico, bastera' spegnere questo mock e
accendere il Raspberry con `../sonarMqtt.py`: i messaggi sono identici,
quindi il `boundaryworker` non richiede alcuna modifica.
