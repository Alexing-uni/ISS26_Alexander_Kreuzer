package devices;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/*
 * IoPortSim - simulatore HEADLESS dell'IOPort per la prova automatizzata TP1.
 *
 * Pubblica gli STESSI messaggi della web-gui, sui MEDESIMI canali SEPARATI:
 *   - pushbutton -> "cargoservice26in_out"  (lo ascolta solo il cargoservice)
 *   - sensor     -> "cargoservice26sonar"   (lo ascolta solo il sensormonitor)
 * Canali separati = ogni messaggio e' parsato da UN solo attore: si evita il
 * parsing concorrente dello stesso messaggio da parte di due attori (il runtime
 * tuProlog non e' thread-safe e cadrebbe sotto la raffica del sensor).
 *
 * Cosi' il tester esercita il sistema esattamente come farebbe l'utente dalla
 * web-gui, ma senza interazione umana. Broker: IP di sito (come DisplayMqtt),
 * per raggiungere il mosquitto del container anche con un mosquitto nativo sul
 * loopback; su una macchina pulita l'IP di sito instrada comunque al broker.
 */
public class IoPortSim {

    public static final String TOPIC_IN    = "cargoservice26in_out";
    public static final String TOPIC_SONAR = "cargoservice26sonar";

    private MqttClient client = null;
    private int seq = 0;

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

    public IoPortSim() {
        String broker = "tcp://" + localSiteIp() + ":1883";
        try {
            client = new MqttClient(broker, "ioportsim" + System.nanoTime(), new MemoryPersistence());
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            client.connect(opts);
            System.out.println("[IOPORTSIM] connesso al broker " + broker);
        } catch (Exception e) {
            client = null;
            System.out.println("[IOPORTSIM] broker non raggiungibile (" + broker + "): " + e.getMessage());
        }
    }

    private void pub(String topic, String payload) {
        if (client == null || !client.isConnected()) return;
        try { client.publish(topic, new MqttMessage(payload.getBytes())); }
        catch (Exception e) { System.out.println("[IOPORTSIM] publish fallita: " + e.getMessage()); }
    }

    /** pushbutton: richiesta di carico -> canale del cargoservice */
    public void loadrequest(String caller) {
        pub(TOPIC_IN, "msg(loadrequest,event,tester,none,loadrequest(" + caller + ")," + (++seq) + ")");
    }

    /** sensor: una lettura del sonar -> canale del sensormonitor */
    public void distance(int d) {
        pub(TOPIC_SONAR, "msg(sonaralarm,event,tester,none,distance(" + d + ")," + (++seq) + ")");
    }
}
