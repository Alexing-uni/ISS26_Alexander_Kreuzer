package conway.domain;

import io.javalin.Javalin;
import main.java.conway.domain.Life; // Importación exacta según tu código

public class ConwayService {
    private Life game;
    private final int ROWS = 20;
    private final int COLS = 20;

    public void startService() {
        // 1. Iniciamos Javalin (El Servicio)
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
        }).start(8080);

        // 2. Usamos el constructor de tu clase Life
        game = new Life(ROWS, COLS);

        System.out.println("Servicio ConwayLife corriendo en el puerto 8080...");

        // --- HTTP: Obtener estado en JSON ---
        app.get("/grid", ctx -> {
            // Usamos tu método getGrid() que devuelve boolean[][]
            ctx.json(game.getGrid()); 
        });

        // --- WebSocket: Control ---
        app.ws("/conway", ws -> {
            ws.onMessage(ctx -> {
                String msg = ctx.message();
                if ("next".equalsIgnoreCase(msg)) {
                    // LLAMADA CORREGIDA: Usamos el nombre exacto de tu método
                    game.nextGeneration(); 
                    ctx.send("Generación actualizada");
                } else if ("clear".equalsIgnoreCase(msg)) {
                    game.clear(); // Usamos tu método clear()
                    ctx.send("Tablero limpiado");
                }
            });
        });
    }

    public static void main(String[] args) {
        new ConwayService().startService();
    }
}