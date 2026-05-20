"""
mockPicowRadar.py
=================================================================
Simulatore (mock) del Raspberry PicoW che realizza il Radar del
sistema DDRBoundary, da eseguire sul PC al posto dell'hardware reale.

Pubblica sul broker MQTT gli STESSI messaggi del PicoW reale
(vedi Picow/sonarMqtt.py):

    topic   : mqttdemotopic
    payload : msg(sonardata,event,picow,none,sonardata(D),0)

dove D e' la distanza in cm.

Simula un 'intruso' che si avvicina e si allontana ciclicamente,
cosi' da poter collaudare la reazione del boundaryworker agli
eventi intruderClose / intruderGone senza hardware fisico.

-----------------------------------------------------------------
PREREQUISITI
-----------------------------------------------------------------
1) Broker MQTT in esecuzione sul PC. Con mosquitto:
       mosquitto -p 1883 -v
2) Libreria paho-mqtt:
       pip install paho-mqtt

-----------------------------------------------------------------
USO
-----------------------------------------------------------------
   python mockPicowRadar.py
   python mockPicowRadar.py --broker 127.0.0.1 --port 1883 --dmin 30

Per fermarlo: Ctrl+C
-----------------------------------------------------------------
"""

import argparse
import time
import sys

try:
    import paho.mqtt.client as mqtt
except ImportError:
    print("ERRORE: manca paho-mqtt. Installa con:  pip install paho-mqtt")
    sys.exit(1)


# ---- Configurazione di default (allineata a Picow/sonarMqtt.py) ----
DEFAULT_BROKER = "127.0.0.1"
DEFAULT_PORT   = 1883
TOPIC          = "mqttdemotopic"
CLIENT_ID      = "mockpicosonar"
DEFAULT_DMIN   = 30          # soglia 'intruso vicino' in cm
PERIOD_S       = 1.0         # un campione al secondo, come il PicoW reale


def build_msg(distance_cm: int) -> str:
    """Costruisce il payload identico a quello del PicoW reale."""
    value = "sonardata(%d)" % distance_cm
    return "msg(sonardata,event,picow,none," + value + ",0)"


def intruder_profile():
    """
    Generatore infinito di distanze (cm) che simula un intruso che
    si avvicina (100 -> 10), resta vicino qualche secondo, poi si
    allontana (10 -> 100). Ciclo ripetuto all'infinito.
    """
    while True:
        # avvicinamento
        for d in range(100, 8, -10):
            yield d
        # intruso fermo molto vicino per 4 cicli
        for _ in range(4):
            yield 8
        # allontanamento
        for d in range(10, 110, 10):
            yield d
        # nessun intruso per 4 cicli
        for _ in range(4):
            yield 120


def main():
    parser = argparse.ArgumentParser(description="Mock del PicoW Radar (sonar HC-SR04) via MQTT")
    parser.add_argument("--broker", default=DEFAULT_BROKER, help="indirizzo del broker MQTT")
    parser.add_argument("--port",   type=int, default=DEFAULT_PORT, help="porta del broker MQTT")
    parser.add_argument("--dmin",   type=int, default=DEFAULT_DMIN, help="soglia DMIN (cm) per intruso vicino")
    parser.add_argument("--period", type=float, default=PERIOD_S, help="intervallo tra campioni (secondi)")
    args = parser.parse_args()

    client = mqtt.Client(client_id=CLIENT_ID)
    try:
        client.connect(args.broker, args.port, keepalive=60)
    except Exception as e:
        print(f"ERRORE: impossibile connettersi al broker {args.broker}:{args.port}")
        print(f"  -> {type(e).__name__}: {e}")
        print("  Assicurati che mosquitto sia in esecuzione (es:  mosquitto -p 1883 -v)")
        sys.exit(1)

    print(f"Mock PicoW Radar connesso a {args.broker}:{args.port}, topic '{TOPIC}', DMIN={args.dmin} cm")
    print("Premi Ctrl+C per fermare.\n")

    client.loop_start()
    prev_close = None

    try:
        for d in intruder_profile():
            msg = build_msg(d)
            client.publish(TOPIC, msg)

            # Stato logico solo per chiarezza a video (la decisione vera
            # la prende il boundaryworker confrontando D con DMIN)
            is_close = d < args.dmin
            tag = "INTRUSO VICINO" if is_close else "via libera   "
            if is_close != prev_close:
                tag += "   <-- cambio stato"
                prev_close = is_close
            print(f"  D={d:3d} cm  [{tag}]   pub: {msg}")

            time.sleep(args.period)
    except KeyboardInterrupt:
        print("\nInterrotto manualmente.")
    finally:
        client.loop_stop()
        client.disconnect()
        print("Disconnesso dal broker.")


if __name__ == "__main__":
    main()
