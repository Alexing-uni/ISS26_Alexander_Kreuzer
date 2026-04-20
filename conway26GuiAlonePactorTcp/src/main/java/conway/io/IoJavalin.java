package main.java.conway.io;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.websocket.WsConnectContext;
 
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import main.java.conwaygui.GuiServer;
import main.java.conwaygui.MainGuiServer;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.ws.WsConnection;
import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.mqtt.MqttInteraction;
import unibo.basicomm23.msg.ApplMessage;

public class IoJavalin {
	/*
	 * Interazione con la pagina HTML via WS
	 * Interazione con il lifegame via MQTT
	 */
	private static AtomicInteger pageCounter = new AtomicInteger(0);
	public WsMessageContext pageCtx;
	private WsMessageContext lifeCtrlCtx ;
	private String name;
	private String firstCaller        = null;
	private String controllerName     = "lifegame";
	private String controllerProtocol = null;
	private String controllerUrl      = null;
	
	private GuiServer controller;
//	private Interaction controllerconn= null;
	
	private Vector<WsConnectContext> allConns = new Vector<WsConnectContext>();

	public IoJavalin(String name, GuiServer controller) { //name="guiserver"
		this.name       = name;
		this.controller = controller;
        var app = Javalin.create(config -> {
        	// Configurazione globale del timeout per le connessioni (dalla versione 6.x in avanti)
            //config.http.asyncTimeout = 300000L; // 5 minuti in millisecondi
        	config.jetty.modifyWebSocketServletFactory(factory -> {
                // Imposta il timeout (ad esempio 5 minuti)
                factory.setIdleTimeout(Duration.ofMinutes(30));
            });
        	config.staticFiles.add(staticFiles -> {
        		staticFiles.directory = "/main/resources/page"; // <-- RUTA CORREGIDA
        		staticFiles.location = Location.CLASSPATH; // Cerca dentro il JAR/Classpath
				/*
				 * i file sono "impacchettati" con il codice, non cercati sul disco rigido esterno.
				 */
		    });
		}).start(8080);
 
/*
 * --------------------------------------------
 * Parte HTTP        
 * --------------------------------------------
 */
        /*
         * --------------------------------------------
         * Parte HTTP        
         * --------------------------------------------
         */
        /*
         * --------------------------------------------
         * Parte HTTP        
         * --------------------------------------------
         */
                app.get("/", ctx -> {
                    String pageToServe = "";
                    
                    // VARIANTE 2: Si es el primero (0), le damos la página con botones. Si no, la de espectador.
                    if (pageCounter.get() == 0) {
                        pageToServe = "/main/resources/page/LifeIInOutCanvas.html";
                        CommUtils.outgreen(name + " | Sirviendo página de DUEÑO (Owner)");
                    } else {
                        pageToServe = "/main/resources/page/LifeIInOutCanvasObserver.html";
                        CommUtils.outyellow(name + " | Sirviendo página de ESPECTADOR (Observer)");
                    }
                    
                    var inputStream = getClass().getResourceAsStream(pageToServe); 
                    if (inputStream != null) {
                        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        ctx.html(content);
                    } else {
                        ctx.status(404).result("File non trovato nel file system: " + pageToServe);
                    }
                });
        
        
 /*
 * --------------------------------------------
 * Parte Websocket
 * --------------------------------------------
 */      
        app.ws("/eval", ws -> {
            ws.onConnect(
		        ctx -> { 
	    	        int idAssegnato = pageCounter.incrementAndGet();
	    	        String callerName = "caller"+idAssegnato;
	     	        sendsafe(ctx, "ID:" + callerName);
	     			if( firstCaller == null ) {
	     				firstCaller = callerName;
	     				//ownerctx    = ctx;
     			}
     			CommUtils.outmagenta("connected ..." + callerName);
     			allConns.add(ctx);
     			//sendToAll("Nuova connessione " + allConns.size());
     			 
        // Ogni 20 secondi invia un segnale per "svegliare" i proxy
//    	heartbeatTask = executor.scheduleAtFixedRate(
//            () -> { if(ctx.session.isOpen()) sendsafe(ctx,"PING");}, //lambda // CommUtils.outcyan("PING");
//                    20,  //QUANTO ASPETTARE LA PRIMA VOLTA. Se 0, il primo PING parte istantaneamente (inutile)
//                    20,  //OGNI QUANTO RIPETERE
//                    TimeUnit.SECONDS
//            );
		            });
             
            ws.onMessage(ctx -> {
                String message = ctx.message();     
                   	//Il server 'parla'   IApplMessage
	                IApplMessage m;
	                try {
	                	m = new ApplMessage(message);
	                }catch( Exception e) {
	                	CommUtils.outyellow(name + "receives a non ApplMessage:" + message);
	                	return;
	                }
//                    CommUtils.outcyan(name + " | receives from:" + m.msgSender() + " mid=" + m.msgId() + " dest=" + m.msgReceiver());
//                    		" pageCtx=" + (pageCtx!=null) + " allConns:" +allConns.size());
  
            		if(  m.msgSender().equals("unknown") && m.msgContent().contains("canvasready") && pageCtx==null) { 
                      	pageCtx = ctx;  //memorizzo connessione pagina
                    	CommUtils.outmagenta(name + " |  memorizzo pageCtx:" + pageCtx);
                    	return;
                    }		                	

            	    if( m.msgSender().equals(controllerName) )   hanleMsgFromAppl(m,ctx);
                	else if( m.msgSender().equals(firstCaller) ) hanleMsgFromPage(m);
             });
        });        
	}
 
