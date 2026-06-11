package devices;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/*
 * DisplayMqtt - display dell'IOPort come WEB-GUI (Sprint 2).
 *
 * Ogni show(msg) viene:
 *   1) stampato a video (come DisplaySim dello Sprint 0/1);
 *   2) pubblicato su MQTT, topic "cargoservice26display".
 * La pagina webgui/display.html (mqtt.js su websocket, porta 9001 di mosquitto)
 * si sottoscrive al topic e mostra il display in un browser.
 *
 * Robustezza: se il broker non c'e', il display continua a funzionare a video
 * (il cargoservice non dipende dal trasporto: e' un dettaglio sostituibile).
 */
public class DisplayMqtt {

    public static final String BROKER = "tcp://127.0.0.1:1883";
    public static final String TOPIC  = "cargoservice26display";

    private MqttClient client = null;

    public DisplayMqtt() {
        try {
            client = new MqttClient(BROKER, "cargodisplay" + System.nanoTime(), new MemoryPersistence());
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setCleanSession(true);
            client.connect(opts);
            System.out.println("[DISPLAY] web-gui connessa al broker " + BROKER + " topic=" + TOPIC);
        } catch (Exception e) {
            client = null;
            System.out.println("[DISPLAY] broker non raggiungibile: display solo a video (" + e.getMessage() + ")");
        }
    }

    public void show(String what) {
        System.out.println("[DISPLAY] " + what);
        if (client != null && client.isConnected()) {
            try {
                client.publish(TOPIC, new MqttMessage(what.getBytes()));
            } catch (Exception e) {
                System.out.println("[DISPLAY] publish fallita: " + e.getMessage());
            }
        }
    }
}
