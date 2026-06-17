package devices;

import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * WebIoPort - SERVE e APRE la web-gui dell'IOPort (Sprint 3).
 *
 * Risponde all'osservazione del docente: la web-gui non puo' essere un file
 * HTML aperto a mano con la sola uscita. Qui:
 *   1) un piccolo server HTTP (com.sun.net.httpserver, gia' nel JDK: nessuna
 *      dipendenza nuova) SERVE la pagina webgui/ioport.html su http://localhost:PORT;
 *   2) all'avvio l'APPLICAZIONE APRE da sola la pagina nel browser.
 * La pagina e' INTERATTIVA (pushbutton + sensor + display) e dialoga col sistema
 * via MQTT su websocket: il WebIoPort fa solo da "erogatore" della pagina,
 * coerente col pattern del corso (IoJavalin di conway26GuiAlone), ma senza
 * aggiungere il framework Javalin.
 */
public class WebIoPort {

    public static final int PORT = 8095;
    private HttpServer server = null;

    public WebIoPort() {
        try {
            byte[] page    = loadAsset("ioport.html");
            byte[] mqttLib = loadAsset("mqtt.min.js");   // SPRINT3: libreria MQTT servita in locale (no Internet)
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            // la libreria MQTT e' servita come FILE SEPARATO: context piu' specifico di "/", quindi ha
            // precedenza per questo path. Cosi' la pagina la carica con <script src="mqtt.min.js"> senza
            // dipendere da Internet e senza doverla inlineare (l'inline rompeva il tag <script>).
            server.createContext("/mqtt.min.js", exchange -> {
                exchange.getResponseHeaders().set("Content-Type", "application/javascript; charset=utf-8");
                exchange.sendResponseHeaders(200, mqttLib.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(mqttLib); }
            });
            server.createContext("/", exchange -> {
                byte[] body = page;
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
            });
            server.setExecutor(null);
            server.start();
            String url = "http://localhost:" + PORT;
            System.out.println("[WEBIOPORT] IOPort web-gui servita su " + url);
            openBrowser(url);
        } catch (Exception e) {
            System.out.println("[WEBIOPORT] impossibile avviare il server: " + e.getMessage());
        }
    }

    /** cerca un asset della web-gui nei path tipici (cwd = cartella del progetto quando si fa gradlew run) */
    private byte[] loadAsset(String filename) throws Exception {
        String[] dirs = { "webgui/", "./webgui/", "cargoservice26/webgui/" };
        for (String d : dirs) {
            Path p = Paths.get(d + filename);
            if (Files.exists(p)) return Files.readAllBytes(p);
        }
        throw new Exception(filename + " non trovato (cwd=" + Paths.get("").toAbsolutePath() + ")");
    }

    /**
     * L'applicazione apre la pagina in un profilo Edge DEDICATO, garantendo UNA
     * sola scheda della web-gui. Motivo: il sensor della pagina emette in CONTINUO
     * la distanza corrente; se restassero aperte piu' schede, ognuna emetterebbe
     * il proprio valore e le letture del sensor si CONTRADDIREBBERO (una a 45, una
     * a 20...) impedendo la rilevazione. I flag anti-throttling tengono vivo il
     * timer del sensor anche quando la scheda non e' in primo piano (l'utente
     * guarda la scena): senza, il browser rallenta i timer e il sensor "si addormenta".
     * Fallback: browser predefinito.
     */
    private void openBrowser(String url) {
        String profile = System.getProperty("java.io.tmpdir") + "edge_ioport_profile";
        try {
            // 1) chiudi eventuali schede precedenti della web-gui (SOLO questo profilo)
            new ProcessBuilder("powershell", "-NoProfile", "-Command",
                "Get-CimInstance Win32_Process -Filter \"Name='msedge.exe'\" -ErrorAction SilentlyContinue | " +
                "Where-Object { $_.CommandLine -like '*edge_ioport_profile*' } | " +
                "ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }"
            ).start().waitFor();
            Thread.sleep(1200);
            // 2) apri UNA scheda nel profilo dedicato, con flag anti-throttling
            new ProcessBuilder("cmd", "/c", "start", "", "msedge",
                "--user-data-dir=" + profile, "--no-first-run", "--new-window",
                "--disable-background-timer-throttling", "--disable-renderer-backgrounding",
                "--disable-backgrounding-occluded-windows", url).start();
            System.out.println("[WEBIOPORT] web-gui aperta (profilo dedicato, una sola scheda)");
            return;
        } catch (Exception ignored) { }
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (Exception ignored) { }
        try {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
        } catch (Exception e) {
            System.out.println("[WEBIOPORT] apri manualmente: " + url);
        }
    }
}
