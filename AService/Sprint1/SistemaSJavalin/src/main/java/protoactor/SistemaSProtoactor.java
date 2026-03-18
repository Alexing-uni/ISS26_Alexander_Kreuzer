package main.java.protoactor;

import io.javalin.Javalin;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.utils.CommUtils;
import java.util.concurrent.CompletableFuture;
import main.java.protoactor26.ProtoActorContext26; // Importamos el contexto

public class SistemaSProtoactor {

    public static void main(String[] args) {
        
        // 1. Creamos un contexto REAL para el actor (el "mundo" donde vive)
        // Nota: Le pasamos un nombre ("ctxWeb") y un puerto libre (8070)
        ProtoActorContext26 actorContext = new ProtoActorContext26("ctxWeb", 8070);

        // 2. Inicializamos el actor pasándole su contexto (¡adiós al null!)
        MathActor mathActor = new MathActor("mathActor", actorContext);

        // 3. Arrancamos Javalin
        Javalin app = Javalin.create().start(8080);
        CommUtils.outblue("SistemaSProtoactor avviato sulla porta 8080...");

        // Endpoint HTTP: http://localhost:8080/eval?x=5.0
        // Usamos 'webCtx' para que no choque con el contexto del actor
        app.get("/eval", webCtx -> {
            String parametro = webCtx.queryParam("x");
            // Por si acaso entramos sin poner número, le ponemos 0.0 por defecto
            if (parametro == null) parametro = "0.0"; 
            
            IApplMessage req = CommUtils.buildRequest("javalinWeb", "doEval", parametro, "mathActor");
            CommUtils.outcyan("Javalin | Invio richiesta all'attore e libero il thread Web!");
            
            CompletableFuture<String> promesa = CompletableFuture.supplyAsync(() -> {
                try {
                    IApplMessage reply = mathActor.elabRequest(req);
                    return "Risultato dall'attore: " + reply.msgContent();
                } catch (Exception e) {
                    return "Errore: " + e.getMessage();
                }
            });
            
            // LA CLAVE: Le decimos a Javalin explícitamente que cuando la promesa termine, 
            // coja el texto y lo ponga en el 'result' de la web para cerrar la conexión.
            webCtx.future(() -> promesa.thenAccept(resultadoTexto -> {
                webCtx.result(resultadoTexto);
            }));
        });
    }
}