	protected void hanleMsgFromPage(IApplMessage m) {
		IApplMessage msgToForward = m;
		
		// VARIANTE 1: Transformar cell y clear en REQUEST
		String content = m.msgContent();
		if (content.startsWith("cell(") || content.equals("clear")) {
			// Construimos una request manteniendo el remitente y el destinatario
			msgToForward = CommUtils.buildRequest(
					m.msgSender(), 
					m.msgId(), 
					content, 
					m.msgReceiver()
			);
			CommUtils.outgreen(name + " | Transformed to REQUEST: " + msgToForward);
		}
		
		if( MainGuiServer.workingForPolling) controller.answerToReadPolling( msgToForward );
		else controller.answerToReadEvent( msgToForward );
	}
	
	/*
	 * La gui emette un dispatch. Per payload cell(x,y) si può traformare in request
	 */
 	
	protected void hanleMsgFromAppl (IApplMessage m, WsMessageContext ctx) {
       	if( m.msgReceiver().equals(name) && m.msgContent().startsWith("[[")) { //canvas rep
            //CommUtils.outcyan(name + " | receives [[" + " from " + m.msgSender() + " to " + m.msgReceiver());
            sendToAll(  m.msgContent() );   //così aggiorno tutte le pagine e gli observer
    		return;
    	}
    	if( m.msgReceiver().equals(name) && m.msgContent().contains("cell(")) {    
       	 //Il controller remoto ha detto di modificare il colore di una cella
	       	if (pageCtx != null) {
	           	//Ci sono 3 arg - es. cell(5,6,1)
	       		pageCtx.send( m.msgContent()); 
	        }
	       	return;
    	}
    	if( m.msgReceiver().equals(name) && m.msgId().contains("endremoteclient")) { 
    		CommUtils.outmagenta(name + " | receives endremoteclient. Removing a ctx");
    		allConns.remove(ctx);
    	}
	}
	
    protected void sendsafe(WsContext ctx, String msg) {
    	synchronized (ctx.session) {  
            if (ctx.session.isOpen()) {
                ctx.send(msg);
            }
        }
    }
    
    public void sendToAll(String m) {
    	//CommUtils.outmagenta(name + " | sendToAll " + allConns.size());
    	allConns.forEach( (conn) ->	conn.send( m ) );
    }
	

	

}
