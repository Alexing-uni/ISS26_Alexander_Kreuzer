package conway.io; 

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsContext;

public class IoJavalin {
    
    protected AtomicInteger pageCounter = new AtomicInteger(0);
    protected Vector<WsContext> allConns = new Vector<>();
    protected String firstCaller = null;
    
    // NUEVO: El tablero global en memoria
    protected boolean[][] grid = new boolean[20][20];
    
    public IoJavalin() {
        var app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "src/main/resources/page";
                staticFiles.location = Location.EXTERNAL;
            });
            config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setIdleTimeout(java.time.Duration.ofMinutes(30));
            });
        }).start(8080);

        app.get("/", ctx -> {
            try {
                // NUEVO: Apuntamos al archivo Canvas del profesor
                java.nio.file.Path path = java.nio.file.Paths.get("src/main/resources/page/LifeIInOutCanvas.html");
                String content = java.nio.file.Files.readString(path);
                ctx.html(content);
            } catch (Exception e) {
                ctx.status(500).result("Error leyendo HTML: " + e.getMessage());
            }
        }); 

        app.ws("/eval", ws -> {
            ws.onConnect(ctx -> {
                ctx.session.setIdleTimeout(java.time.Duration.ofMinutes(30));
                
                int idAssegnato = pageCounter.incrementAndGet(); 
                String callerName = "caller" + idAssegnato;
                allConns.add(ctx);
                
                sendsafe(ctx, "ID:" + callerName);
                if (firstCaller == null) firstCaller = callerName;
                
                // Le pasamos la matriz entera al nuevo usuario para que vea el tablero al instante
                sendsafe(ctx, gridToJson());
            });

            ws.onMessage(ctx -> {
                String message = ctx.message();
                if (message.equals("PING") || message.contains("canvas-ready")) return;

                System.out.println("IoJavalin | eval receives: " + message);
                try {
                    if (message.contains("cell(")) { 
                        // Leer la fila y columna del click
                        Matcher m = Pattern.compile("cell\\((\\d+),(\\d+)\\)").matcher(message);
                        if(m.find()){
                            int r = Integer.parseInt(m.group(1));
                            int c = Integer.parseInt(m.group(2));
                            if (r < 20 && c < 20) {
                                grid[r][c] = !grid[r][c]; // Cambia la celda (viva/muerta)
                            }
                        }
                        // Enviar toda la matriz actualizada a todo el mundo de golpe
                        String jsonGrid = gridToJson();
                        for (WsContext conn : allConns) sendsafe(conn, jsonGrid);
                    } 
                    else if (message.contains("clear")) {
                        grid = new boolean[20][20]; // Resetear matriz
                        String jsonGrid = gridToJson();
                        for (WsContext conn : allConns) sendsafe(conn, jsonGrid);
                    }
                    else {
                        for (WsContext conn : allConns) sendsafe(conn, message);
                    }
                } catch(Exception e) {
                    System.err.println("IoJavalin | error: " + e.getMessage());
                }               
            });
            
            ws.onClose(ctx -> allConns.remove(ctx));
        });        
    }
    
    // Función mágica: Convierte la matriz de Java en formato JSON [[false, true]...]
    private String gridToJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int r = 0; r < 20; r++) {
            sb.append("[");
            for (int c = 0; c < 20; c++) {
                sb.append(grid[r][c] ? "true" : "false");
                if (c < 19) sb.append(",");
            }
            sb.append("]");
            if (r < 19) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    protected void sendsafe(WsContext ctx, String msg) {
        synchronized (ctx.session) {
            if (ctx.session.isOpen()) ctx.send(msg);
        }
    }

    public static void main(String[] args) {
        new IoJavalin();
    }
}