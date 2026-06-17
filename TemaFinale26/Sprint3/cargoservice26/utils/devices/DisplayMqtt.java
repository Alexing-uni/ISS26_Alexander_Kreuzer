package devices;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/*
 * DisplayMqtt - display dell'IOPort come WEB-GUI (Sprint 2).
 *
 * Ogni show(msg) viene:
 *   1) stampato a video (come DisplaySim dello Sprint 0/1);
 *   2) pubblicato su MQTT (RETAINED), topic "cargoservice26display".
 * La pagina webgui/display.html (mqtt.js su websocket ws://127.0.0.1:9001)
 * si sottoscrive al topic e mostra il display in un browser.
 *
 * BROKER: l'IP REALE del PC, NON localhost (avvertenza del corso!).
 * Motivo verificato sul campo: se sul PC gira anche un mosquitto nativo
 * (servizio Windows), questo occupa 127.0.0.1/::1:1883 e i messaggi non
 * arrivano al broker del container Docker (quello raggiunto dalla pagina).
 * L'indirizzo di sito (192.168.x/10.x) instrada sempre al container.
 *
 * Robustezza: se il broker non c'e', il display continua a funzionare a video.
 */
public class DisplayMqtt {

    public static final String TOPIC = "cargoservice26display";

    private MqttClient client = null;

    /** IP IPv4 di sito del PC (192.168.x / 10.x / 172.16-31), come da avvertenza del corso. */
    private static String localSiteIp() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                if (!nic.isUp() || nic.isLoopback() || nic.isVirtual()) continue;
                String nm = nic.getDisplayName().toLowerCase();
                if (nm.contains("wsl") || nm.contains("virtual") || nm.contains("vethernet")
                    || nm.contains("hyper-v") || nm.contains("loopback")) continue;
                Enumeration<InetAddress> addrs = nic.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress a = addrs.nextElement();
                    if (a.isSiteLocalAddress() && a.getAddress().length == 4) return a.getHostAddress();
                }
            }
        } catch (Exception e) { /* fallback sotto */ }
        return "127.0.0.1";
    }

    public DisplayMqtt() {
        String broker = "tcp://" + localSiteIp() + ":1883";
        try {
            client = new MqttClient(broker, "cargodisplay" + System.nanoTime(), new MemoryPersistence());
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            client.connect(opts);
            System.out.println("[DISPLAY] web-gui connessa al broker " + broker + " topic=" + TOPIC);
        } catch (Exception e) {
            client = null;
            System.out.println("[DISPLAY] broker non raggiungibile (" + broker + "): display solo a video");
        }
    }

    public void show(String what) {
        System.out.println("[DISPLAY] " + what);
        if (client != null && client.isConnected()) {
            try {
                MqttMessage m = new MqttMessage(what.getBytes());
                m.setRetained(true);   // la pagina vede subito l'ultimo stato
                client.publish(TOPIC, m);
            } catch (Exception e) {
                System.out.println("[DISPLAY] publish fallita: " + e.getMessage());
            }
        }
    }
}
